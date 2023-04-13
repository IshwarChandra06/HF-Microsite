package com.eikona.tech.dto;

public class DeviceStatusDto {
	
	private long totalDevice;
	
	private long onlineDevice;
	
	private String organization;

	public long getTotalDevice() {
		return totalDevice;
	}

	public void setTotalDevice(long totalDevice) {
		this.totalDevice = totalDevice;
	}

	public long getOnlineDevice() {
		return onlineDevice;
	}

	public void setOnlineDevice(long onlineDevice) {
		this.onlineDevice = onlineDevice;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	
	
}
