package com.eikona.tech.service.impl.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.AreaConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Area;
import com.eikona.tech.entity.Department;
import com.eikona.tech.repository.AreaRepository;
import com.eikona.tech.service.AreaService;
import com.eikona.tech.util.GeneralSpecificationUtil;

@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaRepository areaRepository;


	@Autowired
	private GeneralSpecificationUtil<Area> generalSpecificationArea;


	@Override
	public List<Area> getAll() {
		return areaRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(Area area) {
		area.setDeleted(false);
		areaRepository.save(area);
	}

	@Override
	public Area getById(long id) {
		Optional<Area> optional = areaRepository.findById(id);
		Area area = null;
		if (optional.isPresent()) {
			area = optional.get();
		} else {
			throw new RuntimeException(AreaConstants.NOT_FOUND_FOR_ID + id);
		}
		return area;
	}

	@Override
	public void deletedById(long id) {
		Optional<Area> optional = areaRepository.findById(id);
		Area area = null;
		if (optional.isPresent()) {
			area = optional.get();
			area.setDeleted(true);
		} else {
			throw new RuntimeException(AreaConstants.NOT_FOUND_FOR_ID + id);
		}
		this.areaRepository.save(area);
	}


	@Override
	public PaginationDto<Area> searchByField(Long id, String name, String office, int pageno, String sortField,
			String sortDir,String orgName) {

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Page<Area> page = getPaginatedArea(id, name, office, pageno, sort,orgName);
		List<Area> accessLevelList = page.getContent();

		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir)) ? ApplicationConstants.DESC : ApplicationConstants.ASC;
		PaginationDto<Area> dtoList = new PaginationDto<Area>(accessLevelList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir,
				ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<Area> getPaginatedArea(Long id, String name, String office, int pageno, Sort sort, String orgName) {
		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);

		Specification<Area> isdeleted = generalSpecificationArea.isDeletedSpecification();

		Specification<Area> idSpc = generalSpecificationArea.longSpecification(id, ApplicationConstants.ID);
		Specification<Area> nameSpc = generalSpecificationArea.stringSpecification(name, ApplicationConstants.NAME);
		Specification<Area> officeSpc = generalSpecificationArea.foreignKeyStringSpecification(office, AreaConstants.BRANCH, ApplicationConstants.NAME);
		Specification<Area> orgSpc = generalSpecificationArea.foreignKeyStringSpecification(orgName, AreaConstants.ORGANIZATION, ApplicationConstants.NAME);
		Page<Area> page = areaRepository.findAll(isdeleted.and(idSpc).and(nameSpc).and(officeSpc).and(orgSpc),pageable);
		return page;
	}


}
