package aurora.ide.statistics.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import aurora.ide.api.statistics.cvs.CVSEntryLineTag;
import aurora.ide.api.statistics.cvs.CVSTag;
import aurora.ide.api.statistics.cvs.FolderSyncInfo;

public class CVSFileReader {

	// the famous CVS meta directory name
	public static final String CVS_DIRNAME = "CVS"; //$NON-NLS-1$

	// CVS meta files located in the CVS subdirectory
	public static final String REPOSITORY = "Repository"; //$NON-NLS-1$
	public static final String ROOT = "Root"; //$NON-NLS-1$
	public static final String STATIC = "Entries.Static";	 //$NON-NLS-1$
	public static final String TAG = "Tag";	 //$NON-NLS-1$
	public static final String ENTRIES = "Entries"; //$NON-NLS-1$
	//private static final String PERMISSIONS = "Permissions"; //$NON-NLS-1$
	public static final String ENTRIES_LOG="Entries.Log"; //$NON-NLS-1$
	public static final String NOTIFY = "Notify"; //$NON-NLS-1$
	public static final String BASE_DIRNAME = "Base"; //$NON-NLS-1$
	public static final String BASEREV = "Baserev"; //$NON-NLS-1$
	
	// the local workspace file that contains pattern for ignored resources
	public static final String IGNORE_FILE = ".cvsignore"; //$NON-NLS-1$

	// Some older CVS clients may of added a line to the entries file consisting
	// of only a 'D'. It is safe to ignore these entries.	
	private static final String FOLDER_TAG="D"; //$NON-NLS-1$
	
	// Command characters found in the Entries.log file
	private static final String ADD_TAG="A "; //$NON-NLS-1$
	private static final String REMOVE_TAG="R "; //$NON-NLS-1$	
	
	// key for saving the mod stamp for each written meta file
	public static final QualifiedName MODSTAMP_KEY = new QualifiedName("org.eclipse.team.cvs.core", "meta-file-modtime"); //$NON-NLS-1$ //$NON-NLS-2$
	
	private static boolean folderExists(IFolder cvsSubDir) {
	    try {
	    	URI uri = cvsSubDir.getLocationURI();
	    	if (uri != null){
				IFileStore store = EFS.getStore(uri);
				if (store != null){
					return store.fetchInfo().exists();
				}
	    	}
		} catch (CoreException e) {
			
		} 
		return false;
	}


	/**
	 * Reads the CVS/Root, CVS/Repository, CVS/Tag, and CVS/Entries.static files from
	 * the specified folder and returns a FolderSyncInfo instance for the data stored therein.
	 * If the folder does not have a CVS subdirectory then <code>null</code> is returned.
	 */
	public static FolderSyncInfo readFolderSync(IContainer folder) {
		IFolder cvsSubDir = getCVSSubdirectory(folder);
		
        if (!folderExists(cvsSubDir)){
        	return null;
        }

     
		// check to make sure the the cvs folder is hidden
		if (!cvsSubDir.isTeamPrivateMember() && cvsSubDir.exists()) {
			try {
				cvsSubDir.setTeamPrivateMember(true);
			} catch (CoreException e) {
			
			}
		}
				
		// read CVS/Root
		String root = readFirstLine(cvsSubDir.getFile(ROOT));
		if (root == null) return null;
		
		// read CVS/Repository
		String repository = readFirstLine(cvsSubDir.getFile(REPOSITORY));
		if (repository == null) return null;
		
		// read CVS/Tag
		String tag = readFirstLine(cvsSubDir.getFile(TAG));

		CVSTag cvsTag = (tag != null) ? new CVSEntryLineTag(tag) : null;

		// read Entries.Static
		String staticDir = readFirstLine(cvsSubDir.getFile(STATIC));
		
		boolean isStatic = (staticDir != null);
		
		// return folder sync
		return new FolderSyncInfo(repository, root, cvsTag, isStatic);		
	}
	


	


				
	/**
	 * Returns the CVS subdirectory for this folder.
	 */
	private static IFolder getCVSSubdirectory(IContainer folder) {
		return folder.getFolder(new Path(CVS_DIRNAME));
	}
	


	protected static boolean existsInFileSystem(IFolder cvsSubDir) {
		URI uri = cvsSubDir.getLocationURI();
		if (uri != null) {
			try {
				IFileStore store = EFS.getStore(uri);
				if (store != null) {
					return store.fetchInfo().exists();
				}
			} catch (CoreException e) {
				
			}
		}
		return false;
	}

	/*
	 * Reads the first line of the specified file.
	 * Returns null if the file does not exist, or the empty string if it is blank.
	 */
	private static String readFirstLine(IFile file)  {
		try {
			InputStream in = getInputStream(file);
			if (in != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in), 512);
				try {
					String line = reader.readLine();
					if (line == null) return ""; //$NON-NLS-1$
					return line;
				} finally {
					reader.close();
				}
            }
            return null;
		} catch (IOException e) {
			
		} catch (CoreException e) {
			// If the IFile doesn't exist or the underlying File doesn't exist,
			// just return null to indicate the absence of the file
			if (e.getStatus().getCode() == IResourceStatus.RESOURCE_NOT_FOUND
					|| e.getStatus().getCode() == IResourceStatus.FAILED_READ_LOCAL)
				return null;
			
		}
		return null;
	}

	private static InputStream getInputStream(IFile file) throws CoreException, FileNotFoundException {
		if (file.exists()) {
		    return file.getContents(true);
		}
		
		URI uri = file.getLocationURI();
		if (uri != null) {
			IFileStore store = EFS.getStore(uri);
			if (store != null) {
				return store.openInputStream(EFS.NONE, null);
			}
		}
		
		IPath location = file.getLocation();
		if (location != null) {
			File ioFile = location.toFile();
			if (ioFile != null && ioFile.exists()) {
				return new FileInputStream(ioFile);
			}
		}

		return null;
	}
	
	/*
	 * Reads all lines of the specified file.
	 * Returns null if the file does not exist.
	 */
	private static String[] readLines(IFile file) {
		try {
			InputStream in = getInputStream(file);
			if (in != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in), 512);
				List<String> fileContentStore = new ArrayList<String>();
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						fileContentStore.add(line);
					}
					return (String[]) fileContentStore.toArray(new String[fileContentStore.size()]);
				} finally {
					reader.close();
				}
			}
			return null;
		} catch (IOException e) {
			
		} catch (CoreException e) {
			// If the IFile doesn't exist or the underlying File doesn't exist,
			// just return null to indicate the absence of the file
			if (e.getStatus().getCode() == IResourceStatus.RESOURCE_NOT_FOUND
					|| e.getStatus().getCode() == IResourceStatus.FAILED_READ_LOCAL)
				return null;
		
		}
		return null;
	}
	
	
	
	


	
	
	private static IFolder getBaseDirectory(IFile file) {
		IContainer cvsFolder = getCVSSubdirectory(file.getParent());
		IFolder baseFolder = cvsFolder.getFolder(new Path(BASE_DIRNAME));
		return baseFolder;
	}
	
	
	/**
	 * Method isEdited.
	 * @param resource
	 * @return boolean
	 */
	public static boolean isEdited(IFile file) {
		IFolder baseFolder = getBaseDirectory(file);
		IFile baseFile = baseFolder.getFile(file.getName());
		return baseFile.exists();
	}
	
	



}
