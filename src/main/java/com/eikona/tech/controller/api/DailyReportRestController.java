package com.eikona.tech.controller.api;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.dto.DailyReportRequestDto;
import com.eikona.tech.dto.DailyReportResponseDto;
import com.eikona.tech.dto.PaginatedDto;
import com.eikona.tech.entity.DailyReport;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.DailyAttendanceRepository;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.DailyAttendanceService;

@RestController
@RequestMapping("/apis/V1")
public class DailyReportRestController {
	
	@Autowired
	private DailyAttendanceService dailyAttendanceService;
	
	@Autowired
	private DailyAttendanceRepository  dailyAttendanceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value = "/daily-report/search", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('transaction_view')")
	public @ResponseBody PaginatedDto<DailyReportResponseDto> search(@RequestBody DailyReportRequestDto dailyReportRequestDto,
			Principal principal) {
		PaginatedDto<DailyReportResponseDto> eventDtoList = null;
		List<DailyReportResponseDto> dailyReportResponseDtoList = new ArrayList<>();
		
		try {
			if (0 == dailyReportRequestDto.getPageNo() || 0 == dailyReportRequestDto.getPageSize()) 
				return new PaginatedDto<DailyReportResponseDto>(dailyReportResponseDtoList, 0, 0, 0, 0, 0,
						"Page No and Page Size should be greater than 0", "I");
			

			Date startDate = null;
			Date endDate = null;
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			if (null == dailyReportRequestDto.getStartDate())
				dailyReportRequestDto.setStartDate("");

			if (null == dailyReportRequestDto.getEndDate())
				dailyReportRequestDto.setEndDate("");

			if (!dailyReportRequestDto.getStartDate().isEmpty() && !dailyReportRequestDto.getEndDate().isEmpty()) {
				startDate = format.parse(dailyReportRequestDto.getStartDate());
				endDate = format.parse(dailyReportRequestDto.getEndDate());
				long difference_In_Time = endDate.getTime() - startDate.getTime();

				long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

				if (3 < difference_In_Days) 
					return new PaginatedDto<DailyReportResponseDto>(dailyReportResponseDtoList, 0, 0, 0, 0, 0,
							"Start Date and End Date Gap should not be more than 3 days", "I");
			}
			else if (dailyReportRequestDto.getStartDate().isEmpty() && !dailyReportRequestDto.getEndDate().isEmpty()) 
				return new PaginatedDto<DailyReportResponseDto>(dailyReportResponseDtoList, 0, 0, 0, 0, 0,
						"Both Start Date and End Date are mandatory for Date Filter", "I");
			
			 else if (!dailyReportRequestDto.getStartDate().isEmpty() && dailyReportRequestDto.getEndDate().isEmpty()) 
				return new PaginatedDto<DailyReportResponseDto>(dailyReportResponseDtoList, 0, 0, 0, 0, 0,
						"Both Start Date and End Date are mandatory for Date Filter", "I");
			

			eventDtoList = setEventListInPaginationDto(dailyReportRequestDto, principal, dailyReportResponseDtoList, format);

		} catch (Exception e) {
			return new PaginatedDto<DailyReportResponseDto>(dailyReportResponseDtoList, 0, 0, 0, 0, 0, "Contact Admin!!", "E");
		}
		return eventDtoList;
		
	}

	private PaginatedDto<DailyReportResponseDto> setEventListInPaginationDto(DailyReportRequestDto dailyReportRequestDto, Principal principal,
			List<DailyReportResponseDto> dailyReportResponseDtoList, SimpleDateFormat format) throws ParseException {
		PaginatedDto<DailyReportResponseDto> eventDtoList;
		String message = "";
		String messageType = "";

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());

		Page<DailyReport> page = setTransactionData(dailyReportRequestDto, dailyReportResponseDtoList, user);
		

		List<DailyReport> totalDailyReportResponse = dailyAttendanceRepository.findByOrganization(user.getOrganization().getName());
		Page<DailyReport> totalPage = new PageImpl<DailyReport>(totalDailyReportResponse);
		message = "Success";
		messageType = "S";
		eventDtoList = new PaginatedDto<DailyReportResponseDto>(dailyReportResponseDtoList, page.getTotalPages(),
				page.getNumber() + 1, page.getSize(), page.getTotalElements(), totalPage.getTotalElements(),
				message, messageType);
		return eventDtoList;
	}

	private Page<DailyReport> setTransactionData(DailyReportRequestDto dailyReportRequestDto, List<DailyReportResponseDto> dailyReportResponseDtoList,
			User user) {
		Page<DailyReport> page = dailyAttendanceService.searchByField(dailyReportRequestDto, user.getOrganization().getName());
		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		for(DailyReport dailyreport:page.getContent()) {
			DailyReportResponseDto dailyReportResponseDto= new DailyReportResponseDto();
			dailyReportResponseDto.setDate(format.format(dailyreport.getDate()));
			dailyReportResponseDto.setEmpId(dailyreport.getEmpId());
			dailyReportResponseDto.setName(dailyreport.getEmployeeName());
			dailyReportResponseDto.setDepartment(dailyreport.getDepartment());
			dailyReportResponseDto.setDesignation(dailyreport.getDesignation());
			dailyReportResponseDto.setGrade(dailyreport.getGrade());
			dailyReportResponseDto.setContactNo(dailyreport.getMobile());
			dailyReportResponseDto.setPunchStatus(dailyreport.getPunchInDevice());
			dailyReportResponseDto.setAttendanceStatus(dailyreport.getAttendanceStatus());
			dailyReportResponseDto.setShift(dailyreport.getShift());
			dailyReportResponseDto.setShiftInTime(dailyreport.getShiftInTime());
			dailyReportResponseDto.setShiftOutTime(dailyreport.getShiftOutTime());
			dailyReportResponseDto.setEmpInTime(dailyreport.getEmpInTime());
			dailyReportResponseDto.setEmpOutTime(dailyreport.getEmpOutTime());
			dailyReportResponseDto.setEmpInLocation(dailyreport.getEmpInLocation());
			dailyReportResponseDto.setEmpOutLocation(dailyreport.getEmpOutLocation());
			dailyReportResponseDto.setEmpInAccessType(dailyreport.getEmpInAccessType());
			dailyReportResponseDto.setEmpOutAccessType(dailyreport.getEmpOutAccessType());
			dailyReportResponseDto.setEarlyComing(dailyreport.getEarlyComing());
			dailyReportResponseDto.setEarlyGoing(dailyreport.getEarlyGoing());
			dailyReportResponseDto.setLateComing(dailyreport.getLateComing());
			dailyReportResponseDto.setLateGoing(dailyreport.getLateGoing());
			dailyReportResponseDto.setMissedOutPunch(dailyreport.getMissedOutPunch());
			dailyReportResponseDto.setWorkTime(dailyreport.getWorkTime());
			dailyReportResponseDto.setOverTime(dailyreport.getOverTimeStr());
			dailyReportResponseDto.setCompany("");
			
			dailyReportResponseDtoList.add(dailyReportResponseDto);
		}
		return page;
	}

}
