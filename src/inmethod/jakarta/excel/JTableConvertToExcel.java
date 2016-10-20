package inmethod.jakarta.excel;

import inmethod.jakarta.excel.CreateExcel;
import inmethod.commons.rdb.DataSet;
import java.util.Vector;
import javax.swing.table.TableModel;
import java.io.*;
import java.text.*;

/**
 * 將JTable的資料轉成excel
 * @see inmethod.jakarta.excel.CreateExcel
 */

public class JTableConvertToExcel{
  private static JTableConvertToExcel converter = null;
  private JTableConvertToExcel(){}

  public static JTableConvertToExcel getInstance(){
    if( converter==null )
      converter = new JTableConvertToExcel();
    return converter;
  }

  public void convertToExcel(TableModel ResultTable1,File file){
    NumberFormat a = new DecimalFormat("#0.0");
    FileOutputStream out = null;
    Object aO = null;
    CreateExcel aFE;
    DataSet aDS = null;
    Vector aCellData = null;
    try {
      aDS = new DataSet();
      out = new FileOutputStream(file);
      aFE =  new CreateExcel(out);
      aCellData = new Vector();
      for(int i=0; i < ResultTable1.getColumnCount(); i++) {
        aCellData.add(ResultTable1.getColumnName(i));
      }
      aDS.addData(aCellData);
      for(int i=0; i< ResultTable1.getRowCount(); i++) {
        aCellData = new Vector();
        for(int j=0; j < ResultTable1.getColumnCount(); j++) {
          aO = ResultTable1.getValueAt(i,j);
          aCellData.add(aO);
        }
        aDS.addData(aCellData);
     }
      aFE.setCurrentSheet();
      aFE.setGlobalCurrencyFormat("####0.0");     //小數位四位
      //aFE.setPrintResultSetHeader(true);
      aFE.calculateExcel(aDS);
      aFE.buildExcel();
      out.flush();
      out.close();
    }
    catch(Exception ex){ex.printStackTrace();}
    finally{
    }
   }
}