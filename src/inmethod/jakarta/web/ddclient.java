package inmethod.jakarta.web;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class ddclient {
  public static void main(String arg[]) {
	  System.out.println("ddclient googledomains [usrename] [password] [hostname]");
	  System.out.println("ddclient googledomains [usrename] [password] [hostname] [myip]");
	  System.out.println("ddclient ddclientProxyServlet [PROXY URL] [usrename] [password] [hostname]");
	  System.out.println("ddclient ddclientProxyServlet [PROXY URL] [usrename] [password] [hostname] [myip]");
	  if( arg.length!=4 && arg.length!=5  && arg.length!=6){
		  System.out.println("arg error");
		  return;
	  }
	  else if( arg[0]!=null && arg[0].equalsIgnoreCase("googledomains")){
		  googledomains(arg); 
	  }
	  else if( arg[0]!=null && arg[0].equalsIgnoreCase("ddclientProxyServlet")){
		  ddclientProxyServlet(arg);
	  }
  }
  public static void googledomains(String arg[]){
	  
		  try{
		  if( arg[0].trim().equalsIgnoreCase("googledomains")){
			  System.out.println("google dynamic domains");
		      String httpsURL = "https://domains.google.com/nic/update?hostname="+arg[3];
		      if( arg.length==5 && arg[4]!=null && !arg[4].trim().equals("")){
		    	  httpsURL = httpsURL + "&myip="+arg[4]; 
		      }
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
  
  public static void ddclientProxyServlet(String arg[]){
	  
	  try{
	  if( arg[0].trim().equalsIgnoreCase("ddclientProxyServlet")){
		  System.out.println("ddclientProxyServlet pass to google dynamic domains");
	      String httpsURL = arg[1]+"?username="+arg[2]+"&password="+arg[3]+"&hostname="+arg[4];
	      if( arg.length==6 && arg[5]!=null && !arg[5].trim().equals("")){
	    	  httpsURL = httpsURL + "&myip="+arg[5]; 
	      }
	      URL myurl = new URL(httpsURL);
	      HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
	      
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