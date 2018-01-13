package edu.uci.isr.archstudio4.comp.filetracker;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxRegistry;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.xarchflat.XArchFileEvent;
import edu.uci.isr.xarchflat.XArchFileListener;
import edu.uci.isr.xarchflat.XArchFlatEvent;
import edu.uci.isr.xarchflat.XArchFlatInterface;
import edu.uci.isr.xarchflat.XArchFlatListener;

public class FileTrackerMyxComponent extends AbstractMyxSimpleBrick implements XArchFileListener, XArchFlatListener{

	public static final IMyxName INTERFACE_NAME_OUT_XARCH = MyxUtils.createName("xarch");
	public static final IMyxName INTERFACE_NAME_IN_FLATEVENTS = MyxUtils.createName("xarchflatevents");
	public static final IMyxName INTERFACE_NAME_IN_FILEEVENTS = MyxUtils.createName("xarchfileevents");
	
	protected XArchFlatInterface xarch = null;
	
	private FileTrackerImplementation _imp;
	
	public FileTrackerMyxComponent(){
		_imp = new FileTrackerImplementation();
		_imp.setArch(this);
	}
	
	public void begin(){
		xarch = (XArchFlatInterface)MyxUtils.getFirstRequiredServiceObject(this, INTERFACE_NAME_OUT_XARCH);
		_imp.begin();
	}
	
	public void end(){
		_imp.end();
	}

	public Object getServiceObject(IMyxName interfaceName){
		if(interfaceName.equals(INTERFACE_NAME_IN_FILEEVENTS)){
			return this;
		}
		else if(interfaceName.equals(INTERFACE_NAME_IN_FLATEVENTS)){
			return this;
		}
		return null;
	}
	
	public void handleXArchFileEvent(XArchFileEvent evt){
		_imp.handleXArchFileEvent(evt);
	}
	
	public void handleXArchFlatEvent(XArchFlatEvent evt){
		_imp.handleXArchFlatEvent(evt);
	}

}
