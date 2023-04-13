package com.eikona.tech.service.impl.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Action;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.ActionRepository;
import com.eikona.tech.service.ActionDetailsService;
import com.eikona.tech.service.ActionService;
import com.eikona.tech.util.CalendarUtil;
import com.eikona.tech.util.GeneralSpecificationUtil;

@Service
@EnableScheduling
public class ActionServiceImpl implements ActionService {

	@Autowired
	private ActionRepository actionRepository;
	

	@Autowired
	private ActionDetailsService actionDetailsService;

	
	@Autowired
	private CalendarUtil  calendarUtil;
	
	@Autowired
	private GeneralSpecificationUtil<Action> generalSpecificationAction;


	@Override
	public void save(Action action) {
		action = actionRepository.save(action);
		actionDetailsService.saveAsAction(action);
	}

	@Override
	public Action getById(long id) {
		return actionRepository.findById(id).get();
	}


	@Override
	public void employeeAction(Employee employee, String type, String source) {
			Action action = new Action();
			action.setType(type);
			action.setEmployee(employee);
			action.setSource(source);
			action = actionRepository.save(action);
			actionDetailsService.saveAsAction(action);
	}
	
	@Override
	public void employeeDeviceAction(Device device,Employee employee, String type, String source) {
		if (null != device.getArea()) {
			Action action = new Action();
			action.setType(type);
			action.setEmployee(employee);
			action.setSource(source);
			action = actionRepository.save(action);
			actionDetailsService.saveAsDeviceAction(action,device);
		}
	}



	@Override
	public PaginationDto<Action> searchByField(String sDate, String eDate, String employeeId, String name,
			 int pageno, String sortField, String sortDir,String orgName) {
	
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
		Page<Action> page = getPageData(startDate,endDate, employeeId, name,pageno, sortField, sortDir,orgName);
        List<Action> employeeList =  page.getContent();
        
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Action> dtoList = new PaginationDto<Action>(employeeList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	
	}

	private Page<Action> getPageData(Date startDate, Date endDate, String employeeId, String name,
			int pageno, String sortField, String sortDir, String orgName) {

		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		Specification<Action> dateSpec = generalSpecificationAction.dateSpecification(startDate, endDate, ApplicationConstants.LAST_MODIFIED_DATE);
		Specification<Action> empIdSpc = generalSpecificationAction.foreignKeyStringSpecification(employeeId, "employee", "empId");
		Specification<Action> employeeNameSpc = generalSpecificationAction.foreignKeyStringSpecification(name,"employee", "name");
		Specification<Action> orgSpc = generalSpecificationAction.foreignKeyDoubleObjectStringSpecification(orgName,"employee", "organization","name");
		
    	Page<Action> page = actionRepository.findAll(dateSpec.and(empIdSpc).and(employeeNameSpc).and(orgSpc), pageable);
		return page;
	
	}
}
