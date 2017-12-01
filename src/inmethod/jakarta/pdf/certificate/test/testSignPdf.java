package inmethod.jakarta.pdf.certificate.test;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;

import inmethod.jakarta.pdf.certificate.DigitalSignaturePDF;

public class testSignPdf {
	
	public static void main(String ar[]) {
		iText();
	}

	public static void iText() {
		String sSrc = "/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/TEST.pdf";
		String sDest = "/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/TEST_SIGNED.pdf";
		String sPFX = "/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/920405.pfx";
		try {
			
			Calendar aC = Calendar.getInstance();
			aC.set(Calendar.HOUR, aC.get(Calendar.HOUR) - 1);
			
			
			InputStream aIS = new FileInputStream(new File(sSrc));
			OutputStream os = new FileOutputStream(sDest);
			FileInputStream fis = new FileInputStream(sPFX);
			//DigitalSignaturePDF.getInstance().signDetached(aIS, os, fis, "Signature1", "123456", null, null, aC);
			
			
			DigitalSignaturePDF.getInstance().signDetached(sSrc,sDest,sPFX, "Signature1", "123456", null, null, aC);
			
			os.flush();
			os.close();
			aIS.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
}
