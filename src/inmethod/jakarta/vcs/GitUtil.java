package inmethod.jakarta.vcs;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitUtil {

	private Git git;
	private String sRemoteUrl;
	private String sLocalDirectory;
	private File aLocalGitFile = null;

	private GitUtil() {
	}

	public void close() {
		if(git!=null)
		git.close();
	}
	
	public GitUtil(String sRemoteUrl, String sLocalDirectory) throws Exception {
		this.sRemoteUrl = sRemoteUrl;
		this.sLocalDirectory = sLocalDirectory;

		try {
			aLocalGitFile = new File(sLocalDirectory + "/.git");
			git = Git.open(aLocalGitFile);
		} catch (Exception ee) {
			// ee.printStackTrace();
		}
	}

	
	public  boolean createLocalRepository() {
		
		try {
			git =  Git.init().setDirectory( new File(sLocalDirectory)).call();
			return true;
		} catch (IllegalStateException | GitAPIException e) {
			e.printStackTrace();
		}
		return false;
		
	}

	public static String getGitShortName(String sName){
		return Repository.shortenRefName(sName);
	}
	
	/**
	 * clone from remote repository to local repository
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean clone(String sUserName, String sPasswd) {
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);

		try {
			Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory)).setCredentialsProvider(cp)
					.setCloneAllBranches(true).call();
			if (git == null) {
				aLocalGitFile = new File(sLocalDirectory + "/.git");
				git = Git.open(aLocalGitFile);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * get default checkout repository
	 * @return
	 */
	public String getDefaultBranch() {
		try {
			// System.out.println("default branch is
			// "+git.getRepository().getBranch());
			return git.getRepository().getBranch();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean removeLocalGitRepository() {
		if (this.sLocalDirectory == null)
			return false;
		try {
			FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
			Repository repository = repositoryBuilder.setGitDir(aLocalGitFile).findGitDir().build();
			// System.out.println("default branch = " + repository.getBranch());
			
			if (repository.findRef("HEAD") != null) {
				return deleteDir(repository.getDirectory().getParentFile());
			} else {
				return false;
			}
		} catch (Exception ee) {
			return false;
		}
	}
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
	/**
	 * check local directory is git repository
	 * @return
	 */
	public boolean checkLocalRepository() {
		if (this.sLocalDirectory == null)
			return false;
		try {
			FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
			Repository repository = repositoryBuilder.setGitDir(aLocalGitFile).findGitDir().build();
			// System.out.println("default branch = " + repository.getBranch());
			
			if (repository.findRef("HEAD") != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ee) {
			return false;
		}
	}

	/**
	 * No Need for user name and password.
	 * @return
	 */
	public boolean checkRemoteRepository() {
		return checkRemoteRepository(null, null);
	}

	/**
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean checkRemoteRepository(String sUserName, String sPasswd) {
		if (sRemoteUrl == null)
			return false;

		try {

			if (sUserName != null && sPasswd != null) {
				X509TrustManager a = new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}

					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}
				};
				TrustManager[] trustAllCerts = new TrustManager[] { a };
				// Install the all-trusting trust manager
				try {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				} catch (GeneralSecurityException e) {
					// e.printStackTrace();
				}
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);

				Git.lsRemoteRepository().setRemote(sRemoteUrl).setCredentialsProvider(cp).call();
			} else
				Git.lsRemoteRepository().setRemote(sRemoteUrl).call();
			return true;
		} catch (GitAPIException e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * get all local tag object.
	 * @return
	 */
	public List<Ref> getLocalTags() {
		try {
			return git.tagList().call();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param aRefTag
	 * @return
	 */
	public String getCommitMessageByTagName(Ref aRefTag) {
		try {
			RevWalk walk = new RevWalk(git.getRepository());
			String sMessage = walk.parseCommit(((Ref) aRefTag).getObjectId()).getFullMessage();
			walk.close();
			return sMessage;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * get Tag create date
	 * @param aTag
	 * @return format yyyyMMddHHmm
	 */
	public String getTagDate(Ref aTag){
		return getTagDate(aTag,"yyyyMMddHHmm");
	}	

	/**
	 * get Tag create date
	 * @param aTag
	 * @param sFormat SimpleDateFormat ex: "yyyyMMddHHmm"
	 * @return format yyyyMMddHHmm
	 */
	public String getTagDate(Ref aTag,String sFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
		
		final RevWalk walk = new RevWalk(git.getRepository());
		try {
			return  sdf.format(walk.parseTag(aTag.getObjectId()).getTaggerIdent().getWhen());
		} catch (IOException e) {
			return "";
		}
	}
	
	/**
	 * get commit  date
	 * @param aTag
	 * @return format yyyyMMddHHmm
	 */
	public String getCommitDate(Ref aTag){
		return getCommitDate(aTag,"yyyyMMddHHmm");
	}	

	/**
	 * get Commit  date
	 * @param aTag
	 * @param sFormat SimpleDateFormat ex: "yyyyMMddHHmm"
	 * @return format yyyyMMddHHmm
	 */
	public String getCommitDate(Ref aTag,String sFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
		final RevWalk walk2 = new RevWalk(git.getRepository());
		
		try {
			return  sdf.format(walk2.parseCommit(((Ref) aTag).getObjectId()).getCommitTime()*1000L );
		} catch (IOException e) {
			return "";
		}
	}
	
	public List<Ref> getBranches() {
		try {
			return Git.open(aLocalGitFile).branchList().setListMode(ListMode.ALL).call();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * checkout repository by  a tag name or branch name (compatible for short name or long name)
	 * @param sTagName
	 * @return
	 */
	public boolean checkout(String sTagName) {
		try {
			Git.open(aLocalGitFile).checkout().setName(sTagName).call();
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	/**
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @param sMessage
	 * @return
	 */
	public boolean commit(String sMessage) {
		try {
			git.add().setUpdate(true).addFilepattern(".").call();
			git.add().addFilepattern(".").call();
			git.commit().setMessage( sMessage ).call();
			return true;
		}catch(Exception ee) {
			ee.printStackTrace();
		}
		return false;
	}	

	/**
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @param sMessage
	 * @return
	 */
	public boolean commit(String sMessage,String sAuthorName,String sAuthorEmail) {
		try {
			git.add().setUpdate(true).addFilepattern(".").call();
			git.add().addFilepattern(".").call();
			git.commit().setAuthor(sAuthorName,sAuthorEmail).setMessage( sMessage ).call();
			return true;
		}catch(Exception ee) {
			ee.printStackTrace();
		}
		return false;
	}	
	
	/**
	 * 
	 * @param sRemote default is origin
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean push(String sRemote,String sUserName, String sPasswd) {	
		try {
			if (sUserName != null && sPasswd != null) {
				X509TrustManager a = new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}

					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}
				};
				TrustManager[] trustAllCerts = new TrustManager[] { a };
				
				try {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				} catch (GeneralSecurityException e) {
					// e.printStackTrace();
				}
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);
				git.push().setCredentialsProvider(cp).setRemote(sRemote).call();
			}			
			return true;
		}catch(Exception ee) {
			ee.printStackTrace();
		}
		return false;
		
	}

	/**
	 * No Need for user name and password.
	 * 
	 * @return
	 */
	public boolean pull() {
		return pull(null, null);
	}

	/**
	 * update must apply user id and password.
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean pull(String sUserName, String sPasswd) {
		try {
			if (sUserName != null && sPasswd != null) {
				X509TrustManager a = new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}

					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
					}
				};
				TrustManager[] trustAllCerts = new TrustManager[] { a };
				
				try {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				} catch (GeneralSecurityException e) {
					// e.printStackTrace();
				}
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);
				git.pull().setCredentialsProvider(cp).setRemoteBranchName(getDefaultBranch()).call();
			} else
				git.pull().setRemoteBranchName(getDefaultBranch()).call();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * check local directory is git repository.
	 * @param sDirectory
	 * @return
	 */
	public static boolean checkLocalRepository(String sDirectory) {
		if (sDirectory == null)
			return false;
		try {
			FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
			Repository repository = repositoryBuilder.setGitDir(new File(sDirectory)).findGitDir().build();
			// System.out.println("default branch = " + repository.getBranch());

			if (repository.findRef("HEAD") != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ee) {
			return false;
		}
	}

	public static void main(String ar[]) {
		try {
			GitUtil aGitUtil = new GitUtil(null, "/tmp/asdf/local");
			if( !aGitUtil.checkLocalRepository() ) {
				aGitUtil.createLocalRepository();
			}
			aGitUtil.commit("asdf");
			
		}catch(Exception ee) {
			
		}
	}
	
	/**
	 * Command Mode for test .
	 * 
	 * <pre>
	 * GitUtil [git https remote url] [local directory] [login id] [login password]
	 * </pre>
	 * 
	 * @param ar
	 */
	public static void main2(String ar[]) {
		GitUtil aGitUtil;
		
		String sRemoteUrl = ar[0];
		String sLocalDirectory = ar[1];
		String sUserName = ar[2];
		String sUserPassword = ar[3];

		
		
		try {
			aGitUtil = new GitUtil(sRemoteUrl, sLocalDirectory);

			System.out.println("Remote repository exists ? " + aGitUtil.checkRemoteRepository(sUserName, sUserPassword));
			System.out.println("Local repository exists ? " + aGitUtil.checkLocalRepository());
			if (aGitUtil.checkRemoteRepository(sUserName, sUserPassword) && !aGitUtil.checkLocalRepository()) {
				System.out.println("try to clone remote repository if local repository is not exists \n");
				if (aGitUtil.clone(sUserName, sUserPassword))
					System.out.println("clone finished!");
				else
					System.out.println("clone failed!");
			} else if (aGitUtil.checkRemoteRepository(sUserName, sUserPassword) && aGitUtil.checkLocalRepository()) {
				System.out.println("pull branch = " + aGitUtil.getDefaultBranch() + " , status : "
						+ aGitUtil.pull(sUserName, sUserPassword));
			}

			System.out.println("Default branch : " + aGitUtil.getDefaultBranch());
			if (aGitUtil.checkLocalRepository()) {
				List<Ref> aAllBranches = aGitUtil.getBranches();
				if (aAllBranches != null) {
					System.out.println("\nList All Local Branch Name\n--------------------------------");
					for (Ref aBranch : aAllBranches) {
						System.out.println("branch : " +  aBranch.getName());
					}
					System.out.println("");
				}
				System.out.println("Switch local branch to master: " + aGitUtil.checkout("master"));
				List<Ref> aAllTags = aGitUtil.getLocalTags();
				if (aAllTags != null) {
					System.out.println("\nList All Local Tags Name\n--------------------------------");
					for (Ref aTag : aAllTags) {
						System.out.println("Tag : " + aTag.getName() +"("+aGitUtil.getTagDate(aTag,"yyyy-MM-dd HH:mm:ss")+" created!)" );
						System.out.println("Commit messages\n==\n" + aGitUtil.getCommitMessageByTagName(aTag) + "\n");
					}
					System.out.println("");
				}
				aGitUtil.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
