package inmethod.jakarta.text;
/**
 * 讀取文字檔之後,可以定義欄位的屬性.
 * @see FileTransferReader
 */
public class TargetFieldDefinition{
  // Data Type
  public static final String STRING = "STRING"; // 字串
  public static final String INTEGER = "INTEGER"; // 整數
  public static final String DECIMAL = "DECIMAL"; // 小數
  public static final String DATE = "DATE";// 日期
  // is null
  public static final String NULL = "true"; // 可 NULL
  public static final String NOT_NULL = "false"; // 不可 NULL
  // user define or class define or both
  public static final String USER_DEFINE = "UD";
  public static final String NO_USER_DEFINE = "noUD";

  private String sLabelName = null;
  private String sSourceDataType = null;
  private String sTargetDataType = null;
  private String sNull = null;
  private String sDefineWay = null;

  TargetFieldDefinition(String sLabelName,String sSourceDataType,String sNull,String sTargetDataType,String sDefineWay){
    this.sLabelName = sLabelName;
    this.sSourceDataType = sSourceDataType;
    this.sTargetDataType = sTargetDataType;
    this.sNull = sNull;
    this.sDefineWay = sDefineWay;
  };

  public String getLabelName(){
    return sLabelName;
  }

  public String getSourceDataType(){
    return sSourceDataType;
  }
  public String getTargetDataType(){
    return sTargetDataType;
  }

  public String getIsNULL(){
    return sNull;
  }

  public String getDefineWay(){
    return sDefineWay;
  }
}