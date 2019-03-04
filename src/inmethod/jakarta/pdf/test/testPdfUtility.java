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
		String sSrc = "test_files/certificate/TEST.pdf";
		try {
			
			InputStream aIS = new FileInputStream(new File(sSrc));
			
			PdfUtility aPdfUtility = new PdfUtility(sSrc);
			System.out.println(aPdfUtility.getFieldValue("NAME"));
		    System.out.println(aPdfUtility.getFieldValue("VERSION"));
			System.out.println(aPdfUtility.getFieldValue("RELEASED_DATE"));
		    System.out.println(aPdfUtility.getFieldValue("中文欄位名稱測試"));
			
			aIS.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
