package inmethod.jakarta.excel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import inmethod.commons.rdb.DataSet;

import org.apache.poi.xssf.usermodel.XSSFRichTextString;

public class CreateXLSX  implements ICreateExcel {
	private java.io.OutputStream aOutput;
	private java.io.InputStream aInput;
	private int iNextRow;

	private boolean bolPrintResultSetHeader;
	private String sCurrencyFormat = "#,##0.0";
	private String sIntCurrencyFormat = "#,##0";
	private String sSpaceInsteadOf = "";

	private XSSFWorkbook workBook;
	private XSSFSheet sheet;
	private XSSFRow headerRow = null;
	private XSSFCell headerCell = null;
	private XSSFDrawing patriarch = null;
	
	private CreateXLSX() {
	}

	public CreateXLSX(OutputStream aOS) {
		aOutput = aOS;
		init();
	}

	public CreateXLSX(OutputStream aOS, InputStream aIS) {
		aOutput = aOS;
		aInput = aIS;
		init();
	}

	private synchronized int getNextRowID() {
		return ++iNextRow;
	}
	
	private  synchronized void setRowID(int iRowID) {
		iNextRow = iRowID;
	}

	private void init() {
		try {
			// create a new workbook
			if (aInput == null){
				//System.out.println("no input template");
				workBook = new XSSFWorkbook();
			}	
			else {
				workBook = new XSSFWorkbook( aInput);
			}
			bolPrintResultSetHeader = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * create a new sheet and set as current
	 */
	public void setCurrentSheet() {
		sheet = getCurrentWorkBook().createSheet();
	    patriarch = sheet.createDrawingPatriarch();
		iNextRow = -1;
	}

	/**
	 * set current sheet as specified name
	 */
	public void setCurrentSheet(String sName) {
		sheet = workBook.getSheet(sName);
		patriarch = sheet.createDrawingPatriarch();
		iNextRow = -1;
	}

	/**
	 * decide print result set column name to excel
	 * 
	 * @param bol
	 *            true: print , false: none print
	 */
	public void setPrintResultSetHeader(boolean bol) {
		bolPrintResultSetHeader = bol;
	}

	/**
	 * create merged header.
	 * 
	 * @param sHeaderMessage
	 * @param iRowBegin
	 * @param iRowEnd
	 * @param iColBegin
	 * @param iColEnd
	 */
	public void createHeader(String sHeaderMessage, short iRowBegin, short iRowEnd, short iColBegin, short iColEnd) {
		try {
			headerRow = getNextRow();
			for (short i = iRowBegin; i < iRowEnd; i++) {
				getNextRowID();
			}

			sheet.addMergedRegion(new CellRangeAddress(iRowBegin, iRowEnd, iColBegin, iColEnd));
			headerCell = headerRow.createCell((short) 0);

			headerCell.setCellValue(new XSSFRichTextString(sHeaderMessage));
			// create a style for the header cell
			XSSFCellStyle headerStyle = workBook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerCell.setCellStyle(headerStyle);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get current workbook.
	 */
	public XSSFWorkbook getCurrentWorkBook() {
		return workBook;
	}

	/**
	 * Get current sheet.
	 */
	public XSSFSheet getCurrentSheet() {
		return sheet;
	}

	/**
	 * calculate csv formate
	 * 
	 * @param aDS
	 * @param sEncode
	 * @return
	 */
	public boolean calculateCsv(DataSet aDS, String sEncode) {
		return calculateCsv(0, aDS, sEncode);
	}

	/**
	 * calculate csv formate
	 * 
	 * @param checkCells
	 * @param aDS
	 * @param sEncode
	 * @return
	 */
	public boolean calculateCsv(int checkCells, DataSet aDS, String sEncode) {
		int iCheckCells = checkCells;
		String sCheckString1 = "begin";
		String sCheckString2 = null;
		if (aDS == null)
			return false;
		Vector aTempVector = null;
		Object obj = null;
		try {
			java.io.OutputStreamWriter aOSW = new java.io.OutputStreamWriter(aOutput, sEncode);
			while (aDS.next()) {
				aTempVector = (Vector) aDS.getData();
				if (iCheckCells > 0) {
					sCheckString2 = "";
					for (int i = 0; i < iCheckCells; i++)
						sCheckString2 = sCheckString2 + aTempVector.get(i);
				}
				for (short i = 0; i < aTempVector.size(); i++) {
					if (iCheckCells > 0)
						if (sCheckString1.equals(sCheckString2))
							if ((i + 1) <= iCheckCells) {
								if (i == 0)
									aOSW.write("\"\"");
								else
									aOSW.write(",\"\"");
								continue;
							}

					if (i == 0)
						aOSW.write("\"" + aTempVector.get((int) i) + "\"");
					else
						aOSW.write(",\"" + aTempVector.get((int) i) + "\"");
				}
				sCheckString1 = sCheckString2;
				aOSW.write("\r\n");
				aOSW.flush();
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * calculate csv formate
	 * 
	 * @param aRS
	 * @param sEncode
	 * @return
	 */
	public boolean calculateCsv(ResultSet aRS, String sEncode) {
		return calculateCsv(0, aRS, sEncode);
	}

	/**
	 * calculate csv formate
	 * 
	 * @param checkCells
	 * @param aRS
	 * @param sEncode
	 * @return
	 */
	public boolean calculateCsv(int checkCells, ResultSet aRS, String sEncode) {
		int iCheckCells = checkCells;
		String sCheckString1 = "begin";
		String sCheckString2 = null;
		XSSFCell aTempCell = null;
		Object obj = null;
		String sColumnTypeName = null;
		String sColumnName = null;
		String sDataTypeName = null;
		String sData = null;
		ResultSetMetaData metaData = null;
		try {
			java.io.OutputStreamWriter aOSW = new java.io.OutputStreamWriter(aOutput, sEncode);
			metaData = aRS.getMetaData();
			if (bolPrintResultSetHeader) {

				for (short i = 0; i < metaData.getColumnCount(); i++) {
					if (i == 0)
						aOSW.write("\"" + metaData.getColumnName((int) (i + 1)) + "\"");
					else
						aOSW.write(",\"" + metaData.getColumnName((int) (i + 1)) + "\"");
				}
				aOSW.write("\r\n");
			}
			while (aRS.next()) {
				if (iCheckCells > 0) {
					sCheckString2 = "";
					for (int i = 0; i < iCheckCells; i++)
						sCheckString2 = sCheckString2 + aRS.getString(i + 1);
				}
				for (short i = 0; i < metaData.getColumnCount(); i++) {
					sDataTypeName = getDataType(metaData.getColumnType((int) (i + 1)));
					sColumnName = metaData.getColumnName((int) (i + 1));
					sData = aRS.getString(sColumnName);
					if (iCheckCells > 0)
						if (sCheckString1.equals(sCheckString2)) {
							if ((i + 1) <= iCheckCells)
								sData = "";
							else
								sData = aRS.getString(sColumnName);
						} else
							sData = aRS.getString(sColumnName);
					else
						sData = aRS.getString(sColumnName);
					if (sData == null)
						sData = "";
					if (i == 0)
						aOSW.write("\"" + sData + "\"");
					else
						aOSW.write(",\"" + sData + "\"");
				}

				sCheckString1 = sCheckString2;
				aOSW.write("\r\n");
				aOSW.flush();
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	/**
	 * build csv file
	 */
	public void buildCsv() {
	}

	/**
	 * build a new Excel files.
	 */
	public void buildExcel() {
		try {
			workBook.write(aOutput);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get next row .
	 */
	public XSSFRow getNextRow() {
		return sheet.createRow(getNextRowID());

	}

	/**
	 * write value to specify row and column
	 * 
	 * @param x
	 * @param y
	 * @param aVector
	 */
	public void setValue(short x, short y, Vector aVector) {
		XSSFRow aRow = sheet.getRow(x);
		XSSFCell aCell = null;
		if (sheet.getRow(x) == null)
			aRow = sheet.createRow(x);
		aCell = aRow.getCell(y);
		if (aCell == null)
			aCell = aRow.createCell(y);

		XSSFCellStyle cs = getCurrentWorkBook().createCellStyle();
		cs.setWrapText(true);
		aRow.setHeight((short) 0x349);
		aCell.setCellType(CellType.STRING);
		aCell.setCellValue(new XSSFRichTextString((String) aVector.get(0)));
		aCell.setCellStyle(cs);
		getCurrentSheet().setColumnWidth((short) 2, (short) (100));

	}

	/**
	 * write value to specify row and column
	 * 
	 * @param x
	 * @param y
	 * @param sValue
	 * @param autosize
	 */
	public void setValue(short x, short y, String sValue, boolean autosize) {
		if (!autosize) {
			setValue(x, y, sValue);
			return;
		}
		XSSFRow aRow = sheet.getRow(x);
		XSSFCell aCell = null;
		if (sheet.getRow(x) == null)
			aRow = sheet.createRow(x);
		aCell = aRow.getCell(y);
		if (aCell == null)
			aCell = aRow.createCell(y);
		XSSFCellStyle cs = getCurrentWorkBook().createCellStyle();
		cs.setWrapText(true);
		aRow.setHeight((short) 0x349);
		aCell.setCellType(CellType.STRING);
		aCell.setCellValue(new XSSFRichTextString(sValue));
		aCell.setCellStyle(cs);
		getCurrentSheet().setColumnWidth((short) aCell.getColumnIndex(), (short) ((sValue.trim().length()) * 256 * 4));

	}

	/**
	 * write value to specify row and column
	 * 
	 * @param x
	 * @param y
	 * @param sValue
	 */
	public void setValue(short x, short y, String sValue) {
		XSSFRow aRow = sheet.getRow(x);
		XSSFCell aCell = null;
		if (sheet.getRow(x) == null)
			aRow = sheet.createRow(x);
		aCell = aRow.getCell(y);
		if (aCell == null)
			aCell = aRow.createCell(y);
		aCell.setCellValue(new XSSFRichTextString(sValue));

	}

	/**
	 * write value to specify row and column
	 * 
	 * @param x
	 * @param y
	 * @param dValue
	 */
	public void setValue(short x, short y, Integer dValue) {
		XSSFRow aRow = sheet.getRow(x);
		XSSFCell aCell = null;
		if (sheet.getRow(x) == null)
			aRow = sheet.createRow(x);
		aCell = aRow.getCell(y);
		if (aCell == null)
			aCell = aRow.createCell(y);
		XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
		XSSFCellStyle cs = getCurrentWorkBook().createCellStyle();
		cs.setDataFormat(df.getFormat(sIntCurrencyFormat));
		aCell.setCellStyle(cs);
		aCell.setCellValue(dValue.intValue());

	}

	/**
	 * write value to specify row and column
	 * 
	 * @param x
	 * @param y
	 * @param dValue
	 */
	public void setValue(short x, short y, Double dValue) {
		XSSFRow aRow = sheet.getRow(x);
		XSSFCell aCell = null;
		if (sheet.getRow(x) == null)
			aRow = sheet.createRow(x);
		aCell = aRow.getCell(y);
		if (aCell == null)
			aCell = aRow.createCell(y);
		XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
		XSSFCellStyle cs = getCurrentWorkBook().createCellStyle();
		cs.setDataFormat(df.getFormat(sCurrencyFormat));
		aCell.setCellStyle(cs);
		aCell.setCellValue(dValue.doubleValue());

	}

	/**
	 * give a currency format , print number to some where
	 * 
	 * @param x
	 *            x location
	 * @param y
	 *            y location
	 * @param dValue
	 *            Double class
	 * @param sFormat
	 *            currency format "#,##0.0";
	 */
	public void setValue(short x, short y, Double dValue, String sFormat) {
		XSSFRow aRow = sheet.getRow(x);
		XSSFCell aCell = null;
		if (sheet.getRow(x) == null)
			aRow = sheet.createRow(x);
		aCell = aRow.getCell(y);
		if (aCell == null)
			aCell = aRow.createCell(y);
		XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
		XSSFCellStyle cs = getCurrentWorkBook().createCellStyle();
		cs.setDataFormat(df.getFormat(sFormat));
		aCell.setCellStyle(cs);
		aCell.setCellValue(dValue.doubleValue());

	}

	/**
	 * set currency format
	 * 
	 * @param sFormat
	 */
	public void setGlobalCurrencyFormat(String sFormat) {
		sCurrencyFormat = sFormat;
	}
	
    private XSSFRow getRow(int iRow){
    	XSSFRow aReturn = getCurrentSheet().getRow(iRow);
    	if( aReturn == null)
    		aReturn = getCurrentSheet().createRow(iRow);
    	setRowID(iRow);
    	return aReturn;
    }
    
	/**
	 * calculate shift excel format
	 * 
	 * @param checkCells
	 * @param iShiftRow
	 * @param aDS
	 * @return
	 */
	public boolean calculateShiftExcel(int checkCells, int iShiftRow, DataSet aDS) {
		int iCheckCells = checkCells;
		String sCheckString1 = "begin";
		String sCheckString2 = null;

		if (aDS == null)
			return false;
		Vector aTempVector = null;
		XSSFCell aTempCell = null;
		XSSFRow aTempRow = null;
		XSSFRow aTempRow2 = null;
		Object obj = null;
		int iOffSet = 0;
		int targetRowFrom = 0;
		int targetRowTo = 0;
		try {
			XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
			XSSFCellStyle CurrencyCS = getCurrentWorkBook().createCellStyle();
			CurrencyCS.setDataFormat(df.getFormat(sCurrencyFormat));

			while (aDS.next()) {
				iOffSet++;
				aTempVector = (Vector) aDS.getData();

                while(iShiftRow>getCurrentSheet().getLastRowNum() )  getCurrentSheet().createRow(iShiftRow);
                getCurrentSheet().shiftRows( iShiftRow, getCurrentSheet().getLastRowNum(),1,true,false);

				aTempRow = getRow(iShiftRow);
				

				aTempRow2 = getRow(iShiftRow + iOffSet);
				if (aTempRow2 != null) {

					for (int n = 0; n < getCurrentSheet().getNumMergedRegions(); n++) {
						CellRangeAddress region = getCurrentSheet().getMergedRegion(n);
						if ((region.getFirstRow() >= iShiftRow + iOffSet)
								&& (region.getLastRow() <= iShiftRow + iOffSet)) {
							targetRowFrom = region.getFirstRow() - iOffSet;
							targetRowTo = region.getLastRow() - iOffSet;
							region.setFirstRow(targetRowFrom);
							region.setLastRow(targetRowTo);
							getCurrentSheet().addMergedRegion(region);
						}
					}
					for (short m = aTempRow2.getFirstCellNum(); m < aTempRow2.getLastCellNum(); m++) {
						XSSFCell sourceCell = aTempRow2.getCell(m);
						XSSFCell targetCell = aTempRow.createCell(m);
						// targetCell.setEncoding(sourceCell.getEncoding());
						if (sourceCell != null) {
							targetCell.setCellStyle(sourceCell.getCellStyle());
							targetCell.setCellType(sourceCell.getCellType());
						}

					}
				}

				iShiftRow++;
				if (iCheckCells > 0) {
					sCheckString2 = "";
					for (int i = 0; i < iCheckCells; i++)
						sCheckString2 = sCheckString2 + aTempVector.get(i);
				}
				for (short i = 0; i < aTempVector.size(); i++) {
					if (aTempRow.getCell(i) == null)
						aTempCell = aTempRow.createCell(i);
					else
						aTempCell = aTempRow.getCell(i);

					obj = aTempVector.get((int) i);
					if (iCheckCells > 0)
						if (sCheckString1.equals(sCheckString2))
							if ((i + 1) <= iCheckCells) {
								aTempCell.setCellValue(new XSSFRichTextString(""));
								continue;
							}

					if (obj instanceof Double) {
						aTempCell.setCellStyle(CurrencyCS);
						aTempCell.setCellValue(((Double) obj).doubleValue());
					}

					if (obj instanceof Integer) {
						aTempCell.setCellStyle(CurrencyCS);
						aTempCell.setCellValue(((Integer) obj).intValue());
					}

					if (obj instanceof String) {
						aTempCell.setCellValue(new XSSFRichTextString((String) obj));
					}
					if (obj instanceof Short) {
						aTempCell.setCellType(CellType.NUMERIC);
						aTempCell.setCellStyle(CurrencyCS);
						aTempCell.setCellValue(((Short) obj).shortValue());
					}
				}
				sCheckString1 = sCheckString2;

			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * buildExcel data sheet.
	 * 
	 * @param aRS
	 *            sql resultset
	 */
	public boolean calculateShiftExcel(int checkCells, int iShiftRow, ResultSet aRS) {
		int iCheckCells = checkCells;
		String sCheckString1 = "begin";
		String sCheckString2 = null;
		if (aRS == null)
			return false;
		XSSFCell aTempCell = null;
		XSSFRow aTempRow = null;
		XSSFRow aTempRow2 = null;
		Object obj = null;
		String sColumnTypeName = null;
		String sColumnName = null;
		String sDataTypeName = null;
		String sData = null;
		ResultSetMetaData metaData = null;
		int iOffSet = 0;
		int targetRowFrom = 0;
		int targetRowTo = 0;
		try {
			metaData = aRS.getMetaData();
			if (bolPrintResultSetHeader) {
				aTempRow = getNextRow();
				for (short i = 0; i < metaData.getColumnCount(); i++) {
					aTempCell = aTempRow.createCell(i);
					aTempCell.setCellValue(new XSSFRichTextString(metaData.getColumnName((int) (i + 1))));
				}
			}
			XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
			XSSFCellStyle CurrencyCS = getCurrentWorkBook().createCellStyle();
			CurrencyCS.setDataFormat(df.getFormat(sCurrencyFormat));

			while (aRS.next()) {
				if (iCheckCells > 0) {
					sCheckString2 = "";
					for (int i = 0; i < iCheckCells; i++)
						sCheckString2 = sCheckString2 + aRS.getString(i + 1);
				}

				iOffSet++;
				while (iShiftRow > getCurrentSheet().getLastRowNum())
					getCurrentSheet().createRow(iShiftRow);
				getCurrentSheet().shiftRows(iShiftRow, getCurrentSheet().getLastRowNum(), 1, true, false);
				aTempRow = getRow(iShiftRow);
				
				aTempRow2 = getRow(iShiftRow + iOffSet);
				if (aTempRow2 != null) {
					for (int n = 0; n < getCurrentSheet().getNumMergedRegions(); n++) {
						CellRangeAddress region = getCurrentSheet().getMergedRegion(n);
						if ((region.getFirstRow() >= iShiftRow + iOffSet)
								&& (region.getLastRow() <= iShiftRow + iOffSet)) {
							targetRowFrom = region.getFirstRow() - iOffSet;
							targetRowTo = region.getLastRow() - iOffSet;
							region.setFirstRow(targetRowFrom);
							region.setLastRow(targetRowTo);
							getCurrentSheet().addMergedRegion(region);
						}
					}
					for (short m = aTempRow2.getFirstCellNum(); m < aTempRow2.getLastCellNum(); m++) {
						XSSFCell sourceCell = aTempRow2.getCell(m);
						XSSFCell targetCell = aTempRow.createCell(m);

						// targetCell.setEncoding(sourceCell.getEncoding());
						if (sourceCell != null) {
							targetCell.setCellStyle(sourceCell.getCellStyle());
							targetCell.setCellType(sourceCell.getCellType());
						}

					}
				}
				iShiftRow++;

				for (short i = 0; i < metaData.getColumnCount(); i++) {
					if (aTempRow.getCell(i) == null)
						aTempCell = aTempRow.createCell(i);
					else
						aTempCell = aTempRow.getCell(i);
					sDataTypeName = getDataType(metaData.getColumnType((int) (i + 1)));
					sColumnName = metaData.getColumnName((int) (i + 1));
					if (iCheckCells > 0)
						if (sCheckString1.equals(sCheckString2)) {
							if ((i + 1) <= iCheckCells)
								sData = "";
							else
								sData = aRS.getString(sColumnName);
						} else
							sData = aRS.getString(sColumnName);
					else
						sData = aRS.getString(sColumnName);

					if (sDataTypeName.equalsIgnoreCase("String") || sDataTypeName.equalsIgnoreCase("Date")
							|| sDataTypeName.equalsIgnoreCase("object")) {
						aTempCell.setCellType(CellType.STRING);
						aTempCell.setCellValue(new XSSFRichTextString(sData));

					}
					if (sDataTypeName.equalsIgnoreCase("Integer")) {
						if (sData != null && !sData.trim().equals("")) {
							aTempCell.setCellStyle(CurrencyCS);
							aTempCell.setCellValue((new Integer(sData)).intValue());
						}

					}

					if (sDataTypeName.equalsIgnoreCase("Double") || sDataTypeName.equalsIgnoreCase("Float")
							|| sDataTypeName.equalsIgnoreCase("Long")) {
						if (sData != null && !sData.trim().equals("")) {
							aTempCell.setCellStyle(CurrencyCS);
							aTempCell.setCellValue((new Double(sData)).doubleValue());
						}

					}

				}

				sCheckString1 = sCheckString2;

			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	/**
	 * buildExcel data sheet.
	 * 
	 * @param aRS
	 *            sql resultset
	 */
	public boolean calculateExcel(ResultSet aRS) {
		return calculateExcel(0, aRS);
	}

	/**
	 * last row number
	 * 
	 * @return
	 */
	public int getLastRowNum() {
		return getCurrentSheet().getLastRowNum();
	}

	/**
	 * calcute data sheet.
	 * 
	 * @param aRS
	 *            sql resultset
	 */
	public boolean calculateExcel(int checkCells, ResultSet aRS) {
		int iCheckCells = checkCells;
		String sCheckString1 = "begin";
		String sCheckString2 = null;
		if (aRS == null)
			return false;
		XSSFCell aTempCell = null;
		XSSFRow aTempRow = null;
		Object obj = null;
		String sColumnTypeName = null;
		String sColumnName = null;
		String sDataTypeName = null;
		String sData = null;
		ResultSetMetaData metaData = null;
		int iCounter = 0;
		try {
			metaData = aRS.getMetaData();
			if (bolPrintResultSetHeader) {

				aTempRow = getNextRow();
				for (short i = 0; i < metaData.getColumnCount(); i++) {
					aTempCell = aTempRow.createCell(i);
					aTempCell.setCellValue(new XSSFRichTextString(metaData.getColumnName((int) (i + 1))));
				}
			}
			XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
			XSSFCellStyle CurrencyCS = getCurrentWorkBook().createCellStyle();
			CurrencyCS.setDataFormat(df.getFormat(sCurrencyFormat));

			while (aRS.next()) {
				iCounter++;
				if (iCheckCells > 0) {
					sCheckString2 = "";
					for (int i = 0; i < iCheckCells; i++)
						sCheckString2 = sCheckString2 + aRS.getString(i + 1);
				}

				aTempRow = getNextRow();
				for (short i = 0; i < metaData.getColumnCount(); i++) {
					aTempCell = aTempRow.createCell(i);
					sDataTypeName = getDataType(metaData.getColumnType((int) (i + 1)));
					sColumnName = metaData.getColumnName((int) (i + 1));
					if (iCheckCells > 0)
						if (sCheckString1.equals(sCheckString2)) {
							if ((i + 1) <= iCheckCells)
								sData = "";
							else
								sData = aRS.getString(sColumnName);
						} else
							sData = aRS.getString(sColumnName);
					else
						sData = aRS.getString(sColumnName);

					if (sDataTypeName.equalsIgnoreCase("String") || sDataTypeName.equalsIgnoreCase("Date")
							|| sDataTypeName.equalsIgnoreCase("object")) {
						aTempCell.setCellType(XSSFCell.CELL_TYPE_STRING);
						aTempCell.setCellValue(new XSSFRichTextString(sData));

					}
					if (sDataTypeName.equalsIgnoreCase("Integer")) {
						if (sData != null && !sData.trim().equals("")) {
							aTempCell.setCellStyle(CurrencyCS);
							aTempCell.setCellValue((new Double(sData)).doubleValue());
						}

					}

					if (sDataTypeName.equalsIgnoreCase("Double") || sDataTypeName.equalsIgnoreCase("Float")
							|| sDataTypeName.equalsIgnoreCase("Long")) {
						if (sData != null && !sData.trim().equals("")) {
							aTempCell.setCellStyle(CurrencyCS);
							aTempCell.setCellValue((new Double(sData)).doubleValue());
						}

					}
				}

				sCheckString1 = sCheckString2;

			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * create picture (jpeg)
	 * 
	 * @param aIS
	 * @param x1
	 *            col number 1..n
	 * @param y1
	 *            row number 1..n
	 * @param x2
	 *            col number 1..n
	 * @param y2
	 *            row number 1..n
	 * @return
	 */
	public boolean createPic(InputStream aIS, int x1, int y1, int x2, int y2) {
		try {
			XSSFClientAnchor anchor;
			anchor = new XSSFClientAnchor(0,0,0,0,(short)x1,y1,(short)x2,y2);
			anchor.setAnchorType( ClientAnchor.AnchorType.MOVE_DONT_RESIZE );
			XSSFPicture picture = patriarch.createPicture(anchor, loadPicture( aIS, getCurrentWorkBook()));
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * create picture (jpeg)
	 * 
	 * @param sPicLocation
	 * @param x1
	 *            col number 1..n
	 * @param y1
	 *            row number 1..n
	 * @param x2
	 *            col number 1..n
	 * @param y2
	 *            row number 1..n
	 * @return
	 */
	public boolean createPic(String sPicLocation, int x1, int y1, int x2, int y2) {
		try {

			 XSSFClientAnchor anchor;
			 anchor = new XSSFClientAnchor(0,0,0,0,(short)x1,y1,(short)x2,y2);
			 anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
			 XSSFPicture picture = patriarch.createPicture(anchor,
			 loadPicture( sPicLocation, getCurrentWorkBook()));
			 picture.setNoFill(false);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private int loadPicture(String path, XSSFWorkbook wb) throws IOException {
		InputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return loadPicture(fis, wb);
	}

	private int loadPicture(InputStream aIS, XSSFWorkbook wb) throws IOException {
		int pictureIndex;
		InputStream fis = null;
		ByteArrayOutputStream bos = null;
		try {
			fis = aIS;
			bos = new ByteArrayOutputStream();
			int c;
			while ((c = fis.read()) != -1)
				bos.write(c);
			pictureIndex = wb.addPicture(bos.toByteArray(), XSSFWorkbook.PICTURE_TYPE_JPEG);
		} finally {
			if (fis != null)
				fis.close();
			if (bos != null)
				bos.close();
		}
		return pictureIndex;
	}

	/**
	 * buildExcel data sheet.
	 * 
	 * @param aDS
	 *            com.fromtw.commons.rdb.DataSet , aDS must include Object of
	 *            Vector and Vector must include Double , String and Short
	 *            object.
	 */
	public boolean calculateExcel(DataSet aDS) {
		return calculateExcel(0, aDS);
	}

	public boolean calculateExcel(int checkCells, DataSet aDS) {
		int iCheckCells = checkCells;
		String sCheckString1 = "begin";
		String sCheckString2 = null;

		if (aDS == null)
			return false;
		Vector aTempVector = null;
		XSSFCell aTempCell = null;
		XSSFRow aTempRow = null;
		Object obj = null;
		try {
			XSSFDataFormat df = getCurrentWorkBook().createDataFormat();
			XSSFCellStyle CurrencyCS = getCurrentWorkBook().createCellStyle();
			CurrencyCS.setDataFormat(df.getFormat(sCurrencyFormat));

			while (aDS.next()) {
				aTempVector = (Vector) aDS.getData();
				aTempRow = getNextRow();
				if (iCheckCells > 0) {
					sCheckString2 = "";
					for (int i = 0; i < iCheckCells; i++)
						sCheckString2 = sCheckString2 + aTempVector.get(i);
				}
				for (short i = 0; i < aTempVector.size(); i++) {
					aTempCell = aTempRow.createCell(i);
					obj = aTempVector.get((int) i);
					if (iCheckCells > 0)
						if (sCheckString1.equals(sCheckString2))
							if ((i + 1) <= iCheckCells) {
								aTempCell.setCellValue(new XSSFRichTextString(""));
								continue;
							}

					if (obj instanceof Double) {
						aTempCell.setCellStyle(CurrencyCS);
						aTempCell.setCellValue(((Double) obj).doubleValue());
					}

					if (obj instanceof Integer) {
						aTempCell.setCellStyle(CurrencyCS);
						aTempCell.setCellValue(((Integer) obj).intValue());
					}

					if (obj instanceof String) {
						// aTempCell.setEncoding(HSSFCell.ENCODING_UTF_16 );
						aTempCell.setCellValue(new XSSFRichTextString((String) obj));
					}
					if (obj instanceof Short) {
						aTempCell.setCellType(CellType.NUMERIC);
						aTempCell.setCellStyle(CurrencyCS);
						aTempCell.setCellValue(((Short) obj).shortValue());
					}
				}
				sCheckString1 = sCheckString2;

			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * convert iSqlType to String
	 * 
	 * @param iSqlType
	 * @return String
	 */
	private String getDataType(int iSqlType) {
		String strObjectType = null;
		switch (iSqlType) {
		// suppose bigint, integer , tinyint to be Integer
		case java.sql.Types.BIGINT:
			strObjectType = "Long";
			break;
		case java.sql.Types.INTEGER:
		case java.sql.Types.TINYINT:
			strObjectType = "Integer";
			break;
		// float
		case java.sql.Types.FLOAT:
			strObjectType = "Float";
			break;
		// double, decimal convert to Double
		case java.sql.Types.DOUBLE:
		case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
			strObjectType = "Double";
			break;
		// char,varbinary,date,varchar to String
		case java.sql.Types.CHAR:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.VARCHAR:
			strObjectType = "String";
			break;
		case java.sql.Types.DATE:
			strObjectType = "Date";
			break;
		case java.sql.Types.TIMESTAMP:
			strObjectType = "String";
			break;
		default:
			strObjectType = "Object";
			break;
		}
		return strObjectType;
	}

}