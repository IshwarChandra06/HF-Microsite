package com.eikona.tech.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.Area;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Organization;

@Repository
public interface DeviceRepository extends DataTablesRepository<Device, Long> {

	List<Device> findAllByIsDeletedFalse();
	
	Device findByIpAddressAndIsDeletedFalse(String ipAddress);

	Device findByNameAndIsDeletedFalse(String device);

	Device findBySerialNoAndIsDeletedFalse(String serialno);

	List<Device> findAllByIsDeletedFalseAndIsSyncFalse();
	
	Device findByIdAndIsDeletedFalse(long id);

	List<Device> findByOrganizationAndIsDeletedFalse(Organization organization);

	@Query("select de from com.eikona.tech.entity.Device as de where de.area in :area and isDeleted=false")
	List<Device> findByAreaAndIsDeletedFalseCustom(List<Area> area);
	
	@Query("select d from com.eikona.tech.entity.Device d where d.isDeleted=false and d.organization is not null order by d.organization.name")
	List<Device> findAllByIsDeletedFalseOrderByOrganizationCustom();
}
