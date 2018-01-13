package edu.uci.isr.archstudio4.comp.aim;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxDynamicBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.IMyxRuntime;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public class AIMMyxComponent extends AbstractMyxSimpleBrick implements IMyxDynamicBrick{

	public static final IMyxName INTERFACE_NAME_IN_AIM = MyxUtils.createName("aim");
	public static final IMyxName INTERFACE_NAME_OUT_MYXRUNTIME = MyxUtils.createName("myxruntime");
	public static final IMyxName INTERFACE_NAME_OUT_XARCH = MyxUtils.createName("xarch");
	
	protected AIMImpl aim;
	
	protected XArchFlatInterface xarch = null;
	
	public AIMMyxComponent(){
	}
	
	public void init(){
		aim = new AIMImpl(xarch);
	}
	
	private void setupImpl(){
		aim.setXArch(xarch);
	}

	public void interfaceConnected(IMyxName interfaceName, Object serviceObject){
		if(interfaceName.equals(INTERFACE_NAME_OUT_XARCH)){
			xarch = (XArchFlatInterface)serviceObject;
		}
		setupImpl();
	}

	public void interfaceDisconnecting(IMyxName interfaceName, Object serviceObject){
		if(interfaceName.equals(INTERFACE_NAME_OUT_XARCH)){
			xarch = null;
		}
		setupImpl();
	}

	public void interfaceDisconnected(IMyxName interfaceName, Object serviceObject){}

	public Object getServiceObject(IMyxName interfaceName){
		if(interfaceName.equals(INTERFACE_NAME_IN_AIM)){
			return aim;
		}
		return null;
	}

}
