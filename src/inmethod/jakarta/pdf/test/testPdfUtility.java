package inmethod.jakarta.pdf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import inmethod.jakarta.pdf.PdfUtility;

public class testPdfUtility {
	public static void main(String ar[]) {
		iText();
	}

	public static void iText() {
		String sSrc = "/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/QP0502.pdf";
		try {
			
			InputStream aIS = new FileInputStream(new File(sSrc));
			
			
			System.out.println(PdfUtility.getInstance().getFieldValue(aIS,"RELEASED_DATE"));
			
			aIS.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
