package inmethod.jakarta.text;

/**
 * abstract 類別,用來做字串切割用的.
 * @author william
 */
public abstract class ITokenizer {
  protected String sCurrent ; // 目前的String
  public abstract int countTokens();
  public abstract boolean hasMoreTokens();
  public abstract String nextToken();
  public abstract void reset(String str);
}