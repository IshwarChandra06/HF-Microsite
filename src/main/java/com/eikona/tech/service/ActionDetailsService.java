package com.eikona.tech.service;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Action;
import com.eikona.tech.entity.ActionDetails;
import com.eikona.tech.entity.Device;

public interface ActionDetailsService {

	Object getAll();

	void save(ActionDetails actionDetails);

	ActionDetails getById(long id);

	void saveAsAction(Action action);

	void saveAsDeviceAction(Action action,Device device);

	PaginationDto<ActionDetails> searchByField(String sDate, String eDate, String employeeId, String name, String device,
			String status, int pageno, String sortField, String sortDir, String orgName);

}
