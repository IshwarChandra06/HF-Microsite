package com.eikona.tech.config.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eikona.tech.entity.Privilege;
import com.eikona.tech.entity.Role;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.PrivilegeRepository;
import com.eikona.tech.repository.RoleRepository;
import com.eikona.tech.repository.UserRepository;

@Service
public class DBSeeder implements CommandLineRunner {
	
	 private UserRepository userRepository;
	 
	 private PrivilegeRepository privilegeRepository;
	 
	 private RoleRepository roleRepository;
	 
     private PasswordEncoder passwordEncoder;
     
     public DBSeeder(PrivilegeRepository privilegeRepository,RoleRepository roleRepository,UserRepository userRepository, PasswordEncoder passwordEncoder) {
    	 this.privilegeRepository=privilegeRepository;
    	 this.roleRepository=roleRepository;
    	 this.userRepository = userRepository;
         this.passwordEncoder = passwordEncoder;
     }

	@Override
	public void run(String... args) throws Exception {
		List<Privilege> privilegeList = privilegeRepository.findAllByIsDeletedFalse();
			
			if(null==privilegeList || privilegeList.isEmpty()) {
				List<Privilege> privileges = SeedPrivileges();
				Role admin = seedRole(privileges);
				seedUser(admin);
			}
		}
	private List<Privilege> SeedPrivileges() {
		
		Privilege orgView = new Privilege("organization_view", false);
		Privilege orgCreate = new Privilege("organization_create", false);
		Privilege orgUpdate = new Privilege("organization_update", false);
		Privilege orgDelete = new Privilege("organization_delete", false);
		
		Privilege userView = new Privilege("user_view", false);
		Privilege userCreate = new Privilege("user_create", false);
		Privilege userUpdate = new Privilege("user_update", false);
		Privilege userDelete = new Privilege("user_delete", false);
		
		Privilege roleView = new Privilege("role_view", false);
		Privilege roleCreate = new Privilege("role_create", false);
		Privilege roleUpdate = new Privilege("role_update", false);
		Privilege roleDelete = new Privilege("role_delete", false);
		
		Privilege privilegeView = new Privilege("privilege_view", false);
		Privilege privilegeUpdate = new Privilege("privilege_update", false);
		Privilege privilegeDelete = new Privilege("privilege_delete", false);
		
		
		Privilege dailyreportView = new Privilege("dailyreport_view", false);
		Privilege dailyreportGenerate = new Privilege("dailyreport_generate", false);
		Privilege dailyreportGenerateShiftwise = new Privilege("dailyreport_generate_shiftwise", false);
		Privilege dailyreportExport = new Privilege("dailyreport_export", false);
		
		Privilege departmentView = new Privilege("department_view", false);
		Privilege departmentCreate = new Privilege("department_create", false);
		Privilege departmentUpdate = new Privilege("department_update", false);
		Privilege departmentDelete = new Privilege("department_delete", false);
		
		Privilege designationView = new Privilege("designation_view", false);
		Privilege designationCreate = new Privilege("designation_create", false);
		Privilege designationUpdate = new Privilege("designation_update", false);
		Privilege designationDelete = new Privilege("designation_delete", false);
		
		
		Privilege deviceView = new Privilege("device_view", false);
		Privilege deviceCreate = new Privilege("device_create", false);
		Privilege deviceUpdate = new Privilege("device_update", false);
		Privilege deviceDelete = new Privilege("device_delete", false);
		
		Privilege trasactionView = new Privilege("transaction_view", false);
		Privilege monthlyReportView = new Privilege("monthlyattendance_view", false);
		Privilege monthlyReportExport = new Privilege("monthlyattendance_export", false);
		
		
		Privilege employeeView = new Privilege("employee_view", false);
		Privilege employeeCreate = new Privilege("employee_create", false);
		Privilege employeeUpdate = new Privilege("employee_update", false);
		Privilege employeeDelete = new Privilege("employee_delete", false);
		
		Privilege employeeImport = new Privilege("employee_import", false);
		Privilege employeeExport = new Privilege("employee_export", false);
		
		
		
		List<Privilege> privileges = Arrays.asList(
				orgView, orgCreate, orgUpdate, orgDelete,
				userView, userCreate, userUpdate, userDelete,
				roleView, roleCreate, roleUpdate, roleDelete,
				privilegeView,privilegeUpdate,privilegeDelete,
				dailyreportView,dailyreportGenerate,dailyreportGenerateShiftwise,dailyreportExport,
				departmentView,departmentCreate,departmentUpdate,departmentDelete,
				designationView,designationCreate,designationUpdate,designationDelete,
				deviceView,deviceCreate,deviceUpdate,deviceDelete,
				 trasactionView,monthlyReportView,monthlyReportExport,
				employeeView, employeeCreate, employeeUpdate, employeeDelete,employeeImport,employeeExport
				
				);
		privilegeRepository.saveAll(privileges);
		
		List<Privilege> adminPrivileges = Arrays.asList(
				orgView, orgCreate, orgUpdate, orgDelete,
				userView, userCreate, userUpdate, userDelete,
				roleView, roleCreate, roleUpdate, roleDelete,
				privilegeView,privilegeUpdate,privilegeDelete,
				dailyreportView,dailyreportExport,
				departmentView,departmentCreate,departmentUpdate,departmentDelete,
				designationView,designationCreate,designationUpdate,designationDelete,
				deviceView,deviceCreate,deviceUpdate,deviceDelete,
				 trasactionView,
				employeeView, employeeCreate, employeeUpdate, employeeDelete,employeeImport
				
				);
		 
		return adminPrivileges;
	}

	private Role seedRole(List<Privilege> privileges) {
		Role admin=roleRepository.findByName("Admin");
		if(null==admin) {
			 admin= new Role("Admin", privileges, false);
			roleRepository.save(admin);
		}
		return admin;
	}

	private void seedUser(Role admin) {
		List<User> userList=userRepository.findAllByIsDeletedFalse();
		if(null==userList || userList.isEmpty()) {
			User adminUser= new User("Admin", passwordEncoder.encode("Admin@123"), true, admin, false);
			userRepository.save(adminUser);
		}
	}
}
