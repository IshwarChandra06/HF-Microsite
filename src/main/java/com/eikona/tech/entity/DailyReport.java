package com.eikona.tech.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

@Component
@Entity(name = "et_daily_report")
public class DailyReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	@Column
	private String empId;

	@Column
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private Date date;

	@Transient
	private String dateStr;

	@Column
	private String employeeName;

	@Column
	private String company;

	@Column
	private String city;
	
	@Column
	private String grade;
	
	@Column
	private String mobile;

	@Column
	private String branch;

	@Column
	private String department;

	@Column
	private String organization;

	@Column
	private String designation;

	@Column
	private String employeeType;

	@Column
	private String contractor;

	@Column
	private String userType;

	@Column
	private String shift;

	@Column
	private String shiftInTime;

	@Column
	private String shiftOutTime;

	@Column
	private String empInTime;

	@Column
	private String empOutTime;

	@Column
	private String empInTemp;

	@Column
	private String empOutTemp;

	@Column
	private Boolean empInMask;

	@Column
	private Boolean empOutMask;

	@Column
	private String empInLocation;

	@Column
	private String empOutLocation;

	@Column
	private String empInAccessType;

	@Column
	private String empOutAccessType;

	@Column
	private Boolean missedOutPunch;

	@Column
	private String attendanceStatus;

	@Column
	private String workTime;

	@Column
	private Long overTime;

	@Column
	private Long earlyComing;

	@Column
	private Long lateComing;

	@Column
	private Long earlyGoing;

	@Column
	private Long lateGoing;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDateStr() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getShiftInTime() {
		return shiftInTime;
	}

	public void setShiftInTime(String shiftInTime) {
		this.shiftInTime = shiftInTime;
	}

	public String getShiftOutTime() {
		return shiftOutTime;
	}

	public void setShiftOutTime(String shiftOutTime) {
		this.shiftOutTime = shiftOutTime;
	}
	
	/*
	 * private String ShiftOutDate; private String ShiftInDate;
	 * 
	 * public String getShiftOutDate() { SimpleDateFormat outputFormat = new
	 * SimpleDateFormat("HH:mm:ss"); this.ShiftOutDate =
	 * outputFormat.format(shiftOutTime); return ShiftOutDate; }
	 * 
	 * public String getShiftInDate() { SimpleDateFormat outputFormat = new
	 * SimpleDateFormat("HH:mm:ss"); this.ShiftInDate =
	 * outputFormat.format(shiftInTime); return ShiftInDate; }
	 */
	public String getEmpInTime() {
		return empInTime;
	}

	public void setEmpInTime(String empInTime) {
		this.empInTime = empInTime;
	}

	public String getEmpOutTime() {
		return empOutTime;
	}

	public void setEmpOutTime(String empOutTime) {
		this.empOutTime = empOutTime;
	}

	public String getEmpInTemp() {
		return empInTemp;
	}

	public void setEmpInTemp(String empInTemp) {
		this.empInTemp = empInTemp;
	}

	public String getEmpOutTemp() {
		return empOutTemp;
	}

	public void setEmpOutTemp(String empOutTemp) {
		this.empOutTemp = empOutTemp;
	}

	public Boolean isEmpInMask() {
		return empInMask;
	}

	public void setEmpInMask(Boolean empInMask) {
		this.empInMask = empInMask;
	}

	public Boolean isEmpOutMask() {
		return empOutMask;
	}

	public void setEmpOutMask(Boolean empOutMask) {
		this.empOutMask = empOutMask;
	}

	public String getEmpInLocation() {
		return empInLocation;
	}

	public void setEmpInLocation(String empInLocation) {
		this.empInLocation = empInLocation;
	}

	public String getEmpOutLocation() {
		return empOutLocation;
	}

	public void setEmpOutLocation(String empOutLocation) {
		this.empOutLocation = empOutLocation;
	}

	public String getEmpInAccessType() {
		return empInAccessType;
	}

	public void setEmpInAccessType(String empInAccessType) {
		this.empInAccessType = empInAccessType;
	}

	public String getEmpOutAccessType() {
		return empOutAccessType;
	}

	public void setEmpOutAccessType(String empOutAccessType) {
		this.empOutAccessType = empOutAccessType;
	}

	public Boolean isMissedOutPunch() {
		return missedOutPunch;
	}

	public void setMissedOutPunch(Boolean missedOutPunch) {
		this.missedOutPunch = missedOutPunch;
	}

	public String getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(String attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public String getWorkTime() {
		return workTime;
	}

	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

	public Long getOverTime() {
		return overTime;
	}

	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}

	public Long getEarlyComing() {
		return earlyComing;
	}

	public void setEarlyComing(Long earlyComing) {
		this.earlyComing = earlyComing;
	}

	public Long getLateComing() {
		return lateComing;
	}

	public void setLateComing(Long lateComing) {
		this.lateComing = lateComing;
	}

	public Long getEarlyGoing() {
		return earlyGoing;
	}

	public void setEarlyGoing(Long earlyGoing) {
		this.earlyGoing = earlyGoing;
	}

	public Long getLateGoing() {
		return lateGoing;
	}

	public void setLateGoing(Long lateGoing) {
		this.lateGoing = lateGoing;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public Boolean getEmpInMask() {
		return empInMask;
	}

	public Boolean getEmpOutMask() {
		return empOutMask;
	}

	public Boolean getMissedOutPunch() {
		return missedOutPunch;
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

}


