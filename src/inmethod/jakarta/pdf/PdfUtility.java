package inmethod.jakarta.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.forms.fields.PdfFormField;

public class PdfUtility {
	
	private static PdfUtility aPdfReader;

	private PdfUtility() {
	}

	public static PdfUtility getInstance() {

		if (aPdfReader == null) {
			aPdfReader = new PdfUtility();
		}
		return  aPdfReader;

	}
	
	public String getFieldValue(String src,String sField) throws IOException{
		return getFieldValue(new File(src),sField);
	}
	
	public String getFieldValue(File src,String sField) throws IOException{
		return getFieldValue(new FileInputStream(src),sField);
	}	
	
	public String getFieldValue(InputStream src,String sField) throws IOException{
        String sReturn = "";
	    PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
 
        Map<String, PdfFormField> fields = form.getFormFields();
        sReturn = form.getField(sField).getValueAsString();
        pdfDoc.close();
        return sReturn;
	}
}
