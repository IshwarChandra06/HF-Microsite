package com.eikona.tech.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Action;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.ActionService;
import com.eikona.tech.service.EmployeeService;

@Controller
public class ActionController {

	@Autowired
	private ActionService actionService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private UserRepository userRepository;
	

	@GetMapping("/action")
	public String actionList(Model model) {
		return "action/action_list";
	}
	
	

	@GetMapping("/action/new")
	public String newAction(Model model) {
		model.addAttribute("listEmployee", employeeService.getAll());
		Action action = new Action();
		model.addAttribute("actionObj", action);
		model.addAttribute("title", "New Audit");
		return "action/action_new";
	}

	@PostMapping("/action/add")
	public String saveAction(@ModelAttribute("actionObj") Action actionObj, @Valid Action enitiy, Errors errors, String title,
			Model model) {

		if (errors.hasErrors()) {
			model.addAttribute("title", title);
			return "action/action_new";
		} else {
			model.addAttribute("message", "Add Successfully");
			actionService.save(actionObj);
			return "redirect:/action";
		}
	}

	@GetMapping("/action/edit/{id}")
	public String editAction(@PathVariable(value = "id") long id, Model model) {
		model.addAttribute("listEmployee", employeeService.getAll());
		Action action = actionService.getById(id);
		model.addAttribute("actionObj", action);
		model.addAttribute("title", "Update Audit");
		return "action/action_new";
	}

	@RequestMapping(value = "/api/search/action", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('action_view')")
	public @ResponseBody PaginationDto<Action> searchEmployee(String sDate,String eDate, String employeeId, String name, int pageno, String sortField, String sortDir, Principal principal) {

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());
		
		PaginationDto<Action> dtoList = actionService.searchByField(sDate,eDate,employeeId, name,pageno, sortField, sortDir,orgName);
		return dtoList;
	}	
}
