package inmethod.jakarta.certificate.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import inmethod.jakarta.certificate.DigitalSinagurePDF;

public class testSignPdf {
	public static void main(String ar[]) {
	     try{
			InputStream aIS = new FileInputStream(new File("D:\\Users\\william\\Desktop\\合世生醫科技.pdf"));

			OutputStream os = new FileOutputStream("D:\\Users\\william\\Desktop\\dest.pdf");

			FileInputStream fis = new FileInputStream("D:\\Users\\william\\Desktop\\920405.pfx");
			
			DigitalSinagurePDF.getInstance().signDetached(aIS, os, fis,"Signature1", "!lois0023", null, null, null);
	     }catch(Exception ee){
	    	 ee.printStackTrace();
	     }
		}
}
