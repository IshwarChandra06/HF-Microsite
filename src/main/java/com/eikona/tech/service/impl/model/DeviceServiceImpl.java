package com.eikona.tech.service.impl.model;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.AreaConstants;
import com.eikona.tech.constants.DeviceConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Device;
import com.eikona.tech.repository.DeviceRepository;
import com.eikona.tech.service.DeviceService;
import com.eikona.tech.util.GeneralSpecificationUtil;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private GeneralSpecificationUtil<Device> generalSpecification;

	@Override
	public List<Device> getAll() {
		return deviceRepository.findAllByIsDeletedFalse();

	}

	@Override
	public void save(Device device, Principal principal) {

		device.setDeleted(false);
		device.setSync(false);
		if (null == device.getId()) {

			this.deviceRepository.save(device);
		} else {
			Device deviceObj = deviceRepository.findById(device.getId()).get();
			device.setCreatedBy(deviceObj.getCreatedBy());
			device.setCreatedDate(deviceObj.getCreatedDate());

			this.deviceRepository.save(device);
//			}
		}
	}

	@Override
	public Device getById(long id) {
		Device optional = deviceRepository.findByIdAndIsDeletedFalse(id);
		Device device = null;
		if (null != optional) {
			device = optional;
		} else {
			throw new RuntimeException(DeviceConstants.DEVICE_NOT_FOUND + id);
		}
		return device;

	}

	@Override
	public void deleteById(long id) {
		Optional<Device> optional = deviceRepository.findById(id);
		Device device = null;
		if (optional.isPresent()) {
			device = optional.get();
			device.setDeleted(true);
			device.setSync(false);
		} else {
			throw new RuntimeException(DeviceConstants.DEVICE_NOT_FOUND + id);
		}
		this.deviceRepository.save(device);
	}


	@Override
	public PaginationDto<Device> searchByField(Long id, String name, String ipAddress,  String status, int pageno, String sortField, String sortDir,String organization) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Device> page = getDevicePage(id, name, ipAddress, pageno, sortField, sortDir,organization);
		
		List<Device> deviceList = page.getContent();
		if(!status.isEmpty()) {
			List<Device> newDeviceList = new ArrayList<>();
			for(Device device :deviceList) {
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String dateStr = format.format(new Date());
					Date date = format.parse(dateStr);
					Date lastonline = device.getLastOnline();
					
					long mileseconds = date.getTime() - lastonline.getTime();
					
					if("active".equalsIgnoreCase(status) && mileseconds<=900000) {
						newDeviceList.add(device);
					}else if("inactive".equalsIgnoreCase(status) && mileseconds>900000) {
						newDeviceList.add(device);
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			deviceList = newDeviceList;
		}

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC
				: ApplicationConstants.ASC;
		PaginationDto<Device> dtoList = new PaginationDto<Device>(deviceList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(),
				page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Device> getDevicePage(Long id, String name, String ipAddress, int pageno,
			String sortField, String sortDir,String organization) {
		
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Device> isdeleted = generalSpecification.isDeletedSpecification();
		Specification<Device> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Device> nameSpc = generalSpecification.stringSpecification(name, DeviceConstants.NAME);
		Specification<Device> ipSpc = generalSpecification.stringSpecification(ipAddress, DeviceConstants.IP_ADDRESS);
		Specification<Device> orgSpc = generalSpecification.foreignKeyStringSpecification(organization, AreaConstants.ORGANIZATION,
				DeviceConstants.NAME);

		Page<Device> page = deviceRepository.findAll(isdeleted.and(idSpc).and(nameSpc).and(ipSpc).and(orgSpc), pageable);
		return page;
	}
	
}
