package com.eikona.tech.dto;

public class TransactionDto {
	
	String organization;
	
	long loginEmployee;
	
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public long getLoginEmployee() {
		return loginEmployee;
	}
	public void setLoginEmployee(long loginEmployee) {
		this.loginEmployee = loginEmployee;
	}
	
	
	public TransactionDto() {
		super();
	}
	public TransactionDto(String organization, long loginEmployee) {
		super();
		this.organization = organization;
		this.loginEmployee = loginEmployee;
	}
	
}
