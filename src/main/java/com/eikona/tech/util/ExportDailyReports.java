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
import com.eikona.tech.constants.HeaderConstants;
import com.eikona.tech.constants.NumberConstants;
import com.eikona.tech.entity.DailyReport;
import com.eikona.tech.repository.DailyAttendanceRepository;

@Component
public class ExportDailyReports {

	
	@Autowired
	private DailyAttendanceRepository dailyAttendanceRepository;
	
	@Autowired
	private GeneralSpecificationUtil<DailyReport> generalSpecification;
	
	@Autowired
	private CalendarUtil calendarUtil;
 
	public void fileExportBySearchValue(HttpServletResponse response, Long id, String sDate, String eDate,
			String employeeName, String employeeId, String designation, String department, String status,String shift, String flag,
			String orgName) throws ParseException, IOException {

		Date startDate = null;
		Date endDate = null;
		if (!sDate.isEmpty() && !eDate.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT_OF_US);
			try {
				 startDate = format.parse(sDate);
				 endDate = format.parse(eDate);
				endDate = calendarUtil.getConvertedDate(endDate, NumberConstants.TWENTY_THREE, NumberConstants.FIFTY_NINE, NumberConstants.FIFTY_NINE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		List<DailyReport> dailyAttendanceList = getListOfDailyAttendance(id, employeeName, employeeId, designation,
				department, status, startDate, endDate, orgName,shift);
		
		excelGenerator(response, dailyAttendanceList);
	}
	
	private List<DailyReport> getListOfDailyAttendance(Long id, String employeeName, String employeeId, String designation,
			String department, String status, Date startDate, Date endDate, String orgName,  String shift) {
		
		Specification<DailyReport> idSpec = generalSpecification.longSpecification(id, ApplicationConstants.ID);
		Specification<DailyReport> dateSpec = generalSpecification.dateSpecification(startDate, endDate,ApplicationConstants.DATE);
		Specification<DailyReport> employeeNameSpec = generalSpecification.stringSpecification(employeeName,DailyAttendanceConstants.EMPLOYEE_NAME);
		Specification<DailyReport> employeeIdSpec = generalSpecification.stringSpecification(employeeId,DailyAttendanceConstants.EMPLOYEE_ID);
    	Specification<DailyReport> departmentSpec = generalSpecification.stringSpecification(department,DailyAttendanceConstants.DEPARTMENT); 
    	Specification<DailyReport>  designationSpec = generalSpecification.stringSpecification(designation,DailyAttendanceConstants.DESIGNATION);
    	Specification<DailyReport> statusSpec = generalSpecification.stringSpecification(status,DailyAttendanceConstants.ATTENDANCE_STATUS);
    	Specification<DailyReport> orgSpec = generalSpecification.stringSpecification(orgName, AreaConstants.ORGANIZATION);
    	Specification<DailyReport> shiftSpec = generalSpecification.stringSpecification(shift,DailyAttendanceConstants.SHIFT);
		
		List<DailyReport> dailyAttendanceList =dailyAttendanceRepository.findAll(idSpec.and(dateSpec).and(employeeNameSpec)
				.and(employeeIdSpec).and(departmentSpec).and(designationSpec).and(statusSpec).and(orgSpec).and(shiftSpec));
		return dailyAttendanceList;
	}
	
	public void excelGenerator(HttpServletResponse response, List<DailyReport> dailyAttendanceList)
			throws ParseException, IOException {

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
		setExcelDataCellWise(dailyAttendanceList, sheet, rowCount, cellStyle);

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
	
	private void setExcelDataCellWise(List<DailyReport> dailyAttendanceList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		for (DailyReport dailyAttendance : dailyAttendanceList) {
			if(rowCount==90000)
				break;
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getDateStr());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpId());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmployeeName());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getAttendanceStatus());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getDepartment());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getDesignation());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getGrade());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getMobile());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getShift());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getShiftInTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getShiftOutTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInTime());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpOutTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getWorkTime());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getEarlyComing())
				cell.setCellValue(dailyAttendance.getEarlyComing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getLateGoing())
				cell.setCellValue(dailyAttendance.getLateGoing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getEarlyGoing())
				cell.setCellValue(dailyAttendance.getEarlyGoing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getLateComing())
				cell.setCellValue(dailyAttendance.getLateComing());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getMissedOutPunch());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInLocation());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getEmpOutLocation())
				cell.setCellValue(dailyAttendance.getEmpOutLocation());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(dailyAttendance.getEmpInAccessType());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			if(null!=dailyAttendance.getEmpOutAccessType())
				cell.setCellValue(dailyAttendance.getEmpOutAccessType());
			else
				cell.setCellValue(ApplicationConstants.DELIMITER_EMPTY);
			cell.setCellStyle(cellStyle);

		}
	}
	
	private void setHeaderForExcel(Row row, CellStyle cellStyle) {
		int columnCount = NumberConstants.ZERO;

		Cell cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.DATE);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMPLOYEE_ID);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMPLOYEE_NAME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.ATTENDANCE_STATUS);
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
		cell.setCellValue(HeaderConstants.CONTACT_NO);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.SHIFT);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.SHIFT_IN_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.SHIFT_OUT_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMP_IN_TIME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMP_OUT_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.WORK_TIME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EARLY_COMING);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.LATE_GOING);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EARLY_GOING);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.LATE_COMING);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.MISSED_OUT_PUNCH);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMP_IN_LOCATION);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMP_OUT_LOCATION);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMP_IN_ACCESS_TYPE);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.EMP_OUT_ACCESS_TYPE);
		cell.setCellStyle(cellStyle);
	}

}
