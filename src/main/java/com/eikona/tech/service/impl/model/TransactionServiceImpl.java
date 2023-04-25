package com.eikona.tech.service.impl.model;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.AreaConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.constants.TransactionConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.TransactionRepository;
import com.eikona.tech.service.TransactionService;
import com.eikona.tech.util.CalendarUtil;
import com.eikona.tech.util.GeneralSpecificationUtil;
import com.eikona.tech.util.ImportTransaction;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private GeneralSpecificationUtil<Transaction> generalSpecification;

	@Autowired
	private CalendarUtil calendarUtil;
	
	@Autowired
	private ImportTransaction importTransaction;

	@Override
	public PaginationDto<Transaction> searchByField(String sDate, String eDate, String employeeId,
			String employeeName, String device, String department, String designation, int pageno,
			String sortField, String sortDir, String orgName) {
		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {

				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}

		Page<Transaction> page = getTransactionBySpecification(employeeId, employeeName, device, department,
				designation, pageno, sortField, sortDir, startDate, endDate, orgName);
		List<Transaction> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Transaction> dtoList = new PaginationDto<Transaction>(employeeShiftList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Transaction> getTransactionBySpecification(String employeeId,String employeeName,
			String device, String department, String designation, int pageno, String sortField,
			String sortDir, Date startDate, Date endDate, String orgName) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno -NumberConstants.ONE , NumberConstants.TEN, sort);

//		Specification<Transaction> allSpec = null;
//		if(TransactionConstants.ONLY_EMPLOYEE.equalsIgnoreCase(employee)) {
//			allSpec = generalSpecification.isNotNullSpecification(TransactionConstants.EMP_ID);
//		}else {
//			allSpec = generalSpecification.isNotNullSpecification(ApplicationConstants.DELIMITER_EMPTY);
//		}
		Specification<Transaction> dateSpec = generalSpecification.dateSpecification(startDate, endDate,
				TransactionConstants.PUNCH_DATE);
		Specification<Transaction> empIdSpec = generalSpecification.stringSpecification(employeeId, TransactionConstants.EMP_ID);
		Specification<Transaction> empNameSpec = generalSpecification.stringSpecification(employeeName, ApplicationConstants.NAME);
		Specification<Transaction> devSpec = generalSpecification.stringEqualSpecification(device, TransactionConstants.DEVICE_NAME);
		Specification<Transaction> deptSpec = generalSpecification.stringSpecification(department,
				TransactionConstants.DEPARTMENT);
		Specification<Transaction> desiSpec = generalSpecification.stringSpecification(designation,
				TransactionConstants.DESIGNATION);
		Specification<Transaction> orgSpec = generalSpecification.stringSpecification(orgName,
				AreaConstants.ORGANIZATION);

		Page<Transaction> page = transactionRepository.findAll(dateSpec.and(empIdSpec).and(empNameSpec)
				.and(devSpec).and(deptSpec).and(desiSpec).and(orgSpec), pageable);
		return page;
	}

	//	to store the time log excel to database
	@SuppressWarnings("static-access")
	public void storeTimeLog(MultipartFile file) {

		try {
			List<Transaction> transaction = importTransaction.parseExcelFileTimelog(file.getInputStream());

			List<Transaction> saveTransaction = new ArrayList<Transaction>(); 
			
			String currDate = "";
			String currOrg = "";
			
			List<Transaction> tempTransactionList = new ArrayList<Transaction>();
			boolean isNew = true;
			for (Transaction currTransaction : transaction) {
				isNew = true;
				if(!currDate.equalsIgnoreCase(currTransaction.getPunchDateStr()) && !currOrg.equalsIgnoreCase(currTransaction.getOrganization())){
					currDate = currTransaction.getPunchDateStr();
					currOrg  = currTransaction.getOrganization();
					tempTransactionList = transactionRepository.findByPunchDateStrAndOrganization(currDate, currOrg);
				}
				
				for (Transaction tempTransaction : tempTransactionList) {
					if(
							tempTransaction.getEmpId().equalsIgnoreCase(currTransaction.getEmpId()) &&
							tempTransaction.getPunchDateStr().equalsIgnoreCase(currTransaction.getPunchDateStr()) &&
							tempTransaction.getPunchTimeStr().equalsIgnoreCase(currTransaction.getPunchTimeStr()) 
					) {
						isNew =false;
						break;
					}
				}
				
				if(isNew) {
					saveTransaction.add(currTransaction);
				}
			}
			
			transactionRepository.saveAll(saveTransaction);
			System.out.println("saveTransaction.size()");
			System.out.println(saveTransaction.size());
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// to store temperature excel to Database
	public void storeTemp(MultipartFile file) throws Exception {
		try {
			List<Transaction> transaction = importTransaction.parseExcelFileTemp(file.getInputStream());

			//Save temperature excel to DataBase
			for(Transaction t: transaction) {
				if(t!=null)
					transactionRepository.save(t);
			}
//			transactionRepository.saveAll(transaction);
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}
		
	// to store the bio security excel to database
	@SuppressWarnings("static-access")
	public void storeBio(MultipartFile file) throws Exception {
		try {
			List<Transaction> transaction = importTransaction.parseExcelFileBioSecurity(file.getInputStream());

			//Save bio security to DataBase
			transactionRepository.saveAll(transaction);
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}

	// to store Employee details excel to Database (import)
	@SuppressWarnings("static-access")
	public void storeEmp(MultipartFile file) throws Exception {
		try {
			List<Transaction> transaction = importTransaction.parseExcelFileEmployeeDetails(file.getInputStream());

			//Save temperature excel to DataBase
			transactionRepository.saveAll(transaction);
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}
		
	// to store the Easy Time Pro excel to database
	@SuppressWarnings("static-access")
	public void storeEtp(MultipartFile file) throws Exception {
		try {
			List<Transaction> transaction = importTransaction.parseExcelFileEasyTimePro(file.getInputStream());

			//Save Easy Time Pro to DataBase
			transactionRepository.saveAll(transaction);
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}

	@Override
	public PaginationDto<Transaction> searchByField(int pageno, String sortField, String sortDir, String orgName,String flag) {
		Page<Transaction> page = null;
		
		List<Transaction> unregisteredEmpList = new ArrayList<Transaction>();
		if("Unregistered".equalsIgnoreCase(flag))
			page = getUnregisteredTransactionBySpecification(pageno, sortField, sortDir, orgName,flag);
		else 
			page = getUnEnrolledTransactionBySpecification(pageno, sortField, sortDir, orgName,flag);
		
		unregisteredEmpList = page.getContent();
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Transaction> dtoList = new PaginationDto<Transaction>(unregisteredEmpList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Transaction> getUnEnrolledTransactionBySpecification(int pageno, String sortField, String sortDir,
			String orgName, String flag) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno -NumberConstants.ONE , NumberConstants.TEN, sort);

		Specification<Transaction> orgSpec = generalSpecification.stringSpecification(orgName,
				AreaConstants.ORGANIZATION);
		Specification<Transaction> enrollStatusSpec = generalSpecification.stringSpecification(flag,TransactionConstants.ENROLL_STATUS);
		Page<Transaction> page = transactionRepository.findAll(orgSpec.and(enrollStatusSpec), pageable);
		return page;
	
	}

	private Page<Transaction> getUnregisteredTransactionBySpecification(int pageno, String sortField, String sortDir,
			String orgName, String flag) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno -NumberConstants.ONE , NumberConstants.TEN, sort);

		Specification<Transaction> orgSpec = generalSpecification.stringSpecification(orgName,
				AreaConstants.ORGANIZATION);
		Specification<Transaction> nameSpec = generalSpecification.stringSpecification(flag,
				ApplicationConstants.NAME);
		Page<Transaction> page = transactionRepository.findAll(orgSpec.and(nameSpec), pageable);
		return page;
	}
}
