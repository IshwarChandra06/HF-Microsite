package com.eikona.tech.controller.web;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.AreaRepository;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.repository.OrganizationRepository;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.AreaService;
import com.eikona.tech.service.DeviceService;
import com.eikona.tech.util.HFSecurityDeviceUtil;

@Controller
public class DeviceController {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private AreaService areaService;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private HFSecurityDeviceUtil hFSecurityDeviceUtil;
	
	@GetMapping("/device")
	@PreAuthorize("hasAuthority('device_view')")
	public String deviceList(Model model) {
		return "device/device_list";
	}
	
	@GetMapping("/device/new")
	@PreAuthorize("hasAuthority('device_create')")
	public String newDevice(Model model, Principal principal) {
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("listArea", areaService.getAll());
		Device device = new Device();
		model.addAttribute("device", device);
		model.addAttribute("title", "New Device");
		return "device/device_new";
	}

	@PostMapping("/device/add")
	@PreAuthorize("hasAnyAuthority('device_create','device_update')")
	public String saveDevice(@ModelAttribute("device") Device device, @Valid Device dev, Errors errors, String title,
			Model model, Principal principal) {

		if (errors.hasErrors()){
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			List<Organization> organizationList = null;
			if(null == user.getOrganization()) {
				organizationList = (List<Organization>) organizationRepository.findAll();
			}else {
				organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
			}
			model.addAttribute("listOrganization", organizationList);
			model.addAttribute("listArea", areaRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
			model.addAttribute("title", title);
			return "device/device_new";
		} else {
			if (null == device.getId())
				deviceService.save(device, principal);
			else {
				Device deviceObj = deviceService.getById(device.getId());
				device.setLastOnline(deviceObj.getLastOnline());
				device.setRefId(deviceObj.getRefId());
				device.setSerialNo(deviceObj.getSerialNo());
				device.setCreatedBy(deviceObj.getCreatedBy());
				device.setCreatedDate(deviceObj.getCreatedDate());
				deviceService.save(device, principal);
			}
			return "redirect:/device";
		}
	}

	@GetMapping("/device/edit/{id}")
	@PreAuthorize("hasAuthority('device_update')")
	public String editDevice(@PathVariable(value = "id") long id, Model model, Principal principal) {

		Device device = deviceService.getById(id);
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		List<Organization> organizationList = null;
		if(null == user.getOrganization()) {
			organizationList = (List<Organization>) organizationRepository.findAll();
		}else {
			organizationList = organizationRepository.findByIdAndIsDeletedFalse(user.getOrganization().getId());
		}
		model.addAttribute("listArea", areaRepository.findByOrganizationAndIsDeletedFalse(user.getOrganization()));
		model.addAttribute("listOrganization", organizationList);
		model.addAttribute("device", device);
		model.addAttribute("title", "Update Device");
		return "device/device_new";
	}

	@GetMapping("/device/delete/{id}")
	@PreAuthorize("hasAuthority('device_delete')")
	public String deleteDevice(@PathVariable(value = "id") long id) {
		this.deviceService.deleteById(id);
		return "redirect:/device";
	}
	
	@RequestMapping(value = "/api/search/device", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('device_view')")
	public @ResponseBody PaginationDto<Device> search(Long id, String name, String ipAddress, String status, int pageno, String sortField, String sortDir, Principal principal) {
		
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		PaginationDto<Device> dtoList = deviceService.searchByField(id, name, ipAddress, status, pageno, sortField, sortDir, orgName);
		return dtoList;
	}
	@GetMapping("/mata-to-device-sync/{id}")
//	@PreAuthorize("hasAuthority('mata_to_device_sync')")
	public String employeeSyncFromMataToDeviceSync(@PathVariable(value = "id") long id,Principal principal) {
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		deviceService.employeeSyncFromMataToDevice(id,orgName);
		return "redirect:/device";
	}
	@GetMapping("/mata-to-device-manual-sync/{id}")
//	@PreAuthorize("hasAuthority('mata_to_device_sync')")
	public String employeeSyncFromMataToDevice(@PathVariable(value = "id") long id, Principal principal) {
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		List<Employee> employeeList=employeeRepository.findAllByIsDeletedFalseAndOrganizationAndArea(orgName);
		Device device=deviceService.getById(id);
		for(Employee employee:employeeList) {
			hFSecurityDeviceUtil.addEmployeeToHFDevice(employee, device);
			hFSecurityDeviceUtil.addEmployeeFaceToHFDevice(employee, device);
		}
		return "redirect:/device";
	}
	
	@GetMapping("/delete-person-from-device/{id}")
	public String deleteAllEmployeeFromDevice(@PathVariable(value = "id") long id) {
		Device device=deviceService.getById(id);
		String code=hFSecurityDeviceUtil.deleteEmployeeFromHFDevice("-1", device);
		if("000".equalsIgnoreCase(code)) 
		 return "All Persons are Deleted Successfully!!";
		else
		 return "Failed!!";
	}
}
