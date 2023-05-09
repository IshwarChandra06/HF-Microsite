package com.eikona.tech.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.DefaultConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.entity.Image;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.EmployeeRepository;
import com.eikona.tech.repository.ImageRepository;

import net.coobird.thumbnailator.Thumbnails;

@Component
public class ImageProcessingUtil {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ImageRepository imageRepository;
	

	public Employee searchEmployee(String empId) {
		return employeeRepository.findByEmpIdAndIsDeletedFalse(empId);
	}

	@SuppressWarnings(ApplicationConstants.UNUSED)
	public byte[] searchEmployeeImage(long id) {
		Employee emp = new Employee();
		emp.setId(id);
		List<Image> imageList = imageRepository.findByEmployeeOrderByIdDesc(emp);
		String bytesBase64 = null;
		byte[] bytes = null;
		if (null != imageList) {

			try {
				    Image image=imageList.get(0);
					String imagePath = image.getThumbnailPath();
					InputStream inputStream = new FileInputStream(imagePath);
					bytes = IOUtils.toByteArray(inputStream);
					bytesBase64 = Base64.encodeBase64String(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	public byte[] searchTransactionImage(Transaction trans) {

		byte[] bytes = null;
		try {
			if(null!=trans.getCropimagePath()) {
				String imagePath = trans.getCropimagePath();
				InputStream inputStream = new FileInputStream(imagePath);

				bytes = IOUtils.toByteArray(inputStream);
				Base64.encodeBase64String(bytes);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public String[] imageProcessing(BufferedImage originalImage, String empId, String orgName) {
		String[] stringArray = new String[NumberConstants.THREE];
		try {
			String writeImageForOrginal = ApplicationConstants.DELIMITER_EMPTY;
			String writeImageForThumbnail = ApplicationConstants.DELIMITER_EMPTY;
			String writeImageForResize = ApplicationConstants.DELIMITER_EMPTY;


			File original = new File(orgName+ApplicationConstants.DELIMITER_FORWARD_SLASH+DefaultConstants.EMPLOYEE_ORIGINAL_PATH);
			writeImageForOrginal = getOriginalImagePath(stringArray, empId, original,orgName);

			File resize = new File(orgName+ApplicationConstants.DELIMITER_FORWARD_SLASH+DefaultConstants.EMPLOYEE_RESIZE_PATH);
			writeImageForResize = getResizeImagePath(stringArray, empId, resize,orgName);

			File thumbnail = new File(orgName+ApplicationConstants.DELIMITER_FORWARD_SLASH+DefaultConstants.EMPLOYEE_THUIMBNAIL_PATH);
			writeImageForThumbnail = getThumbnailImagePath(stringArray, empId, thumbnail,orgName);

			if (null != originalImage) {
				ImageIO.write(originalImage, ApplicationConstants.FORMAT_JPG, new File(writeImageForOrginal));

				BufferedImage outputImageForThumbnail = resizeImage(originalImage, NumberConstants.HUNDRED,
						NumberConstants.HUNDRED);
				BufferedImage outputImageForResize = resizeImage(originalImage, NumberConstants.THOUSAND_EIGHTY,
						NumberConstants.NINETEEN_HUNDRED_TWENTY);

				ImageIO.write(outputImageForThumbnail, ApplicationConstants.FORMAT_JPG,
						new File(writeImageForThumbnail));
				ImageIO.write(outputImageForResize, ApplicationConstants.FORMAT_JPG, new File(writeImageForResize));
			}
			return stringArray;
		} catch (IOException e) {
			e.printStackTrace();

			return stringArray;
		}

	}

	private String getThumbnailImagePath(String[] stringArray,String empId, File thum, String orgName) {
		String writeImageForEmployee;
		String thumDirPath = orgName+ApplicationConstants.DELIMITER_FORWARD_SLASH+DefaultConstants.EMPLOYEE_THUIMBNAIL_PATH;
		if (!thum.exists()) {
			thum.mkdirs();
			writeImageForEmployee = stringArray[NumberConstants.TWO] = thumDirPath
					+ ApplicationConstants.DELIMITER_FORWARD_SLASH + empId + ".jpg";
		} else {
			writeImageForEmployee = stringArray[NumberConstants.TWO] = thumDirPath
					+ ApplicationConstants.DELIMITER_FORWARD_SLASH +  empId + ".jpg";
		}
		return writeImageForEmployee;
	}

	private String getResizeImagePath(String[] stringArray,  String empId, File emp, String orgName) {
		String writeImageForDevice;
		String empDirPath = orgName+ApplicationConstants.DELIMITER_FORWARD_SLASH+DefaultConstants.EMPLOYEE_RESIZE_PATH;
		if (!emp.exists()) {
			emp.mkdirs();
			writeImageForDevice = stringArray[NumberConstants.ONE] = empDirPath
					+ ApplicationConstants.DELIMITER_FORWARD_SLASH +  empId + ".jpg";
		} else {
			writeImageForDevice = stringArray[NumberConstants.ONE] = empDirPath
					+ ApplicationConstants.DELIMITER_FORWARD_SLASH +  empId + ".jpg";
		}
		return writeImageForDevice;
	}

	private String getOriginalImagePath(String[] stringArray,  String empId, File file, String orgName) {
		String writeImageForOrginal;
		String orgDirPath = orgName+ApplicationConstants.DELIMITER_FORWARD_SLASH+DefaultConstants.EMPLOYEE_ORIGINAL_PATH;
		if (!file.exists()) {
			file.mkdirs();
			writeImageForOrginal = stringArray[NumberConstants.ZERO] = orgDirPath
					+ ApplicationConstants.DELIMITER_FORWARD_SLASH + empId + ".jpg";
		} else {
			writeImageForOrginal = stringArray[NumberConstants.ZERO] = orgDirPath
					+ ApplicationConstants.DELIMITER_FORWARD_SLASH + empId + ".jpg";
		}
		return writeImageForOrginal;
	}

	public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Thumbnails.of(originalImage).size(targetWidth, targetHeight).outputFormat(ApplicationConstants.FORMAT_JPEG)
				.outputQuality(0.90).toOutputStream(outputStream);
		byte[] data = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		return ImageIO.read(inputStream);
	}

	public void saveEmployeeImageWhileEnrolling(MultipartFile file, Employee employee) {
		try {
			List<Image> imageList = new ArrayList<>();
			List<Employee> employeeList = new ArrayList<Employee>();
			if (null != employee) {
				employee.setSync(false);
				employee.setFaceSync(false);
				employeeList.add(employee);

				byte[] bytes = file.getBytes();
				InputStream is = new ByteArrayInputStream(bytes);
				BufferedImage originalImage = ImageIO.read(is);

				String[] imagePath = imageProcessing(originalImage, employee.getEmpId(),employee.getOrganization().getName());
				Image imageObj = imageRepository.findByOriginalPath(imagePath[NumberConstants.ZERO]);
				if (null == imageObj) {
					Image imageSaved = new Image();
					setImagePath(imageList, employeeList, imagePath, imageSaved);
				}
				employeeRepository.save(employee);
			}
			
			imageRepository.saveAll(imageList);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void uploadEmployeeImageDirectory(MultipartFile[] files) {
		try {
			List<Image> imageList = new ArrayList<>();
			for (MultipartFile file : files) {

				String fileName = file.getOriginalFilename();
				String[] fileNameArray = fileName.split(ApplicationConstants.DELIMITER_FORWARD_SLASH);
				String imageName = fileNameArray[NumberConstants.ONE];
				String[] splitBydot = imageName.split(Pattern.quote(ApplicationConstants.DELIMITER_DOT));
				String empId = splitBydot[NumberConstants.ZERO];
				Employee employee = searchEmployee(empId);
				List<Employee> employeeList = new ArrayList<Employee>();
				if (null != employee) {
					employee.setSync(false);
					employee.setFaceSync(false);
					employeeList.add(employee);

					byte[] bytes = file.getBytes();
					InputStream is = new ByteArrayInputStream(bytes);
					BufferedImage originalImage = ImageIO.read(is);

					String[] imagePath = imageProcessing(originalImage, empId,employee.getOrganization().getName());
					Image imageObj = imageRepository.findByOriginalPath(imagePath[NumberConstants.ZERO]);
					if (null == imageObj) {
						Image imageSaved = new Image();
						setImagePath(imageList, employeeList, imagePath, imageSaved);
					}
					employeeRepository.save(employee);
				}
				
			}

			imageRepository.saveAll(imageList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setImagePath(List<Image> imageList, List<Employee> employeeList, String[] imagePath,
			Image imageSaved) {
		imageSaved.setEmployee(employeeList);
		imageSaved.setOriginalPath(imagePath[NumberConstants.ZERO]);
		imageSaved.setResizePath(imagePath[NumberConstants.ONE]);
		imageSaved.setThumbnailPath(imagePath[NumberConstants.TWO]);
		imageSaved.setDeleted(false);

		imageList.add(imageSaved);
	}

}
