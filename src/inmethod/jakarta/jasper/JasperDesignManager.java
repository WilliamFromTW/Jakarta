package inmethod.jakarta.jasper;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class JasperDesignManager {

	private java.io.OutputStream aOutput;
	private JasperReport report;
	private JasperDesign aJD;
    private JRBand[] aJRbrands;
    
	private JasperDesignManager() {

	}
	
	/**
	 * 
	 * @param sXMLFile jasper design file 
	 * @throws JRException
	 */
	public JasperDesignManager(String sXMLFile) throws JRException {
		aJD = JRXmlLoader.load(sXMLFile);
		aJRbrands= aJD.getAllBands();
	}
	


	/**
	 * 
	 * @param sKey  image  properties key name
	 * @param sImagePath
	 */
	public void setImagePath(String sKey,String sImagePath) {
		for( JRBand a:aJRbrands ) {
			JRElement b = a.getElementByKey(sKey);
          	        	  
	        	if(b instanceof JRDesignImage) {
	        		JRDesignImage aJRI = ((JRDesignImage)b);
	        		JRDesignExpression aJRExpression = new JRDesignExpression();
	        		
	        		aJRExpression.setValueClass(java.lang.String.class);
	        		aJRExpression.setText("\""+sImagePath+"\"");
	            	aJRI.setExpression(aJRExpression);
	            	aJRI.setScaleImage(ScaleImageEnum.REAL_SIZE);
	            	
	        	}
	          }
	}
	
	/**
	 * 
	 * @param sXMLFile  report location 
	 * @param sFileOutputStreamPDF   generate report pdf file
	 * @throws Exception
	 */
	public  void  setJasperReport(String sXMLFile, String sFileOutputStreamPDF) throws Exception {
		report = (JasperReport) JRLoader.loadObjectFromFile(sXMLFile);

		aOutput = new FileOutputStream(sFileOutputStreamPDF);
	}

	public void saveJasperDesign(String sXMLFile) throws JRException {
		if( aJD!=null)
		  JRXmlWriter.writeReport(aJD,sXMLFile,"UTF-8");
		else throw new JRException("no JasperDesign object");
	}
	
	public void compileJasperReport(String sXMLFile) throws JRException {
		if( aJD!=null) {
			
			//JasperReport aReport = JasperCompileManager.compileReport(aJD);
			JasperCompileManager.compileReportToFile(aJD,sXMLFile);
		}
		else throw new JRException("no JasperDesign object");
		
	}
	
	public  void addPdfCustomFont() throws JRException {
		if( aJD==null)  throw new JRException("no JasperDesign object");
		for( JRBand a:aJRbrands ) {
	          for( JRElement b: a.getElements()) {
	        	if(b instanceof JRStaticText) {
	        		JRStaticText aJRS = ((JRStaticText)b); 
	        		aJRS.setFontName("Arial Unicode MS");
	        		//aJRS.setPdfEmbedded(true);
	        		aJRS.setPdfFontName("Arial Unicode MS");
	        		aJRS.setPdfEncoding("Identity-H");
	        	}else if( b instanceof JRTextField) {
	        		JRTextField aJRT = ((JRTextField)b);
	        		aJRT.setFontName("Arial Unicode MS");
	        		//aJRT.setPdfEmbedded(true);
	        		aJRT.setPdfFontName("Arial Unicode MS");
	        		aJRT.setPdfEncoding("Identity-H");
	        	}
	          }
		}
	}
	
}
