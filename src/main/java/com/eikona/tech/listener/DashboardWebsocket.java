package com.eikona.tech.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.DeviceListenerConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.service.impl.hfsecurity.HFSecurityListenerServiceImpl;
import com.eikona.tech.service.impl.uniview.UnvListenerServiceImpl;

@RestController
public class DashboardWebsocket {

	@Autowired
	private UnvListenerServiceImpl heartBeatService;
	
	@Autowired
	private HFSecurityListenerServiceImpl hfSecurityService;
	
	//HFSecurity Heartbeat
		@PostMapping(path ="/hfsecurity/heartbeat")
		public  ResponseEntity<String> hfSecurityHeartReportInfo(@RequestParam("ip") String ipAddress,
				@RequestParam("deviceKey") String deviceKey, @RequestParam("time") String time,
				@RequestParam("version") String version, 
				@RequestParam("faceCount") String faceCount, @RequestParam("personCount") String personCount) throws ParseException {
	        
			hfSecurityService.saveDeviceInfoFromResponse(ipAddress,deviceKey,time,version,faceCount,personCount);
			
			System.out.println("Heartbeat received!!");
			JSONObject response = new JSONObject();

			response.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);

			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	    }
		
		//HFSecurity Eventlog
		@PostMapping(path ="/hfsecurity/eventlog")
		public String hfSecurityEventLogInfo(@RequestParam("deviceKey") String deviceKey, @RequestParam("time") String time, @RequestParam("type") String type, 
				@RequestParam("searchScore") String searchScore,@RequestParam("livenessScore") String livenessScore,@RequestParam("personId") String personId,
				@RequestParam("mask") String mask,@RequestParam("imgBase64") String imgBase64) throws ParseException {
			
			hfSecurityService.saveTransactionInfoFromResponse(deviceKey,time,type,searchScore,livenessScore,mask,imgBase64,personId);
			
			System.out.println("Event log received!!");
			JSONObject response = new JSONObject();
			response.put("result", 1);
			response.put("success", true);

			return response.toString();
			
	}
	
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
