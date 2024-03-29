package com.eikona.tech.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "et_employee")
public class Employee extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	@NotBlank(message = "Please provide a valid name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@ManyToOne
	@JoinColumn(name = "designation_id")
	private Designation designation;

	@Column(unique = true,name = "emp_id")
	@NotBlank(message = "Please provide a unique employee id")
	private String empId;
	
	@Column(name = "aadhar_no")
	private String aadharNo;

	@Column
	private String gender;

	@Column
	private String mobile;

	@Column
	private String email;
	
	@Column
	private String fatherName;
	
	@Column
	private String grade;
	
	@Column
	private String permanentAddress;
	
	@Column
	private String residentialAddress;
	
	@Column
	private String joinDate;
	
	@Column
	private String syncDeviceKey;
	
	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@JoinTable(name = "et_employee_area",
    joinColumns = @JoinColumn(name = "employee_id"),
    inverseJoinColumns = @JoinColumn(name = "area_id"),
    indexes = {
	        @Index(name = "idx_area_id", columnList = "area_id"),
	        @Index(name = "idx_employee_id", columnList = "employee_id")
	}
	    )
	private List<Area> area;

	@Column(name = "crop_image")
	private byte[] cropImage;
	
	@Column(name = "sync_date")
	private Date syncDate;
	
	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "is_sync")
	private boolean isSync;
	
	@Column(name = "is_face_sync")
	private boolean isFaceSync;
	
	@Column(name = "is_sync_from_device")
	private boolean isSyncFromDevice;
	
	@Column(name = "is_face_sync_from_device")
	private boolean isFaceSyncFromDevice;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Designation getDesignation() {
		return designation;
	}
	public void setDesignation(Designation designation) {
		this.designation = designation;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getPermanentAddress() {
		return permanentAddress;
	}
	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}
	public String getResidentialAddress() {
		return residentialAddress;
	}
	public void setResidentialAddress(String residentialAddress) {
		this.residentialAddress = residentialAddress;
	}
	public String getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}
	public byte[] getCropImage() {
		return cropImage;
	}
	public void setCropImage(byte[] cropImage) {
		this.cropImage = cropImage;
	}
	public Date getSyncDate() {
		return syncDate;
	}
	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public boolean isSync() {
		return isSync;
	}
	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}
	
	public boolean isFaceSync() {
		return isFaceSync;
	}
	public void setFaceSync(boolean isFaceSync) {
		this.isFaceSync = isFaceSync;
	}
	public boolean isSyncFromDevice() {
		return isSyncFromDevice;
	}
	public void setSyncFromDevice(boolean isSyncFromDevice) {
		this.isSyncFromDevice = isSyncFromDevice;
	}
	public boolean isFaceSyncFromDevice() {
		return isFaceSyncFromDevice;
	}
	public void setFaceSyncFromDevice(boolean isFaceSyncFromDevice) {
		this.isFaceSyncFromDevice = isFaceSyncFromDevice;
	}
	
	public String getAadharNo() {
		return aadharNo;
	}
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}
	public Employee( String name,
			@NotBlank(message = "Please provide a unique employee id") String empId, boolean isDeleted) {
		super();
		this.name = name;
		this.empId = empId;
		this.isDeleted = isDeleted;
	}
	public Employee()
	{
		
	}
	public List<Area> getArea() {
		return area;
	}
	public void setArea(List<Area> area) {
		this.area = area;
	}
	public String getSyncDeviceKey() {
		return syncDeviceKey;
	}
	public void setSyncDeviceKey(String syncDeviceKey) {
		this.syncDeviceKey = syncDeviceKey;
	}
	
}
