package inmethod.jakarta.excel.test;

import java.util.*;
import java.sql.*;
import java.io.*;

import inmethod.jakarta.excel.*;
import inmethod.commons.rdb.*;
import inmethod.commons.util.FileTool;

import org.apache.poi.hssf.model.*;
import org.apache.poi.hssf.dev.*;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.*;

/**
 * create opt directory in the same drive root directory ( for example: d:/opt )
 * and copy test files in directory "test_files" to "opt" directory.
 * 
 * @author william chen
 *
 */
public class testExcel {

	public static void main(String a[]) {
		//測試產出excel
		new testExcel().testCreateExcel("testShiftExcel.xls");
		new testExcel().testCreateExcel("testShiftExcel.xlsx");
		
		//測試讀取excel
		new testExcel().testReadExcel("testShiftExcel.xls");
		new testExcel().testReadExcel("testShiftExcel.xlsx");
	}

	/**
	 * 使用 excel 模板, 填入資料後, 再產出新的 excel 文件. excel模板文件請放在 /opt 目錄下
	 * 
	 * @param sFileName
	 */
	private void testCreateExcel(String sFileName) {
		ICreateExcel aFE;
		FileOutputStream aFO = null;
		FileInputStream aFI = null;
		DataSet aDS = null;
		Vector aDataCell = null;
		String sFileExtName = FileTool.getFileExtension(sFileName);
		try {

			aFI = new FileInputStream(
					System.getProperty("file.separator") + "opt" + System.getProperty("file.separator") + sFileName);
			aFO = new FileOutputStream(System.getProperty("file.separator") + "opt"
					+ System.getProperty("file.separator") + sFileName + "-" + Calendar.getInstance().get(Calendar.YEAR)
					+ "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + sFileExtName);

			
			if (sFileExtName.equalsIgnoreCase("xls"))
				aFE = new CreateXLS(aFO, aFI);
			else if (sFileExtName.equalsIgnoreCase("xlsx"))
				aFE = new CreateXLSX(aFO, aFI);
			else {
				System.out.println("create excel failed!");
				return;
			}

			aFE.setCurrentSheet("Stock"); // sheet名稱(測試excel有一頁名稱叫做"Stock"
			aFE.createHeader("test Shift Excel", (short) 0, (short) 1, (short) 0, (short) 8);

			aDS = new DataSet();

			// 第一行
			aDataCell = new Vector();
			aDataCell.add("asdf");
			aDataCell.add("asdf");
			aDataCell.add("row 1 , cell 3");
			aDataCell.add(new Integer(1));
			aDataCell.add(new String("1"));
			aDS.addData(aDataCell);

			// 第二行
			aDataCell = new Vector();
			aDataCell.add("asdf");
			aDataCell.add("asdf");
			aDataCell.add("row 2 , cell 3");
			aDS.addData(aDataCell);

			// 從第4行開始, 插入aDS資料,若ds資料前面有2個cell相同, 則接下相同的不顯示 , 從第四row開始新增
			aFE.calculateShiftExcel(2, 10, aDS);

			// 圖片測試
			// aFE.createPic("/opt/testExcel.jpg", 4,4, 5, 5);
			aFE.createPic(new FileInputStream(System.getProperty("file.separator") + "opt" + System.getProperty("file.separator")+"testExcel.jpg"), 5, 5, 6, 6);
			aFE.buildExcel();
			System.out.println("finished!");
			aFO.flush();
			aFO.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testReadExcel(String sFileName) {
		IReadExcel aRE = null;
		Vector aExcelReturn = null;
		String sFileExtName = FileTool.getFileExtension(sFileName);
		try {
			
			if( sFileExtName.equalsIgnoreCase("xls"))
			  aRE = new ReadXLS(new FileInputStream(System.getProperty("file.separator") + "opt" + System.getProperty("file.separator")+sFileName));
			else if( sFileExtName.equalsIgnoreCase("xlsx"))
			  aRE = new ReadXLSX(new FileInputStream(System.getProperty("file.separator") + "opt" + System.getProperty("file.separator")+sFileName));
			else{
				System.out.println("read excel failed!");
				return;
			}
			// example 1
			aExcelReturn = aRE.getAllExcelData();
			System.out.println("size=" + aExcelReturn.size());
			for (int i = 0; i < aExcelReturn.size(); i++) {
				DataSet aDataSet = (DataSet) aExcelReturn.get(i);
				System.out.println("=======================");
				if (aDataSet != null)
					while (aDataSet.next()) {// get Cell Data
						Vector aDataCell = (Vector) aDataSet.getData();
						for (int j = 0; j < aDataCell.size(); j++) {
							System.out.print(aDataCell.get(j));
						}
						System.out.println("");
					}
				System.out.println("=======================");
			}
			// example 2
			Vector aSheetNames = aRE.getSheetNames();
			for (int i = 0; i < aSheetNames.size(); i++) {
				System.out.println(aRE.getSheetCellData((String) aSheetNames.get(i), 0, (short) 0));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}