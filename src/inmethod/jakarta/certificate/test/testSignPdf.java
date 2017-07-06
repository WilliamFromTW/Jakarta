package inmethod.jakarta.certificate.test;

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

import inmethod.jakarta.certificate.DigitalSignaturePDF;

public class testSignPdf {
	
	public static void main(String ar[]) {
		iText();
	}

	public static void iText() {
		try {
			InputStream aIS = new FileInputStream(new File(
					"/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/QP0502.pdf"));

			OutputStream os = new FileOutputStream(
					"/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/signed_QP0502.pdf");

			FileInputStream fis = new FileInputStream(
					"/media/veracrypt2/william.fromtw@gmail.com/Dropbox/git/src/inmethod/Java/InMethodJakarta/test_files/certificate/920405.pfx");
			Calendar aC = Calendar.getInstance();
			aC.set(Calendar.HOUR, aC.get(Calendar.HOUR) - 1);
			DigitalSignaturePDF.getInstance().signDetached(aIS, os, fis, "Signature1", "123456", null, null, aC);
			// DigitalSinagurePDF.getInstance().cc();
			os.flush();
			os.close();
			aIS.close();

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void cc() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);// Represents
																					// an
																					// image
																					// with
																					// 8-bit
																					// RGBA
																					// color
																					// components
																					// packed
																					// into
																					// integer
																					// pixels.
		Graphics2D graphics2d = image.createGraphics();
		Font font = new Font("TimesNewRoman", Font.BOLD, 24);
		graphics2d.setFont(font);
		FontMetrics fontmetrics = graphics2d.getFontMetrics();
		int width = fontmetrics.stringWidth("asdf");
		int height = fontmetrics.getHeight();
		graphics2d.dispose();

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics2d = image.createGraphics();
		graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		graphics2d.setFont(font);
		fontmetrics = graphics2d.getFontMetrics();
		graphics2d.setColor(java.awt.Color.GREEN);
		graphics2d.drawString("asdf", 0, fontmetrics.getAscent());
		graphics2d.dispose();
		try {
			ImageIO.write(image, "png", new File("d:/Sample.jpg"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
