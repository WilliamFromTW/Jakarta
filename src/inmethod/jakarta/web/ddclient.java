package inmethod.jakarta.web;

import java.net.URL;
import java.util.Base64;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class ddclient {
  public static void main(String arg[]) {
	  System.out.print("ddclient googledomains [user id] [password] [dynamic host name]");
	  if( arg.length!=4 ){
		  System.out.println("arg error");
		  return;
	  }else{
		  try{
		  if( arg[0].trim().equalsIgnoreCase("googledomains")){
			  System.out.println("google domains");
		      String httpsURL = "https://domains.google.com/nic/update?hostname="+arg[3];
		      String userpass = arg[1] + ":" + arg[2];
		      String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
		      URL myurl = new URL(httpsURL);
		      HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		      con.setRequestProperty ("Authorization", basicAuth);
		      
		      InputStream ins = con.getInputStream();
		      InputStreamReader isr = new InputStreamReader(ins);
		      BufferedReader in = new BufferedReader(isr);
		      String inputLine;
		      while ((inputLine = in.readLine()) != null)
		      {
		        System.out.println(inputLine);
		      }
		      in.close();
		    }
		  }catch(Exception ee){
			  ee.printStackTrace();
		  }
	  }
  }
}