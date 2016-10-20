package inmethod.jakarta.text;
import java.util.*;

/**
 * 將字串格式化,只適用於Big5.
 * @author william chen
 */

public class CPrintfFormat {

  public static final int THOUSAND_SEPARATOR = 4; // 將數字每千位數標示","
  public static final int NOT_NEED_THOUSAND_SEPARATOR = 128; // 將數字每千位數標示","
  public static final int PADDING_ZERO = 8; // 補零
  public static final int PADDING_SPACE = 16; // 補空白
  public static final int ALIGN_RIGHT = 32; // 靠右
  public static final int ALIGN_LEFT = 64; // 靠左
  public static final int ALIGN_MID = 128; // 置中

  /**
   * 列印字串
   */
  public static String printString(int iLength, String sValue) throws Exception {
    return printString(iLength, sValue, 0);
  }

  /**
   * 列印字串 
   */
  public static String printString(int iLength, String sValue, int iAlign) throws Exception {
    int iOrgLen = iLength;
    int i = 0;
    int iTemp = iLength;
    int j, k = 0;
    char a[] = null;
    byte b = 0;
    byte d = 0;
    byte c[] = null;
    if (sValue == null)
      sValue = " ";
    String sTemp = sValue;
    j = sTemp.length();
    for (j = 0; j < sTemp.length(); j++) {

      c = sTemp.substring(j, j + 1).getBytes("BIG5");

      if ((iTemp - c.length) < 0)
        break;
      else {
        if (c.length == 2) {
          k++;
          d = c[1];
        }
        b = c[0];
        iTemp = iTemp - c.length;
      }
    }
    if (j == 0)
      return " ";

    sTemp = sTemp.substring(0, j);
    iLength = iLength - (k);
    sValue = sValue.substring(0, sValue.length() - k);

    String LengthFormat = new String("%");
    switch (iAlign) {
      case ALIGN_LEFT :
        LengthFormat += "-" + iLength + "s";
        break;
      case ALIGN_MID :
        int iBefSpc = (iOrgLen - doubleByteLength(sTemp)) / 2;
        StringBuffer sSpc = new StringBuffer();
        for (int x = 0; x < iBefSpc; x++) {
          sSpc.append(" ");
        }
        sTemp = sSpc.toString() + sTemp;
        LengthFormat += "-" + iLength + "s";
        break;
      case ALIGN_RIGHT :
      case 0 :
        LengthFormat += iLength + "s";
        break;
      default :
        throw new Exception("wrong align parameter");
    }
    PrintfFormat pf = new PrintfFormat(LengthFormat);
    return pf.sprintf(sTemp).toString();

  }

  /**
   * 列印int
   */
  public static String printInt(int iLength, int iDotLength, int iValue) throws Exception {
    Integer i = new Integer(iValue);
    return printInt(iLength, iDotLength, i);
  }

  /**
   * 列印int
   */
  public static String printInt(int iLength, int iDotLength, Integer iValue) throws Exception {
    Double j = new Double(iValue.toString());
    return printDecimal(iLength, iDotLength, j);
  }

  /**
   * 列印double
   */
  public static String printDouble(int iLength, int iDotLength, double dValue) throws Exception {
    Double j = new Double(dValue);
    return printDouble(iLength, iDotLength, j);
  }

  /**
   * 列印double
   */
  public static String printDouble(int iLength, int iDotLength, Double dValue) throws Exception {
    return printDecimal(iLength, iDotLength, dValue);
  }

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, long lValue) throws Exception {
    Long i = new Long(lValue);
    return printLong(iLength, iDotLength, i);
  }

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, Long lValue) throws Exception {
    Double j = new Double(lValue.toString());
    return printDecimal(iLength, iDotLength, j);
  }

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, float fValue) throws Exception {
    Float i = new Float(fValue);
    return printFloat(iLength, iDotLength, i);
  }

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, Float fValue) throws Exception {
    Double j = new Double(fValue.toString());
    return printDecimal(iLength, iDotLength, j);
  }

  /**
   * 列印 數字 (private)
   */
  private static String printDecimal(int iLength, int iDotLength, Object oValue) throws Exception {
    return printDecimal(iLength, iDotLength, oValue, 0, 0, 0);
  }

  /**
   * 列印 int
   */
  public static String printInt(int iLength, int iDotLength, int iValue, int iAlign) throws Exception {
    Integer i = new Integer(iValue);
    return printInt(iLength, iDotLength, i, iAlign);
  }

  /**
   * 列印int
   */
  public static String printInt(int iLength, int iDotLength, Integer iValue, int iAlign) throws Exception {
    Double j = new Double(iValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign);
  }

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, long lValue, int iAlign) throws Exception {
    Long i = new Long(lValue);
    return printLong(iLength, iDotLength, i, iAlign);
  }

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, Long lValue, int iAlign) throws Exception {
    Double j = new Double(lValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign);
  }

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, float fValue, int iAlign) throws Exception {
    Float i = new Float(fValue);
    return printFloat(iLength, iDotLength, i, iAlign);
  }

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, Float fValue, int iAlign) throws Exception {
    Double j = new Double(fValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign);
  }

  /**
   * 列印 double
   */
  public static String printDouble(int iLength, int iDotLength, double dValue, int iAlign) throws Exception {
    Double j = new Double(dValue);
    return printDouble(iLength, iDotLength, j, iAlign);
  }

  /**
   * 列印 double
   */
  public static String printDouble(int iLength, int iDotLength, Double dValue, int iAlign) throws Exception {
    return printDecimal(iLength, iDotLength, dValue, iAlign);
  }

  /**
   * 列印 數字(private)
   */
  private static String printDecimal(int iLength, int iDotLength, Object Value, int iAlign) throws Exception {
    return printDecimal(iLength, iDotLength, Value, iAlign, 0, 0);
  }

  /**
   * 列印 int
   */
  public static String printInt(int iLength, int iDotLength, int iValue, int iAlign, int iPadding) throws Exception {
    Integer i = new Integer(iValue);
    return printInt(iLength, iDotLength, i, iAlign, iPadding);
  };

  /**
   * 列印 int
   */
  public static String printInt(int iLength, int iDotLength, Integer iValue, int iAlign, int iPadding) throws Exception {
    Double j = new Double(iValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign, iPadding);
  };

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, long lValue, int iAlign, int iPadding) throws Exception {
    Long i = new Long(lValue);
    return printLong(iLength, iDotLength, i, iAlign, iPadding);
  };

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, Long lValue, int iAlign, int iPadding) throws Exception {
    Double j = new Double(lValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign, iPadding);
  };

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, float fValue, int iAlign, int iPadding) throws Exception {
    Float i = new Float(fValue);
    return printFloat(iLength, iDotLength, i, iAlign, iPadding);
  };

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, Float fValue, int iAlign, int iPadding) throws Exception {
    Double j = new Double(fValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign, iPadding);
  }

  /**
   * 列印 double
   */
  public static String printDouble(int iLength, int iDotLength, double dValue, int iAlign, int iPadding) throws Exception {
    Double j = new Double(dValue);
    return printDouble(iLength, iDotLength, j, iAlign, iPadding);
  }

  /**
   * 列印 double
   */
  public static String printDouble(int iLength, int iDotLength, Double dValue, int iAlign, int iPadding) throws Exception {
    return printDecimal(iLength, iDotLength, dValue, iAlign, iPadding);
  };

  /**
   * 列印 數字(private)
   */
  private static String printDecimal(int iLength, int iDotLength, Object Value, int iAlign, int iPadding) throws Exception {
    return printDecimal(iLength, iDotLength, Value, iAlign, iPadding, 0);
  };

  /**
   * 列印 int
   */
  public static String printInt(int iLength, int iDotLength, int iValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Integer i = new Integer(iValue);
    return printInt(iLength, iDotLength, i, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 int
   */
  public static String printInt(int iLength, int iDotLength, Integer iValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Double j = new Double(iValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, long lValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Long i = new Long(lValue);
    return printLong(iLength, iDotLength, i, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 long
   */
  public static String printLong(int iLength, int iDotLength, Long lValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Double j = new Double(lValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, float fValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Float i = new Float(fValue);
    return printFloat(iLength, iDotLength, i, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 float
   */
  public static String printFloat(int iLength, int iDotLength, Float fValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Double j = new Double(fValue.toString());
    return printDecimal(iLength, iDotLength, j, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 double
   */
  public static String printDouble(int iLength, int iDotLength, double dValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    Double j = new Double(dValue);
    return printDouble(iLength, iDotLength, j, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 double
   */
  public static String printDouble(int iLength, int iDotLength, Double dValue, int iAlign, int iPadding, int iSeparator) throws Exception {
    return printDecimal(iLength, iDotLength, dValue, iAlign, iPadding, iSeparator);
  }

  /**
   * 列印 數字
   */
  public static String printDecimal(int iLength, int dotLength, Object Value, int iAlign, int iPadding, int iSeparator) throws Exception {
    String LengthFormat = getFormatString(Value, iLength, dotLength, iAlign, iPadding, iSeparator);
    if (iSeparator == CPrintfFormat.THOUSAND_SEPARATOR) {
      PrintfFormat pf = new PrintfFormat(Locale.ENGLISH, LengthFormat);
      //        PrintfFormat pf2 = new PrintfFormat(String.valueOf(iLength));
      PrintfFormat pf2 = null;
      if (iAlign == ALIGN_LEFT)
        pf2 = new PrintfFormat("%-" + iLength + "s");
      else
        pf2 = new PrintfFormat("%" + iLength + "s");
      return pf2.sprintf(pf.sprintf(Value).toString().trim()).toString();
    } else {
      PrintfFormat pf = new PrintfFormat(LengthFormat);
      return pf.sprintf(Value).toString();
    }
  }

  /**
   * 列印 數字(private)
   */
  public static String printDecimal(String LengthFormat, Object Value) throws Exception {

    boolean bSeparator = false;
    boolean bAlign = false;
    int iLength = 0, i = 0, j = 0;

    if (LengthFormat.indexOf("'") != -1)
      bSeparator = true;

    if (bSeparator) {
      PrintfFormat pf = new PrintfFormat(Locale.ENGLISH, LengthFormat);
      PrintfFormat pf2 = null;
      if (LengthFormat.indexOf("-") != -1) {
        i = LengthFormat.indexOf("-");
        j = LengthFormat.indexOf(".");
        try {
          iLength = Integer.parseInt(LengthFormat.substring(i + 1, j));
        } catch (NumberFormatException ex) {
          return "";
        }

        pf2 = new PrintfFormat("%-" + iLength + "s");
      } else {
        i = LengthFormat.indexOf("'");
        j = LengthFormat.indexOf(".");
        try {
          iLength = Integer.parseInt(LengthFormat.substring(i + 1, j));
        } catch (NumberFormatException ex) {

          return "";
        }

        pf2 = new PrintfFormat("%" + iLength + "s");
      }
      return pf2.sprintf(pf.sprintf(Value).toString().trim()).toString();
    } else {
      PrintfFormat pf = new PrintfFormat(LengthFormat);
      return pf.sprintf(Value).toString();
    }
  }

  public static String getFormatString(Object value, int iLength, int dotLength, int iAlign, int iPadding, int iSeparator) throws Exception {
    String LengthFormat = new String("%");
    String Padding = new String();
    switch (iSeparator) {
      case THOUSAND_SEPARATOR :
        LengthFormat += "'";
        break;
      case NOT_NEED_THOUSAND_SEPARATOR :
        break;
      default :
        ; //throw new Exception("wrong spearator parameter");
    }
    switch (iAlign) {
      case ALIGN_LEFT :
        LengthFormat += "-";
        break;
      case ALIGN_RIGHT :
      case 0 :
        break;
      default :
        throw new Exception("wrong align parameter");
    }
    switch (iPadding) {
      case 0 :
      case PADDING_SPACE :
        break;
      case PADDING_ZERO :
        Padding += "0";
        break;
      default :
        throw new Exception("wrong padding parameter");
    }
    if (value instanceof String)
      LengthFormat += Padding + String.valueOf(iLength) + "s";
    else
      LengthFormat += Padding + String.valueOf(iLength) + "." + String.valueOf(dotLength) + "f";
    return LengthFormat;
  };

  /**
   * 取得字串長度(以byte計算)的誤差值
   * 因中文字在String只算一個字
   * 但卻用了2 bytes ,所以誤差值為1
   * 因此在列印報表時候需要調整實際長度
   */
  public static int getPercise(String str) {
    /*
      if(str!=null)
         return str.getBytes().length-str.length();
      else*/
    return 0;
  }

  /**
   * 傳回字串長度(Byte數).
   * @param pString
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

  public static void main(String[] args) {
    try {
      //===========   印字串 =================
      //====================================
      System.out.println(">>> 印字串");
      // 將hello印在長度為10的位置上(若字串不足10則靠右)
      System.out.println(CPrintfFormat.printString(10, "hello"));
      // 將hello印在長度為10的位置上,且強迫靠右
      System.out.println(CPrintfFormat.printString(10, "hello", CPrintfFormat.ALIGN_RIGHT));
      // 將hello印在長度為10的位置上,且強迫靠左
      System.out.println(CPrintfFormat.printString(10, "hello", CPrintfFormat.ALIGN_LEFT));

      //========== 印integer =================
      //======================================
      System.out.println(">>> 印integer");
      // 將整數10印在長度為十的位置上(若整數長度不足10則靠右)
      System.out.println(CPrintfFormat.printInt(10, 0, 10));
      // 將整數10印在長度為10且有小數1位的位置上(若整數長度不足10則靠右)
      System.out.println(CPrintfFormat.printInt(10, 1, 10));
      // 將整數10印在長度為十的位置上,且強迫靠右
      System.out.println(CPrintfFormat.printInt(10, 0, 10, CPrintfFormat.ALIGN_RIGHT));
      // 將整數10印在長度為十的位置上,且強迫靠左
      System.out.println(CPrintfFormat.printInt(10, 0, 10, CPrintfFormat.ALIGN_LEFT));
      // 將整數10印在長度為十的位置上,且強迫靠右,且不足的長度補0
      System.out.println(CPrintfFormat.printInt(10, 0, 10, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_ZERO)); // 注意 補0(PADDING_ZERO)必須使用ALIGN_RIGHT
      // 將整數10000印在長度為十的位置上,且強迫靠右,且標示千位數
      System.out.println(CPrintfFormat.printInt(10, 0, 10000, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_SPACE, CPrintfFormat.THOUSAND_SEPARATOR));

      // =========== 印long ============
      //================================
      System.out.println(">>> 印long");
      // 將10印在長度為十的位置上(若整數長度不足10則靠右)
      System.out.println(CPrintfFormat.printLong(10, 0, 10L));
      // 將10印在長度為十的位置上,且強迫靠左
      System.out.println(CPrintfFormat.printLong(10, 0, 10L, CPrintfFormat.ALIGN_LEFT));
      // 將10印在長度為十的位置上,且強迫靠右
      System.out.println(CPrintfFormat.printLong(10, 0, 10L, CPrintfFormat.ALIGN_RIGHT));
      // 將10印在長度為十的位置上,且強迫靠右,且不足的長度補0
      System.out.println(CPrintfFormat.printLong(10, 0, 10L, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_ZERO));
      // 將1200印在長度為十的位置上,且強迫靠右,且標示千位數
      System.out.println(CPrintfFormat.printLong(10, 0, 1200L, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_SPACE, CPrintfFormat.THOUSAND_SEPARATOR));

      // ============= 印float =============
      // ===================================
      System.out.println(">>> 印float");
      // 將10.1印在長度為10小數為1的位置上(若整數長度不足10則靠右)
      System.out.println(CPrintfFormat.printFloat(10, 1, 10.1f));
      // 將10.1印在長度為10小數為1的位置上,且強迫靠左
      System.out.println(CPrintfFormat.printFloat(10, 1, 10.1f, CPrintfFormat.ALIGN_LEFT));
      // 將10.1印在長度為10小數為1的位置上,且強迫靠右
      System.out.println(CPrintfFormat.printFloat(10, 1, 10.1f, CPrintfFormat.ALIGN_RIGHT));
      // 將10印在長度為十小數為1的位置上,且強迫靠右,且不足的長度補0
      System.out.println(CPrintfFormat.printFloat(10, 1, 10.1f, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_ZERO));
      // 將1200.1印在長度為10小數為1的位置上,且強迫靠右,且標示千位數
      System.out.println(CPrintfFormat.printFloat(10, 1, 1200.1f, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_SPACE, CPrintfFormat.THOUSAND_SEPARATOR));

      // ============= 印double ============
      // ===================================
      System.out.println(">>> 印double");
      // 將10.1印在長度為10小數為1的位置上(若整數長度不足10則靠右)
      System.out.println(CPrintfFormat.printDouble(10, 1, 101.1D));
      // 將10.1印在長度為10小數為1的位置上,且強迫靠左
      System.out.println(CPrintfFormat.printDouble(10, 1, 101.1D, CPrintfFormat.ALIGN_LEFT));
      // 將10.1印在長度為10小數為1的位置上,且強迫靠右
      System.out.println(CPrintfFormat.printDouble(10, 1, 101.1D, CPrintfFormat.ALIGN_RIGHT));
      // 將10印在長度為10小數為1的位置上,且強迫靠右,且不足的長度補0
      System.out.println(CPrintfFormat.printDouble(10, 1, 101.1D, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_ZERO));
      // 將1201.1印在長度為10小數為1的位置上,且強迫靠右,且標示千位數
      System.out.println(CPrintfFormat.printDouble(10, 1, 1201.1D, CPrintfFormat.ALIGN_RIGHT, CPrintfFormat.PADDING_SPACE, CPrintfFormat.THOUSAND_SEPARATOR));

    } catch (Exception ex) {
      ex.printStackTrace();
    };
  }

}