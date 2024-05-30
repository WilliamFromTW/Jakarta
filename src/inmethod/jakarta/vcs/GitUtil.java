package inmethod.jakarta.vcs;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.ContentMergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.jgit.api.CreateBranchCommand;

public class GitUtil {

	private Git git;
	private String sRemoteUrl;
	private String sLocalDirectory;
	private File aLocalGitFile = null;
	private String sRemoteDefaultBranch = "master";
    private PullResult aPR;
    private ContentMergeStrategy aContentMergeStrategy = null;
    
	private GitUtil() {
	}

	public Git getGit() {
		return git;
	}
	
	public void setContentMergeStrategyOURS() {
		aContentMergeStrategy = ContentMergeStrategy.OURS;
	}
	
	public void setContentMergeStrategyTHEIRS() {
		aContentMergeStrategy = ContentMergeStrategy.THEIRS;		
	}
	
	public void setContentMergeStrategyCONFLICT() {
		aContentMergeStrategy = ContentMergeStrategy.CONFLICT;		
		
	}

	public void close() {
		if (git != null)
			git.close();
	}

	/**
	 * get pullResult after call pull method
	 * @return
	 */
	public PullResult getPullResult() {
		return aPR;
	}
	public GitUtil(String sRemoteUrl, String sLocalDirectory) throws Exception {
		this.sRemoteUrl = sRemoteUrl;
		this.sLocalDirectory = sLocalDirectory;
       
		try {
			aLocalGitFile = new File(sLocalDirectory + "/.git");
			git = Git.open(aLocalGitFile);
		} catch (Exception ee) {
			 ee.printStackTrace();
		}
	}

	/**
	 * create local git repository
	 * 
	 * @return
	 */
	public boolean createLocalRepository() {

		try {
			git = Git.init().setDirectory(new File(sLocalDirectory)).call();
			return true;
		} catch (IllegalStateException | GitAPIException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static String getGitShortName(String sName) {
		return Repository.shortenRefName(sName);
	}

	/**
	 * clone from remote repository to local repository
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean clone(String sUserName, String sPasswd) {
		return clone(sUserName,sPasswd,false);
	}
	
	public boolean clone(String sUserName, String sPasswd,boolean isBare) {

		try {
			if (sUserName != null && sPasswd != null) {

				// Install the all-trusting trust manager
	
					SSLContext sc = SSLContext.getInstance("SSL");

					sc.init(null, null, new java.security.SecureRandom());
					
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);

				if(isBare) {
					try {
					Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory+"/.git"))
					.setCredentialsProvider(cp).setBare(true).setCloneAllBranches(true) .call();
					}catch(Exception ee) {
						ee.printStackTrace();
					}
				}
				else {
				Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory))
						.setCredentialsProvider(cp).setCloneAllBranches(true).call();
				}
			} else {
				if(isBare) {
					Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory+"/.git")).setBare(true)
					.setCloneAllBranches(true).call();
				}
				else {
				Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory))
						.setCloneAllBranches(true).call();
				}				

			}
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
	 * clone from remote repository to local repository
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean clone(String sUserName, String sPasswd,int Depth) {

		try {
			if (sUserName != null && sPasswd != null) {

				// Install the all-trusting trust manager
	
					SSLContext sc = SSLContext.getInstance("SSL");

					sc.init(null, null, new java.security.SecureRandom());
					
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);

				Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory))
						.setCredentialsProvider(cp).setCloneAllBranches(true).setDepth(Depth).call();
				
			} else {
				Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory))
						.setCloneAllBranches(true).call();

			}
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
	 * clone from remote repository to local repository
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean cloneIgnoreCertification(String sUserName, String sPasswd) {
		return cloneIgnoreCertification(sUserName,sPasswd,false);
	}
	
	public boolean cloneIgnoreCertification(String sUserName, String sPasswd,boolean isBare) {

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
				if(isBare) {
					Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory+"/.git"))
					.setCredentialsProvider(cp).setBare(true).setCloneAllBranches(true) .call();
				}
				else {
				Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory))
						.setCredentialsProvider(cp).setCloneAllBranches(true).call();
				}
			} else {
				if(isBare) {
					Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory+"/.git")).setBare(true)
					.setCloneAllBranches(true).call();
				}
				else {
				Git.cloneRepository().setURI(sRemoteUrl).setDirectory(new File(sLocalDirectory))
						.setCloneAllBranches(true).call();
				}
			}
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
	 * 
	 * @return
	 */
	public String getLocalDefaultBranch() {
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
			for (int i = 0; i < children.length; i++) {
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
	 * 
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
	 * 
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
				SSLContext sc = SSLContext.getInstance("SSL");

				sc.init(null, null, new java.security.SecureRandom());
				
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);

				Git.lsRemoteRepository().setRemote(sRemoteUrl).setCredentialsProvider(cp).call();
			} else
				Git.lsRemoteRepository().setRemote(sRemoteUrl).call();
			return true;
		} catch (Exception e) {
			 e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean checkRemoteRepositoryIgnoreCertification(String sUserName, String sPasswd) {
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
	 * 
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
	public String getCommitMessageByTagName(Ref aRefTag) throws Exception {
			RevWalk walk = new RevWalk(git.getRepository());
			String sMessage = walk.parseCommit(((Ref) aRefTag).getObjectId()).getFullMessage();
			walk.close();
			return sMessage;
	}

	/**
	 * get Tag create date
	 * 
	 * @param aTag
	 * @return format yyyyMMddHHmm
	 */
	public String getTagDate(Ref aTag) {
		return getTagDate(aTag, "yyyyMMddHHmm");
	}

	/**
	 * get Tag create date
	 * 
	 * @param aTag
	 * @param sFormat SimpleDateFormat ex: "yyyyMMddHHmm"
	 * @return format yyyyMMddHHmm
	 */
	public String getTagDate(Ref aTag, String sFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat);

		final RevWalk walk = new RevWalk(git.getRepository());
		try {
			return sdf.format(walk.parseTag(aTag.getObjectId()).getTaggerIdent().getWhen());
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * get commit date
	 * 
	 * @param aTag
	 * @return format yyyyMMddHHmm
	 */
	public String getCommitDate(Ref aTag) {
		return getCommitDate(aTag, "yyyyMMddHHmm");
	}

	/**
	 * get Commit date
	 * 
	 * @param aTag
	 * @param sFormat SimpleDateFormat ex: "yyyyMMddHHmm"
	 * @return format yyyyMMddHHmm
	 */
	public String getCommitDate(Ref aTag, String sFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
		final RevWalk walk2 = new RevWalk(git.getRepository());

		try {
			return sdf.format(walk2.parseCommit(((Ref) aTag).getObjectId()).getCommitTime() * 1000L);
		} catch (IOException e) {
			return "";
		}
	}

	public List<Ref> getLocalBranches() {
		try {
			return Git.open(aLocalGitFile).branchList().setListMode(ListMode.ALL).call();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * checkout repository by a tag name or branch name (compatible for short name
	 * or long name)
	 * 
	 * @param sBranchName
	 * @return
	 */
	public boolean checkout(String sBranchName) throws Exception{
		try {
			git.branchDelete().setBranchNames(sBranchName).setForce(true).call();
			setRemoteDefaultBranch(sBranchName);
		}catch(CannotDeleteCurrentBranchException ee) {
			ee.printStackTrace();
		}
		try {
			//System.out.println("branch name="+sBranchName);
			git.checkout().setCreateBranch(true).setName(sBranchName).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
					.setStartPoint("origin/" + sBranchName).call();
		}catch(RefAlreadyExistsException ee) {
			ee.printStackTrace();
		}
			return true;
	}
	
	

	/**
	 * 
	 * @param sMessage
	 * @return
	 */
	public boolean commit(String sMessage)  throws Exception{
			git.add().setUpdate(true).addFilepattern(".").call();
			git.add().addFilepattern(".").call();
			git.commit().setMessage(sMessage).call();
			return true;
	}

	/**
	 * 
	 * @param sMessage
	 * @param sAuthorName
	 * @param sAuthorEmail
	 * @return
	 */
	public boolean commit(String sMessage, String sAuthorName, String sAuthorEmail)  throws Exception{
			git.add().setUpdate(true).addFilepattern(".").call();
			git.add().addFilepattern(".").call();
			git.commit().setAuthor(sAuthorName, sAuthorEmail).setMessage(sMessage).call();
			return true;
	}

	public boolean push(String sUserName, String sPasswd) {
		return push(null, sUserName, sPasswd);
	}

	/**
	 * push local repository to remote server.
	 * @param sRemote
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean push(String sRemote, String sUserName, String sPasswd) {
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
			} else {
				git.push().setRemote(sRemote).call();

			}
			return true;
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return false;

	}

	/**
	 * 
	 * @param sRemote
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean pushIgnoreCertification(String sRemote, String sUserName, String sPasswd) {
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
			} else {
				git.push().setRemote(sRemote).call();

			}
			return true;
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return false;

	}
	
	protected File getFileListByTag(String sTag) {
		
		return git.getRepository().getWorkTree(); 
	}
	
	/**
	 * No Need for user name and password.
	 * 
	 * @return
	 */
	public boolean pull() throws Exception{
		return pull(null, null);
	}

	public String getRemoteDefaultBranch() {
		return sRemoteDefaultBranch;
	}

	public void setRemoteDefaultBranch(String param) {
		sRemoteDefaultBranch = param;
	}

	/**
	 * update must apply user id and password.
	 * 
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean pull(String sUserName, String sPasswd) throws Exception{
		return pull(getRemoteDefaultBranch(), sUserName, sPasswd);
	}

	/**
	 * update must apply user id and password.
	 * 
	 * @param sBranch
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean pull(String sBranch, String sUserName, String sPasswd)  throws Exception{
			
			if (sUserName != null && sPasswd != null) {

				try {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, null, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}

				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPasswd);
				if( aContentMergeStrategy!=null)
				  aPR = git.pull().setCredentialsProvider(cp).setContentMergeStrategy(aContentMergeStrategy) .setRemoteBranchName(sBranch).call();
				else
				  aPR = git.pull().setCredentialsProvider(cp).setRemoteBranchName(sBranch).call();
		
			} else
				aPR = git.pull().setRemoteBranchName(sBranch).call();
			return true;
	}

	/**
	 * update must apply user id and password.
	 * 
	 * @param sBranch
	 * @param sUserName
	 * @param sPasswd
	 * @return
	 */
	public boolean pullIgnoreCertification(String sBranch, String sUserName, String sPasswd)  throws Exception{
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
				git.pull().setCredentialsProvider(cp).setRemoteBranchName(sBranch).call();
			} else
				git.pull().setRemoteBranchName(sBranch).call();
			return true;
	}
	
	
	/**
	 * check local directory is git repository.
	 * 
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
	
	public static Collection<Ref> getRemoteTagListIgnoreCertification(String sRemoteUrl,String sUserName,String sUserPassword) {
		Collection<Ref> tags = null;
			try {
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

					CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sUserPassword);
				 tags = Git.lsRemoteRepository()
					    .setTags(true)
					    .setRemote(sRemoteUrl).setCredentialsProvider(cp)
					    .call();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return tags;
	}

	public static Collection<Ref> getRemoteTagList(String sRemoteUrl,String sUserName,String sUserPassword) {
		Collection<Ref> tags = null;
			try {
				try {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, null, new java.security.SecureRandom());
					HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				} catch (GeneralSecurityException e) {
					// e.printStackTrace();
				}

				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sUserPassword);
				 tags = Git.lsRemoteRepository()
					    .setTags(true)
					    .setRemote(sRemoteUrl).setCredentialsProvider(cp)
					    .call();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return tags;
	}
	
	/**
	 * 
	 * @param FileTypeFilter  pdf
	 * @return
	 */
	public  List<java.io.File> getRemoteFileListByTagAndFileTypeFilter(Ref ref,String FileTypeFilter) {
		java.util.List<java.io.File> aFiles = new ArrayList<>();
		try {
			
		    Repository repository = git.getRepository();
		    List<Ref> call = new Git(repository).tagList().call();
		        System.out.println("Tag: " +  ref.getName() + "  Commit ID: " + ref.getObjectId().getName());
		        try (RevWalk revWalk = new RevWalk(repository)) {
		            RevCommit commit = revWalk.parseCommit(ref.getObjectId());
		            
		            try (TreeWalk treeWalk = new TreeWalk(repository)) {
		            	  treeWalk.reset(commit.getTree());
		            	  treeWalk.setRecursive(true);
		            	  
		            	  treeWalk.setFilter(PathSuffixFilter.create("."+FileTypeFilter));
		            	  while (treeWalk.next()) {
		            		  System.out.println(treeWalk. getPathString());
		            		  aFiles.add(new File(treeWalk. getPathString()));
		            	  }
		            	}
		        }
		}catch(Exception ee) {
			ee.printStackTrace();
		}
		return aFiles;

	}
	
	public  List<java.io.File> getRemoteFileListByTagAndPathFilter(Ref ref,String sPathFilter) {
		java.util.List<java.io.File> aFiles = new ArrayList<>();
		try {
			
		    Repository repository = git.getRepository();
		    List<Ref> call = new Git(repository).tagList().call();
	        System.out.println("Tag: " +  ref.getName() + "  Commit ID: " + ref.getObjectId().getName());
	        try (RevWalk revWalk = new RevWalk(repository)) {
	            RevCommit commit = revWalk.parseCommit(ref.getObjectId());
		            
	            try (TreeWalk treeWalk = new TreeWalk(repository)) {
	            	  treeWalk.reset(commit.getTree());
	            	  treeWalk.setRecursive(true);
		            	  
	            	  treeWalk.setFilter(PathFilter.create(sPathFilter));
	            	  while (treeWalk.next()) {
	            		  System.out.println(treeWalk. getPathString());
	            		  aFiles.add(new File(treeWalk. getPathString()));
	            	  }
	            }
		    }
		}catch(Exception ee) {
			ee.printStackTrace();
		}
		return aFiles;
	}
	
	//public TreeWalk getTreeWalkByTagName
	
	public static void main(String ar[]) {
		GitUtil aGitUtil;
		/*
		 * String sRemoteUrl = ar[0]; String sLocalDirectory = ar[1]; String sUserName =
		 * ar[2]; String sUserPassword = ar[3];
		 */
		String sRemoteUrl = "https://gitblit.hlmt.com.tw/r/rd2/test.git";
		String sLocalDirectory = "/tmp/test";
		String sUserName = "920405";

		String sUserPassword = "!lois0023";
		try {
			Collection<Ref>  aTL = GitUtil.getRemoteTagListIgnoreCertification(sRemoteUrl,sUserName,sUserPassword);
			for (Ref aTag : aTL) {
				System.out.println("branch : " +  GitUtil.getGitShortName( aTag.getName()));
			}
			Collection<Ref>  aTL2 = GitUtil.getRemoteTagList(sRemoteUrl,sUserName,sUserPassword);
			for (Ref aTag : aTL2) {
				System.out.println("branch : " +  GitUtil.getGitShortName( aTag.getName()));
			}

			aGitUtil = new GitUtil(sRemoteUrl, sLocalDirectory);

			System.out
					.println("Remote repository exists ? " + aGitUtil.checkRemoteRepository(sUserName, sUserPassword));
			System.out.println("Local repository exists ? " + aGitUtil.checkLocalRepository());
	
			if (aGitUtil.checkRemoteRepository(sUserName, sUserPassword) && !aGitUtil.checkLocalRepository()) {
				System.out.println("try to clone remote repository if local repository is not exists \n");
				if (aGitUtil.clone(sUserName, sUserPassword,true))
					System.out.println("clone finished!");
				else
					System.out.println("clone failed!");
			} else if (aGitUtil.checkRemoteRepository(sUserName, sUserPassword) && aGitUtil.checkLocalRepository()) {
				// System.out.println("pull branch = " + aGitUtil.getRemoteDefaultBranch() + " ,
				// status : "
				// + aGitUtil.pull(aGitUtil.getRemoteDefaultBranch(), sUserName,
				// sUserPassword));
			}

			// System.out.println("Default branch : " + aGitUtil.getRemoteDefaultBranch());
			if (aGitUtil.checkLocalRepository()) {
				List<Ref> aAllBranches = aGitUtil.getLocalBranches();
				if (aAllBranches != null) {
					System.out.println("\nList All Local Branch Name\n--------------------------------");
					for (Ref aBranch : aAllBranches) {
						System.out.println("branch : " + aBranch.getName());
					}
					System.out.println("");
				}
				System.out.println("Switch local branch master = " + aGitUtil.checkout("master"));
				List<Ref> aAllTags = aGitUtil.getLocalTags();
				if (aAllTags != null) {
					System.out.println("\nList All Local Tags Name\n--------------------------------");
					for (Ref aTag : aAllTags) {
						aGitUtil.getRemoteFileListByTagAndPathFilter(aTag,"ARTIFACTS");
						aGitUtil.getRemoteFileListByTagAndFileTypeFilter(aTag,"pdf");

						System.out.println("Tag : " + aTag.getName() + "("
								+ aGitUtil.getTagDate(aTag, "yyyy-MM-dd HH:mm:ss") + " created!)");
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
	
	
	/**
	 * diff example
	 * 
	 * @param ar
	 */
	public static void main2(String ar[]) {
		try {
			GitUtil aGitUtil = new GitUtil(null, "/Users/william/git/inmethodgitnotetaking");
			if (!aGitUtil.checkLocalRepository()) {
				System.out.println("no local repository found!");
				// aGitUtil.createLocalRepository();
			} else {
				System.out.println("local repository found!");
//				aGitUtil.commitHistory();
				List<RevCommit> aCommitList = aGitUtil.getLocalCommitIdList();

				if (aCommitList.size() >= 2) {
					String sCurrentCommitID = aCommitList.get(0).getName();
					String sPreviousCommitID = aCommitList.get(1).getName();

					System.out.println("######\nCommit ID=" + aCommitList.get(0).getName() + ",date="
							+ aCommitList.get(0).getCommitTime());
					List<String> aFileList = aGitUtil.getCommitFileList(aCommitList.get(0));
					for (String sFilePath : aFileList) {
						System.out.println(sFilePath);
						List<DiffEntry> diff = aGitUtil.getDiff(sFilePath, sCurrentCommitID, sPreviousCommitID);
						for (DiffEntry entry : diff) {

							System.out.println("Entry : Change Type = " + entry.getChangeType() + ", from: "
									+ entry.getOldId() + ", to: " + entry.getNewId());
							try (DiffFormatter formatter = new DiffFormatter(System.out)) {
								formatter.setRepository(aGitUtil.getGit().getRepository());
								formatter.setContext(0);
								formatter.setDiffComparator(RawTextComparator.WS_IGNORE_TRAILING);
								formatter.format(entry);
								int linesDeleted = 0;
								int linesAdded = 0;

								for (Edit edit : formatter.toFileHeader(entry).toEditList()) {
									linesDeleted += edit.getEndA() - edit.getBeginA();
									linesAdded += edit.getEndB() - edit.getBeginB();
									System.out.println(edit.toString());
								}
								System.out.println("linew Deleted # = " + linesDeleted);
								System.out.println("linew added # = " + linesAdded);
							}
						}
					}
				}
			}

		} catch (Exception ee) {

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
	public static void main3(String ar[]) {
		GitUtil aGitUtil;
		/*
		 * String sRemoteUrl = ar[0]; String sLocalDirectory = ar[1]; String sUserName =
		 * ar[2]; String sUserPassword = ar[3];
		 */
		String sRemoteUrl = "https://github.com/WilliamFromTW/test.git";
		String sLocalDirectory = "/tmp/test";
		String sUserName = "william.fromtw@gmail.com";

		String sUserPassword = "xxxxxxxxxxxxxxxxxxxxx";
		try {
			aGitUtil = new GitUtil(sRemoteUrl, sLocalDirectory);

			System.out
					.println("Remote repository exists ? " + aGitUtil.checkRemoteRepository(sUserName, sUserPassword));
			System.out.println("Local repository exists ? " + aGitUtil.checkLocalRepository());
			if (aGitUtil.checkRemoteRepository(sUserName, sUserPassword) && !aGitUtil.checkLocalRepository()) {
				System.out.println("try to clone remote repository if local repository is not exists \n");
				if (aGitUtil.clone(sUserName, sUserPassword))
					System.out.println("clone finished!");
				else
					System.out.println("clone failed!");
			} else if (aGitUtil.checkRemoteRepository(sUserName, sUserPassword) && aGitUtil.checkLocalRepository()) {
				// System.out.println("pull branch = " + aGitUtil.getRemoteDefaultBranch() + " ,
				// status : "
				// + aGitUtil.pull(aGitUtil.getRemoteDefaultBranch(), sUserName,
				// sUserPassword));
			}

			// System.out.println("Default branch : " + aGitUtil.getRemoteDefaultBranch());
			if (aGitUtil.checkLocalRepository()) {
				List<Ref> aAllBranches = aGitUtil.getLocalBranches();
				if (aAllBranches != null) {
					System.out.println("\nList All Local Branch Name\n--------------------------------");
					for (Ref aBranch : aAllBranches) {
						System.out.println("branch : " + aBranch.getName());
					}
					System.out.println("");
				}
				System.out.println("Switch local branch master = " + aGitUtil.checkout("master"));
				List<Ref> aAllTags = aGitUtil.getLocalTags();
				if (aAllTags != null) {
					System.out.println("\nList All Local Tags Name\n--------------------------------");
					for (Ref aTag : aAllTags) {
						System.out.println("Tag : " + aTag.getName() + "("
								+ aGitUtil.getTagDate(aTag, "yyyy-MM-dd HH:mm:ss") + " created!)");
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

	/**
	 * get all local commit log
	 * 
	 * @return
	 */
	public ArrayList<RevCommit> getLocalCommitIdList() {
		ArrayList<RevCommit> aList = new ArrayList<RevCommit>();
		try {
			Iterable<RevCommit> logs = git.log().call();

			for (RevCommit commit : logs) {
				String commitID = commit.getName();
				// System.out.println("################" + commit.getCommitTime() + "," +
				// commit.getFullMessage());
				if (commitID != null && !commitID.isEmpty()) {
					aList.add(commit);
				}
			}
		} catch (Exception ee) {

		}
		return aList;
	}

	public ArrayList<String> getCommitFileList(RevCommit commit) {
		ArrayList<String> aList = new ArrayList<String>();
		try {
			if (commit.getName() != null && !commit.getName().isEmpty()) {
				LogCommand logs2 = git.log().all();
				Repository repository = logs2.getRepository();
				TreeWalk tw = new TreeWalk(repository);
				tw.setRecursive(true);
				RevCommit commitToCheck = commit;
				tw.addTree(commitToCheck.getTree());
				for (RevCommit parent : commitToCheck.getParents()) {
					tw.addTree(parent.getTree());
				}
				while (tw.next()) {
					int similarParents = 0;
					for (int i = 1; i < tw.getTreeCount(); i++)
						if (tw.getFileMode(i) == tw.getFileMode(0) && tw.getObjectId(0).equals(tw.getObjectId(i)))
							similarParents++;

					if (similarParents == 0) {
						aList.add(tw.getPathString());
						// System.out.println("File names: " + tw.getPathString());
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return aList;
	}

	public List<String> getRemoteBranches(String sUserName, String sPassword) {

		Collection<Ref> refs;
		List<String> branches = new ArrayList<String>();
		try {
	

			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, null, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			} catch (GeneralSecurityException e) {
				// e.printStackTrace();
			}
	

			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPassword);
			refs = Git.lsRemoteRepository().setHeads(true).setRemote(sRemoteUrl).setCredentialsProvider(cp).call();
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			Collections.sort(branches);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return branches;
	}

	public List<String> getRemoteBranchesIgnoreCertification(String sUserName, String sPassword) {

		Collection<Ref> refs;
		List<String> branches = new ArrayList<String>();
		try {
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

			CredentialsProvider cp = new UsernamePasswordCredentialsProvider(sUserName, sPassword);
			refs = Git.lsRemoteRepository().setHeads(true).setRemote(sRemoteUrl).setCredentialsProvider(cp).call();
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			Collections.sort(branches);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return branches;
	}
	
	
	public void commitHistory()
			throws NoHeadException, GitAPIException, IncorrectObjectTypeException, CorruptObjectException, IOException {
		Iterable<RevCommit> logs = git.log().call();
		int k = 0;
		for (RevCommit commit : logs) {
			String commitID = commit.getName();
			System.out.println("################" + commit.getCommitTime() + "," + commit.getFullMessage());
			if (commitID != null && !commitID.isEmpty()) {
				LogCommand logs2 = git.log().all();
				Repository repository = logs2.getRepository();
				TreeWalk tw = new TreeWalk(repository);
				tw.setRecursive(true);
				RevCommit commitToCheck = commit;
				tw.addTree(commitToCheck.getTree());
				for (RevCommit parent : commitToCheck.getParents()) {
					tw.addTree(parent.getTree());
				}
				while (tw.next()) {
					int similarParents = 0;
					for (int i = 1; i < tw.getTreeCount(); i++)
						if (tw.getFileMode(i) == tw.getFileMode(0) && tw.getObjectId(0).equals(tw.getObjectId(i)))
							similarParents++;
					if (similarParents == 0)
						System.out.println("File names: " + tw.getPathString());
				}
			}
		}
	}

	/**
	 * ResetType.HARD、ResetType.MIXED
	 * @param aResetType
	 */
	public void reset(ResetType aResetType) {
		try {
			git.reset().setMode(aResetType).call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param aResetType  ResetType.HARD、ResetType.MIXED
	 * @param sBranchName  HEAD，master、origin/master
	 */
	public void reset(ResetType aResetType,String sBranchName) {
		try {
			git.reset().setMode(aResetType).setRef(sBranchName).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private AbstractTreeIterator prepareTreeParser(String objectId) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser
		// noinspection Duplicates

		try (RevWalk walk = new RevWalk(git.getRepository())) {
			RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = git.getRepository().newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}

	/**
	 * 
	 * @param sCurrentCommitID
	 * @param sPreviousCommitID
	 * @return
	 */
	public List<DiffEntry> getDiff(String sFilePath, String sCurrentCommitID, String sPreviousCommitID) {
		try {
			return git.diff().setOldTree(prepareTreeParser(sPreviousCommitID))
					.setNewTree(prepareTreeParser(sCurrentCommitID)).setPathFilter(PathFilter.create(sFilePath)).call();
		} catch (GitAPIException | IOException e) {

			e.printStackTrace();
		}
		return null;
	}
}
