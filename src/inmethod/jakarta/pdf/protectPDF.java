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
public class protectPDF {

	private static protectPDF aEncryptPDF = null;


	/**
	 * 不給用
	 */
	private protectPDF() {
	}

	public static protectPDF getInstance() {
		if (aEncryptPDF == null)
			aEncryptPDF = new protectPDF();
		return new protectPDF();

	}
	
	/**
	 * Copy all PDF files from source directory to destination directory and encrypt PDF.
	 * @param args "source directory" "destination directory" "use password" "owner password" 
	 * "user password" for open pdf files.
	 * "owner password" is for unlock encryption. 
	 */
	public static void main(String[] args) {
		
		System.out.println("args length=" +args.length);
		
		if( args.length!=2 && args.length!=4) {
			System.out.println("arg number is wrong\nExample:\nEncryptPDF source dest user_pass owner_pass\n");
		}	
		
 	    protectPDF aConvert = protectPDF.getInstance();
 	    if( args.length==2)
		  aConvert.encryptFile(args[0], args[1],null,null);
 	    else if(args.length==4) {
 	    	System.out.println(args[2].toString());
 	      if( args[2].equalsIgnoreCase("null") )
		    aConvert.encryptFile(args[0], args[1],null,args[3].getBytes());
 	      else
		    aConvert.encryptFile(args[0], args[1],args[2].getBytes(),args[3].getBytes());
 	    }
	}



	/**
	 * copy dir to dir , some specify file will encode
	 * 
	 * @param sSourceDir source directory
	 * @param sDestDir  source directory
	 * @param byteUserPass user password , can be null
	 * @param byteOwnerPass  owner password , can be null
	 *
	 */
	public boolean encryptFile(String sSourceDir, String sDestDir,byte[] byteUserPass,byte[] byteOwnerPass) {
		return encryptFile(new File(sSourceDir),new File(sDestDir),byteUserPass,byteOwnerPass);
	}	
	/**
	 * copy dir to dir , some specify file will encode
	 * 
	 * @param aSourceDir
	 *            source directory
	 * @param aDestDir
	 * @param byteUserPass user password , can be null
	 * @param byteOwnerPass  owner password , can be null
	 *
	 */
	public boolean encryptFile(File aSourceDir, File aDestDir,byte[] byteUserPass,byte[] byteOwnerPass) {
		String sSourceDir = aSourceDir.getAbsolutePath();
		String sDestDir = aDestDir.getAbsolutePath();
		File aFiles[] = null;
		boolean bSuccess = false;
		if (aSourceDir.isDirectory()) {
			aFiles = aSourceDir.listFiles();
			for (int i = 0; i < aFiles.length; i++) {
				if (aFiles[i].isDirectory())
					try {
						bSuccess = encryptFile(aFiles[i], new File(sDestDir + getNearestDir(aFiles[i].getPath())),byteUserPass,byteOwnerPass);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						bSuccess = false;
					}
				else if (aFiles[i].isFile()) {
					bSuccess = encrypSingleFile(aFiles[i], new File(sDestDir),byteUserPass,byteOwnerPass);
				} else {
				  System.out.println("Dir Copy Fail: file type error");
				}
			}
		} 
		return bSuccess;
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
	private boolean encrypSingleFile(File sourceFile, File destDir,byte[] byteUserPass,byte[] byteOwnerPass) {
		boolean bSuccess = true;
		destDir.mkdirs(); // creates the directory if it doesn't already exist.
		File destFile = new File(destDir, sourceFile.getName());
		FileInputStream in;
		FileOutputStream out;
		   if( isExtName(sourceFile.getName())) {
		try {
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destFile);
		  System.out.println("convert pdf , name = " + sourceFile.getAbsolutePath());
		  plugin(sourceFile, destDir);
		  bSuccess = EncryptPDFAndHideToolBar(in, out,byteUserPass,byteOwnerPass);
		  out.close();
		  in.close();
		   
		  } catch (Exception e) {
			bSuccess = false;
		  }
		
		if (!bSuccess) {
			System.out.println("Pdf encrypt fail , copy original to dest dir");
			fileCopy2DirWithNoEncrypt(sourceFile, destDir);
		}
		   }
		return bSuccess;
	}

	/**
	 * copy file to dest directory [binary mode]
	 * 
	 * @param sourceFile
	 * @param destDir
	 */
	private boolean fileCopy2DirWithNoEncrypt(File sourceFile, File destDir)  {
		byte[] buffer = new byte[4096]; // You can change the size of this if you want.

		destDir.mkdirs(); // creates the directory if it doesn't already exist.
        boolean bSuccess = true;
		try {
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
		}catch(Exception ee) {
			bSuccess = false;
		}
		return bSuccess;
	}

	private boolean EncryptPDFAndHideToolBar(InputStream aIS, OutputStream aOS,byte[] byteUserPass,byte[] byteOwnerPass) {
		try {
			PdfReader reader = new PdfReader(aIS);
			java.util.List pdfReaderList = new ArrayList<PdfReader>();
			pdfReaderList.add(reader);
			return EncryptPDFAndHideToolBar(pdfReaderList, aOS,byteUserPass,byteOwnerPass);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Merge multi pdf and output pdf with hidden toolbar
	 */
	private boolean EncryptPDFAndHideToolBar(java.util.List pdfReaderList, OutputStream aOS,byte[] byteUserPass,byte[] byteOwnerPass) {

		try {
			if (null != pdfReaderList && !pdfReaderList.isEmpty()) {
				Iterator iter = pdfReaderList.iterator();
				while (iter.hasNext()) {
					PdfReader pdfReader = (PdfReader) iter.next();
					if (pdfReader.isEncrypted()) {
						return false;
					} else {
						pdfReader.setUnethicalReading(true);

						WriterProperties aWP = new WriterProperties().setStandardEncryption(byteUserPass, byteOwnerPass,
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
	  private  boolean isExtName(String sFile){
		    String sExt = null;
		    String kk = null;
		    StringTokenizer aST = new StringTokenizer(sFile,".");
		    while( aST.hasMoreTokens() ){
		      sExt=aST.nextToken();
		    }
		    if( "pdf".equalsIgnoreCase (sExt) )
		        return true;
		    else
		      return false;
		  }
}