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

import com.eikona.tech.entity.ActionDetails;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.ActionDetailsRepository;
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
	private DeviceRepository deviceRepository;
	
	@Autowired
	private HFSecurityDeviceUtil hfSecurityDeviceUtil;
	
	@Scheduled(cron = "0 0 0/8 * * ?")
	public void syncPendingDataFromMataToDevice() {
		
		List<ActionDetails> actionDetailList = actionDetailsRepository.findByStatus("Pending");
		List<ActionDetails> saveActionDetailList=new ArrayList<ActionDetails>();
		
		for(ActionDetails actionDetails:actionDetailList) {
			actionDetailsServiceImpl.addEmployeeToHFSecurity(actionDetails,actionDetails.getAction().getEmployee(),actionDetails.getDevice());
			saveActionDetailList.add(actionDetails);
		}
		actionDetailsRepository.saveAll(saveActionDetailList);
	}
	
 //	@Scheduled(cron = "0 0 0/10 * * ?")
 //@Scheduled(fixedDelay = 5000)
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

@Scheduled(cron = "0 0 0/12 * * ?")
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
	List<ActionDetails> saveActionDetailList=new ArrayList<ActionDetails>();
	
	for(ActionDetails actionDetails:actionDetailList) {

		List<ActionDetails> errorActionDetailList= actionDetailsRepository.findByEmpIdAndDeviceCustom(actionDetails.getAction().getEmployee().getEmpId(), actionDetails.getDevice().getId());
		
		if(errorActionDetailList.size()>1) {
			for(int i=1; i<errorActionDetailList.size(); i++) {
				ActionDetails errorActionDetails = errorActionDetailList.get(i);
				errorActionDetails.setStatus("Completed");
				errorActionDetails.setMessage("Completed");
				saveActionDetailList.add(errorActionDetails);
			}
		}
	}
	actionDetailsRepository.saveAll(saveActionDetailList);
}
}
