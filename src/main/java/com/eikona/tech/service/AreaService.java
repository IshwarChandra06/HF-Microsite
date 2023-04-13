package com.eikona.tech.service;


import java.util.List;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Area;


public interface AreaService {
/**
 * Returns all area List, which are isDeleted false.
 * @param
 */
	List<Area> getAll();
	
	/**
	 * This function saves the area in database according to the respective object.  
	 * @param 
	 */
	void save(Area area);
	/**
	 * This function retrieves the area from database according to the respective id.  
	 * @param
	 */
	Area getById(long id);
	/**
	 * This function deletes the area from database according to the respective id.  
	 * @param
	 */
	void deletedById(long id);

	PaginationDto<Area> searchByField(Long id, String name, String office, int pageno, String sortField, String sortDir,
			String orgName);

	
	
}
