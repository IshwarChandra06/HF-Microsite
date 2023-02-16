package com.eikona.tech.controller;

import java.security.Principal;
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
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.impl.model.MonthlyAttendanceReportServiceImpl;


@Controller
public class MonthlyAttendanceReportController {
	
	@Autowired
	private MonthlyAttendanceReportServiceImpl monthlyAttendanceReportServiceImpl;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/monthly-attendance/report")
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public String attendanceReportPage() {
		return "monthlyreport/monthlyAttendanceReport";
	}
	
	
	@GetMapping("/api/monthly/attendance/export-to-file")
	@PreAuthorize("hasAuthority('monthlyattendance_export')")
	public void excelGenerateIncapAttendance(HttpServletResponse response, String date,String employeeId,String employeeName,String designation,String department,String flag,Principal principal) {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		String currentDateTime = dateFormat.format(new Date());
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=Monthly_Attendance_" + currentDateTime + "."+flag;
		response.setHeader(headerKey, headerValue);
		
		try {
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			String orgname = (null == user.getOrganization()?null:user.getOrganization().getName());
			MonthlyReportDto<TSPMonthlyAttendanceDto> monthlyDataList = monthlyAttendanceReportServiceImpl.calculateMonthlyReport(date,employeeId,employeeName,department,designation,orgname);
			monthlyAttendanceReportServiceImpl.excelGenerator(response, monthlyDataList);
			
		} catch (Exception  e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/api/search/monthly-attendance-report", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('monthlyattendance_view')")
	public @ResponseBody PaginationDto<MonthlyReportDto<TSPMonthlyAttendanceDto>> searchMonthlyReport(String date,String employeeId,String employeeName,String department,String designation,int pageno, String sortField, String sortDir,Principal principal) {
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgname = (null == user.getOrganization()?null:user.getOrganization().getName());
		PaginationDto<MonthlyReportDto<TSPMonthlyAttendanceDto>> dtoList = monthlyAttendanceReportServiceImpl.search(date,employeeId,employeeName,department,designation, pageno, sortField, sortDir,orgname);
		
		return dtoList;
	}
	
	//Employee Monthly Report With In Out Time 
	
		@GetMapping("/in-out/monthly-attendance/report")
		@PreAuthorize("hasAuthority('monthlyattendance_view')")
		public String inOutAttendanceReportPage() {
			return "monthlyreport/inOutMonthlyAttendanceReport";
		}
		
		@GetMapping("/api/in-out/monthly-attendance/export-to-file")
		@PreAuthorize("hasAuthority('monthlyattendance_export')")
		public void excelGenerateIncapAttendance1(HttpServletResponse response, String date,String employeeId,String employeeName,String designation,String department,String flag, Principal principal) {
			
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			String orgname = (null == user.getOrganization()?null:user.getOrganization().getName());response.setContentType("application/octet-stream");
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
			String currentDateTime = dateFormat.format(new Date());
			String headerKey = "Content-Disposition";
			String headerValue = "attachment; filename=Monthly_Attendance_" + currentDateTime + "."+flag;
			response.setHeader(headerKey, headerValue);
			
			try {
				
				MonthlyReportDto<TSPMonthlyAttendanceDto> monthlyDataList = monthlyAttendanceReportServiceImpl.calculateMonthlyReportWithInOutTime(date,employeeId,employeeName,department,designation, orgname);
				monthlyAttendanceReportServiceImpl.excelGeneratorWithInOutTime(response, monthlyDataList);
				
			} catch (Exception  e) {
				e.printStackTrace();
			}
		}
		
		@RequestMapping(value = "/api/search/in-out/monthly-attendance-report", method = RequestMethod.GET)
		@PreAuthorize("hasAuthority('monthlyattendance_view')")
		public @ResponseBody PaginationDto<MonthlyReportDto<TSPMonthlyAttendanceDto>> searchInOutMonthlyReport(String date,String employeeId,String employeeName,String department,String designation,int pageno, String sortField, String sortDir, Principal principal) {
			
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			String orgname = (null == user.getOrganization()?null:user.getOrganization().getName());
			PaginationDto<MonthlyReportDto<TSPMonthlyAttendanceDto>> dtoList = monthlyAttendanceReportServiceImpl.searchInOut(date,employeeId,employeeName,department,designation, orgname, pageno, sortField, sortDir);
			
			return dtoList;
		}
}
