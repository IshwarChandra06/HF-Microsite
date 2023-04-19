package com.eikona.tech.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.tech.dto.CountDto;
import com.eikona.tech.dto.DeviceDto;
import com.eikona.tech.dto.DeviceStatusDto;
import com.eikona.tech.dto.TransactionDto;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.entity.Privilege;
import com.eikona.tech.entity.Role;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.DeviceRepository;
import com.eikona.tech.repository.OrganizationRepository;
import com.eikona.tech.repository.TransactionRepository;
import com.eikona.tech.repository.UserRepository;

@Controller
public class DashboardController {
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@GetMapping(value="/")
	public String home(Principal principal) {
		
		User user=userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		Role role=user.getRole();
		List<String> privilegeNameList = new ArrayList<>();
		for(Privilege privilege:role.getPrivileges()) {
			privilegeNameList.add(privilege.getName());
		}
		if(privilegeNameList.contains("dashboard_view"))
			return "redirect:/home";
		else
			return "organization/organization_list";
		
	}
	
	@GetMapping("/home")
	@PreAuthorize("hasAuthority('dashboard_view')")
	public String dashboard(Model model) {
		try {
			CountDto countDto = new CountDto();
			List<Device> deviceList= deviceRepository.findAllByIsDeletedFalse();
			List<Organization> organizationList= organizationRepository.findAllByIsDeletedFalse();
			
			List<Device> onlineDeviceList = new ArrayList<>();
			List<Device> offlineDeviceList = new ArrayList<>();
			for(Device device:deviceList) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr = format.format(new Date());
				Date date = format.parse(dateStr);
				Date lastonline = device.getLastOnline();
				
				long mileseconds = date.getTime() - lastonline.getTime();
				
				if(mileseconds<=120000){
					onlineDeviceList.add(device);
				}else if(mileseconds>120000) {
					offlineDeviceList.add(device);
				}
			}
			long totalDeviceInstalled= deviceList.size();
			long totalOnlineDevice = onlineDeviceList.size();
			long totalOfflineDevice = offlineDeviceList.size();
			long totalOrganization = organizationList.size();
			
			countDto.setTotalInstalledDevice(totalDeviceInstalled);
			countDto.setTotalOnline(totalOnlineDevice);
			countDto.setTotalOffline(totalOfflineDevice);
			countDto.setTotalOrganization(totalOrganization);
			
			model.addAttribute("countDto", countDto);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "dashboard";
	}
	
	@SuppressWarnings("unchecked")
	public @ResponseBody JSONArray deviceStatus() {
		JSONArray jsonArray = new JSONArray();
		try {
			List<Organization> organizationList= organizationRepository.findAllByIsDeletedFalse();
			
			
			for(Organization organization:organizationList) {
				
				DeviceStatusDto deviceDto= new DeviceStatusDto();
				List<Device> deviceList=deviceRepository.findByOrganizationAndIsDeletedFalse(organization);
				List<Device> onlineDeviceList = new ArrayList<>();
					for(Device device:deviceList) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String dateStr = format.format(new Date());
						Date date = format.parse(dateStr);
						Date lastonline = device.getLastOnline();
						
						long mileseconds = date.getTime() - lastonline.getTime();
						if(mileseconds<=120000){
							onlineDeviceList.add(device);
						}
					}
				deviceDto.setOrganization(organization.getName());
				deviceDto.setOnlineDevice(onlineDeviceList.size());
				deviceDto.setTotalDevice(deviceList.size());
				jsonArray.add(deviceDto);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/api/common-chart-data")
	public @ResponseBody JSONObject commonChart() {
		
		JSONObject returnObject = new JSONObject();
		
		JSONArray deviceArray = deviceStatus();
		returnObject.put("device", deviceArray);
		
//		JSONObject employeeObject = employeeLoginChart();
//		returnObject.put("employee", employeeObject);
		
		return returnObject;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value="/api/employee-login-chart")
	public @ResponseBody JSONObject employeeLoginChart() {
		  JSONObject returnObject = new JSONObject();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<TransactionDto> listTransaction = transactionRepository.findTransactionByPunchDateStrCustom(format.format(new Date()));
		
		JSONArray employeeArray = new JSONArray();
		 for (TransactionDto companyDto : listTransaction) {
		    	JSONArray currObject = new JSONArray();
		    	currObject.add(companyDto.getOrganization());
		    	currObject.add(companyDto.getLoginEmployee());
		    	employeeArray.add(currObject);
		    	
			}
		 returnObject.put("data",employeeArray);
		    return returnObject;
	}
	
	@GetMapping(value="/api/device-table")
	public @ResponseBody List<DeviceDto> deviceInfo() {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		List<Device> deviceList = deviceRepository.findAllByIsDeletedFalseOrderByOrganizationCustom();
		List<DeviceDto> deviceDtoList = new ArrayList<>();
		for (Device device : deviceList) {
			DeviceDto deviceDto = new DeviceDto();
			Long transactionCount = transactionRepository.findEventCountByDateAndDeviceCustom(format.format(new Date()),
					device.getName());
			Long unregisterTransaction = transactionRepository
					.findUnregisterCountByDateAndDeviceCustom(format.format(new Date()), device.getName());
			deviceDto.setCapacity(10000);
			deviceDto.setDevice(device.getName());
			if(null!=device.getOrganization())
			 deviceDto.setOrganization(device.getOrganization().getName());
			else
				deviceDto.setOrganization("");
			deviceDto.setSerialNo(device.getSerialNo());
			deviceDto.setTotalPerson(device.getPersonNo());
			if(null != transactionCount)
				deviceDto.setTotalTransaction(transactionCount);
			else
				deviceDto.setTotalTransaction(0);
			if(null != unregisterTransaction)
				deviceDto.setTotalUnregisterTransaction(unregisterTransaction);
			else
				deviceDto.setTotalUnregisterTransaction(0);
			
			deviceDtoList.add(deviceDto);
		}

		return deviceDtoList;
	}
}
