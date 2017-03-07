package com.loadcell.fileoutput;

import com.loadcell.model.BridgeData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExcelWriter extends Thread {
	// Excel Buffer Size
	private static final int EXCEL_BUFFER_SIZE = 50000;

	// Bridge Data
	private BridgeData bridgeData;

	// Excel Elements
	private Workbook workbook;
	private Sheet sheet;
	private Row row;
	private Cell cell;
	private int rowCount = 0;

	// DateTime Control
	private static final int NANO_SECOND_UNIT = 1000000;
	private Calendar today;
	private SimpleDateFormat timeFormmater;

	// Excel File Name
	private String filePrefix;

	public ExcelWriter(String filePrefix, BridgeData bridgeData) {
		// Initialize File Name Prefix
		setFilePrefix(filePrefix);

		// Initialize Bridge Data to Record
		setBridgeData(bridgeData);

		// Initialize Excel Elements
		workbook = new SXSSFWorkbook(EXCEL_BUFFER_SIZE);
		sheet = workbook.createSheet("Bridge Data");

		// Initialize DateTime Control
		timeFormmater = new SimpleDateFormat("mm:ss.SSS");
	}

	@Override
	public void run() {
		try {
			Date standard = new Date();
			today = Calendar.getInstance();

			while (!Thread.currentThread().isInterrupted()) {
				long start = System.nanoTime();
				long delay = start + NANO_SECOND_UNIT * bridgeData.getDataRate();

				Date currentTime = new Date();
				String strTime = timeFormmater.format(currentTime.getTime() - standard.getTime());

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);

				cell.setCellValue(strTime);
				cell = row.createCell(1);
				cell.setCellValue(Double.parseDouble(String.format("%010.5f", bridgeData.peekFinalBridgeDataQueue())));

				while (System.nanoTime() < delay) {
					;
				}
			}
		} finally {
			try {
				// Store Excel File
				String fileNamePrefix = String.format("%d-%d-%d %d-%d-%d", today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DATE), today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), today.get(Calendar.SECOND));
				workbook.write(new FileOutputStream(new File(fileNamePrefix + " - Bridge " + getFilePrefix() + ".xlsx")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Bridge Data
	public BridgeData getBridgeData() {
		return bridgeData;
	}

	public void setBridgeData(BridgeData bridgeData) {
		this.bridgeData = bridgeData;
	}

	// Excel File Name
	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}
}