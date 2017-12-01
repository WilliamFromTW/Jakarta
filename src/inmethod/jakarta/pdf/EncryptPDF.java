package inmethod.jakarta.pdf;

import java.io.*;
import java.util.*;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

/**
 * Encrypt PDF and Hide PDF toolbar.
 * <pre> 
 * exmaple:
 *   java inmethod.jarkarta.pdf.EncryptPDF source_dir dest_dir user_pass owner_pass 
 * </pre>
 * @author william
 *
 */
public class EncryptPDF {

	private static EncryptPDF aEncryptPDF = null;


	/**
	 * 不給用
	 */
	private EncryptPDF() {
	}

	public static EncryptPDF getInstance() {
		if (aEncryptPDF == null)
			aEncryptPDF = new EncryptPDF();
		return new EncryptPDF();

	}
	
	/**
	 * Copy all PDF files from source directory to destination directory and encrypt PDF.
	 * @param args "source directory" "destination directory" "use password" "owner password" 
	 * "user password" for open pdf files.
	 * "owner password" is for unlock encryption. 
	 */
	public static void main(String[] args) throws Exception{
		if( args.length!=2 || args.length!=4) throw new Exception("arg number is wrong\n ex:\nEncryptPDF source dest user_pass owner_pass\n");
		
 	    EncryptPDF aConvert = EncryptPDF.getInstance();
 	    if( args.length==2)
		  aConvert.encryptFile(args[0], args[1],null,null);
 	    else if(args.length==4)
		  aConvert.encryptFile(args[0], args[1],args[2],args[3]);
 	    	
		
	}


	/**
	 * copy dir to dir , some specify file will encode
	 * 
	 * @param aSourceDir
	 *            source directory
	 * @param aDestDir
	 * @param sUserPass user password , can be null
	 * @param sOwnPass  owner password , can be null
	 *
	 */
	public void encryptFile(File aSourceDir, File aDestDir,String sUserPass,String sOwnPass) throws Exception {
		String sSourceDir = aSourceDir.getAbsolutePath();
		String sDestDir = aDestDir.getAbsolutePath();
		File aFiles[] = null;
		if (aSourceDir.isDirectory()) {
			aFiles = aSourceDir.listFiles();
			for (int i = 0; i < aFiles.length; i++) {
				if (aFiles[i].isDirectory())
					encryptFile(aFiles[i], new File(sDestDir + getNearestDir(aFiles[i].getPath())),sUserPass,sOwnPass);
				else if (aFiles[i].isFile()) {
					encrypSingleFile(aFiles[i], new File(sDestDir),sUserPass,sOwnPass);
				} else
					throw new Exception("Dir Copy Fail: file type error");
			}
		} else
			return;
	}

	/**
	 * copy dir to dir , some specify file will encode
	 * 
	 * @param sSourceDir source directory
	 * @param sDestDir  source directory
	 * @param sUserPass user password , can be null
	 * @param sOwnPass  owner password , can be null
	 *
	 */
	public void encryptFile(String sSourceDir, String sDestDir,String sUserPass,String sOwnPass) throws Exception {
		File aFiles[] = null;
		File aSourceDir = new File(sSourceDir);
		if (aSourceDir.isDirectory()) {
			aFiles = aSourceDir.listFiles();
			for (int i = 0; i < aFiles.length; i++) {
				if (aFiles[i].isDirectory())
					encryptFile(aFiles[i], new File(sDestDir + getNearestDir(aFiles[i].getPath())),sUserPass,sOwnPass);
				else if (aFiles[i].isFile()) {
					encrypSingleFile(aFiles[i], new File(sDestDir),sUserPass,sOwnPass);
				} else
					throw new Exception("Dir Copy Fail: file type error");
			}
		} else
			return;
	}	
	
	private String getNearestDir(String sDir) throws Exception {
		StringTokenizer aST = new StringTokenizer(sDir, System.getProperty("file.separator"));
		String sNearestDir = null;
		while (aST.hasMoreTokens()) {
			sNearestDir = aST.nextToken();
		}
		if (sNearestDir != null)
			return System.getProperty("file.separator") + sNearestDir;
		else
			throw new Exception("getNearestDir error");
	}

	/**
	 * Prerequisite before copy single pdf file for source directory to destination directory and encrypt,
	 * This should be overwrite.
	 * @param sourceFile
	 * @param destDir
	 */
	public void plugin(File sourceFile, File destDir) {
		// ExeShellCmd aExe = new ExeShellCmd();
		// String[] a = new String[2];
		// a[0] = "\"C:\\Program Files\\PDF Password Remover v3.1\\pdfdecrypt.exe\" -i
		// \""+ sourceFile.getAbsolutePath() +"\"";
		// a[1]="exit";
		// System.out.println(a[0]);
		//
		// aExe.exec(a);
	}


	/**
	 * copy file to dest directory
	 * 
	 * @param sourceFile
	 * @param destDir
	 */
	private void encrypSingleFile(File sourceFile, File destDir,String sUserPass,String sOwnPass) throws Exception {
		boolean bSuccess = false;
		byte[] buffer = new byte[4096]; // You can change the size of this if you want.
		destDir.mkdirs(); // creates the directory if it doesn't already exist.
		File destFile = new File(destDir, sourceFile.getName());
		FileInputStream in = new FileInputStream(sourceFile);
		FileOutputStream out = new FileOutputStream(destFile);
		System.out.println("convert pdf , name = " + sourceFile.getAbsolutePath());
		plugin(sourceFile, destDir);
		bSuccess = EncryptPDFAndHideToolBar(in, out,sUserPass,sOwnPass);
		out.close();
		in.close();
		if (!bSuccess) {
			System.out.println("Pdf encrypt fail , copy original to dest dir");
			fileCopy2DirWithNoEncrypt(sourceFile, destDir);
		}
	}

	/**
	 * copy file to dest directory [binary mode]
	 * 
	 * @param sourceFile
	 * @param destDir
	 */
	private void fileCopy2DirWithNoEncrypt(File sourceFile, File destDir) throws Exception {
		byte[] buffer = new byte[4096]; // You can change the size of this if you want.

		destDir.mkdirs(); // creates the directory if it doesn't already exist.

		File destFile = new File(destDir, sourceFile.getName());
		FileInputStream in = new FileInputStream(sourceFile);
		FileOutputStream out = new FileOutputStream(destFile);
		int len;
		int sum = 0;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
			sum += len;
		}
		out.close();
		in.close();
	}

	public boolean EncryptPDFAndHideToolBar(InputStream aIS, OutputStream aOS,String sUserPass,String sOwnPass) {
		try {
			PdfReader reader = new PdfReader(aIS);
			java.util.List pdfReaderList = new ArrayList<PdfReader>();
			pdfReaderList.add(reader);
			return EncryptPDFAndHideToolBar(pdfReaderList, aOS,sUserPass,sOwnPass);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Merge multi pdf and output pdf with hidden toolbar
	 */
	public boolean EncryptPDFAndHideToolBar(java.util.List pdfReaderList, OutputStream aOS,String sUserPass,String sOwnPass) {

		try {
			if (null != pdfReaderList && !pdfReaderList.isEmpty()) {
				Iterator iter = pdfReaderList.iterator();
				while (iter.hasNext()) {
					String pageNOs = "";
					PdfReader pdfReader = (PdfReader) iter.next();
					if (pdfReader.isEncrypted()) {
						return false;
					} else {
						byte[] byteUserPass = null;
						byte[] byteOwnPass = null;
						if(  sUserPass!=null ) byteUserPass = sUserPass.getBytes();
						if(  sOwnPass!=null ) byteOwnPass = sOwnPass.getBytes();
						
						WriterProperties aWP = new WriterProperties().setStandardEncryption(byteUserPass, byteOwnPass,
								~(EncryptionConstants.ALLOW_PRINTING | EncryptionConstants.ALLOW_SCREENREADERS
										| EncryptionConstants.ALLOW_MODIFY_ANNOTATIONS
										| EncryptionConstants.ALLOW_DEGRADED_PRINTING | EncryptionConstants.ALLOW_COPY
										| EncryptionConstants.ALLOW_MODIFY_CONTENTS
										| EncryptionConstants.ALLOW_ASSEMBLY),
								EncryptionConstants.ENCRYPTION_AES_256);
						PdfWriter aPW = new PdfWriter(aOS, aWP);
						PdfDocument pdfStamper = new PdfDocument(pdfReader, aPW);
						PdfViewerPreferences aPdfViewerPreferences = new PdfViewerPreferences();
						aPdfViewerPreferences.setHideToolbar(true);
						aPdfViewerPreferences.setHideWindowUI(true);
						pdfStamper.getCatalog().setViewerPreferences(aPdfViewerPreferences);
						pdfStamper.close();
						if (!aPW.isCloseStream())
							aPW.close();
						return true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}