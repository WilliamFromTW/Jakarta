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
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class JasperReportManager {

	private java.io.OutputStream aOutput;
	private java.io.InputStream aInput;
	private Map<String, Object> aMap;
	private Connection aConn;
	private JasperReport report;
	private JasperPrint jasperPrint;
    private JRBand[] aAllBrands;
    
	private JasperReportManager() {

	}
	

	
	/**
	 * 
	 * @param sXMLFile   jasper report file 
	 * @param sFileOutputStreamPDF   generate report pdf file
	 * @throws Exception
	 */
	public JasperReportManager(String sXMLFile, String sFileOutputStreamPDF) throws Exception {
		report = (JasperReport) JRLoader.loadObjectFromFile(sXMLFile);
		this.aAllBrands = report.getAllBands();
		aOutput = new FileOutputStream(sFileOutputStreamPDF);
	}

	/**
	 * 
	 * @param aIS  jasper report file 
	 * @param aOS
	 * @throws Exception
	 */
	public JasperReportManager(InputStream aIS, OutputStream aOS) throws Exception {
		aOutput = aOS;
		aInput = aIS;
		report = (JasperReport) JRLoader.loadObject(aInput);
	}

	public void addParameter(Map<String, String> paraMap) {
		if( report==null) {
			System.out.println("no JasperReport object");
			return;
		}
		if (aMap == null)
			aMap = new HashMap<String, Object>();
		aMap.putAll(paraMap);
	}

	public void addParameter(String aKey, Object value) {
		if( report==null) {
			System.out.println("no JasperReport object");
			return;
		}

		if (aMap == null)
			aMap = new HashMap<String, Object>();
		aMap.put(aKey, value);
	}

	public void setConnection(Connection paraConn) {
		aConn = paraConn;
	}

	public JRElement getElementByKey(String sKey) {
		if( report==null) {
			System.out.println("no JasperReport object");
			return null;
		}
		JRElement aJRE = null;
        for(JRBand aJRB:this.aAllBrands) {
        	aJRE = aJRB.getElementByKey(sKey);
        	if( aJRE!=null ) {
        		return aJRE;
        	}
        }
		return null;
	}
	
	public void buildExcel() throws Exception {
		if( report==null) {
			System.out.println("no JasperReport object");
			return;
		}

		JRXlsxExporter exporter = new JRXlsxExporter();

		jasperPrint = JasperFillManager.fillReport(report, aMap, aConn);
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(aOutput));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setDetectCellType(true);// Set configuration as you like it!!
		configuration.setCollapseRowSpan(true);
		configuration.setOnePagePerSheet(false);
		configuration.setRemoveEmptySpaceBetweenRows(true);
		configuration.setRemoveEmptySpaceBetweenColumns(true);
		configuration.setDetectCellType(true);
		configuration.setShrinkToFit(true);
		exporter.setConfiguration(configuration);

		exporter.exportReport();
	}

	public void buildPDF() throws Exception {
		if (aMap == null)
			aMap = new HashMap<String, Object>();
		jasperPrint = JasperFillManager.fillReport(report, aMap, aConn);

		// JasperReportsContext jasperReportsContext =
		// DefaultJasperReportsContext.getInstance();

		// report.setProperty("net.sf.jasperreports.default.pdf.font.name","Arial
		// Unicode MS");
		// report.setProperty("net.sf.jasperreports.default.pdf.encoding",
		// "Identity-H");
		/*
		 * JFrame frame = new JFrame("Report");
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * frame.getContentPane().add(new JRViewer(jasperPrint)); frame.pack();
		 * frame.setVisible(true);
		 */
		JasperExportManager.exportReportToPdfStream(jasperPrint, aOutput);

		aOutput.flush();
		aOutput.close();

	}

}
