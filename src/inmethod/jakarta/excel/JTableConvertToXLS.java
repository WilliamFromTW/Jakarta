package inmethod.jakarta.excel;

import inmethod.jakarta.excel.CreateXLS;
import inmethod.commons.rdb.DataSet;
import java.util.Vector;
import javax.swing.table.TableModel;
import java.io.*;
import java.text.*;

/**
 * 將JTable的資料轉成excel
 * @see inmethod.jakarta.excel.CreateXLS
 */

public class JTableConvertToXLS{
  private static JTableConvertToXLS converter = null;
  private JTableConvertToXLS(){}

  public static JTableConvertToXLS getInstance(){
    if( converter==null )
      converter = new JTableConvertToXLS();
    return converter;
  }

  public void convertToExcel(TableModel ResultTable1,File file){
    NumberFormat a = new DecimalFormat("#0.0");
    FileOutputStream out = null;
    Object aO = null;
    CreateXLS aFE;
    DataSet aDS = null;
    Vector aCellData = null;
    try {
      aDS = new DataSet();
      out = new FileOutputStream(file);
      aFE =  new CreateXLS(out);
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