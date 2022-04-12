package inmethod.jakarta.jasper.test;

import inmethod.jakarta.jasper.JasperDesignManager;
import inmethod.jakarta.jasper.JasperReportManager;


public class testJasper {
	public static void main(String[] args) {
		try  {
            String sJRxml = "/opt/test_data.jrxml";
            String sJRxmlPdfFont = "/opt/test_data_pdf_font.jrxml";
            String sJRreport = "/opt/test_data_pdf_font.jasper";
            JasperDesignManager aJDM = new JasperDesignManager(sJRxml);
            aJDM.addPdfCustomFont();
            aJDM.setImagePath("sImage", "/opt/cropped-kafeiou_logo2.png");
            aJDM.saveJasperDesign(sJRxmlPdfFont);
            aJDM.compileJasperReport(sJRreport);
            
			//JasperReportManager aJasperReportManager = new JasperReportManager("\\opt\\test_data.jasper", "/opt/aaa.pdf");
			
			
		} catch (Exception ex) {
ex.printStackTrace();
		}
	}
}
