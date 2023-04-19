package com.eikona.tech.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import com.eikona.tech.dto.TransactionDto;
import com.eikona.tech.entity.Transaction;

public interface TransactionRepository extends DataTablesRepository<Transaction, Long>{

	@Query("SELECT tr FROM com.eikona.tech.entity.Transaction as tr where tr.punchDate >=:sDate and tr.punchDate <=:eDate and tr.organization =:organization"
			+ " and tr.empId is not null order by tr.punchDateStr asc, tr.punchTimeStr asc")
	List<Transaction> getTransactionData(Date sDate, Date eDate, String organization);

	List<Transaction> findByPunchDateStrAndOrganization(String currDate, String currOrg);

	List<Transaction> findByPunchDateStrAndOrganizationIn(String currDate, List<String> orgList);

	Transaction findByEmployeeCodeAndEnrollStatus(String employeeCode, String enrollStatus);
	
	@Query("SELECT new com.eikona.tech.dto.TransactionDto(tr.organization, count(distinct tr.empId)) FROM com.eikona.tech.entity.Transaction as tr "
			+ "where tr.punchDateStr =:dateStr and tr.empId is not null GROUP BY tr.organization")
	List<TransactionDto> findTransactionByPunchDateStrCustom(String dateStr);

	@Query("SELECT count(distinct tr.empId) FROM com.eikona.tech.entity.Transaction as tr "
			+ "where tr.punchDateStr =:dateStr and tr.deviceName=:device and tr.empId is not null GROUP BY tr.organization")
	Long findEventCountByDateAndDeviceCustom(String dateStr, String device);
	
	@Query("SELECT count(tr.name) FROM com.eikona.tech.entity.Transaction as tr "
			+ "where tr.punchDateStr =:dateStr and tr.deviceName=:device and tr.name='Unregistered' GROUP BY tr.organization")
	Long findUnregisterCountByDateAndDeviceCustom(String dateStr, String device);

//	@Query("SELECT tr FROM com.eikona.tech.entity.Transaction as tr where tr.punchDate =:date and tr.empId=:personId")
//	List<Transaction> findByPunchDateAndEmpIdCustom(Date date, String personId);

}
