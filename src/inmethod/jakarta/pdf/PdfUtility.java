package inmethod.jakarta.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
			/*
			fields.keySet().forEach(System.out::println);
			java.util.Iterator<String> it = fields.keySet().iterator();
			PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);  
			
			while (it.hasNext()) {
				//获取文本域名称
				String name = it.next().toString();
				//填充文本域
				fields.get(name).setFont(font) .setValue(str[i++]).setFont(font).setFontSize(12);
				System.out.println(name);
			}						*/
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
