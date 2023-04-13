package com.eikona.tech.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="et_transaction")
public class Transaction implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;
	
	@Column
	private String empId;
	
	@Column
	private String employeeCode;

	@Column
	private String name;
	
	@Column
	private String department;
	
	@Column
	private String designation;
	
	@Column
	private String employeeType;
	
	@Column
	private String contractor;
	
	@Column
	private String organization;

	@Column
	private Double deviceId;
	
	@Column
	private Integer logId;
	
	@Column
	private String deviceName;

	@Column
	private String serialNo;
	
	@Column
	private Boolean wearingMask;
	
	@Column
	private String temperature;
	
	@Column
	private String accessType;
	
	@Column
	private Date punchDate;
	
	@Column
	private Date punchTime;
	
	@Column
	private String punchDateStr;
	
	@Column
	private String punchTimeStr;
	
	@Column
	private String mobile;
	
	@Column
	private String deviceType;
	
	@Column
	private byte[] cropImageByte;
	
	@Column
	private String cropimagePath;
	
	@Column
	private String enrollStatus;
	
	@Column
	private String maskStatus;
	
	@Column
	private String searchScore;
	
	@Column
	private String livenessScore;
	
	@Column
	private String grade;
	
	@Column
	private String area;
	
	public String getEnrollStatus() {
		return enrollStatus;
	}

	public void setEnrollStatus(String enrollStatus) {
		this.enrollStatus = enrollStatus;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPunchDateStr() {
		
		return punchDateStr;
	}

	public void setPunchDateStr(String punchDateStr) {
		this.punchDateStr = punchDateStr;
	}

	public String getPunchTimeStr() {
		return punchTimeStr;
	}

	public void setPunchTimeStr(String punchTimeStr) {
		this.punchTimeStr = punchTimeStr;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public Boolean getWearingMask() {
		return wearingMask;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	
	public Boolean isWearingMask() {
		return wearingMask;
	}

	public void setWearingMask(Boolean wearingMask) {
		this.wearingMask = wearingMask;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Date getPunchTime() {
		return punchTime;
	}

	public void setPunchTime(Date punchTime) {
		this.punchTime = punchTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public Date getPunchDate() {
		return punchDate;
	}

	public void setPunchDate(Date punchDate) {
		this.punchDate = punchDate;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	public Double getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Double deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	
	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public byte[] getCropImageByte() {
		return cropImageByte;
	}

	public void setCropImageByte(byte[] cropImageByte) {
		this.cropImageByte = cropImageByte;
	}

	public String getCropimagePath() {
		return cropimagePath;
	}

	public void setCropimagePath(String cropimagePath) {
		this.cropimagePath = cropimagePath;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMaskStatus() {
		return maskStatus;
	}

	public void setMaskStatus(String maskStatus) {
		this.maskStatus = maskStatus;
	}

	public String getSearchScore() {
		return searchScore;
	}

	public void setSearchScore(String searchScore) {
		this.searchScore = searchScore;
	}

	public String getLivenessScore() {
		return livenessScore;
	}

	public void setLivenessScore(String livenessScore) {
		this.livenessScore = livenessScore;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
