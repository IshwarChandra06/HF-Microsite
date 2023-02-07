package com.eikona.tech.repository;


import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import com.eikona.tech.entity.Department;
import com.eikona.tech.entity.Organization;
@Repository
public interface DepartmentRepository extends DataTablesRepository<Department, Long> {

	List<Department> findAllByIsDeletedFalse();

	Department findByNameAndIsDeletedFalse(String department);

	List<Department> findByOrganizationAndIsDeletedFalse(Organization organization);

}
