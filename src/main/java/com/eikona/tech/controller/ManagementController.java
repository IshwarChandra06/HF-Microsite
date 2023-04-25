package com.eikona.tech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eikona.tech.service.impl.model.SchedulerServiceImpl;

@RestController
public class ManagementController {
	
	@Autowired
	private SchedulerServiceImpl schedulerServiceImpl;

	@GetMapping("/employee-push-test")
	public void employeePushToAllDevice() {
		schedulerServiceImpl.autoSyncEmployeeFromMataToDevice();
	}
	
	@GetMapping("/pending-push-test")
	public void pendingEmployeePushToAllDevice() {
		schedulerServiceImpl.syncPendingDataFromMataToDevice();
	}
	
	@GetMapping("/update-action-details")
	public void updateActionDetails() {
		schedulerServiceImpl.updateErrorActionDetails();
	}
	
	@GetMapping("/keep-online-test")
	public void keepDeviceOnline() {
		schedulerServiceImpl.keepDeviceOnline();
	}
}
