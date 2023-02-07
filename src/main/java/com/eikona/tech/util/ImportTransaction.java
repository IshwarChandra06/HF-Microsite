package com.eikona.tech.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eikona.tech.entity.Transaction;
import com.eikona.tech.repository.TransactionRepository;

@Component
public class ImportTransaction {
	
	@Autowired
	private TransactionRepository transactionRepository;

	// excel file of Time log
	@SuppressWarnings("deprecation")
	public static List<Transaction> parseExcelFileTimelog(InputStream is) throws Exception {
		try {
			SimpleDateFormat inputFormat24 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm:ss");
			Workbook workbook = new XSSFWorkbook(is);

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();

			List<Transaction> transactions = new ArrayList<Transaction>();

			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber == 0) {
					rowNumber++;
					continue;
				}

				Iterator<Cell> cellsInRow = currentRow.iterator();

				Transaction trans = new Transaction();
				Date transactionDate = new Date();
				while (cellsInRow.hasNext()) {
					
					Cell currentCell = cellsInRow.next();
					int cellIndex = currentCell.getColumnIndex();
					if (cellIndex == 0) {
						trans.setLogId((int) currentCell.getNumericCellValue());
					} else if (cellIndex == 1) {
						currentCell.setCellType(CellType.STRING);
						if (currentCell.getCellType() == CellType.NUMERIC) {
							trans.setEmployeeCode(String.valueOf(currentCell.getNumericCellValue()));
						} else if (currentCell.getCellType() == CellType.STRING) {
							trans.setEmployeeCode(currentCell.getStringCellValue());
						}
					} else if (cellIndex == 2) {
						currentCell.setCellType(CellType.STRING);
						if (currentCell.getCellType() == CellType.NUMERIC) {
							trans.setEmpId(String.valueOf(currentCell.getNumericCellValue()));
						} else if (currentCell.getCellType() == CellType.STRING) {
							trans.setEmpId(currentCell.getStringCellValue());
						}
					} else if (cellIndex == 3) {
						currentCell.setCellType(CellType.STRING);
						if (currentCell.getCellType() == CellType.NUMERIC) {
							trans.setName(String.valueOf(currentCell.getNumericCellValue()));
						} else if (currentCell.getCellType() == CellType.STRING) {
							trans.setName(currentCell.getStringCellValue());
						}
					} else if (cellIndex == 4) {
						if (currentCell.getCellType() == CellType.NUMERIC) {
							trans.setDeviceId(currentCell.getNumericCellValue());
						} else if (currentCell.getCellType() == CellType.STRING) {
							trans.setDeviceId(Double.parseDouble(currentCell.getStringCellValue()));
						}
					} else if (cellIndex == 5) {
						if (currentCell.getCellType() == CellType.STRING) {
							String transactionDateStr = currentCell.getStringCellValue().trim();
							System.out.println(transactionDateStr);
							if(transactionDateStr.contains("AM") || transactionDateStr.contains("PM")) {
								transactionDate = inputFormat.parse(transactionDateStr);
							}else {
								transactionDate = inputFormat24.parse(transactionDateStr);
							}
						} else {
							transactionDate=(Date) currentCell.getDateCellValue();
						}
						
						
						trans.setPunchTimeStr(timeformat.format(transactionDate));
						trans.setPunchTime(timeformat.parse(trans.getPunchTimeStr()));
						trans.setPunchDate(transactionDate);
						trans.setPunchDateStr(dateFormat.format(transactionDate));
					}else if (cellIndex == 6) {
						trans.setDeviceName(currentCell.getStringCellValue());
						
						if(trans.getDeviceName().contains("HYD") || trans.getDeviceName().contains("MUM") ||  trans.getDeviceName().contains("NOIDA")) {
							trans.setOrganization("Tata Projects Limited");
						}else if(trans.getDeviceName().contains("NMDC")) {
							trans.setOrganization("NMDC Nagarnar");
						}else{
							trans.setOrganization("ESL Bokaro");
						}
					}
					trans.setAccessType("Face");
				}
				transactions.add(trans);
			}

			// Close WorkBook
			workbook.close();
			return transactions;
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}

	// excel file of temperature
	public List<Transaction> parseExcelFileTemp(InputStream is) throws Exception {
		try {
			DateFormat dateFormat24 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			DateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat databaseTimeFormat=new SimpleDateFormat("HH:mm:ss");
			Workbook workbook = new XSSFWorkbook(is);

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();

			List<Transaction> transaction = new ArrayList<Transaction>();
			
			String currDate = "";
			
			List<Transaction> tempTransactionList = new ArrayList<Transaction>();
			
			List<String> orgList = new ArrayList<String>();
			
			orgList.add("ESL Bokaro");
			orgList.add("Tata Projects Limited");
			
			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber <= 20) {
					rowNumber++;
					continue;
				}

				Iterator<Cell> cellsInRow = currentRow.iterator();
				Date transactionDate = null;
				int cellIndex = 0;
				Transaction trans = null;
				
				while (cellsInRow.hasNext()) {
					Cell currentCell = cellsInRow.next();

					if (cellIndex == 0) {
						//System.out.println("Date : " + currentCell.getStringCellValue());
						String transactionDateStr = currentCell.getStringCellValue().trim();
						if(transactionDateStr.contains("AM") || transactionDateStr.contains("PM")) {
							transactionDate = dateFormat.parse(transactionDateStr);
						}else {
							System.out.println(cellsInRow.next().getStringCellValue());
							transactionDate = dateFormat24.parse(transactionDateStr);
						}
					} else if (cellIndex == 9) {
						String empCode = "";
						if (currentCell.getCellType() == CellType.NUMERIC) {
							Double empCodeLong = 	currentCell.getNumericCellValue();
							empCode = empCodeLong.toString();
						}else if (currentCell.getCellType() == CellType.STRING) {
							empCode =currentCell.getStringCellValue();
						}
						
						if(!currDate.equalsIgnoreCase(databaseDateFormat.format(transactionDate))){
								currDate = databaseDateFormat.format(transactionDate);
								tempTransactionList = transactionRepository.findByPunchDateStrAndOrganizationIn(currDate,orgList);
						}
						
						
						for (Transaction tempTransaction : tempTransactionList) {
							if(
									null!= tempTransaction.getEmployeeCode() &&
									tempTransaction.getEmployeeCode().equalsIgnoreCase(empCode) &&
									tempTransaction.getPunchDateStr().equalsIgnoreCase(databaseDateFormat.format(transactionDate)) &&
									tempTransaction.getPunchTimeStr().equalsIgnoreCase(databaseTimeFormat.format(transactionDate)) 
							) {
								trans = tempTransaction;
								break;
							}
						}
						
						tempTransactionList.remove(trans);
						
					} else if (cellIndex == 16) {
						if (null == trans) {
							break;
						}
						String str=currentCell.getStringCellValue();
						trans.setDepartment(str);
					} else if (cellIndex == 19) {
						trans.setTemperature(currentCell.getStringCellValue());
					} else if (cellIndex == 22) {
						trans.setWearingMask("With Mask".equalsIgnoreCase(currentCell.getStringCellValue().trim()));

					}
					cellIndex++;
				}
				if(null!=trans) {
					transaction.add(trans);
				}
			}
			workbook.close();
			return transaction;

		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}
	
	public static List<Transaction> parseExcelFileBioSecurity(InputStream is) throws Exception {
		try {

			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
			Workbook workbook = new HSSFWorkbook(is);
			Sheet sheet = workbook.getSheetAt(0);

			Iterator<Row> rows = sheet.iterator();

			List<Transaction> transaction = new ArrayList<Transaction>();

			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber <= 1) {
					rowNumber++;
					continue;
				}

				Iterator<Cell> cellsInRow = currentRow.iterator();
				Date transactionDate = new Date();
				int cellIndex = 0;
				Transaction trans = new Transaction();
				while (cellsInRow.hasNext()) {
					Cell currentCell = cellsInRow.next();

					if (null == trans) {
						break;
					}

					else if (cellIndex == 1) {
						if(currentCell.getCellType()==CellType.STRING) {
						String str = currentCell.getStringCellValue();
						transactionDate = inputFormat.parse(str);
						}else {
							transactionDate = currentCell.getDateCellValue();
						}
						trans.setPunchDate(transactionDate);
						trans.setPunchDateStr(dateFormat.format(transactionDate));
						trans.setPunchTimeStr(timeformat.format(transactionDate));
						trans.setPunchTime(timeformat.parse(trans.getPunchTimeStr()));
						// System.out.println("Date : " + currentCell.getStringCellValue());
					}else if (cellIndex == 3) {
						trans.setDeviceName(currentCell.getStringCellValue());
						System.out.println("Device Name : " + currentCell.getStringCellValue());

					} else if (cellIndex == 5) {
						if(currentCell.getCellType()==CellType.STRING) {
							if ("0".equalsIgnoreCase(currentCell.getStringCellValue()))
								continue;
							trans.setEmpId(currentCell.getStringCellValue());
						}else {
							trans.setEmpId(String.valueOf(currentCell.getNumericCellValue()));
						}
						//System.out.println("Employee Id : " + currentCell.getStringCellValue());

					} else if (cellIndex == 6) {
						trans.setName(currentCell.getStringCellValue());
						System.out.println("Name : " + currentCell.getStringCellValue());

					} else if (cellIndex == 8) {
						trans.setDepartment(currentCell.getStringCellValue());
						System.out.println("Department : " + currentCell.getStringCellValue());

					} else if (cellIndex == 9) {
						trans.setWearingMask("yes".equalsIgnoreCase(currentCell.getStringCellValue()));

						System.out.println("WearingMask : " + currentCell.getStringCellValue());

					} else if (cellIndex == 10) {
						if(currentCell.getCellType()==CellType.STRING) {
						trans.setTemperature(currentCell.getStringCellValue());
						System.out.println("Temperature : " + currentCell.getStringCellValue());
						}else {
							trans.setTemperature(String.valueOf(currentCell.getNumericCellValue()));
						}
					}
					trans.setAccessType("Face");
					trans.setOrganization("Tata Electronics Pilot Plant");
					cellIndex++;
				}
				if (null != trans.getEmpId() && !trans.getEmpId().isEmpty())
					transaction.add(trans);
			}

			// Close WorkBook
			workbook.close();

			return transaction;
		} catch (IOException e) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}
	
	// import Employee Details
		public static List<Transaction> parseExcelFileEmployeeDetails(InputStream is) throws ParseException {
			try {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Workbook workbook = new XSSFWorkbook(is);

				Sheet sheet = workbook.getSheet("New Employee Details");
				Iterator<Row> rows = sheet.iterator();

				List<Transaction> transaction = new ArrayList<Transaction>();

				int rowNumber = 0;
				while (rows.hasNext()) {
					Row currentRow = rows.next();

					// skip header
					if (rowNumber == 0) {
						rowNumber++;
						continue;
					}

					Iterator<Cell> cellsInRow = currentRow.iterator();

					Transaction trans = new Transaction();
					// Date transactionDate = null;
					int cellIndex = 0;
					while (cellsInRow.hasNext()) {
						Cell currentCell = cellsInRow.next();

						if (cellIndex == 0) {
							if (currentCell.getCellType() == CellType.NUMERIC) {
								trans.setEmployeeCode(String.valueOf(currentCell.getNumericCellValue()));
							} else if (currentCell.getCellType() == CellType.STRING) {
								trans.setEmployeeCode(currentCell.getStringCellValue());
							}
						} else if (cellIndex == 1) {
							if (currentCell.getCellType() == CellType.NUMERIC) {
								trans.setEmpId(String.valueOf(currentCell.getNumericCellValue()));
							} else if (currentCell.getCellType() == CellType.STRING) {
								trans.setEmpId(currentCell.getStringCellValue());
							}
						} else if (cellIndex == 2) {
							trans.setName(currentCell.getStringCellValue());
						}else if (cellIndex == 4) {
							trans.setAccessType(currentCell.getStringCellValue());
						} else if (cellIndex == 5) {
							trans.setDeviceId(currentCell.getNumericCellValue());
						} else if (cellIndex == 6) {
							if (currentCell.getCellType() == CellType.NUMERIC) {
								trans.setDeviceName(String.valueOf(currentCell.getNumericCellValue()));
							} else if (currentCell.getCellType() == CellType.STRING) {
								trans.setDeviceName(currentCell.getStringCellValue());
							}
						} else if (cellIndex == 7) {
							trans.setPunchTime(df.parse(currentCell.getStringCellValue()));
						} else if (cellIndex == 8) {
							trans.setPunchDate(df.parse(currentCell.getStringCellValue()));
						} else if (cellIndex == 9) {
							trans.setPunchTimeStr(currentCell.getStringCellValue());
						} else if (cellIndex == 10) {
							trans.setPunchDateStr(currentCell.getStringCellValue());
						} else if (cellIndex == 11) {
							trans.setSerialNo(currentCell.getStringCellValue());
						} else if (cellIndex == 12) {
							trans.setDeviceType(currentCell.getStringCellValue());
						} else if (cellIndex == 13) {
							trans.setDepartment(currentCell.getStringCellValue());
						} else if (cellIndex == 14) {
							trans.setTemperature(currentCell.getStringCellValue());
						} 
						cellIndex++;
					}

					transaction.add(trans);
				}

				// Close WorkBook
				workbook.close();
				
				return transaction;
			} catch (IOException e) {
				throw new RuntimeException("FAIL! -> message = " + e.getMessage());
			}
		}

		public static List<Transaction> parseExcelFileEasyTimePro(InputStream is) throws Exception {
			try {

				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Workbook workbook = new XSSFWorkbook(is);
				Sheet sheet = workbook.getSheetAt(0);

				/*
				 * FileInputStream file = new FileInputStream (new
				 * File("Temperature Raw Record_20210106182748")); XSSFWorkbook workbook = new
				 * XSSFWorkbook(file); XSSFSheet sheet = workbook.getSheetAt(0);
				 */
				Iterator<Row> rows = sheet.iterator();

				List<Transaction> transaction = new ArrayList<Transaction>();
				int rowNumber = 0;
				while (rows.hasNext()) {
					Row currentRow = rows.next();

					// skip header
					if (rowNumber <= 3) {
						rowNumber++;
						continue;
					}

					Iterator<Cell> cellsInRow = currentRow.iterator();
					
					String punchDateTimeConcat = "";
					
					int cellIndex = 0;
					Transaction trans = new Transaction();
					while (cellsInRow.hasNext()) {
						
						//transactionDate = new Date();
						Cell currentCell = cellsInRow.next();

						if (null == trans) {
							break;
						} else if (cellIndex == 0) {
							if ("0".equalsIgnoreCase(currentCell.getStringCellValue()))
								continue;

							trans.setEmpId(currentCell.getStringCellValue());
							System.out.println("Employee Id : " + currentCell.getStringCellValue());

						} else if (cellIndex == 1) {
							trans.setName(currentCell.getStringCellValue());
							System.out.println("Name : " + currentCell.getStringCellValue());
						}
						else if (cellIndex == 3) {
							 trans.setDepartment(currentCell.getStringCellValue());
								System.out.println("Department : " + currentCell.getStringCellValue());
						}else if (cellIndex == 5) {
							 String str=currentCell.getStringCellValue();
							 trans.setPunchDateStr(str);
							 punchDateTimeConcat = punchDateTimeConcat + str;
						}else if (cellIndex == 6) {
							 String str=currentCell.getStringCellValue();
							 trans.setPunchTimeStr(str);
							 punchDateTimeConcat = punchDateTimeConcat +" "+ str;
						}
						else if (cellIndex == 8) {
							trans.setAccessType(currentCell.getStringCellValue());
							System.out.println("Access Type : " + currentCell.getStringCellValue());

						} else if (cellIndex == 10) {
							trans.setSerialNo(currentCell.getStringCellValue());
							System.out.println("Serial No : " + currentCell.getStringCellValue());

						} else if (cellIndex == 11) {
							trans.setDeviceName(currentCell.getStringCellValue());
							System.out.println("Device Name : " + currentCell.getStringCellValue());

						} 
						 else if (cellIndex == 12) {
							 Date transactionDate = inputFormat.parse(punchDateTimeConcat);
							 trans.setPunchDate(transactionDate);
							 trans.setPunchTime(transactionDate);
							 }
						else if (cellIndex == 13) {
							trans.setTemperature(currentCell.getStringCellValue());
							System.out.println("Temperature : " + currentCell.getStringCellValue());
						}
						else if (cellIndex == 14) {
							trans.setWearingMask("yes".equalsIgnoreCase(currentCell.getStringCellValue()));

							System.out.println("WearingMask : " + currentCell.getStringCellValue());

						}
						trans.setOrganization("SBG3-UI");
						cellIndex++;
					}
					if (null != trans.getEmpId() && !trans.getEmpId().isEmpty())
						transaction.add(trans);
				}

				// Close WorkBook
				workbook.close();

				return transaction;
			} catch (IOException e) {
				throw new RuntimeException("FAIL! -> message = " + e.getMessage());
			}
		}
}
