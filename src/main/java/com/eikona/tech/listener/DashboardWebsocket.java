package com.eikona.tech.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.DeviceListenerConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.service.impl.uniview.UnvListenerServiceImpl;

@Controller
public class DashboardWebsocket {

	@Autowired
	private UnvListenerServiceImpl heartBeatService;
	
	//Uniview Listener
	@PostMapping(path = DeviceListenerConstants.UNV_HEARTBEAT_API)
	public ResponseEntity<String> unvHeartReportInfo(@RequestBody String request)
			throws ParseException {

		
		heartBeatService.saveDeviceInfo(request);

		String currentTime = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US).format(new Date());

		JSONObject response = new JSONObject();
		response.put(DeviceListenerConstants.RESPONSE_URL, DeviceListenerConstants.UNV_HEARTBEAT_API);
		response.put(DeviceListenerConstants.CODE, NumberConstants.ZERO);
		response.put(DeviceListenerConstants.DATA, new JSONObject().put(DeviceListenerConstants.TIME, currentTime));

		System.out.println(response.toString());

		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}

	@PostMapping(path = DeviceListenerConstants.UNV_TRANSACTION_API, produces = ApplicationConstants.APPLICATION_JSON, consumes = ApplicationConstants.MIME_TYPE_TEXT)
	public ResponseEntity<String> unvPersonVerification(@RequestBody String request) throws ParseException {

		heartBeatService.saveTransactionInfo(request);

		String currentTime = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US).format(new Date());

		JSONObject requestJson = new JSONObject(request);
		JSONObject masterResponse = new JSONObject();
		JSONObject response = new JSONObject();

		response.put(DeviceListenerConstants.RESPONSE_URL, DeviceListenerConstants.UNV_TRANSACTION_API);
		response.put(DeviceListenerConstants.STATUS_CODE, NumberConstants.ZERO);
		response.put(DeviceListenerConstants.STATUS_STRING, DeviceListenerConstants.SUCCEEED);
		response.put(DeviceListenerConstants.DATA, new JSONObject().put(DeviceListenerConstants.TIME, currentTime).put(DeviceListenerConstants.RECORD_ID, requestJson.get(DeviceListenerConstants.SEQ)));

		masterResponse.put(DeviceListenerConstants.RESPONSE, response);

		System.out.println(masterResponse.toString());

		return new ResponseEntity<String>(masterResponse.toString(), HttpStatus.OK);
	}
}
