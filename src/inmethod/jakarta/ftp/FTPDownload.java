package inmethod.jakarta.ftp;
import org.apache.commons.net.ftp.*;
import java.io.*;

/**
 * 
 * Use apache  net component and jakarta oro package
 * to download file from specify ftp server.
 * <pre>
 * Here is the sample code.
 *    FTPDownload aFtp = new FTPDownload("10.192.130.1","guest","1111");
 *    aFtp.changeWorkingDirectory("download");
 *    aFtp.downloadAllFiles("c:/client/b");
 *    aFtp.disconnect();
 * </pre>
 */
public class FTPDownload{

  private FTPClient aClient;
  private String sHost;
  private String sPort;
  private String sUID;
  private String sPWD;

  /**
   * Disable null parameter in construction
   */
  private FTPDownload(){
  }

  /**
   *  constructor
   * @param sHost ftp server
   * @param sUID  user name
   * @param sPWD password
   */
  public FTPDownload(String sHost,String sUID,String sPWD){
    this.sHost = sHost;
    this.sUID = sUID;
    this.sPWD = sPWD;
    try {
      int reply;
      aClient = new FTPClient();
      aClient.connect(sHost);
      System.out.println("Connected to " + sHost + ".");
      System.out.println("Reply string : "+aClient.getReplyString());

      // After connection attempt, you should check the reply code to verify
      // success.
      reply = aClient.getReplyCode();

      if(!FTPReply.isPositiveCompletion(reply)) {
        aClient.disconnect();
        System.out.println("FTP server refused connection.");
        System.exit(1);
      }
      if( !aClient.login(sUID,sPWD) ){
        aClient.disconnect();
        System.out.println("Login Error");
        System.exit(1);
      }
      System.out.println("Reply string : "+aClient.getReplyString());
    } catch(IOException e) {
      e.printStackTrace();
      if(aClient.isConnected()) {
        try {
          aClient.disconnect();
        } catch(IOException f) {
         f.printStackTrace();
        }
      }
      
      System.exit(1);
    }

  }

  /**
   * Get FileList in working directory at ftp server
   * @return FTPFile[]
   */
  public FTPFile[] getFileList(){
    if( aClient == null ) System.exit(1);
    try{

    	System.out.println("Reply string : "+aClient.getReplyString());
      return aClient.listFiles();

    }catch(Exception ex){
    	ex.printStackTrace();
      return null;
    }
  }

  /**
   * Change working directory at ftp server
   * @param sPath working directory path ame
   */
  public void changeWorkingDirectory(String sPath){
    if( aClient == null ) System.exit(1);
    try{
    	System.out.println("preparing to change dir to "+sPath);
      if(!aClient.changeWorkingDirectory(sPath)){
        aClient.disconnect();
        System.out.println("changeWorkingDirectory Error");
        System.exit(1);
      }
      System.out.println("Reply string : "+aClient.getReplyString());
    }catch(Exception ex){
      ex.printStackTrace();
      return ;
    }

  }

  /**
   *
   */
  public void downloadFile(String sFileName){
    java.io.FileOutputStream aFOS = null;

    if( sFileName ==null || aClient == null ) System.exit(1);
    try{
      aFOS = new FileOutputStream( sFileName ) ;
      aClient.retrieveFile(sFileName ,aFOS);
      aFOS.close();
      System.out.println("Reply string : "+aClient.getReplyString());
    }catch(Exception ex){
    	ex.printStackTrace();
      return ;
    }
  }

  /**
   * Download all files to specify local directory.
   * @param sDir local directory name
   */
  public void downloadAllFiles(String sDir){
    FTPFile[] aFile = null;
    new File(sDir).mkdir();
    java.io.FileOutputStream aFOS = null;
    if( aClient == null ) System.exit(1);
    try{
      aClient.setFileType(FTP.BINARY_FILE_TYPE);
      aFile = aClient.listFiles();
      for(int i=0;i<aFile.length;i++){
        if( aFile[i].isDirectory() ){
     //     java.io.File file = new File(aFile[i].getName());
       //   file.mkdir();
          changeWorkingDirectory(aFile[i].getName() );
          downloadAllFiles(sDir+"/"+aFile[i].getName());
          changeWorkingDirectory( ".." );
        }
        if( aFile[i].isFile() ){
          aFOS = new FileOutputStream(sDir+"/"+aFile[i].getName());
          aClient.retrieveFile(aFile[i].getName() ,aFOS);
          System.out.println("Reply string : "+aClient.getReplyString());
          aFOS.close();
        }
      }
    }catch(Exception ex){
    	ex.printStackTrace();
      return ;
    }

  }

  /**
   * disconnect to the ftp server
   */
  public void disconnect(){
    if( aClient==null) System.exit(1);
    try{
      aClient.disconnect();
      System.out.println("Reply string : "+aClient.getReplyString());
    }catch(Exception ex){
    	ex.printStackTrace();
    }
  }

  public static void main(String argv[]){
    FTPDownload aFtp = new FTPDownload("10.192.130.1","guest","1111");
    aFtp.changeWorkingDirectory("download");
    aFtp.downloadAllFiles("c:/client/b");
    aFtp.disconnect();
  }

}