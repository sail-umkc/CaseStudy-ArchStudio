package edu.uci.isr.archstudio4.editors;

import org.eclipse.core.runtime.IProgressMonitor;

import edu.uci.isr.archstudio4.comp.fileman.IFileManagerListener;
import edu.uci.isr.archstudio4.comp.fileman.IFileManager;
import edu.uci.isr.archstudio4.comp.resources.IResources;
import edu.uci.isr.archstudio4.launcher.ILaunchData;
import edu.uci.isr.archstudio4.launcher.ILaunchable;
import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxRegistry;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFileEvent;
import edu.uci.isr.xarchflat.XArchFileListener;
import edu.uci.isr.xarchflat.XArchFlatEvent;
import edu.uci.isr.xarchflat.XArchFlatInterface;
import edu.uci.isr.xarchflat.XArchFlatListener;

public abstract class AbstractArchstudioEditorMyxComponent extends AbstractMyxSimpleBrick implements XArchFileListener, XArchFlatListener, FocusEditorListener, IFileManagerListener, ILaunchable{
	
	public static final IMyxName INTERFACE_NAME_OUT_RESOURCES = MyxUtils.createName("resources");
	public static final IMyxName INTERFACE_NAME_OUT_EDITORMANAGER = MyxUtils.createName("editormanager");
	public static final IMyxName INTERFACE_NAME_OUT_FILEMANAGER = MyxUtils.createName("filemanager");
	public static final IMyxName INTERFACE_NAME_OUT_XARCH = MyxUtils.createName("xarch");
	public static final IMyxName INTERFACE_NAME_IN_LAUNCHER = MyxUtils.createName("launcher");
	public static final IMyxName INTERFACE_NAME_IN_FLATEVENTS = MyxUtils.createName("xarchflatevents");
	public static final IMyxName INTERFACE_NAME_IN_FILEEVENTS = MyxUtils.createName("xarchfileevents");
	public static final IMyxName INTERFACE_NAME_IN_FOCUSEDITOREVENTS = MyxUtils.createName("focuseditorevents");
	public static final IMyxName INTERFACE_NAME_IN_FILEMANAGEREVENTS = MyxUtils.createName("filemanagerevents");

	protected MyxRegistry er = MyxRegistry.getSharedInstance();
	protected XArchFlatInterface xarch = null;
	protected IFileManager fileman = null;
	protected IEditorManager editorManager = null;
	protected IResources resources = null;

	protected String editorName = null;
	protected String eclipseEditorID = null;
	protected boolean registerWithEditorManager = false;
	protected boolean handleUnattachedXArchFlatEvents = false;
	
	protected ILaunchData launchData = null;
	
	public AbstractArchstudioEditorMyxComponent(String editorName, String eclipseEditorID, boolean registerWithEditorManager){
		super();
		this.editorName = editorName;
		this.eclipseEditorID = eclipseEditorID;
		this.registerWithEditorManager = registerWithEditorManager;
	}
	
	public void begin(){
		xarch = (XArchFlatInterface)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_XARCH);
		fileman = (IFileManager)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_FILEMANAGER);
		editorManager = (IEditorManager)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_EDITORMANAGER);
		resources = (IResources)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_RESOURCES);
		
		if(registerWithEditorManager){
			editorManager.registerEditor(editorName, null);
		}
		er.register(this);
	}
	
	public void end(){
		er.unregister(this);
		if(registerWithEditorManager){
			editorManager.unregisterEditor(editorName);
		}
	}

	public Object getServiceObject(IMyxName interfaceName){
		if(interfaceName.equals(INTERFACE_NAME_IN_FILEEVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_FLATEVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_FOCUSEDITOREVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_FILEMANAGEREVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_LAUNCHER)){
			return this;
		}
		return null;
	}
	
	public IEditorManager getEditorManager(){
		return editorManager;
	}

	public IFileManager getFileManager(){
		return fileman;
	}

	public IResources getResources(){
		return resources;
	}

	public XArchFlatInterface getXArchADT(){
		return xarch;
	}

	public void handleXArchFileEvent(XArchFileEvent evt){
		Object[] os = er.getObjects(this);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof XArchFileListener){
				((XArchFileListener)os[i]).handleXArchFileEvent(evt);
			}
		}
	}
	
	public void setHandleUnattachedXArchFlatEvents(boolean handle){
		this.handleUnattachedXArchFlatEvents = handle;
	}
	
	public void handleXArchFlatEvent(XArchFlatEvent evt){
		if((!handleUnattachedXArchFlatEvents) && (!evt.getIsAttached())){
			return;
		}
		Object[] os = er.getObjects(this);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof XArchFlatListener){
				((XArchFlatListener)os[i]).handleXArchFlatEvent(evt);
			}
		}
	}

	public void focusEditor(String editorName, ObjRef[] refs){
		if((editorName != null) && editorName.equals(this.editorName)){
			if(refs.length == 1){
				ObjRef ref = refs[0];
				edu.uci.isr.archstudio4.util.EclipseUtils.focusEditor(xarch, ref, eclipseEditorID, editorName);
			}
		}
	}
	
	public void fileDirtyStateChanged(ObjRef xArchRef, boolean dirty){
		Object[] os = er.getObjects(this);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof IFileManagerListener){
				((IFileManagerListener)os[i]).fileDirtyStateChanged(xArchRef, dirty);
			}
		}
	}
	
	public void fileSaving(ObjRef xArchRef, IProgressMonitor monitor){
		Object[] os = er.getObjects(this);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof IFileManagerListener){
				((IFileManagerListener)os[i]).fileSaving(xArchRef, monitor);
			}
		}
	}
}
