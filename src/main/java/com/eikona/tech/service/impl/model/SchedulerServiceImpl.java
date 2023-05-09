package com.eikona.tech.service.impl.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eikona.tech.entity.Action;
import com.eikona.tech.entity.ActionDetails;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.ActionDetailsRepository;
import com.eikona.tech.repository.ActionRepository;
import com.eikona.tech.repository.DeviceRepository;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.service.ActionService;
import com.eikona.tech.util.HFSecurityDeviceUtil;
@Component
@EnableScheduling
public class SchedulerServiceImpl {
	
	@Autowired
	private ActionDetailsRepository actionDetailsRepository;
	
	@Autowired
	private ActionDetailsServiceImpl actionDetailsServiceImpl;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ActionService actionService;
	
	@Autowired
	private ActionRepository actionRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private HFSecurityDeviceUtil hfSecurityDeviceUtil;
	
	@Scheduled(cron = "0 0 0/4 * * ?")
	public void syncPendingDataFromMataToDevice() {
		
		List<ActionDetails> actionDetailList = actionDetailsRepository.findByStatus("Pending");
		List<ActionDetails> saveActionDetailList=new ArrayList<ActionDetails>();
		
		for(ActionDetails actionDetails:actionDetailList) {
			  actionDetailsServiceImpl.addEmployeeToHFSecurity(actionDetails,actionDetails.getAction().getEmployee(),actionDetails.getDevice());
			  saveActionDetailList.add(actionDetails);
		}
		actionDetailsRepository.saveAll(saveActionDetailList);
	}
	
 	@Scheduled(cron="0 0 13 * * *")
	public void autoSyncEmployeeFromMataToDevice() {
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalseAndIsSyncFalseCustom();
		for (Employee employee : employeeList) {
			actionService.employeeAction(employee, "Sync", "App");
		}
	}
//@Scheduled(cron = "0 0/15 * * * ?")
	public void keepDeviceOnline() {
		List<Device> deviceList=deviceRepository.findAllByIsDeletedFalse();
		for(Device device:deviceList) {
			try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = format.format(new Date());
			Date date = format.parse(dateStr);
            Date lastonline = device.getLastOnline();
			
			long mileseconds = date.getTime() - lastonline.getTime();
			if(mileseconds>300000) {
				hfSecurityDeviceUtil.setHeartbeatUrl(device.getSerialNo());
				hfSecurityDeviceUtil.setEventLogUrl(device.getSerialNo());
			}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
	}

//@Scheduled(cron = "0 0 0/1 * * ?")
public void setDeviceConfig() {
	List<Device> deviceList=deviceRepository.findAllByIsDeletedFalse();
	for(Device device:deviceList) {
		
		try {
			hfSecurityDeviceUtil.setDeviceConfig(device.getSerialNo());
			TimeUnit.SECONDS.sleep(30);
			hfSecurityDeviceUtil.setDeviceConfig(device.getSerialNo());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}

@Scheduled(cron="0 0 3 * * *")
public void rebootAllDevices() {
	List<Device> deviceList=deviceRepository.findAllByIsDeletedFalse();
	for(Device device:deviceList) {
		try {
			hfSecurityDeviceUtil.rebootHFDevice(device.getSerialNo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
@Scheduled(cron="0 0 2 * * *")
public void updateErrorActionDetails() {
	
	List<ActionDetails> actionDetailList = actionDetailsRepository.findByStatus("Error");
	List<ActionDetails> deleteActionDetailList=new ArrayList<ActionDetails>();
	
	for(ActionDetails actionDetails:actionDetailList) {

		List<ActionDetails> errorActionDetailList= actionDetailsRepository.findByEmpIdAndDeviceCustom(actionDetails.getAction().getEmployee().getEmpId(), actionDetails.getDevice().getId());
		
		if(errorActionDetailList.size()>1) {
			for(int i=1; i<errorActionDetailList.size(); i++) {
				ActionDetails errorActionDetails = errorActionDetailList.get(i);
				deleteActionDetailList.add(errorActionDetails);
			}
		}
	}
	actionDetailsRepository.deleteAll(deleteActionDetailList);
}

@Scheduled(cron="0 0 18 * * *")
public void deleteEmployeeFromHF() {
	List<Employee> employeeList = employeeRepository.findAllByIsDeletedTrueAndIsSyncTrue();
	List<Employee> saveEmployeeList=new ArrayList<Employee>();
	for (Employee employee : employeeList) {
		if (null != employee.getArea()) {
			List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(employee.getArea());
			List<ActionDetails> saveActionDetailList=new ArrayList<ActionDetails>();
			for (Device device : deviceList) {
				if ("HF-Security".equalsIgnoreCase(device.getModel())) {
					try {
						boolean isResponse=hfSecurityDeviceUtil.deleteEmployeeFromHFDevice(employee.getEmpId(), device);
						Action action=new Action();
						action.setEmployee(employee);
						action.setSource("App");
						action.setType("Delete");
						action=actionRepository.save(action);
						
						ActionDetails actionDetails = new ActionDetails();
						actionDetails.setAction(action);
						actionDetails.setDevice(device);
						if(isResponse) {
							actionDetails.setStatus("Completed");
							actionDetails.setMessage("Completed");
							employee.setSync(false);
							employee.setFaceSync(false);
							saveEmployeeList.add(employee);
						}
						else {
							actionDetails.setStatus("Delete Pending");
							actionDetails.setMessage("Device not connected with network");
						}
						
						saveActionDetailList.add(actionDetails);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			actionDetailsRepository.saveAll(saveActionDetailList);
		}
	}
	employeeRepository.saveAll(saveEmployeeList);
}

@Scheduled(cron="0 30 2 * * *")
public void deletePendingDataFromDevice() {
	
	List<ActionDetails> actionDetailList = actionDetailsRepository.findByStatus("Delete Pending");
	List<ActionDetails> saveActionDetailList=new ArrayList<ActionDetails>();
	for(ActionDetails actionDetails:actionDetailList) {
		boolean isResponse=hfSecurityDeviceUtil.deleteEmployeeFromHFDevice(actionDetails.getAction().getEmployee().getEmpId(), actionDetails.getDevice());
		if(isResponse) {
			actionDetails.setStatus("Completed");
			actionDetails.setMessage("Completed");
			actionDetails.getAction().getEmployee().setSync(false);
			employeeRepository.save(actionDetails.getAction().getEmployee());
		}
		else {
			actionDetails.setStatus("Delete Pending");
			actionDetails.setMessage("Device not connected with network");
		}
		saveActionDetailList.add(actionDetails);
	}
	actionDetailsRepository.saveAll(saveActionDetailList);
}

//@Scheduled(cron = "0 0 0/6 * * ?")
//@Scheduled(fixedDelay = 5000)
public void autoSyncEmployeeDataFromDevices() {
	List<Device> deviceList=deviceRepository.findAllByIsDeletedFalse();
	for(Device device:deviceList) {
		hfSecurityDeviceUtil.pullAllEmployeeFromHFDevice(device);
	}
}
}
