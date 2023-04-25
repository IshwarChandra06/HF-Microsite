package com.eikona.tech.service.impl.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.AreaConstants;
import com.eikona.tech.constants.DailyAttendanceConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.DailyReport;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.DailyAttendanceRepository;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.repository.OrganizationRepository;
import com.eikona.tech.repository.TransactionRepository;
import com.eikona.tech.service.DailyAttendanceService;
import com.eikona.tech.util.CalendarUtil;
import com.eikona.tech.util.GeneralSpecificationUtil;

@Service
@EnableScheduling
public class DailyAttendanceServiceImpl implements DailyAttendanceService {

	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;

	@Autowired
	private GeneralSpecificationUtil<DailyReport> generalSpecificationDailyAttendance;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private CalendarUtil calendarUtil;
	
	@Value("${dailyreport.autogenerate.enabled}")
	private String enableGenerate;
	
	@Scheduled(cron ="0 0 9 * * *")
	public void autoGenerateDailyReport() {
		
		if("Yes".equalsIgnoreCase(enableGenerate)) {
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date yesterday=calendarUtil.getPreviousDate(new Date(), -1, 0, 0,0);
			String date=inputFormat.format(yesterday);
			List<Organization> orgList=organizationRepository.findAllByIsDeletedFalse();
			for(Organization org:orgList) {
				generateDailyAttendance(date,date,org.getName());
				generateNotPunchDailyAttendance(date, date, org.getName());
			}
			
		}
		
	}
public void generateNotPunchDailyAttendance(String sDate, String eDate, String organization) {
		
		try {
			
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			sDate = format.format(inputFormat.parse(sDate));
			eDate = format.format(inputFormat.parse(eDate));
			
			Date startDate = calendarUtil.getConvertedDate(format.parse(sDate), 00, 00, 00);
			Date endDate = calendarUtil.getConvertedDate(format.parse(eDate), 23, 59, 59);
			
			List<String> dailyReportEmpIdList = dailyAttendanceRepository.findByDateAndOrganizationCustom(startDate, endDate, organization);
			
			if(dailyReportEmpIdList.isEmpty())
				dailyReportEmpIdList.add(" ");
			
			
			List<Employee> absentEmployeeList = employeeRepository.findByEmpIdAndIsDeletedFalseCustom(dailyReportEmpIdList, organization);
			List<DailyReport> dailyReportList = new ArrayList<>();
			for(Employee employee : absentEmployeeList) {
				
				DailyReport dailyReport = new DailyReport();
				dailyReport.setEmpId(employee.getEmpId().trim());
				dailyReport.setDateStr(sDate);
				dailyReport.setDate(startDate);
				dailyReport.setEmployeeName(employee.getName());
				dailyReport.setOrganization((null == employee.getOrganization()?"":employee.getOrganization().getName()));
				dailyReport.setDepartment((null == employee.getDepartment()?"":employee.getDepartment().getName()));
				dailyReport.setDesignation((null == employee.getDesignation()?"":employee.getDesignation().getName()));
				dailyReport.setGrade(employee.getGrade());
				dailyReport.setMobile(employee.getMobile());
				dailyReport.setEmployeeType("Employee");
				dailyReport.setUserType("Employee");
				dailyReport.setPunchInDevice("Not Punched");
				dailyReport.setAttendanceStatus("-");
				dailyReport.setMissedOutPunch(false);
				dailyReportList.add(dailyReport);
				
			}
			
			dailyAttendanceRepository.saveAll(dailyReportList);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	@SuppressWarnings("deprecation")
	@Override
	public List<DailyReport> generateDailyAttendance(String sDate, String eDate, String organization) {

		List<DailyReport> dailyReportList = new ArrayList<>();
		try {
			
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			sDate = format.format(inputFormat.parse(sDate));
			eDate = format.format(inputFormat.parse(eDate));
			
			Date startDate = calendarUtil.getConvertedDate(format.parse(sDate), 00, 00, 00);
			Date endDate = calendarUtil.getConvertedDate(format.parse(eDate), 23, 59, 59);
	
			List<Transaction> transactionList = transactionRepository.getTransactionData(startDate,
					endDate, organization);
			
			List<DailyReport> delDailyReportList = dailyAttendanceRepository.findByDateAndOrganization(startDate,
					endDate, organization);

			Map<String, DailyReport> existingReportMap = new HashMap<String, DailyReport>();
			
			for (DailyReport dailyReport : delDailyReportList) {
				String key = dailyReport.getEmpId() + "-" + dailyReport.getDateStr();
				key = key.trim();
				existingReportMap.put(key, dailyReport);
			}
			
			Map<String, DailyReport> reportMap = new HashMap<String, DailyReport>();

			for (Transaction transaction : transactionList) {
	
				try {
					
					String key = transaction.getEmpId() + "-" + transaction.getPunchDateStr();
					key = key.trim();
					DailyReport dailyReport = null;
					
					Date currDate = format.parse(transaction.getPunchDateStr());
					if((transaction.getOrganization().contains("Angul") || transaction.getOrganization().contains("Kalinganagar") || transaction.getOrganization().contains("Meramandali")) && transaction.getPunchDate().getHours() < 8) {
						
						Calendar currDateCal = Calendar.getInstance();
						currDateCal.setTime(currDate);
						currDateCal.add(Calendar.DAY_OF_MONTH, -1);
						currDate = currDateCal.getTime();
						
						DailyReport dailyReportYesterday = dailyAttendanceRepository.findByEmpIdAndDateAndOrganizationAndPunchInDevice(transaction.getEmpId().trim(),
								currDate, transaction.getOrganization(), "Punched");
						
						if(null!=dailyReportYesterday) {
							if(Integer.parseInt(dailyReportYesterday.getEmpInTime().split(":")[0]) < 20) {
								currDate = format.parse(transaction.getPunchDateStr());
							}else {
								key = dailyReportYesterday.getEmpId() + "-" + dailyReportYesterday.getDateStr();
								key = key.trim();
								if(!existingReportMap.containsKey(key)) {
									existingReportMap.put(key, dailyReportYesterday);
								}
							}
						}
					}
					
					if(existingReportMap.containsKey(key)) {
						reportMap.put(key, existingReportMap.get(key));
					}
					
					if(!reportMap.containsKey(key)) {
						
						if(transaction.getDeviceName().contains("TEPP Exit")) {
							continue;
						}
						dailyReport = new DailyReport();
						dailyReport.setEmpId(transaction.getEmpId().trim());
						dailyReport.setDateStr(transaction.getPunchDateStr());
						dailyReport.setDate(format.parse(transaction.getPunchDateStr()));
						dailyReport.setEmployeeName(transaction.getName());
						dailyReport.setOrganization(transaction.getOrganization());
						dailyReport.setDepartment(transaction.getDepartment());
						dailyReport.setDesignation(transaction.getDesignation());
						dailyReport.setGrade(transaction.getGrade());
						dailyReport.setMobile(transaction.getMobile());
						dailyReport.setEmployeeType("Employee");
						dailyReport.setUserType("Employee");
						dailyReport.setShift("General");
						dailyReport.setShiftInTime("09:00:00");
						dailyReport.setShiftOutTime("18:00:00");
						dailyReport.setMissedOutPunch(true);
						dailyReport.setPunchInDevice("Punched");
						dailyReport.setEmpInTime(transaction.getPunchTimeStr());
						dailyReport.setEmpInTemp(transaction.getTemperature());
						dailyReport.setEmpInMask(transaction.getWearingMask());
						dailyReport.setEmpInAccessType(transaction.getAccessType());
						dailyReport.setEmpInLocation(transaction.getDeviceName());
						dailyReport.setAttendanceStatus("Present");
						String city = "";
						 if(transaction.getDeviceName().contains("Angul") || transaction.getOrganization().contains("Kalinganagar") 
								|| transaction.getOrganization().contains("Meramandali")) {
							city = "Angul";
							if(transaction.getOrganization().contains("Kalinganagar"))
								city = "Kalinganagar";
							else if(transaction.getOrganization().contains("Meramandali")) {
								city = "Meramandali";
							}
							dailyReport.setShiftInTime("09:00:00");
							dailyReport.setShiftOutTime("18:00:00");
							
							int hour = Integer.parseInt(transaction.getPunchTimeStr().split(":")[0]);
							if(hour <= 7) {
									dailyReport.setShift("1st Shift"); //5:30 to 13:00
									dailyReport.setShiftInTime("06:00:00"); 
									dailyReport.setShiftOutTime("14:00:00");
							}
							
							if(hour >= 12) {
									dailyReport.setShift("2nd Shift"); //13:30 to 20:30
									dailyReport.setShiftInTime("14:00:00");
									dailyReport.setShiftOutTime("22:00:00");
							}
							
							if(hour >= 20) {
								dailyReport.setShift("3rd Shift"); //21:30 to 4:30 9to 5:30 
								dailyReport.setShiftInTime("22:00:00");
								dailyReport.setShiftOutTime("06:00:00");
							}
						}
						
						dailyReport.setCity(city);
	
						LocalTime shiftIn = LocalTime.parse(dailyReport.getShiftInTime());
						LocalTime empIn = LocalTime.parse(dailyReport.getEmpInTime().replace("'", ""));
	
						Long lateComing = shiftIn.until(empIn, ChronoUnit.MINUTES);
						Long earlyComing = empIn.until(shiftIn, ChronoUnit.MINUTES);
	
						if (earlyComing > 0)
							dailyReport.setEarlyComing(earlyComing);
	
						if (lateComing > 0)
							dailyReport.setLateComing(lateComing);
						
						dailyReport.setCity(city);
						reportMap.put(key, dailyReport);
					} else {
						
						dailyReport = reportMap.get(key);
						if (dailyReport.getEmpInTime().equalsIgnoreCase(transaction.getPunchTimeStr())) {
							continue;
						} else {
							
							dailyReport.setEmpOutTime(transaction.getPunchTimeStr());
							dailyReport.setEmpOutTemp(transaction.getTemperature());
							dailyReport.setEmpOutMask(transaction.getWearingMask());
							dailyReport.setEmpOutAccessType(transaction.getAccessType());
							dailyReport.setEmpOutLocation(transaction.getDeviceName());
	
							dailyReport.setMissedOutPunch(false);
	
							LocalTime shiftIn = LocalTime.parse(dailyReport.getShiftInTime());
							LocalTime shiftOut = LocalTime.parse(dailyReport.getShiftOutTime());
							LocalTime empIn = LocalTime.parse(dailyReport.getEmpInTime());
							LocalTime empOut = LocalTime.parse(dailyReport.getEmpOutTime());
	
                            Long shiftMinutes = shiftIn.until(shiftOut, ChronoUnit.MINUTES);
							
							if("3rd Shift".equalsIgnoreCase(dailyReport.getShift()))
								shiftMinutes =480l;
	
							Long workHours = empIn.until(empOut, ChronoUnit.HOURS);
							Long workMinutes = empIn.until(empOut, ChronoUnit.MINUTES);
							
							if(workHours<0) {
								workHours = 24 + workHours;
								workMinutes = 60 + workMinutes;
							}
							
	
							dailyReport.setWorkTime(String.valueOf(workHours) + ":" + String.valueOf(workMinutes % 60));
							
							if("3rd Shift".equalsIgnoreCase(dailyReport.getShift()) && workHours>2) {
								Long tillMidNight=empIn.until(LocalTime.parse("23:59:59"), ChronoUnit.MINUTES);
								Long afterMidNight=LocalTime.parse("00:00:00").until(empOut, ChronoUnit.MINUTES);
								workMinutes =tillMidNight+afterMidNight;
								dailyReport.setWorkTime(String.valueOf(workMinutes/60) + ":" + String.valueOf(workMinutes % 60));
							}
//							workMinutes = workMinutes % 60;
							
							
	
							Long overTime = workMinutes - shiftMinutes;
							Long earlyGoing =0l;
							Long lateGoing =0l;
							if("3rd Shift".equalsIgnoreCase(dailyReport.getShift()) && workHours<2) {
								 earlyGoing = empOut.until(LocalTime.parse("23:59:59"), ChronoUnit.MINUTES)+360l;
							}else {
								 lateGoing = shiftOut.until(empOut, ChronoUnit.MINUTES);
								 earlyGoing = empOut.until(shiftOut, ChronoUnit.MINUTES);
							}
	
							
	
							
							if (lateGoing > 0)
								dailyReport.setLateGoing(lateGoing);
							else
								dailyReport.setLateGoing(null);
	
							if (earlyGoing > 0) {
								dailyReport.setEarlyGoing(earlyGoing);
							} else {
								dailyReport.setEarlyGoing(null);
							}
	
							if (overTime > 0) {
								dailyReport.setOverTime(overTime);
								dailyReport.setOverTimeStr(overTime/60+":"+(overTime % 60));
							}
								
							
							reportMap.put(key, dailyReport);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		
			Set<String> keySet =  reportMap.keySet();
			for (String string : keySet) {
				dailyReportList.add(reportMap.get(string));
			}
			dailyAttendanceRepository.saveAll(dailyReportList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dailyReportList;
	}


	@Override
	public PaginationDto<DailyReport> searchByField(String sDate, String eDate, String employeeId,
			String employeeName,String department, String designation, String status,String shift, String punchStatus, int pageno,
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
		
		Page<DailyReport> page = getDailyAttendancePage(employeeId, employeeName,"", department,
				designation, pageno, sortField, sortDir, startDate, endDate, status,shift, orgName,punchStatus);
		List<DailyReport> employeeShiftList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<DailyReport> dtoList = new PaginationDto<DailyReport>(employeeShiftList,
				page.getTotalPages(), page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}
	
	private Page<DailyReport> getDailyAttendancePage(String employeeId, String employeeName, String organization,
			String department, String designation, int pageno, String sortField, String sortDir, Date startDate,
			Date endDate, String status,String shift, String orgName, String punchStatus) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<DailyReport> dateSpec = generalSpecificationDailyAttendance.dateSpecification(startDate, endDate, ApplicationConstants.DATE);
		Specification<DailyReport> empIdSpec = generalSpecificationDailyAttendance.stringSpecification(employeeId, DailyAttendanceConstants.EMPLOYEE_ID);
		Specification<DailyReport> empNameSpec = generalSpecificationDailyAttendance.stringSpecification(employeeName, DailyAttendanceConstants.EMPLOYEE_NAME);
		Specification<DailyReport> orgSpec = generalSpecificationDailyAttendance.stringSpecification(organization, AreaConstants.ORGANIZATION);
		Specification<DailyReport> userOrgSpec = generalSpecificationDailyAttendance.stringSpecification(orgName, AreaConstants.ORGANIZATION);
		Specification<DailyReport> deptSpec = generalSpecificationDailyAttendance.stringSpecification(department, DailyAttendanceConstants.DEPARTMENT);
		Specification<DailyReport> desiSpec = generalSpecificationDailyAttendance.stringSpecification(designation, DailyAttendanceConstants.DESIGNATION);
		Specification<DailyReport> statusSpec = generalSpecificationDailyAttendance.stringSpecification(status, DailyAttendanceConstants.ATTENDANCE_STATUS);
		Specification<DailyReport> shiftSpec = generalSpecificationDailyAttendance.stringSpecification(shift, DailyAttendanceConstants.SHIFT);
		Specification<DailyReport> punchStatusSpec = generalSpecificationDailyAttendance.stringEqualSpecification(punchStatus,"punchInDevice");
		Page<DailyReport> page = dailyAttendanceRepository.findAll(statusSpec.and(dateSpec).and(empIdSpec)
				.and(empNameSpec).and(deptSpec).and(desiSpec).and(userOrgSpec).and(orgSpec).and(shiftSpec).and(punchStatusSpec), pageable);
		return page;
	}

	@Override
	public List<DailyReport> getAllDailyReport() {
		 return (List<DailyReport>) dailyAttendanceRepository.findAll();
	}
}