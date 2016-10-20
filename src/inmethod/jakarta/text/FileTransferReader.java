package inmethod.jakarta.text;
import java.io.*;
import java.util.*;

/**
 *  從Read-Only的 txt 檔 一筆一筆讀取資料.
 */
public abstract class FileTransferReader {

  // 文字檔是否有Header
  public static final boolean HAS_HEADER = true;
  public static final boolean NO_HEADER = false;
  public static final int POLICY_CONTINUE_IF_LINE_BLANK = 0;
  public static final int POLICY_STOP_IF_LINE_BLANK = 2;
  public static final int POLICY_LOG_STOP_IF_LINE_BLANK = 4;
  public static final int POLICY_LOG_CONTINUE_IF_LINE_BLANK = 8;


  // Field definition Set
  protected Vector FieldDefinitionSet = null;
  // Field format Set 
  protected Vector FieldFormatSet = null;
  // log Print Writer
  protected OutputStreamWriter aLogWriter = null;
  // file input stream reader
  protected InputStreamReader aFileReader = null;
  // encode
  protected String sEncode = "BIG5";
  // label vector
  protected Vector aLabelVector = null;
  // data value vector
  protected Vector aDataVector = null;
  // DATA SET
  protected TextDataSet aLDS = null;
  // token
  protected ITokenizer aToken = null;
  //
  private int iPolicy = POLICY_LOG_CONTINUE_IF_LINE_BLANK ;

  /**
   * @param strSouceFieldFormat String[][] 欄位的format
   * @param strTargetFieldDefinition String[] 欄位的定義
   */
  public FileTransferReader(String[][] strSouceFieldFormat,String[][] strTargetFieldDefinition) {
    aLabelVector = new Vector();
    setFieldFormat(strSouceFieldFormat);
    setFieldDefinition(strTargetFieldDefinition);

    aLDS = new TextDataSet();
  }

  /**
   * 此資料檔是否有Header
   */
  public void hasHeader(boolean bolHeader){
  }

  /**
   * <pre>
   * 設定 format,2-way dimension
   * 1: label name 2: length 3: align left or right 4: user define
   * </pre>
   * @param strFieldFormat String[][]
   * <pre>
   * example:
   *   private static final  String[][] aSrcFieldFormat= {
   *     // 1: label name 2: datatype 3: null 4: user define
   *     {"a","6",SourceFieldFormat.NO_TRIM,SourceFieldFormat.LEFT},
   *     {"b","6",SourceFieldFormat.NO_TRIM,SourceFieldFormat.LEFT},
   *     {"c","6",SourceFieldFormat.NO_TRIM,SourceFieldFormat.LEFT}
   *      //一行共有三個欄位a,b,c 長度都是6靠左不trim
   *   };
   * </pre>
   */
  private void setFieldFormat(String[][] strFieldFormat){
    FieldFormatSet = new Vector();
    int iLength = strFieldFormat.length;
    for(int i=0;i<iLength;i++){
      FieldFormatSet.addElement( new SourceFieldFormat(strFieldFormat[i][0],strFieldFormat[i][1],strFieldFormat[i][2],strFieldFormat[i][3]));
      aLabelVector.add(strFieldFormat[i][0]);
    }
  }

  /**
   * <pre>
   * 設定 definition,2-way dimension
   * 1: label name 2,length 3: datatype 4: null 5: user define
   * </pre>
   * @param strFieldDefinition String[][]
   * <pre>
   * example
   *   private static final  String[][] aTargetFieldDefinition= {
   *     // 1: label name 2,length 3: datatype 4: null 5: user define
   *     {"a","6",TargetFieldDefinition.STRING,TargetFieldDefinition.NOT_NULL,TargetFieldDefinition.NO_USER_DEFINE},
   *     {"b","6",,TargetFieldDefinition.STRING,TargetFieldDefinition.NOT_NULL,TargetFieldDefinition.NO_USER_DEFINE},
   *     {"c","6",,TargetFieldDefinition.STRING,TargetFieldDefinition.NOT_NULL,TargetFieldDefinition.NO_USER_DEFINE}
   *     // 一行共有三個欄位a,b,c 都是字串不能null且沒有user define rule
   *   };
   * </pre>
   */
  private void setFieldDefinition(String[][] strFieldDefinition){
    FieldDefinitionSet = new Vector();
    int iLength = strFieldDefinition.length;
    for(int i=0;i<iLength;i++){
      FieldDefinitionSet.addElement( new TargetFieldDefinition(strFieldDefinition[i][0],strFieldDefinition[i][1],strFieldDefinition[i][2],strFieldDefinition[i][3],strFieldDefinition[i][4]));
    }
  }

  /**
   * 設定 Output Stream , 寫 Log 要用
   * @param out OutputStream
   */
  public void setLogWriter(OutputStream out){
    try{
      aLogWriter = new OutputStreamWriter(out,sEncode);
    }catch(Exception ex){ex.printStackTrace();}
  }

  /**
   * 設定切Token的方式
   * @param aFT  ITokenizer
   * @see inmethod.jakarta.text.ITokenizer
   */
  public void setTokenizer(ITokenizer aFT){
    aToken = aFT;
  }

  /**
   * <pre>
   * 將labelVector與dataVector存入LDataSet中取得LDataSet
   * </pre>
   * @return LDataSet if every is ok
   *         null if no file be read or got exception
   * @see inmethod.jakarta.text.LDataSet
   */
  public TextDataSet getLDataSet(){
    if( aToken == null )  return new TextDataSet();
    String sData = null;
    // BufferedReader 可以一次讀取一行, temu 贊助提供
    BufferedReader input = new BufferedReader(aFileReader);
    int iRowIndex = 0; // 目前讀第 0 row
    aLDS.setLabelName(aLabelVector); // 設定 label vector , 在設定欄位格式的時候(setFieldFormat)就已經組好
    try{
      // temu 贊助提供,讀取一筆資料
      while ( (( sData = input.readLine()) != null) ){
        iRowIndex ++; // current row
        int iTokenIndex = -1; // current field
        String sTemp = null; // temp token value
        boolean bCorrectFormat = true; // 此筆所有欄位格式是否正確
        // if line blank
        if( sData.trim().equals("") ){
          if( iPolicy == POLICY_CONTINUE_IF_LINE_BLANK )
            continue;
          if( iPolicy == POLICY_STOP_IF_LINE_BLANK )
            break;
          if( iPolicy == POLICY_LOG_STOP_IF_LINE_BLANK ){
            writeLog("<FileTransferReader>: 第" + iRowIndex + "筆,讀出來的資料是空的");
            break;
          }
          if( iPolicy == POLICY_LOG_CONTINUE_IF_LINE_BLANK ){
            writeLog("<FileTransferReader>: 第" + iRowIndex + "筆,讀出來的資料是空的");
            continue;
          }
        }
        // 另類的StringToken
        aToken.reset(sData);
        aDataVector = new Vector(); // DataVector
        if( aToken.countTokens()!=getAllFieldFormatCount() ){
          writeLog("<FileTransferReader>: 第" + iRowIndex + "筆,可切出來的欄位個數並不符合規定");
          continue;
        }
        while( aToken.hasMoreTokens() ){
          sTemp = aToken.nextToken(); // 欄位資料
          iTokenIndex++;

          try{
            // 轉換適當的Format
            sTemp = reFormatting(iTokenIndex,sTemp);
            // 檢查資料是否符合check rule
            if( checkRule(iTokenIndex,sTemp) ){
              // 轉成適當的物件
              Object aObject= this.translateObject(iTokenIndex,sTemp);
              aDataVector.add(aObject);
            }
          }catch(Exception ex){
            bCorrectFormat = false;
            writeLog("<FileTransferReader>: 第" + iRowIndex + "筆,第" + iTokenIndex + "欄位," + ex.getMessage());
          };
        }
        // 此筆所有欄位格式正確
        if( bCorrectFormat )
          aLDS.addRow(aDataVector);
      }
      return aLDS;
    }catch(Exception ex){ex.printStackTrace();return null;}
  }

  /**
   * 取得 source Field format
   * @param iIndex 第幾個field的format , begin from "0"
   * @return SourceFieldFormat
   * @see inmethod.jakarta.text.SourceFieldFormat
   */
  public SourceFieldFormat getFieldFormat(int iIndex){
    return (SourceFieldFormat) FieldFormatSet.get(iIndex);
  }

  /**
   * 取得 source Field format
   * @return - Vector
   *     Object type in Vector is SourceFieldFormat
   * @see inmethod.jakarta.text.SourceFieldFormat
   */
  public Vector getAllFieldFormat(){
    return FieldFormatSet;
  }

  /**
   * 取得 所有source Field format 的個數
   * @return - get total FieldFormat #
   */
  public int getAllFieldFormatCount(){
    return FieldFormatSet.size();
  }


  /**
   * 取得 Field definition
   * @param iIndex 第幾個field的definition
   * @return TargetFieldDefinition
   * @see inmethod.jakarta.text.TargetFieldDefinition
   */
  public TargetFieldDefinition getFieldDefinition(int iIndex){
    return (TargetFieldDefinition) FieldDefinitionSet.get(iIndex);
  }

  /**
   * 設定編碼語系
   * @param strEnc BIG5,MS950,ISO8859-1,..
   */
  public void setLanguage(String strEnc){
   sEncode = strEnc;
  }

  /**
   * 讀取資料檔
   * @param is InputStream
   */
  public void setFileInputStream(InputStream is){
    try{
      aFileReader = new InputStreamReader(is,sEncode);
    }catch(UnsupportedEncodingException uex){uex.printStackTrace();}
  }

  /**
   * 將field Value轉成適當的data Object,且檢查field Value是否符合Field Definition
   * @param iIndex
   * @param sValue  : field value
   * @return Object data object(String , Double,Integer)
   */
  private Object translateObject(int iIndex,String sValue) throws Exception{
    TargetFieldDefinition aFD = (TargetFieldDefinition)FieldDefinitionSet.get(iIndex);
    try{
      if( aFD.getTargetDataType().equals(TargetFieldDefinition.INTEGER) ){
        return new Integer(Integer.parseInt(sValue.trim()));
      }else if( aFD.getTargetDataType().equals( TargetFieldDefinition.DECIMAL) ){
        return new Double(Double.parseDouble(sValue.trim()));
      }else{
        return sValue;
      }
    }catch(Exception ex){ throw new Exception(ex.toString());}
  }
  /**
   * 檢查資料是否符合欄位的定義
   * @param iIndex iIndex第幾個欄位
   * @param sTemp 該筆欄位的資料
   * @return boolean true: pass  , false: fail
   */
  private boolean checkRule(int iIndex,String sTemp) throws Exception{
    TargetFieldDefinition aFieldDefinition = (TargetFieldDefinition)FieldDefinitionSet.get(iIndex);
    if( aFieldDefinition.getIsNULL().equals(TargetFieldDefinition.NOT_NULL) && sTemp == null)
      throw new Exception("this field can't be null");
    try{
      if( aFieldDefinition.getSourceDataType().equals(TargetFieldDefinition.INTEGER) ){
          Integer.parseInt(sTemp);
      }
      if( aFieldDefinition.getSourceDataType().equals( TargetFieldDefinition.DECIMAL) )
          Double.parseDouble(sTemp);
    }catch(Exception ex){ throw new Exception(ex.toString());};

    if( aFieldDefinition.getDefineWay().equals(TargetFieldDefinition.USER_DEFINE) )
       return UserCheckRule(aFieldDefinition,sTemp);
    return true;
  }

  /**
   * 若有使用者自訂的欄位檢查方式必須自己implement
   * @param aFieldDefinition 此欄位資料的定義
   * @param sTemp 欄位資料
   * @return true: 檢查通過,false: fail
   */
  public abstract boolean UserCheckRule(TargetFieldDefinition aFieldDefinition,String sTemp);

  /**
   * @param iPolicy must be the following variable :
   *    POLICY_CONTINUE_IF_LINE_BLANK - no waring , just go ahead
   *    POLICY_STOP_IF_LINE_BLANK - no waring , just stop reading file
   *    POLICY_LOG_STOP_IF_LINE_BLANK -  write log and stop reading file
   *    POLICY_LOG_CONTINUE_IF_LINE_BLANK - write log and continue reading file
   *
   */
  public void setPolicyIfLineBlank(int iPolicy){
    this.iPolicy = iPolicy;
  }

  /**
   * 將字串轉成適當的格式
   * @param iIndex 該欄位的format index
   * @param sTemp 該欄位的資料
   * @return 該欄位完成之後資料
   */
  private String reFormatting(int iIndex,String sTemp) throws Exception{
    if( sTemp == null ) return null;
    SourceFieldFormat aFieldFormat = (SourceFieldFormat)FieldFormatSet.get(iIndex);
    // 若要trim掉
    if( aFieldFormat.getTrim().equals(aFieldFormat.TRIM) ){
      sTemp = sTemp.trim();
    }
    try{
      // 靠左靠右
      if ( aFieldFormat.getAlign().equals(aFieldFormat.LEFT) ){
        sTemp = CPrintfFormat.printString( Integer.parseInt(aFieldFormat.getLength()),sTemp,CPrintfFormat.ALIGN_LEFT);
      }
      if ( aFieldFormat.getAlign().equals(aFieldFormat.RIGHT) ){
        sTemp = CPrintfFormat.printString( Integer.parseInt(aFieldFormat.getLength()),sTemp,CPrintfFormat.ALIGN_RIGHT);
      }
    }catch(Exception ex){throw new Exception(ex.toString());}
    return sTemp;
  }

  /**
   * 寫log
   */
  public void writeLog(String strMessage){
    if(strMessage == null) return;
    try{
      if( aLogWriter!=null){
        aLogWriter.write(strMessage+"\r\n");
        aLogWriter.flush();
      }
      else
        System.out.println("error");
    }catch(Exception ex){ex.printStackTrace();}
  }
}