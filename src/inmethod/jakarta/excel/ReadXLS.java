package inmethod.jakarta.excel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellType;

import java.io.*;
import java.util.*;
import inmethod.commons.rdb.DataSet;
import java.text.*;

public class ReadXLS implements IReadExcel{

  private InputStream aInput;
  private HSSFWorkbook workBook;
  private HSSFSheet sheet;
  private HSSFRow aRow;
  private Vector aSheetNames;

  public ReadXLS(InputStream aIS){
    aInput = aIS;
    init(); 
  }

  private ReadXLS(){}

  private void init(){
    try{
      org.apache.poi.poifs.filesystem.POIFSFileSystem fs = new org.apache.poi.poifs.filesystem.POIFSFileSystem(aInput);
      workBook = new HSSFWorkbook(fs,true);
      int iNumOfSheets = workBook.getNumberOfSheets();
      System.out.println("number of sheets = " + iNumOfSheets);
      aSheetNames = new Vector();
      for(int i=0;i<iNumOfSheets;i++){
        sheet = workBook.getSheetAt(i);
        System.out.println("sheets("+i+") name = " + workBook.getSheetName(i) );
        aSheetNames.add(workBook.getSheetName(i));
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  /**
   * @return get all sheet name
   */
  public Vector getSheetNames() throws Exception {
    if(aSheetNames==null) throw new Exception("no sheets name");
    return aSheetNames;
  }


  public String getSheetCellData(String sSheetName,int iRow,short shortCell){
    if( workBook.getSheet( sSheetName ).getRow(iRow)==null ) return "";
    return getSheetCellDataByRow(workBook.getSheet( sSheetName ).getRow(iRow),shortCell);
  }


  public String getSheetCellData(int iSheetNum,int iRow,short shortCell){
    if( workBook.getSheetAt( iSheetNum ).getRow(iRow)==null ) return "";
    return getSheetCellDataByRow(workBook.getSheetAt( iSheetNum ).getRow(iRow),shortCell);
  }

  private String getSheetCellDataByRow(HSSFRow aRow,short shortCell){
    if( aRow.getCell(shortCell)==null ) return "";
    return aRow.getCell(shortCell).toString();
  }

  public int getSheetMaxRowNum(int iSheetNum){
    return workBook.getSheetAt( iSheetNum ).getLastRowNum();
  }

  public int getSheetMaxRowNum(String sSheetName){
    return workBook.getSheet( sSheetName ).getLastRowNum();
  }

  public Vector getAllExcelData() throws Exception{
    Vector aReturn = new Vector();
    DataSet aDataSet;
    Vector aDataCell;
    if( aInput==null ) throw new Exception("no excel");
    int iNumOfSheets = workBook.getNumberOfSheets();
    int iNumOfRow;
    short shortNumOfCell;
    System.out.println("number of sheets = " + iNumOfSheets);
    for(int i=0;i<iNumOfSheets;i++){
      sheet = workBook.getSheetAt(i);
      aDataSet = new DataSet();
      System.out.println("sheets("+i+") name = " + workBook.getSheetName(i) );
      iNumOfRow = sheet.getPhysicalNumberOfRows();

      System.out.println("Last Row Number = " + sheet.getLastRowNum());
      for(int j=0;j<iNumOfRow;j++){
        aRow = sheet.getRow(j);
        if(aRow==null) aRow = sheet.createRow(j);
        aDataCell = new Vector();
        shortNumOfCell = aRow.getLastCellNum();
        System.out.println("Last Cell Number = " + shortNumOfCell);
        for(short k=0;k<shortNumOfCell;k++){
          if( aRow.getCell(k)!= null){
        	  try {
        	System.out.println("Data from Cell(" + k + ") = " + aRow.getCell(k).toString() );
            if(aRow.getCell(k).getCellType()==CellType.NUMERIC){
              NumberFormat formatter = new DecimalFormat("##0.#");
              aDataCell.add(formatter.format(aRow.getCell(k).getNumericCellValue()));
            }
            else if(aRow.getCell(k).getCellType()==CellType.BOOLEAN){
              aDataCell.add(aRow.getCell(k).getBooleanCellValue());
            }
            else if(aRow.getCell(k).getCellType()==CellType.BLANK ){
              aDataCell.add("");
            }
            else
              aDataCell.add(aRow.getCell(k).getRichStringCellValue().toString() );
          }catch(Exception ee) {
        	ee.printStackTrace();
            aDataCell.add("");
        }
          }else{
            aDataCell.add("");
          }

        }
        aDataSet.addData(aDataCell);
      }
      aReturn.add(aDataSet);
    }
    return aReturn;
  }

  public static void main(String[] a){
    ReadXLS aRE = null;
    Vector aExcelReturn = null;
    try{
//      aRE = new ReadExcel(new FileInputStream("/opt/TC_PM_FILE.xls"));
      aRE = new ReadXLS(new FileInputStream("c:\\opt\\data.xls"));

      //sample 1
      aExcelReturn = aRE.getAllExcelData();
      System.out.println( "size="+ aExcelReturn.size() );
      for(int i=0;i<aExcelReturn.size();i++){
        DataSet aDataSet = (DataSet)aExcelReturn.get(i);
        System.out.println("=======================");
        if(aDataSet!=null)
          while(aDataSet.next()){//get Cell Data
            Vector aDataCell = (Vector)aDataSet.getData();
            for(int j=0;j<aDataCell.size();j++){
              System.out.print( aDataCell.get(j) );
            }
            System.out.println("");
          }
        System.out.println("=======================");
      }
      //sample 2
      Vector aSheetNames = aRE.getSheetNames();
      for(int i=0;i<aSheetNames.size();i++){
        System.out.println( aRE.getSheetCellData((String)aSheetNames.get(i),0,(short)0));
      }

    }catch(Exception ex){ ex.printStackTrace(); }

  }

}