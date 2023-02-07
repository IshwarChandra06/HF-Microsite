package com.eikona.tech.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.eikona.tech.constants.ApplicationConstants;
import com.eikona.tech.constants.AreaConstants;
import com.eikona.tech.constants.DailyAttendanceConstants;
import com.eikona.tech.constants.EmployeeConstants;
import com.eikona.tech.constants.HeaderConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.entity.Employee;
import com.eikona.tech.repository.EmployeeRepository;
@Component
public class ExportEmployee {
	
	@Autowired
	private GeneralSpecificationUtil<Employee> generalSpecification;
	
	@Autowired
	private EmployeeRepository employeeRepository;

	public void fileExportBySearchValue(HttpServletResponse response, Long id, String name, String empId,
			 String department, String designation, String flag, String orgName) throws ParseException, IOException {
		
		List<Employee> employeeList = getListOfEmployee(id, name, empId, designation,
				department, orgName);
		
		excelGenerator(response, employeeList);
		
	}

	private List<Employee> getListOfEmployee(Long id, String name, String empId, String designation, String department,
			String orgName) {
		Specification<Employee> idSpec = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<Employee> employeeNameSpec = generalSpecification.stringSpecification(name,EmployeeConstants.NAME);
		Specification<Employee> employeeIdSpec = generalSpecification.stringSpecification(empId,EmployeeConstants.EMPID);
    	Specification<Employee> departmentSpec = generalSpecification.foreignKeyStringSpecification(department,EmployeeConstants.DEPARTMENT,EmployeeConstants.NAME); 
    	Specification<Employee>  designationSpec = generalSpecification.foreignKeyStringSpecification(designation,EmployeeConstants.DESIGNATION,EmployeeConstants.NAME);
    	Specification<Employee> orgSpec = generalSpecification.foreignKeyStringSpecification(orgName, AreaConstants.ORGANIZATION,EmployeeConstants.NAME);
		
    	List<Employee> employeeList =employeeRepository.findAll(idSpec.and(employeeNameSpec)
				.and(employeeIdSpec).and(departmentSpec).and(designationSpec).and(orgSpec));
		return employeeList;
	}
	
	private void excelGenerator(HttpServletResponse response, List<Employee> employeeList) throws ParseException, IOException {

		DateFormat dateFormat = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_INDIA_SPLIT_BY_SPACE);
		String currentDateTime = dateFormat.format(new Date());
		String filename = DailyAttendanceConstants.DAILY_ATTENDANCE_REPORT + currentDateTime + ApplicationConstants.EXTENSION_EXCEL;
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();

		int rowCount = NumberConstants.ZERO;
		Row row = sheet.createRow(rowCount++);

		Font font = workBook.createFont();
		font.setBold(true);

		CellStyle cellStyle = setBorderStyle(workBook, BorderStyle.THICK, font);

		setHeaderForExcel(row, cellStyle);

		font = workBook.createFont();
		font.setBold(false);
		cellStyle = setBorderStyle(workBook, BorderStyle.THIN, font);
		
		//set data for excel
		setExcelDataCellWise(employeeList, sheet, rowCount, cellStyle);

		FileOutputStream fileOut = new FileOutputStream(filename);
		workBook.write(fileOut);
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		fileOut.close();
		workBook.close();

	}
	
	private CellStyle setBorderStyle(Workbook workBook, BorderStyle borderStyle, Font font) {
		CellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	private void setExcelDataCellWise(List<Employee> employeeList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		for (Employee employee : employeeList) {
			if(rowCount==90000)
				break;
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getName());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getEmpId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=employee.getDepartment())
			    cell.setCellValue(employee.getDepartment().getName());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=employee.getDesignation())
			    cell.setCellValue(employee.getDesignation().getName());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getGrade());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getFatherName());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getGender());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getMobile());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getEmail());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getPermanentAddress());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(employee.getResidentialAddress());
			cell.setCellStyle(cellStyle);

			

		}
	}
	
	private void setHeaderForExcel(Row row, CellStyle cellStyle) {
		int columnCount = NumberConstants.ZERO;
		Cell cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.ID);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.NAME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.DEPARTMENT);  
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.DESIGNATION);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.GRADE);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.FATHER);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.GENDER);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.MOBILE);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMAIL);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.PERMANENT_ADDRESS);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.RESIDENTIAL_ADDRESS);
		cell.setCellStyle(cellStyle);


	}

}
