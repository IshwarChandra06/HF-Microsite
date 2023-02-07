package com.eikona.tech.service.impl.model;


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
import com.eikona.tech.constants.DepartmentConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Department;
import com.eikona.tech.entity.Organization;
import com.eikona.tech.repository.DepartmentRepository;
import com.eikona.tech.service.DepartmentService;
import com.eikona.tech.util.GeneralSpecificationUtil;


@Service
public class DepartmentServiceImpl implements DepartmentService{
	
	@Autowired
    private DepartmentRepository departmentRepository;
	
	@Autowired
    private GeneralSpecificationUtil<Department> generalSpecification;

    @Override
    public List <Department> getAll() {
        return departmentRepository.findAllByIsDeletedFalse();
    }

    @Override
    public void save(Department department) {
    	department.setDeleted(false);
        this.departmentRepository.save(department);
    }

    @Override
    public Department getById(long id) {
        Optional<Department> optional = departmentRepository.findById(id);
        Department department = null;
        if (optional.isPresent()) {
        	department = optional.get();
        } else {
            throw new RuntimeException(DepartmentConstants.DEPARTMENT_NOT_FOUND + id);
        }
        return department;
    }
    
    @Override
	public void deleteById(long id) {
    	Optional<Department> optional = departmentRepository.findById(id);
    	Department department = null;
        if (optional.isPresent()) {
        	department = optional.get();
        	department.setDeleted(true);
        } else {
            throw new RuntimeException(DepartmentConstants.DEPARTMENT_NOT_FOUND + id);
        }
        this.departmentRepository.save(department);
	}

	@Override
	public PaginationDto<Department> searchByField(Long id, String name, int pageno, String sortField, String sortDir, String organization) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<Department> page = getDepartmentPage(id, name, pageno, sortField, sortDir, organization);
        List<Department> departmentList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<Department> dtoList = new PaginationDto<Department>(departmentList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Department> getDepartmentPage(Long id, String name, int pageno, String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<Department> idSpc = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Department> nameSpc = generalSpecification.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Department> isDeletedFalse = generalSpecification.isDeletedSpecification();
		Specification<Department> orgSpc = generalSpecification.foreignKeyStringSpecification(organization, AreaConstants.ORGANIZATION, ApplicationConstants.NAME);
		
		
    	Page<Department> page = departmentRepository.findAll(idSpc.and(nameSpc).and(isDeletedFalse).and(orgSpc), pageable);
		return page;
	}

	@Override
	public List<Department> getAllByOrganization(Organization organization) {
		return departmentRepository.findByOrganizationAndIsDeletedFalse(organization);
	}

}
