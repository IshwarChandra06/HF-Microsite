package com.eikona.tech.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "et_device")
public class Device extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "access_type")
	private String accessType;
	
	@Column(name = "model_no")
	private String modelNo;

	@Column(unique = true,name = "serial_no")
	@NotBlank(message = "Please provide a unique serial no")
	private String serialNo;
	
	@Column
	private String model;

	@Column(name = "ip_address")
	private String ipAddress;
	
	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;
	
	@ManyToOne
	@JoinColumn(name = "area_id")
	private Area area;
	
	@Column(name = "last_online")
	private Date lastOnline;
	
	@Column(name = "ref_id")
	private String refId;
	
	@Column(name = "device_type")
	private Long deviceType;
	
	@Column(name = "last_sync")
	private Date lastSync;
	
	@Column(name = "is_sync")
	private boolean isSync;
	
	@Column(name = "person_no")
	private Long personNo;
	
	@Column(name = "face_no")
	private Long faceNo;
	
	@Column
	private String status;
	
	@Column(name = "is_deleted")
	private boolean isDeleted;

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

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getModelNo() {
		return modelNo;
	}

	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Date getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(Date lastOnline) {
		this.lastOnline = lastOnline;
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public Long getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Long deviceType) {
		this.deviceType = deviceType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Long getPersonNo() {
		return personNo;
	}

	public void setPersonNo(Long personNo) {
		this.personNo = personNo;
	}

	public Long getFaceNo() {
		return faceNo;
	}

	public void setFaceNo(Long faceNo) {
		this.faceNo = faceNo;
	}

	public String getStatus() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = format.format(new Date());
			Date date = format.parse(dateStr);
			Date lastonline = getLastOnline();
			
			long status = date.getTime() - lastonline.getTime();
			
			if(status<900000) {
				return "Green";
			}else if(status<3600000) {
				return "Amber";
			}else {
				return "Red";
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
