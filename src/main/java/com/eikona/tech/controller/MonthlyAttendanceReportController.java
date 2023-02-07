package com.eikona.tech.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.tech.dto.MonthlyReportDto;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.dto.TSPMonthlyAttendanceDto;
import com.eikona.tech.service.impl.model.MonthlyAttendanceReportServiceImpl;


@Controller
public class MonthlyAttendanceReportController {
	
	@Autowired
	private MonthlyAttendanceReportServiceImpl monthlyAttendanceReportServiceImpl;
	
	@GetMapping("/monthly-attendance/report")
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public String attendanceReportPage() {
		return "monthlyreport/monthlyAttendanceReport";
	}
	
	
	@GetMapping("/api/monthly/attendance/export-to-file")
	@PreAuthorize("hasAuthority('monthlyattendance_export')")
	public void excelGenerateIncapAttendance(HttpServletResponse response, String date,String employeeId,String employeeName,String designation,String department,String flag) {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		String currentDateTime = dateFormat.format(new Date());
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Monthly_Attendance_" + currentDateTime + "."+flag;
		response.setHeader(headerKey, headerValue);
		
		try {
			
			MonthlyReportDto<TSPMonthlyAttendanceDto> monthlyDataList = monthlyAttendanceReportServiceImpl.calculateMonthlyReport(date,employeeId,employeeName,department,designation);
			monthlyAttendanceReportServiceImpl.excelGenerator(response, monthlyDataList);
			
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/api/search/monthly-attendance-report", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public @ResponseBody PaginationDto<MonthlyReportDto<TSPMonthlyAttendanceDto>> searchMonthlyReport(String date,String employeeId,String employeeName,String department,String designation,int pageno, String sortField, String sortDir) {
		
		PaginationDto<MonthlyReportDto<TSPMonthlyAttendanceDto>> dtoList = monthlyAttendanceReportServiceImpl.search(date,employeeId,employeeName,department,designation, pageno, sortField, sortDir);
		
		return dtoList;
	}
}
