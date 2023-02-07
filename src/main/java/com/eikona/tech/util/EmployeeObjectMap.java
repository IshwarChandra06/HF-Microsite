package com.eikona.tech.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.tech.entity.Department;
import com.eikona.tech.entity.Designation;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.DepartmentRepository;
import com.eikona.tech.repository.DesignationRepository;
import com.eikona.tech.repository.EmployeeRepository;

@Component
public class EmployeeObjectMap {
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private DesignationRepository designationRepository;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	public Map<String, Department> getDepartment(){
		List<Department> departmentList = departmentRepository.findAllByIsDeletedFalse();
		Map<String, Department> deptMap = new HashMap<String, Department>();
		
		for(Department department: departmentList ) {
			deptMap.put(department.getName(), department);
		}
		return deptMap;
	}
	
	
	public Map<String, Designation> getDesignation(){
		
		List<Designation> desigList = designationRepository.findAllByIsDeletedFalse();
		Map<String, Designation> desigMap = new HashMap<String, Designation>();
		
		
		for(Designation designation: desigList) {
			desigMap.put(designation.getName(), designation);
		}
		
		return desigMap;
	}
	
	public Map<Long, Employee> getDeletedEmployee(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedTrueAndIsSyncTrue();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getId(), employee);
		}
		return employeeMap;
	}
	public Map<Long, Employee> getEmployee(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<Long, Employee> employeeMap = new HashMap<Long, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getId(), employee);
		}
		return employeeMap;
	}
	
	public Map<String, Employee> getEmployeeByEmpId(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<String, Employee> employeeMap = new HashMap<String, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getEmpId(), employee);
		}
		return employeeMap;
	}
	
	public Map<String, Employee> getEmployeeByEmpName(){
		List<Employee> employeeList = employeeRepository.findAllByIsDeletedFalse();
		Map<String, Employee> employeeMap = new HashMap<String, Employee>();
		
		for(Employee employee: employeeList ) {
			employeeMap.put(employee.getName(), employee);
		}
		return employeeMap;
	}
	
}
