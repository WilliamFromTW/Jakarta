package inmethod.jakarta.jasper.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import inmethod.commons.rdb.OracleConnection;
import inmethod.commons.rdb.SQLTools;
import inmethod.jakarta.jasper.JasperReportManager;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;

public class testJasper {
	public static void main(String[] args) {
		try (Connection con = new OracleConnection("localhost", "TOPPROD", "system", "manager")
				.getConnection()) {

			java.sql.Date sqlDate = new java.sql.Date(
					(new SimpleDateFormat("MM/dd/yyyy").parse("07/01/2018")).getTime());
			
			JasperReportManager aJasperReportManager = new JasperReportManager("\\tmp\\BackEndTiptop009.jasper", "/tmp/aaa.pdf");
			aJasperReportManager.setConnection(con);
			aJasperReportManager.addParameter("sDate1", "20190101");
			aJasperReportManager.addParameter("sDate2", "20190130");
			aJasperReportManager.buildPDF();

			JasperReportManager aJasperReportManager2 = new JasperReportManager("\\tmp\\BackEndTiptop009.jasper", "/tmp/aaa.xlsx");
			aJasperReportManager2.setConnection(con);
			aJasperReportManager2.addParameter("paramName", sqlDate);
			aJasperReportManager2.buildExcel();
			
		} catch (Exception ex) {

		}
	}
}
