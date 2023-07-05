package com.eikona.tech.dto;

public class DailyReportResponseDto {
	
	private String date;
	
	private String empId;

	private String name;
	
	private String company;
	
	private String department;

	private String designation;

	private String grade;
	
	private String contactNo;
	
	private String punchStatus;
	
	private String attendanceStatus;

	private String shift;

	private String shiftInTime;

	private String shiftOutTime;

	private String empInTime;

	private String empOutTime;

	private Boolean missedOutPunch;
	
	private Long earlyComing;

	private Long lateComing;

	private Long earlyGoing;

	private Long lateGoing;
	
	private String workTime;
	
	private String overTime;
	
	private String empInLocation;

	private String empOutLocation;

	private String empInAccessType;

	private String empOutAccessType;

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}



	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
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

	public String getPunchStatus() {
		return punchStatus;
	}

	public void setPunchStatus(String punchStatus) {
		this.punchStatus = punchStatus;
	}

	public Boolean getMissedOutPunch() {
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

	public String getOverTime() {
		return overTime;
	}

	public void setOverTime(String overTime) {
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	

}
