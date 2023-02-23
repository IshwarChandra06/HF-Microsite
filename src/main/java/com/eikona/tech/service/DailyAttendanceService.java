package com.eikona.tech.service;

import java.util.List;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.DailyReport;

public interface DailyAttendanceService {
    
	List<DailyReport> generateDailyAttendance(String sDate, String eDate, String orgName);

	PaginationDto<DailyReport> searchByField(String sDate, String eDate, String employeeId, String employeeName,
			 String department, String designation,String status, String shift, int pageno, String sortField, String sortDir, String orgName);

	List<DailyReport> getAllDailyReport();

}

