package inmethod.jakarta.certificate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

public class DigitalSinagurePDF {

	private static DigitalSinagurePDF aDigitalSinagurePDF;

	
	private DigitalSinagurePDF(){};
	
	public static DigitalSinagurePDF getInstance(){
		
		if( aDigitalSinagurePDF == null ){
			aDigitalSinagurePDF = new DigitalSinagurePDF();
		}
		return new DigitalSinagurePDF();
		
	}

	public boolean signDetached(InputStream src, OutputStream dest, InputStream certFile, String sSignatureField,String password, String sReason,String sLocation, Calendar aC) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		
			PdfReader reader = new PdfReader(src);
			PdfStamper stamper = PdfStamper.createSignature(reader, dest, '\0');

			// Creating the appearance
			PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
			if (sReason != null)
				appearance.setReason(sReason);
			if (sLocation != null)
				appearance.setLocation(sLocation);
			if (aC != null)
				appearance.setSignDate(aC);
			else
				appearance.setSignDate(Calendar.getInstance());

			if( sSignatureField!=null)
			  appearance.setVisibleSignature(sSignatureField);
			
			Security.addProvider(new BouncyCastleProvider());

			

			KeyStore ks = KeyStore.getInstance("pkcs12");
			ks.load(certFile, password.toCharArray());
			String alias = ks.aliases().nextElement();

			PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
			
			X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

			ExternalDigest digest = new BouncyCastleDigest();
			ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA512, "BC");
			MakeSignature.signDetached(appearance, digest, signature, new Certificate[] { cert }, null, null, null, 0,
					CryptoStandard.CMS);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return false;
	}
}