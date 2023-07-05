package com.eikona.tech.controller.api;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
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

import com.eikona.tech.dto.EventRequestDto;
import com.eikona.tech.dto.EventResponseDto;
import com.eikona.tech.dto.PaginatedDto;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.TransactionRepository;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.TransactionService;



@RestController
@RequestMapping("/apis/V1")
public class TransactionRestController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionRepository  transactionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value = "/event/search", method = RequestMethod.POST)
	@PreAuthorize("hasAuthority('transaction_view')")
	public @ResponseBody PaginatedDto<EventResponseDto> search(@RequestBody EventRequestDto eventDto,
			Principal principal) {
		PaginatedDto<EventResponseDto> eventDtoList = null;
		List<EventResponseDto> eventResponseDtoList = new ArrayList<>();
		
		try {
			if (0 == eventDto.getPageNo() || 0 == eventDto.getPageSize()) 
				return new PaginatedDto<EventResponseDto>(eventResponseDtoList, 0, 0, 0, 0, 0,
						"Page No and Page Size should be greater than 0", "I");
			
			if(null==eventDto.getDate())
				eventDto.setDate("");

			eventDtoList = setEventListInPaginationDto(eventDto, principal, eventResponseDtoList);

		} catch (Exception e) {
			return new PaginatedDto<EventResponseDto>(eventResponseDtoList, 0, 0, 0, 0, 0, "Contact Admin!!", "E");
		}
		return eventDtoList;
		
	}

	private PaginatedDto<EventResponseDto> setEventListInPaginationDto(EventRequestDto eventDto, Principal principal,
			List<EventResponseDto> eventResponseDtoList) throws ParseException {
		PaginatedDto<EventResponseDto> eventDtoList;
		String message = "";
		String messageType = "";

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());

		Page<Transaction> page = setTransactionData(eventDto, eventResponseDtoList, user);
		
		List<Transaction> totaleventResponse = transactionRepository.findByOrganization(user.getOrganization().getName());
		Page<Transaction> totalPage = new PageImpl<Transaction>(totaleventResponse);
		message = "Success";
		messageType = "S";
		eventDtoList = new PaginatedDto<EventResponseDto>(eventResponseDtoList, page.getTotalPages(),
				page.getNumber() + 1, page.getSize(), page.getTotalElements(), totalPage.getTotalElements(),
				message, messageType);
		return eventDtoList;
	}

	private Page<Transaction> setTransactionData(EventRequestDto eventDto, List<EventResponseDto> eventResponseDtoList,
			User user) {
		Page<Transaction> page = transactionService.searchByField(eventDto, user.getOrganization().getName());
		
		for(Transaction transaction:page.getContent()) {
			EventResponseDto eventResponseDto= new EventResponseDto();
			
			eventResponseDto.setDate(transaction.getPunchDateStr());
			eventResponseDto.setTime(transaction.getPunchTimeStr());
			eventResponseDto.setEmpId(transaction.getEmpId());
			eventResponseDto.setName(transaction.getName());
			eventResponseDto.setDeviceEmpId(transaction.getEmployeeCode());
			eventResponseDto.setCompany("");
			eventResponseDto.setContactNo(transaction.getMobile());
			eventResponseDto.setDepartment(transaction.getDepartment());
			eventResponseDto.setDeviceName(transaction.getDeviceName());
			eventResponseDto.setDesignation(transaction.getDesignation());
			eventResponseDto.setGrade(transaction.getGrade());
			
			eventResponseDtoList.add(eventResponseDto);
		}
		return page;
	}

}
