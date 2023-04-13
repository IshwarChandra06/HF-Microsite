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
import com.eikona.tech.entity.Area;
import com.eikona.tech.entity.User;
import com.eikona.tech.repository.UserRepository;
import com.eikona.tech.service.AreaService;
import com.eikona.tech.service.OrganizationService;

@Controller
public class AreaController {

	@Autowired
	private AreaService areaService;

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@GetMapping("/area")
	@PreAuthorize("hasAuthority('area_view')")
	public String areaListPage(Model model) {
		return "area/area_list";
	}


	@GetMapping("/area/new")
	@PreAuthorize("hasAuthority('area_create')")
	public String newArea(Model model, Principal principal) {

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		model.addAttribute("listOrganization", organizationService.getById(user.getOrganization().getId()));
		Area area = new Area();
		model.addAttribute("area", area);
		model.addAttribute("title", "New Area");
		return "area/area_new";
	}
	


	@PostMapping("/area/add")
	@PreAuthorize("hasAnyAuthority('area_create','area_update')")
	public String saveArea(@ModelAttribute("area") Area area, @Valid Area ar, Errors errors, String title,
			Model model, Principal principal) {
		if (errors.hasErrors()) {
			User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
			model.addAttribute("listOrganization", organizationService.getById(user.getOrganization().getId()));
			model.addAttribute("title", title);
			return "area/area_new";
		} else {
			if (null == area.getId())
				areaService.save(area);
			else {
				Area areaObj = areaService.getById(area.getId());
				area.setCreatedBy(areaObj.getCreatedBy());
				area.setCreatedDate(areaObj.getCreatedDate());
				areaService.save(area);
			}
			return "redirect:/area";

		}

	}

	@GetMapping("/area/edit/{id}")
	@PreAuthorize("hasAuthority('area_update')")
	public String updateArea(@PathVariable(value = "id") long id, Model model, Principal principal) {
		Area area = areaService.getById(id);
		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		model.addAttribute("listOrganization", organizationService.getById(user.getOrganization().getId()));
		model.addAttribute("area", area);
		model.addAttribute("title", "Update Area");
		return "area/area_new";
	}

	@GetMapping("/area/delete/{id}")
	@PreAuthorize("hasAuthority('area_delete')")
	public String deleteArea(@PathVariable(value = "id") long id) {

		this.areaService.deletedById(id);
		return "redirect:/area";
	}

	@RequestMapping(value = "/api/search/area", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('area_view')")
	public @ResponseBody PaginationDto<Area> search(Long id, String name, String office, int pageno, String sortField,
			String sortDir, Principal principal) {

		User user = userRepository.findByUserNameAndIsDeletedFalse(principal.getName());
		String orgName = (null == user.getOrganization()? null: user.getOrganization().getName());

		PaginationDto<Area> dtoList = areaService.searchByField(id, name, office, pageno, sortField, sortDir,orgName);
		return dtoList;
	}

}
