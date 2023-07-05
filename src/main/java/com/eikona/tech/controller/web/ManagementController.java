package com.eikona.tech.controller.web;

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
	
	@GetMapping("/pending-delete-test")
	public void pendingEmployeeDeleteFromAllDevice() {
		schedulerServiceImpl.deletePendingDataFromDevice();
	}
	
	@GetMapping("/employee-delete-test")
	public void deleteEmployeeDeleteromAllDevice() {
		schedulerServiceImpl.deleteEmployeeFromHF();
	}
	
	@GetMapping("/keep-online-test")
	public void keepDeviceOnline() {
		schedulerServiceImpl.keepDeviceOnline();
	}
}
