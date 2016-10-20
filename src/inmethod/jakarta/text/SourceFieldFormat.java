package inmethod.jakarta.text;

/**
 *   欄位的格式設定,用來讀取文字檔.
 * @see FileTransferReader
 */
public class SourceFieldFormat {
  // padding
  public static final String SPACE = "space";  // 長度不足補空白
  public static final String ZERO = "zero"; // 補零
  // trim
  public static final String TRIM = "trim"; // 字串trim掉
  public static final String NO_TRIM = "no_trim"; // 字串不trim掉
  // align
  public static final String LEFT = "left"; // 字串的靠左
  public static final String RIGHT = "right"; // 字串的靠右
  public static final String NO_ALIGN = "none";// 不處理字串的靠左或靠右

  private String sLabelName; // label name
  private String sLength;    // length
  private String sAlign;     // align
  private String sTrim;      // trim

  // 1: label name 2: length 3: align left or right
  public SourceFieldFormat(String sLabelName,String sLength,String sTrim,String sAlign){
    this.sLabelName = sLabelName;
    this.sLength = sLength;
    this.sTrim = sTrim;
    this.sAlign = sAlign;
  }
  public String getAlign(){
    return sAlign;
  }
  public String getLength(){
    return sLength;
  }
  public String getTrim(){
    return sTrim;
  }
}