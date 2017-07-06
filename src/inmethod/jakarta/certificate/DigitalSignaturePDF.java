package inmethod.jakarta.certificate;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;

import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

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
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DigitalSignaturePDF {
	private static DigitalSignaturePDF aDigitalSinagurePDF;

	private DigitalSignaturePDF() {
	};

	public static DigitalSignaturePDF getInstance() {

		if (aDigitalSinagurePDF == null) {
			aDigitalSinagurePDF = new DigitalSignaturePDF();
		}
		return new DigitalSignaturePDF();

	}

	public boolean signDetached(InputStream src, OutputStream dest, InputStream certFile, String sSignatureField,
			String password, String sReason, String sLocation, Calendar aC) {
		try {
			BouncyCastleProvider provider = new BouncyCastleProvider();
			Security.addProvider(provider);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(certFile, password.toCharArray());
			String alias = ks.aliases().nextElement();
			PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);

			X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
			String dn = cert.getSubjectDN().getName();
			String CN = getValByAttributeTypeFromDN(dn, "CN=");
			// Creating the reader and the signer
			PdfReader reader = new PdfReader(src);
			PdfSigner signer = new PdfSigner(reader, dest, false);
			Calendar aCalendar = null;
			if (aC != null) {
				aCalendar = aC;
			} else
				aCalendar = Calendar.getInstance();
			// Creating the appearance
			PdfSignatureAppearance appearance = signer.getSignatureAppearance().setReuseAppearance(false);
			if (sReason != null)
				appearance.setReason(sReason);
			if (sLocation != null)
				appearance.setLocation(sLocation);

			signer.setFieldName(sSignatureField);
			// Custom text and custom font
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			// sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			appearance.setLayer2Text(sdf.format(aCalendar.getTime()) + "+08'00'");
			appearance.setLayer2Font(PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", false));
			System.out.println(CN);
			// System.out.println(PdfFontFactory.createFont("STSong-Light",
			// "UniGB-UCS2-H", false).getWidth("HH:mm:ss+08'00'",(float)
			// 11.8)*1.75);

			signer.setSignDate(aCalendar);
			appearance.setRenderingMode(RenderingMode.NAME_AND_DESCRIPTION);
			// appearance.setImage(ImageDataFactory.create("d:\\Users\\william\\Desktop\\SVN.png"));
			// appearance.setImageScale(1);
			// appearance.setSignatureGraphic(ImageDataFactory.create("d:\\Users\\william\\Desktop\\SVN.png"));

			PdfFormXObject n0 = appearance.getLayer0();
			float x = n0.getBBox().toRectangle().getLeft();
			float y = n0.getBBox().toRectangle().getBottom();
			float width = n0.getBBox().toRectangle().getWidth();
			System.out.println("asdf" + width);
			float height = n0.getBBox().toRectangle().getHeight();
			PdfCanvas canvas = new PdfCanvas(n0, signer.getDocument());
			canvas.setFillColor(Color.LIGHT_GRAY);
			canvas.rectangle(x, y, width, height);
			canvas.fill();
			System.out.println("font size=" + (float) ((float) width / 12.6));
			appearance.setLayer2FontSize((float) ((float) width / 12.6));

			/*
			 * AcroFields form = stamper.getAcroFields(); form.setField(fname,
			 * value); form.setFieldProperty(fname, "setfflags",
			 * PdfFormField.FF_READ_ONLY, null);
			 */

			IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA512, provider.getName());
			IExternalDigest digest = new BouncyCastleDigest();
			signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
			return true;
		} catch (Exception ee) {
			ee.printStackTrace();
			return false;
		}
	}

	/**
	 * attribute include EMAILADDRESS= , CN= , OU= , DC=
	 * 
	 * @param dn
	 * @param attributeType
	 * @return
	 */
	private String getValByAttributeTypeFromDN(String dn, String attributeType) {
		System.out.println(dn);
		String[] dnSplits = dn.split(",");
		for (String dnSplit : dnSplits) {
			if (dnSplit.contains(attributeType)) {
				String[] cnSplits = dnSplit.trim().split("=");
				if (cnSplits[1] != null) {
					return cnSplits[1].trim();
				}
			}
		}
		return "";
	}



	private List<String> wrap(String txt, FontMetrics fm, int maxWidth) {
		StringTokenizer st = new StringTokenizer(txt);

		List<String> list = new ArrayList<String>();
		String line = "";
		String lineBeforeAppend = "";
		while (st.hasMoreTokens()) {
			String seg = st.nextToken();
			lineBeforeAppend = line;
			line += seg + " ";
			int width = fm.stringWidth(line);
			if (width < maxWidth) {
				continue;
			} else { // new Line.
				list.add(lineBeforeAppend);
				line = seg + " ";
			}
		}
		// the remaining part.
		if (line.length() > 0) {
			list.add(line);
		}
		return list;
	}

}