package com.eikona.tech.service;

import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Action;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;

public interface ActionService {

	void save(Action action);

	Action getById(long id);

	void employeeAction(Employee employee, String type, String source);

	void employeeDeviceAction(Device device,Employee employee, String type, String source);

	PaginationDto<Action> searchByField(String sDate, String eDate, String employeeId, String name,
			int pageno, String sortField, String sortDir, String orgName);

}
