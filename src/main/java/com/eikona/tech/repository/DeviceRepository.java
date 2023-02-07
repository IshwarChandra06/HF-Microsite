package com.eikona.tech.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

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
}
