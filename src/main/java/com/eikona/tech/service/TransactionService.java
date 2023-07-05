package com.eikona.tech.service;

import org.springframework.data.domain.Page;

import com.eikona.tech.dto.EventRequestDto;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Transaction;

public interface TransactionService {

	PaginationDto<Transaction> searchByField(String sDate, String eDate, String employeeId,
			String employeeName, String office, String device, String department, int pageno,
			String sortField, String sortDir, String orgName);
	
	PaginationDto<Transaction> searchByField(int pageno, String sortField, String sortDir, String orgName,String flag);

	Page<Transaction> searchByField(EventRequestDto eventDto, String string);

}
