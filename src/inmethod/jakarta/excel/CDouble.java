package inmethod.jakarta.excel;

public class CDouble {
  private Double d;
  private String sCurrencyFormat = "#,##0.0";

  public CDouble(Double d,String sCurrencyFormat){
   this.d = d;
   this.sCurrencyFormat = sCurrencyFormat;
  }

  public CDouble(Double d){
   this.d = d;
  }

  public String getCurrencyFormat(){
    return sCurrencyFormat;
  }

  public void  setCurrencyFormat(String sCurrencyFormat){
   this.sCurrencyFormat = sCurrencyFormat;
  }

  public Double getDouble(){
    return d;
  }

  public double doubleValue(){
    return d.doubleValue();
  }
  public int intValue(){
    return d.intValue();
  }
  public long longValue(){
    return d.longValue();
  }
  public float floatValue(){
    return d.floatValue();
  }
}