package inmethod.jakarta.jasper.test;

import inmethod.jakarta.jasper.JasperReportManager;


public class testJasper {
	public static void main(String[] args) {
		try  {
            String sJRxml = "/tmp/test_data.jrxml";
            String sJRxmlPdfFont = "/tmp/test_data_pdf_font.jrxml";
            String sJRreport = "/tmp/test_data_pdf_font.jasper";
            JasperReportManager aJRM = new JasperReportManager();
            aJRM.setJasperDesign(sJRxml);
            aJRM.addPdfCustomFont();
            aJRM.saveJasperDesign(sJRxmlPdfFont);
            aJRM.compileJasperReport(sJRreport);
            
			//JasperReportManager aJasperReportManager = new JasperReportManager("\\tmp\\test_data.jasper", "/tmp/aaa.pdf");
			
			
		} catch (Exception ex) {
ex.printStackTrace();
		}
	}
}
