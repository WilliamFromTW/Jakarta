package inmethod.jakarta.jasper.test;

import inmethod.jakarta.jasper.JasperDesignManager;
import inmethod.jakarta.jasper.JasperReportManager;


public class testJasper {
	public static void main(String[] args) {
		try  {
            String sJRxml = "/tmp/test_data.jrxml";
            String sJRxmlPdfFont = "/tmp/test_data_pdf_font.jrxml";
            String sJRreport = "/tmp/test_data_pdf_font.jasper";
            JasperDesignManager aJDM = new JasperDesignManager(sJRxml);
            aJDM.addPdfCustomFont();
            aJDM.setImagePath("sImage", "/tmp/cropped-kafeiou_logo2.png");
            aJDM.saveJasperDesign(sJRxmlPdfFont);
            aJDM.compileJasperReport(sJRreport);
            
			//JasperReportManager aJasperReportManager = new JasperReportManager("\\tmp\\test_data.jasper", "/tmp/aaa.pdf");
			
			
		} catch (Exception ex) {
ex.printStackTrace();
		}
	}
}
