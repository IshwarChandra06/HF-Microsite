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
import com.eikona.tech.constants.TransactionConstants;
import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.TransactionRepository;

@Component
public class ExportUnenrolledEmployee {
	
	@Autowired
	private GeneralSpecificationUtil<Transaction> generalSpecification;
	
	@Autowired
	private TransactionRepository transactionRepository;

	public void fileExportBySearchValue(HttpServletResponse response, String flag, String orgName) throws ParseException, IOException {
		
		List<Transaction> transactionList = getListOfUnenrolledTransaction(orgName,"Unenrolled");
		
		excelGenerator(response, transactionList);
		
	}

	private List<Transaction> getListOfUnenrolledTransaction(String orgName, String enrollStatus) {
    	Specification<Transaction> orgSpec = generalSpecification.stringSpecification(orgName,AreaConstants.ORGANIZATION);
    	Specification<Transaction> enrollStatusSpec = generalSpecification.stringSpecification(enrollStatus,TransactionConstants.ENROLL_STATUS);
		
    	List<Transaction> transactionList =transactionRepository.findAll(orgSpec.and(enrollStatusSpec));
		return transactionList;
	}
	
	private void excelGenerator(HttpServletResponse response, List<Transaction> transactionList) throws ParseException, IOException {

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
		setExcelDataCellWise(transactionList, sheet, rowCount, cellStyle);

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
	
	private void setExcelDataCellWise(List<Transaction> transactionList, Sheet sheet, int rowCount,
			CellStyle cellStyle) {
		for (Transaction transaction : transactionList) {
			if(rowCount==90000)
				break;
			Row row = sheet.createRow(rowCount++);

			int columnCount = NumberConstants.ZERO;

			Cell cell = row.createCell(columnCount++);
			cell.setCellValue(transaction.getPunchDateStr());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(transaction.getPunchTimeStr());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(transaction.getName());
			cell.setCellStyle(cellStyle);

			cell = row.createCell(columnCount++);
			cell.setCellValue(transaction.getEmployeeCode());
			cell.setCellStyle(cellStyle);
			
			cell = row.createCell(columnCount++);
			cell.setCellValue(transaction.getDeviceName());
			cell.setCellStyle(cellStyle);

		}
	}
	
	private void setHeaderForExcel(Row row, CellStyle cellStyle) {
		int columnCount = NumberConstants.ZERO;
		
		Cell cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.DATE);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.TIME);
		cell.setCellStyle(cellStyle);
		
		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.NAME);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.DEVICE_EMP_ID);
		cell.setCellStyle(cellStyle);

		cell = row.createCell(columnCount++);
		cell.setCellValue(HeaderConstants.DEVICE);
		cell.setCellStyle(cellStyle);

	}

}
