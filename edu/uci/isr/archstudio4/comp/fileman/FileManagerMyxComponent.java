package edu.uci.isr.archstudio4.comp.fileman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxRegistry;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.sysutils.SystemUtils;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFileEvent;
import edu.uci.isr.xarchflat.XArchFileListener;
import edu.uci.isr.xarchflat.XArchFlatEvent;
import edu.uci.isr.xarchflat.XArchFlatInterface;
import edu.uci.isr.xarchflat.XArchFlatListener;

public class FileManagerMyxComponent extends AbstractMyxSimpleBrick implements XArchFileListener, XArchFlatListener, IFileManager{

	public static final IMyxName INTERFACE_NAME_OUT_XARCH = MyxUtils.createName("xarch");
	public static final IMyxName INTERFACE_NAME_IN_FLATEVENTS = MyxUtils.createName("xarchflatevents");
	public static final IMyxName INTERFACE_NAME_IN_FILEEVENTS = MyxUtils.createName("xarchfileevents");

	public static final IMyxName INTERFACE_NAME_IN_FILEMANAGER = MyxUtils.createName("filemanager");

	public static final IMyxName INTERFACE_NAME_OUT_FILEMANAGEREVENTS = MyxUtils.createName("filemanagerevents");
	
	private MyxRegistry er = MyxRegistry.getSharedInstance();
	
	protected XArchFlatInterface xarch = null;
	protected IFileManagerListener fileManagerListener = null;
	
	protected Set<ObjRef> dirtySet = Collections.synchronizedSet(new HashSet<ObjRef>());
	
	//Keeps track of which tools have which documents open. When no tools have
	//a document open, it is closed in xArchADT.
	protected Map<ObjRef,List<String>> openerMap = Collections.synchronizedMap(new HashMap<ObjRef,List<String>>());
	
	public FileManagerMyxComponent(){
	}
	
	public void begin(){
		xarch = (XArchFlatInterface)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_XARCH);
		fileManagerListener = (IFileManagerListener)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_FILEMANAGEREVENTS);
		er.register(this);
	}
	
	public void end(){
		er.unregister(this);
	}

	public Object getServiceObject(IMyxName interfaceName){
		if(interfaceName.equals(INTERFACE_NAME_IN_FILEEVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_FLATEVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_FILEMANAGER)){
			return this;
		}
		return null;
	}
	
	public void handleXArchFileEvent(XArchFileEvent evt){
		Object[] os = er.getObjects(this);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof XArchFileListener){
				((XArchFileListener)os[i]).handleXArchFileEvent(evt);
			}
		}
	}
	
	public void handleXArchFlatEvent(XArchFlatEvent evt){
		Object[] os = er.getObjects(this);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof XArchFlatListener){
				((XArchFlatListener)os[i]).handleXArchFlatEvent(evt);
			}
		}
		ObjRef xArchRef = xarch.getXArch(evt.getSource());
		makeDirty(xArchRef);
	}

	private static String getURI(IFile f){
		IPath filePath = f.getFullPath();
		return filePath.toPortableString();
	}
	
	private static String getURI(java.io.File f){
		return f.getPath();
		//return filePath.toPortableString();
	}

	public boolean isOpen(IFile f){
		String uri = getURI(f);
		if(xarch.getOpenXArch(uri) != null){
			return true;
		}
		return false;
	}

	public ObjRef getXArch(IFile f){
		String uri = getURI(f);
		return xarch.getOpenXArch(uri);
	}
	
	public ObjRef openXArch(String toolID, IFile f) throws CantOpenFileException{
		InputStream is = null;
		OutputStream os = null;
		String uri = null;
		try{
			uri = getURI(f);
			is = f.getContents();
			os = new ByteArrayOutputStream();
			SystemUtils.blt(is, os);
			String contents = new String(((ByteArrayOutputStream)os).toByteArray());
			ObjRef xArchRef = xarch.parseFromString(uri, contents);
			contents = null;
			
			List<String> toolList = openerMap.get(xArchRef);
			if(toolList == null){
				toolList = new ArrayList<String>();
			}
			toolList.add(toolID);
			openerMap.put(xArchRef, toolList);
			
			return xArchRef;
		}
		catch(Exception e){
			throw new CantOpenFileException("Can't open file: " + uri, e);
		}
		finally{
			try{
				if(is != null) is.close();
			}
			catch(IOException e){}
			try{
				if(os != null) os.close();
			}
			catch(IOException e2){}
		}
	}
	
	public ObjRef openXArch(String toolID, java.io.File f) throws CantOpenFileException{
		InputStream is = null;
		OutputStream os = null;
		String uri = null;
		try{
			uri = getURI(f);
			is = new FileInputStream(f);
			os = new ByteArrayOutputStream();
			SystemUtils.blt(is, os);
			String contents = new String(((ByteArrayOutputStream)os).toByteArray());
			ObjRef xArchRef = xarch.parseFromString(uri, contents);
			contents = null;
			
			List<String> toolList = openerMap.get(xArchRef);
			if(toolList == null){
				toolList = new ArrayList<String>();
			}
			toolList.add(toolID);
			openerMap.put(xArchRef, toolList);
			
			return xArchRef;
		}
		catch(Exception e){
			throw new CantOpenFileException("Can't open file: " + uri, e);
		}
		finally{
			try{
				if(is != null) is.close();
			}
			catch(IOException e){}
			try{
				if(os != null) os.close();
			}
			catch(IOException e2){}
		}
	}

	public void closeXArch(String toolID, ObjRef xArchRef){
		List<String> toolList = openerMap.get(xArchRef);
		if(toolList == null){
			xarch.close(xArchRef);
			return;
		}
		
		toolList.remove(toolID);
		if(toolList.size() == 0){
			xarch.close(xArchRef);
			openerMap.remove(xArchRef);
		}
	}
	
	public void makeDirty(ObjRef xArchRef){
		if(dirtySet.contains(xArchRef)){
			return;
		}
		dirtySet.add(xArchRef);
		if(fileManagerListener != null){
			fileManagerListener.fileDirtyStateChanged(xArchRef, true);
		}
	}
	
	public void makeClean(ObjRef xArchRef){
		if(!dirtySet.contains(xArchRef)){
			return;
		}
		dirtySet.remove(xArchRef);
		if(fileManagerListener != null){
			fileManagerListener.fileDirtyStateChanged(xArchRef, false);
		}
	}
	
	public boolean isDirty(ObjRef xArchRef){
		return dirtySet.contains(xArchRef);
	}
	
	public void save(ObjRef xArchRef, IProgressMonitor monitor){
		if(monitor != null){
			monitor.beginTask("Saving File", 100);
			monitor.worked(1);
		}
		if(fileManagerListener != null){
			monitor.subTask("Notifying Editors");
			monitor.worked(2);
			try{
				fileManagerListener.fileSaving(xArchRef, monitor);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		if(monitor != null){
			monitor.worked(80);
		}
		
		String serializedXML = xarch.serialize(xArchRef);
		String uri = xarch.getXArchURI(xArchRef);
		Path filePath = new Path(uri);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFile(filePath);
		InputStream source = new ByteArrayInputStream(serializedXML.getBytes());
		try{
			file.setContents(source, IFile.NONE, monitor);
			makeClean(xArchRef);
		}
		catch(CoreException ce){
			//TODO:Handle
		}
		if(monitor != null){
			monitor.worked(100);
		}
	}
	
	public void saveAs(ObjRef xArchRef, IFile f){
	}
	
	
}
