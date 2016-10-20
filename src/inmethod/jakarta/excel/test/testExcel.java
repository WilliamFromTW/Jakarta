package inmethod.jakarta.excel.test;
import java.util.*;
import java.sql.*;
import java.io.*;

import inmethod.jakarta.excel.*;
import inmethod.commons.rdb.*;

import org.apache.poi.hssf.model.*;
import org.apache.poi.hssf.dev.*;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *  create tmp directory in the same drive root directory ( for example:  d:/tmp ) 
 *  and copy test files  in directory "test_files" to "tmp" directory.
 * @author william chen
 *
 */
public class testExcel {


  public static void main(String a[]){
    new testExcel().testShiftExcel();
  }
  
  public void testDB(){
	  //SELECT TO_CHAR(MDY(cdc02,1,cdc01),'YYYYMM'),cdc03,cdc041,sfb05,ima02,CASE WHEN cdc04=1 THEN '人工' WHEN cdc04=2 THEN '製費1' WHEN cdc04=3 THEN '製費2' WHEN cdc04=4 THEN '製費3'  WHEN cdc04=5 THEN '製費4' ELSE '製費5' END CASE,cdc05,cdc06,cdc07 from  su.cdc_file,su.sfb_file,su.ima_file   where sfb01=cdc041 and ima01=sfb05  and cdc041 like '%' and sfb05 like '%' and TO_CHAR(MDY(cdc02,1,cdc01),'YYYYMM') between '201401' and '201502' order by 1,2
  };
  
  public  void  testShiftExcel(){
    Connection aConn = null;
    Statement aSt;
    ResultSet aRS;
    String sSqlCmd;
    CreateExcel aFE;
    String sReturn = "";
    FileOutputStream aFO = null;
    FileInputStream aFI = null;
    String sTemp = "";
    DataSet aDS = null;
    Vector aDataCell = null;

    try{
      aFI = new FileInputStream(System.getProperty("file.separator")+"tmp" +System.getProperty("file.separator")+"testShiftExcel.xls");
      aFO = new FileOutputStream(System.getProperty("file.separator")+"tmp" +System.getProperty("file.separator")+"testShiftExcel-"+Calendar.getInstance().get(Calendar.YEAR)+"-"+(Calendar.getInstance().get(Calendar.MONTH)+1)+".xls");
      aFE = new CreateExcel(aFO,aFI);
      aFE.setCurrentSheet("Stock");      //取得Excel sheet名稱
      aFE.createHeader("test Shift Excel",(short)0,(short)1,(short)0,(short)8);

      int iLine = 0; 

      aDS = new DataSet();
      
      // 第一行
      aDataCell = new Vector();
      aDataCell.add("row 1 , cell 1");
      aDataCell.add("row 1 , cell 2");
      aDataCell.add("row 1 , cell 3");
      aDataCell.add(new Integer(1));
      aDataCell.add(new String("1"));
      aDS.addData(aDataCell);
      
      // 第二行
      aDataCell = new Vector();
      aDataCell.add("row 2 , cell 1");
      aDataCell.add("row 2 , cell 2");
      aDataCell.add("row 2 , cell 3");
      aDS.addData(aDataCell);

      // 從第4行開始, 插入aDS資料,若ds資料前面有2個cell相同, 則接下相同的不顯示 
      aFE.calculateShiftExcel(0,4,aDS);
      
      // 圖片測試
      //aFE.createPic("/tmp/testExcel.jpg", 4,4, 5, 5);
      aFE.createPic(new FileInputStream("/tmp/testExcel.jpg"),5,5,6,6);
      aFE.buildExcel();
      System.out.println("finished!");
      aFO.flush();
      aFO.close();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

}