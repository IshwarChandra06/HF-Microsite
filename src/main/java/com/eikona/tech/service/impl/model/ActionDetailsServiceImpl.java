package com.eikona.tech.service.impl.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.dto.PaginationDto;
import com.eikona.tech.entity.Action;
import com.eikona.tech.entity.ActionDetails;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.ActionDetailsRepository;
import com.eikona.tech.repository.DeviceRepository;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.service.ActionDetailsService;
import com.eikona.tech.service.impl.hfsecurity.HFSecurityDeviceServiceImpl;
import com.eikona.tech.util.CalendarUtil;
import com.eikona.tech.util.GeneralSpecificationUtil;
import com.eikona.tech.util.HFSecurityDeviceUtil;
import com.eikona.tech.util.HFSecurityErrorMessage;
import com.eikona.tech.util.RequestExecutionUtil;

@Service
@EnableScheduling
public class ActionDetailsServiceImpl implements ActionDetailsService {

	@Autowired
	private ActionDetailsRepository actionDetailsRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private CalendarUtil  calendarUtil;
	
	@Autowired
	private HFSecurityErrorMessage hfErrorMessage;
	
	@Autowired
	private HFSecurityDeviceUtil hfSecurityDeviceUtil;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private HFSecurityDeviceServiceImpl hfsecurityDeviceServiceImpl;

	
	@Autowired
	private GeneralSpecificationUtil<ActionDetails> generalSpecificationActionDetails;
	
	@Autowired
	private RequestExecutionUtil requestExecutionUtil;
	
	@Value("${hf.server.url}")
	private String hfServerIp;

	@Value("${hf.server.secret}")
	private String hfServerSecret;

	@Override
	public Object getAll() {
		return actionDetailsRepository.findAll();
	}

	@Override
	public void save(ActionDetails actionDetails) {
		actionDetailsRepository.save(actionDetails);

	}

	@Override
	public ActionDetails getById(long id) {
		return actionDetailsRepository.findById(id).get();
	}


	@Override
	public void saveAsDeviceAction(Action action, Device device) {
			ActionDetails actionDetails = new ActionDetails();
			actionDetails.setAction(action);
			actionDetails.setDevice(device);
			if ("HF-Security".equalsIgnoreCase(device.getModel())) {
				 addEmployeeToHFSecurity(actionDetails, action.getEmployee(), device);
			 }
			actionDetailsRepository.save(actionDetails);
	}

	public void addEmployeeToHFSecurity(ActionDetails actionDetails, Employee employee, Device device) {
		String employeeResponseCode=hfsecurityDeviceServiceImpl.addEmployeeToDevice(actionDetails);
		if(!"000".equalsIgnoreCase(employeeResponseCode)) {
			if("404".equalsIgnoreCase(employeeResponseCode) || "408".equalsIgnoreCase(employeeResponseCode)) {
				actionDetails.setStatus(ApplicationConstants.PENDING);
				actionDetails.setMessage("User not added, error: " + hfErrorMessage.errormap.get(employeeResponseCode));
			}else {
				actionDetails.setStatus(ApplicationConstants.ERROR);
				actionDetails.setMessage("User not added, error: " + hfErrorMessage.errormap.get(employeeResponseCode));
			}
		}
		else {
			employee.setSync(true);
			String faceResponseCode=addFaceToHFDevice(employee,device.getSerialNo());
			if("000".equalsIgnoreCase(faceResponseCode)) {
				actionDetails.setStatus(ApplicationConstants.COMPLETED);
				actionDetails.setMessage(ApplicationConstants.COMPLETED);
				employee.setFaceSync(true);
			}
			else if("404".equalsIgnoreCase(faceResponseCode) || "408".equalsIgnoreCase(faceResponseCode)) {
				actionDetails.setStatus(ApplicationConstants.PENDING);
				actionDetails.setMessage("User added, Image error: " + hfErrorMessage.errormap.get(faceResponseCode));
			}
			else {
				actionDetails.setStatus(ApplicationConstants.ERROR);
				actionDetails.setMessage("User added, Image error: " + hfErrorMessage.errormap.get(faceResponseCode));
			}
			employeeRepository.save(employee);
		}
			
	}
	private String addFaceToHFDevice(Employee personnel, String deviceKey) {
		String myurl = ApplicationConstants.HTTP_COLON_DOUBLE_SLASH + hfServerIp + ApplicationConstants.DELIMITER_COLON
				+ NumberConstants.EIGHT_THOUSAND_ONE_HUNDRED_NINTY + "/api/face/add";

		HttpPost request = new HttpPost(myurl);
		request.setHeader(ApplicationConstants.HEADER_CONTENT_TYPE, ApplicationConstants.X_WWW_FORM_URLENCODED);
		String imgBase64 = hfSecurityDeviceUtil.employeeImageConversionToBase64(personnel);

		String returnCode = "";
		if (!imgBase64.isEmpty()) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("deviceKey", deviceKey));
			postParameters.add(new BasicNameValuePair("secret", hfServerSecret));
			postParameters.add(new BasicNameValuePair("personId", personnel.getEmpId()));
			postParameters.add(new BasicNameValuePair("imgBase64", imgBase64));
			try {
				request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
				returnCode = getResponse(request, returnCode);
			} catch (Exception e) {
				returnCode = "Failed";
			}
		}else
			returnCode="-1";
		return returnCode;
	}
	private String getResponse(HttpPost request, String returnCode) throws Exception, ParseException {
		String response = requestExecutionUtil.executeHttpPostRequest(request);
		System.out.println(response);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) jsonParser.parse(response);
		returnCode = (String) jsonResponse.get("code");
		return returnCode;
	}
	

	@Override
	public void saveAsAction(Action action) {

			List<Device> deviceList = deviceRepository.findByAreaAndIsDeletedFalseCustom(action.getEmployee().getArea());

			List<ActionDetails> actionDetailsList = new ArrayList<>();

			if (null != deviceList && !deviceList.isEmpty()) {
				for (Device device : deviceList) {
//					if(!(action.getEmployee().getSyncDeviceKey().equals(device.getSerialNo()))) {
						ActionDetails actionDetails = new ActionDetails();
						actionDetails.setAction(action);
						actionDetails.setDevice(device);

						if ("HF-Security".equalsIgnoreCase(device.getModel())) {
							addEmployeeToHFSecurity(actionDetails, action.getEmployee(), device);
							actionDetailsList.add(actionDetails);
						 }
//					}
					
					
				}
			}
			actionDetailsRepository.saveAll(actionDetailsList);
	}
	
	@Override
	public PaginationDto<ActionDetails> searchByField(String sDate, String eDate, String employeeId, String name,
			String device,String status, int pageno, String sortField, String sortDir,String orgName) {
		

		
		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {
				startDate = format.parse(sDate);
				endDate = format.parse(eDate);
				
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<ActionDetails> page = getPageData(startDate,endDate, employeeId, name, device,status, pageno, sortField, sortDir,orgName);
        List<ActionDetails> employeeList =  page.getContent();
        
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<ActionDetails> dtoList = new PaginationDto<ActionDetails>(employeeList, page.getTotalPages(),
				page.getNumber() + NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	
	
	}

	private Page<ActionDetails> getPageData(Date startDate, Date endDate, String employeeId, String name, String device,
			String status, int pageno, String sortField, String sortDir, String orgName) {


		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<ActionDetails> dateSpec = generalSpecificationActionDetails.dateSpecification(startDate, endDate, ApplicationConstants.LAST_MODIFIED_DATE);
		Specification<ActionDetails> empIdSpc = generalSpecificationActionDetails.foreignKeyDoubleObjectStringSpecification(employeeId, "action","employee", "empId");
		Specification<ActionDetails> employeeNameSpc = generalSpecificationActionDetails.foreignKeyDoubleObjectStringSpecification(name, "action", "employee", "name");
		Specification<ActionDetails> isDeletedEmpSpc = generalSpecificationActionDetails.foreignKeyDoubleObjectBooleanSpecification(false, "action", "employee", "isDeleted");
		Specification<ActionDetails> pendingSpc = generalSpecificationActionDetails.stringEqualSpecification("Pending", "status");
		Specification<ActionDetails> errorSpc = generalSpecificationActionDetails.stringSpecification("Error", "status");
		Specification<ActionDetails> statusSpc = generalSpecificationActionDetails.stringSpecification(status, "status");
		Specification<ActionDetails> deviceSpc = generalSpecificationActionDetails.foreignKeyStringSpecification(device, "device", "name");
		Specification<ActionDetails> orgSpc = generalSpecificationActionDetails.foreignKeyTripleSpecification(orgName, "action", "employee", "organization","name");
		
    	Page<ActionDetails> page = actionDetailsRepository.findAll(dateSpec.and(empIdSpc).and(employeeNameSpc).and(deviceSpc).and(statusSpc).and(isDeletedEmpSpc).and(orgSpc).and(pendingSpc.or(errorSpc)), pageable);
		return page;
	
	
	}	
	
}
