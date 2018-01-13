package edu.uci.isr.archstudio4.comp.filetracker;

import edu.uci.isr.myx.fw.MyxRegistry;
import edu.uci.isr.xarchflat.XArchFileEvent;
import edu.uci.isr.xarchflat.XArchFileListener;
import edu.uci.isr.xarchflat.XArchFlatEvent;
import edu.uci.isr.xarchflat.XArchFlatListener;

public class FileTrackerImplementation {

	private MyxRegistry er = MyxRegistry.getSharedInstance();
	
	private FileTrackerMyxComponent _arch;
	
	public FileTrackerImplementation(){		
	}
	
	public void setArch(FileTrackerMyxComponent arch){
		_arch = arch;
	}

	public void begin(){
		er.register(_arch);		
	}

	public void end(){
		er.unregister(_arch);
	}

	public void handleXArchFileEvent(XArchFileEvent evt){
		//System.out.println("got file event: " + evt);
		Object[] os = er.getObjects(_arch);
		//System.out.println("workbench parts len = " + os.length);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof XArchFileListener){
				((XArchFileListener)os[i]).handleXArchFileEvent(evt);
			}
		}
	}
	
	public void handleXArchFlatEvent(XArchFlatEvent evt){
		Object[] os = er.getObjects(_arch);
		for(int i = 0; i < os.length; i++){
			if(os[i] instanceof XArchFlatListener){
				((XArchFlatListener)os[i]).handleXArchFlatEvent(evt);
			}
		}
	}
}
