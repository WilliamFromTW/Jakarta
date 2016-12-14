package inmethod.jakarta.excel;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Vector;

import inmethod.commons.rdb.DataSet;

public interface ICreateExcel{

	  /**
	   * create a new sheet and set as current
	   */
	  public void setCurrentSheet();

	  /**
	   * set current sheet as specified name
	   */
	  public void setCurrentSheet(String sName);

	  /**
	   * decide print result set column name to excel 
	   * @param bol true: print  , false: none print
	   */
	  public void setPrintResultSetHeader(boolean bol);
	  
	  
	  /**
	   *  create merged header. 
	   * @param sHeaderMessage
	   * @param iRowBegin
	   * @param iRowEnd
	   * @param iColBegin
	   * @param iColEnd
	   */
	  public void createHeader(String sHeaderMessage,short iRowBegin,short iRowEnd,short iColBegin,short iColEnd);

	  /**
	   * Get current workbook.
	   */
	  public Object getCurrentWorkBook();

	  /**
	   * Get current sheet.
	   */
	  public Object getCurrentSheet();

	  /**
	   *  calculate csv formate
	   * @param aDS
	   * @param sEncode
	   * @return
	   */
	  public boolean calculateCsv(DataSet aDS,String sEncode);
	  
	  /**
	   * calculate csv formate
	   * @param checkCells
	   * @param aDS
	   * @param sEncode
	   * @return
	   */
	  public boolean calculateCsv(int checkCells,DataSet aDS,String sEncode);

	  /**
	   * calculate csv formate
	   * @param aRS
	   * @param sEncode
	   * @return
	   */
	  public boolean calculateCsv(ResultSet aRS,String sEncode);

	  /**
	   *  calculate csv formate
	   * @param checkCells
	   * @param aRS
	   * @param sEncode
	   * @return
	   */
	  public boolean calculateCsv(int checkCells,ResultSet aRS,String sEncode);

	  /**
	   * build csv file
	   */
	  public void buildCsv();

	  /**
	   *  build a new Excel files.
	   */
	  public void buildExcel();



	  /**
	   * write value to specify row and column
	   * @param x
	   * @param y
	   * @param aVector
	   */
	 public void setValue(short x, short y, Vector aVector);


	  /**
	   * write value to specify row and column
	   * @param x
	   * @param y
	   * @param sValue
	   * @param autosize
	   */
	  public void setValue(short x, short y, String sValue,boolean autosize);
	  
	  
	  /**
	   * write value to specify row and column
	   * @param x
	   * @param y
	   * @param sValue
	   */
	  public void setValue(short x, short y, String sValue);


	  /**
	   * write value to specify row and column
	   * @param x
	   * @param y
	   * @param dValue
	   */
	  public void setValue(short x, short y, Integer dValue);

	  /**
	   * write value to specify row and column
	   * @param x
	   * @param y
	   * @param dValue
	   */
	  public void setValue(short x, short y, Double dValue);




	  /**
	   * give a currency format , print number to some where
	   * @param x  x location
	   * @param y  y location
	   * @param dValue Double class
	   * @param sFormat currency format "#,##0.0";
	   */
	  public void setValue(short x, short y, Double dValue,String sFormat);

	  /**
	   * set currency format
	   * @param sFormat
	   */
	  public void setGlobalCurrencyFormat(String sFormat);

	  /**
	   * calculate shift excel format
	   * @param checkCells
	   * @param iShiftRow
	   * @param aDS
	   * @return
	   */
	  public boolean calculateShiftExcel(int checkCells,int iShiftRow,DataSet aDS);
	  /**
	   *  buildExcel data sheet.
	   * @param aRS  sql resultset
	   */
	  public boolean calculateShiftExcel(int checkCells,int iShiftRow,ResultSet aRS);

	  /**
	   *  buildExcel data sheet.
	   * @param aRS  sql resultset
	   */
	  public boolean calculateExcel(ResultSet aRS);

	  /**
	   *  last row number 
	   * @return
	   */
	  public int getLastRowNum();

	  /**
	   *  calcute data sheet.
	   * @param aRS  sql resultset
	   */
	  public boolean calculateExcel(int checkCells,ResultSet aRS);

	  /**
	   * create picture (jpeg) 
	   * @param aIS
	   * @param x1  col number 1..n
	   * @param y1  row number 1..n
	   * @param x2  col number 1..n
	   * @param y2  row number 1..n
	   * @return
	   */
	  public boolean createPic(InputStream aIS,int x1,int y1,int x2,int y2) ;

	  /**
	   * create picture (jpeg)
	   * @param sPicLocation
	   * @param x1 col number 1..n
	   * @param y1 row number 1..n
	   * @param x2 col number 1..n
	   * @param y2 row number 1..n
	   * @return
	   */
	  public boolean createPic(String sPicLocation,int x1,int y1,int x2,int y2);

	  /**
	   *  buildExcel data sheet.
	   * @param aDS com.fromtw.commons.rdb.DataSet , aDS must include Object of Vector and Vector must include Double , String and Short object.
	   */
	  public boolean calculateExcel(DataSet aDS);
	  
	  public boolean calculateExcel(int checkCells,DataSet aDS);


	}