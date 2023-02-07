package com.eikona.tech.service.impl.uniview;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.constants.UnvDeviceConstants;
import com.eikona.tech.entity.Device;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.DeviceRepository;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.repository.TransactionRepository;
import com.eikona.tech.util.SavingCropImageUtil;

@Service
public class UnvListenerServiceImpl {
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private SavingCropImageUtil savingCropImageUtil;
	
	public Device saveDeviceInfo(String response) {

		SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US);
		JSONObject jsonHeartReportInfo = new JSONObject(response);

		Device device = deviceRepository.findBySerialNoAndIsDeletedFalse(jsonHeartReportInfo.getString(UnvDeviceConstants.DEVICE_CODE));
		String dateStr = jsonHeartReportInfo.getString(UnvDeviceConstants.TIME);
		try {
			if (null != device) {
				device.setLastOnline(format.parse(dateStr));
				return deviceRepository.save(device);
			} else {
				Device deviceNew = new Device();
				deviceNew.setSync(false);
				deviceNew.setRefId(jsonHeartReportInfo.getString(UnvDeviceConstants.REF_ID));
				deviceNew.setLastOnline(format.parse(dateStr));
				deviceNew.setSerialNo(jsonHeartReportInfo.getString(UnvDeviceConstants.DEVICE_CODE));
				deviceNew.setDeviceType(jsonHeartReportInfo.getLong(UnvDeviceConstants.DEVICE_TYPE));
				return deviceRepository.save(deviceNew);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveTransactionInfo(String response) {

		try {
			JSONObject jsonPersonVerification = new JSONObject(response);

			Transaction transaction = new Transaction();

			transaction.setSerialNo(jsonPersonVerification.getString(UnvDeviceConstants.DEVICE_CODE));
			
			JSONArray libMatarray = jsonPersonVerification.getJSONArray(UnvDeviceConstants.LIB_MAT_INFO_LIST);
			
			setEmployeeDetailsInTransactionFromListener(jsonPersonVerification, transaction, libMatarray);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setEmployeeDetailsInTransactionFromListener(JSONObject jsonPersonVerification, Transaction transaction,
			JSONArray libMatarray) {
		if (libMatarray.length() > NumberConstants.ZERO) {
			JSONObject empObj = libMatarray.getJSONObject(NumberConstants.ZERO).getJSONObject(UnvDeviceConstants.MATCH_PERSON_INFO);
			
			Employee employee = employeeRepository.findByEmpIdAndIsDeletedFalse(empObj.getString(UnvDeviceConstants.IDENTITY_NO));
			Device deviceObj = deviceRepository.findBySerialNoAndIsDeletedFalse(jsonPersonVerification.getString(UnvDeviceConstants.DEVICE_CODE));
		
			
			transaction.setName(empObj.getString(UnvDeviceConstants.PERSON_NAME));
			transaction.setEmployeeCode (empObj.getString(UnvDeviceConstants.PERSON_CODE));
			transaction.setAccessType("Face");
			Transaction enrollStatus=transactionRepository.findByEmployeeCodeAndEnrollStatus(transaction.getEmployeeCode(),"Unenrolled");
			
			if(null!=employee) {
				transaction.setEmpId(employee.getEmpId());
				transaction.setName(employee.getName());
				transaction.setGrade(employee.getGrade());
				transaction.setMobile(employee.getMobile());
				if(null!=employee.getDepartment())
					transaction.setDepartment(employee.getDepartment().getName());
				
				if(null!=employee.getDesignation())
					transaction.setDesignation(employee.getDesignation().getName());
				transaction.setEnrollStatus("Enrolled");
			}else if("Unregistered".equalsIgnoreCase(transaction.getName()))
				transaction.setEnrollStatus("Unregistered");
			else if(null==enrollStatus)
				transaction.setEnrollStatus("Unenrolled");
			
			if(null!=deviceObj) {
				transaction.setDeviceName(deviceObj.getName());
				transaction.setSerialNo(deviceObj.getSerialNo());
				if(null != deviceObj.getOrganization())
					transaction.setOrganization(deviceObj.getOrganization().getName());
			}
		}
		
		setOtherDetailsInTransactionFromListener(jsonPersonVerification, transaction);
	}

	private void setOtherDetailsInTransactionFromListener(JSONObject jsonPersonVerification, Transaction transaction) {
		SimpleDateFormat dateformat=new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
		SimpleDateFormat timeformat=new SimpleDateFormat(ApplicationConstants.TIME_FORMAT_24HR);
		
		JSONArray facearray = jsonPersonVerification.getJSONArray(UnvDeviceConstants.FACE_INFO_LIST);
		if (facearray.length() > NumberConstants.ZERO) {
			JSONObject faceObj = facearray.getJSONObject(NumberConstants.ZERO);
			transaction.setWearingMask(faceObj.getInt(UnvDeviceConstants.MASK_FLAG) == NumberConstants.ONE);
			
			transaction.setPunchDate(new Date(faceObj.getLong(UnvDeviceConstants.TIME_STAMP) * NumberConstants.THOUSAND));
			String dateStr=dateformat.format(transaction.getPunchDate());
			String timeStr=timeformat.format(transaction.getPunchDate());
			transaction.setPunchDateStr(dateStr);
			transaction.setPunchTimeStr(timeStr);
			
			transaction.setTemperature(String.valueOf(faceObj.getFloat(UnvDeviceConstants.TEMPERATURE)));
			JSONObject faceImage=(JSONObject) faceObj.get(UnvDeviceConstants.FACE_IMAGE);
			String base64 = (String) faceImage.get(UnvDeviceConstants.DATA);
			String cropImagePath=savingCropImageUtil.saveCropImages(base64, transaction);
			transaction.setCropimagePath(cropImagePath);
		}
		
		transactionRepository.save(transaction);
	}
}
