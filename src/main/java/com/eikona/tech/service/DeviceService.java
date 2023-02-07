package com.eikona.tech.service;


import java.security.Principal;
import java.util.List;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Device;


public interface DeviceService {
	/**
	 * Returns all device List, which are isDeleted false.
	 * @param
	 */
	List<Device> getAll();
	/**
	 * This function saves the device in database according to the respective object.  
	 * @param 
	 */
    void save(Device device,Principal principal);
    /**
	 * This function retrieves the device from database according to the respective id.  
	 * @param
	 */
    Device getById(long id);
    /**
	 * This function deletes the device from database according to the respective id.  
	 * @param
	 */
    void deleteById(long id);
    
	
	PaginationDto<Device> searchByField(Long id, String name, String ipAddress, String status, int pageno, String sortField, String sortDir, String orgName);
	
}
