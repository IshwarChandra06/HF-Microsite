package com.eikona.tech.service;


import java.util.List;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Designation;
import com.eikona.tech.entity.Organization;


public interface DesignationService {
	/**
	 * Returns all designation List, which are isDeleted false.
	 * @param
	 */
	List<Designation> getAll();
	/**
	 * This function saves the designation in database according to the respective object.  
	 * @param 
	 */
	void save(Designation designation);
	/**
	 * This function retrieves the designation from database according to the respective id.  
	 * @param
	 */
	Designation getById(long id);
	/**
	 * This function deletes the designation from database according to the respective id.  
	 * @param
	 */
	void deleteById(long id);
	
	
	PaginationDto<Designation> searchByField(Long id, String name, int pageno, String sortField, String sortDir, String orgName);
	
	List<Designation> getAllByOrganization(Organization organization);
}
