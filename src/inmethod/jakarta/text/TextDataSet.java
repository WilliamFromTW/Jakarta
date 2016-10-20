package inmethod.jakarta.text;

import java.util.Vector;
import java.sql.*;

/**
 * 很簡易的物件容器,FileTransferReader類別會用到.
 */
public class TextDataSet {

  boolean bDebug = false;
  // 欄位名稱
  private Vector vLabel = null;
  // 欄位名稱相對應的資料
  private Vector vDataValue = null;
  // 目前筆數
  public int iCurIndex = -1;
  public TextDataSet() {
      clear();
  }

  /***
   * set Label Name
   */
  public void setLabelName(Vector vLab) {
    vLabel = vLab;
  }

  /**
   * 新增一筆資料
   */
  public void addRow(Vector vData) {
    if (vDataValue == null)
      vDataValue = new Vector();
    vDataValue.add(vData.clone());
  }

  /**
   * get Label Name
   */
  public Vector getLabelName() {
    return vLabel;
  }

  /**
   * set the cursor to the first record
   */
  public void first()
  {
    iCurIndex = 0;
  }

  /**
   * set the cursor to before the first record
   */
  public void beforeFirst()
  {
    iCurIndex = -1;
  }

  /**
   * set the cursor to the previous record
   */
  public boolean previous()
  {
    if( iCurIndex > 0 )
    {
      iCurIndex--;
      return true;
    }
    else
      return false;
  }

  /**
   * set the cursor to the next record
   */
  public boolean next()
  {
    if( vDataValue == null )
      return false;
    if((iCurIndex+1) < vDataValue.size())
    {
      iCurIndex++;
      return true;
    }
    else
      return false;
  }

  /**
   * set the Cursor to the iRow record
   */
  public void absolute(int iRow)
  {
    iCurIndex = iRow;
  }

  /**
   * get the column index object
   */
  public Object getObject(int columnIndex)
  {//必須知道現在在第幾筆,再給第幾個column data
    Object obj = null;
    java.util.Vector vCurRow = (java.util.Vector)vDataValue.elementAt(iCurIndex);
    obj = (Object)vCurRow.elementAt(columnIndex);
    return obj;
  }

  /**
   * get the column name object
   */
  public Object getObject(String columnName)
  {//先到vLabel中找出此ColumnName 的index
   //then return this column data
   int index =0;
   while(index < vLabel.size())
   {
    if((((String)vLabel.elementAt(index)).toUpperCase()).equals(columnName.toUpperCase()))
      break;
    else
      index++;
   }
    java.util.Vector vCurRow = (java.util.Vector)vDataValue.elementAt(iCurIndex);
    return (Object)vCurRow.elementAt(index);
  }

  /**
   * get the current row
   */
  public java.util.Vector getRow()
  {
    return (java.util.Vector)vDataValue.elementAt(iCurIndex);
  }

  /**
   * get the index column string
   */
  public String getString(int columnIndex)
  {
    java.util.Vector vCurRow = (java.util.Vector)vDataValue.elementAt(iCurIndex);
    return (String)vCurRow.elementAt(columnIndex);
  }

  /**
   * get the column string value
   */
  public String getString(String columnName)
  {
   //先到vLabel中找出此ColumnName 的index
   //then return this column data
   int index =0;
   while(index < vLabel.size())
   {
    if((((String)vLabel.elementAt(index)).toUpperCase()).equals(columnName.toUpperCase()))
      break;
    else
      index++;
   }

    java.util.Vector vCurRow = (java.util.Vector)vDataValue.elementAt(iCurIndex);
    return vCurRow.elementAt(index).toString();
  }

  /**
   * get total data count
   */
  public int getTotalCount(){
    return this.vDataValue.size();
  }

  /**
   * remove all data from data set
   */
  protected void removeAllData(){
    vDataValue.removeAllElements();
  }

  /**
   * clear all data from TextDataSet
   */
  private void clear(){
    if(vDataValue != null)
      vDataValue.clear();
    if(vLabel != null)
      vLabel.clear();
  }//end of clear

  /**
   * 2個步驟
   * STEP 1: 將欄位名稱存入TextDataSet
   * STEP 2: 將資料所組成的Vector存入TextDataSet
   */
//  public static void main(String[] a){
//    try{
//      Connection oConn = JNDIConnection.getConnection();
//      ResultSet rs = oConn.createStatement().executeQuery("select * from dtb0_calendar");
//      ResultSetMetaData aRSMD = rs.getMetaData(); // meta data
//      int iCount = aRSMD.getColumnCount(); // total field number
//      // TextDataSet object
//      TextDataSet aDataSet = new TextDataSet();
//      Vector aDataVector = null;
//      Vector aLabelVector = null;
//      int iRowCount = 0;
//=============================================
// Step 1: 將欄位名稱依序存在 Vector
//=============================================
//      aLabelVector = new Vector();
//      for(int i=1;i<=iCount;i++){
//        String sKey = aRSMD.getColumnName(i);
//        aLabelVector.add(sKey);
//      }
//      aDataSet.setLabelName(aLabelVector); // add to TextDataSet
//=============================================
// Step 2:  將資料所組成的Vector存入TextDataSet
//=============================================
//      while(rs.next()){
//        iRowCount ++;
//        aDataVector = new Vector();
//        for(int i=1;i<=iCount;i++){
//          String sValue = rs.getString(i);
//          aDataVector.add(sValue); // add to Vector
//        }
//        aDataSet.addRow(aDataVector); // add to TextDataSet
//      }
//=============================================
// 測試DataSet
//=============================================
//      int j=aDataSet.getLabelName().size();
//      while( aDataSet.next()){
//        System.out.println("============ Record " + aDataSet.iCurIndex + " ============");
//        for(int i=0;i<j;i++){
//          System.out.println("TextDataSet Field = " + aDataSet.getLabelName().get(i) + ", Value = " +  aDataSet.getString(i));
//        }
//        System.out.println("===================================");
//      }
//    }catch(Exception ex){ex.printStackTrace();};
//  }
}