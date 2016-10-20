package inmethod.jakarta.text;
import java.util.*;
 
/**
 * 以絕對位址來切割一個字串的token(繼承FromtwTokenizer).
 */
public class AbsoluteTokenizer extends ITokenizer {

  private Vector aSourceFieldFormat;
  private int iIndex = -1;

  /**
   *  @param aSourceFieldFormat vector物件,裡面存放的物件必須是  inmethod.jakarta.text.SourceFieldFormat
   *  @see SourceFieldFormat
   */
  public AbsoluteTokenizer(Vector aSourceFieldFormat) {
      this.aSourceFieldFormat = aSourceFieldFormat;
  }

  /**
   * @return String 取得下個token; null if no token
   */
  public String nextToken() {
    String sTemp = null;
    iIndex++;
    if( iIndex>=aSourceFieldFormat.size() ){
      sTemp = sCurrent;
      sCurrent = null;
      return sTemp;
    }

    if( this.doubleByteLength(sCurrent) > Integer.parseInt( ((SourceFieldFormat)aSourceFieldFormat.get(iIndex)).getLength()) ){
       sTemp = doubleByteLeft(sCurrent, Integer.parseInt(((SourceFieldFormat)aSourceFieldFormat.get(iIndex)).getLength()));
       sCurrent = sCurrent.substring(sTemp.length(),sCurrent.length());
       return sTemp;
    }
    else{
      sTemp = sCurrent;
      sCurrent = null;
      return sTemp;
    }
  }

  /**
   * @return int 取得所有token數量
   */
  public int countTokens() {
     String sTemp = new String(sCurrent);
     int iTotalTokens = 0;
     while(hasMoreTokens()){
       nextToken();
       iTotalTokens ++;
     }
     sCurrent = sTemp;
     reset(sCurrent);
    return iTotalTokens;
  }

  /**
   * @param str 給定一個字串,重新分割token
   */
  public void reset(String str) {
    sCurrent = str;
    iIndex = -1;
  }

  /**
   * @return  boolean  true if has next token; false if no token
   */
  public boolean hasMoreTokens() {
    if( sCurrent == null )
      return false;
    else
      return true;
  }

// * 20021107      erenh       modify  中文字計算
  /**
   * 取回字串左邊某個數目的字元數(中文字算兩個字元).
   * <pre> e.g.:
   *      doubleByteLeft("db2資料庫",5) -> "db2資"
   * </pre>
   * @param sSource            來源字串
   * @param iLen               要取回的字元數
   * @return String
   */
  public static String doubleByteLeft(String sSource, int iLen){
    String sReturn = "";
    int nLen = iLen;
    char[] cCharArray = sSource.toCharArray();
    for (int i = 0; i < cCharArray.length ; i++){
      int c = cCharArray[i];
      if ((c >= 0x0001) && (c <= 0x007F))	{
        nLen = nLen - 1;
      }
      else{
        nLen = nLen - 2;
        if (nLen==-1) return sReturn+" ";
      }

      sReturn = sReturn + String.valueOf(cCharArray[i]);
      if (nLen <= 0) return sReturn;
    }

    return sReturn;
  }

  /**
   * 傳回字串長度(Byte數).
   * <pre> e.g.:
   *      doubleByteLength("db2資料庫") -> 9
   * </pre>
   * @param pString            來源字串
   * @return int
   */
  public static int doubleByteLength(String pString) {

    char[] val = pString.toCharArray();
    int utflen = 0;

    for (int i = 0; i < val.length; i++) {
      int c = val[i];
      if ((c >= 0x0001) && (c <= 0x007F)) {
        utflen++;
      } else if (c > 0x07FF) {
        utflen += 2;
      }
    }
    return utflen;
  }

}