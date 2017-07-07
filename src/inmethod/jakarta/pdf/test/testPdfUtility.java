package inmethod.jakarta.pdf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import inmethod.jakarta.pdf.PdfUtility;

public class testPdfUtility {
	public static void main(String ar[]) {
		readFieldValues();
	}

	public static void readFieldValues() {
		String sSrc = "/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/QP0502.pdf";
		try {
			
			InputStream aIS = new FileInputStream(new File(sSrc));
			
			PdfUtility aPdfUtility = new PdfUtility(sSrc);
			System.out.println(aPdfUtility.getFieldValue("NAME"));
		    System.out.println(aPdfUtility.getFieldValue("VERSION"));
			System.out.println(aPdfUtility.getFieldValue("RELEASED_DATE"));
			
			aIS.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
