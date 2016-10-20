package inmethod.jakarta.text;
/**
 *  簡易的StringTokenizer.
 * 可將兩個連續分割符號'中間值以null表示出來.
 */
public class DelimiterTokenizer extends ITokenizer{
  private String sDelim = null;
  private int iDelimLength = 0;

  /**
   * @param delim - delimiter
   */
  public DelimiterTokenizer(String delim){
     sDelim = delim; 
     iDelimLength = delim.length();
  }


  /**
   * @param str - give new String which will be tokenized
   */
  public void reset(String str){
    sCurrent = str;
  }

  /**
   * @return - tatal token #
   */
  public int countTokens(){
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
   * @return - true if has next token
   *           false if no token
   */
  public boolean hasMoreTokens(){
    if( sCurrent == null )
      return false;
    else
      return true;
  }

  /**
   * @return - get each token string
   *           return null if no token
   */
  public String nextToken(){
    int j = sCurrent.indexOf(sDelim);
    String sReturn = null;
    // 若找不到分隔符號且還有值
    if( j == -1 && sCurrent != null ){
      sReturn = sCurrent;
      sCurrent = null;
      if( sReturn.equals("") )
        return null;
      else
        return sReturn;
    }
    else{
      sReturn = sCurrent.substring(0,j);
      // 跳過分隔符號
      sCurrent = sCurrent.substring(j+iDelimLength);
      if( sReturn.equals("") )
        return null;
      else
        return sReturn;
    }
  }

  public static void main(String[] str){
    DelimiterTokenizer a = new DelimiterTokenizer(",,");
    a.reset("asdf,adsf,,f");
    System.out.println(a.countTokens());
    while(a.hasMoreTokens()){
      System.out.println(a.nextToken());
    }
  }
}