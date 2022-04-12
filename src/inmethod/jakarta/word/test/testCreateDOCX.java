package inmethod.jakarta.word.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import inmethod.jakarta.word.CreateDOCX;
import inmethod.jakarta.word.ICreateDOCX;

public class testCreateDOCX {
	public static void main(String[] args) throws IOException {

        String fileName = "/opt/PR-DCF.docx";
        try {
            ICreateDOCX aCreateDOCX = new CreateDOCX("/opt/PR-DCF2.docx","/opt/PR-DCF3.docx");
			aCreateDOCX.addReplace("機型", "咖啡偶\r\n(一)asdf\r\n(二)asdfasdf");
			aCreateDOCX.buildDOCX();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}				
    }
    
	
}
