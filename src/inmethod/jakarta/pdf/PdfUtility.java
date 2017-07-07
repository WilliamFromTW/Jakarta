package inmethod.jakarta.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.forms.fields.PdfFormField;

public class PdfUtility {

	private PdfDocument pdfDoc = null;
	private PdfAcroForm form = null;
	private Map<String, PdfFormField> fields = null;
	private InputStream aIS = null;

	public PdfUtility(InputStream src) {
		try {
			aIS = src;
			pdfDoc = new PdfDocument(new PdfReader(aIS));
			form = PdfAcroForm.getAcroForm(pdfDoc, true);

			fields = form.getFormFields();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public PdfUtility(File src) {
		try {
			aIS = new FileInputStream(src);
			pdfDoc = new PdfDocument(new PdfReader(aIS));
			form = PdfAcroForm.getAcroForm(pdfDoc, true);

			fields = form.getFormFields();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PdfUtility(String src) {
		try {
			aIS = new FileInputStream(new File(src));
			pdfDoc = new PdfDocument(new PdfReader(aIS));
			form = PdfAcroForm.getAcroForm(pdfDoc, true);
			fields = form.getFormFields();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getFieldValue(String sField) {
		String sReturn = "";
		try {
			sReturn = form.getField(sField).getValueAsString();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return sReturn;
	}
}
