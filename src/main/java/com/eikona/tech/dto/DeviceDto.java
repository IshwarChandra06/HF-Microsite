package com.eikona.tech.dto;

public class DeviceDto {
	
	private String device;
	private String serialNo;
	private String organization;
	private long totalPerson;
	private long totalTransaction;
	private long totalUnregisterTransaction;
	private long capacity;
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	
	public long getTotalPerson() {
		return totalPerson;
	}
	public void setTotalPerson(long totalPerson) {
		this.totalPerson = totalPerson;
	}
	public long getTotalTransaction() {
		return totalTransaction;
	}
	public void setTotalTransaction(long totalTransaction) {
		this.totalTransaction = totalTransaction;
	}
	public long getTotalUnregisterTransaction() {
		return totalUnregisterTransaction;
	}
	public void setTotalUnregisterTransaction(long totalUnregisterTransaction) {
		this.totalUnregisterTransaction = totalUnregisterTransaction;
	}
	public long getCapacity() {
		return capacity;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}

}
