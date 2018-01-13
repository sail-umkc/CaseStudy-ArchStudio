package edu.uci.isr.archstudio4.comp.bootstrap;

import edu.uci.isr.archstudio4.comp.aim.IAIM;
import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxDynamicBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.IMyxRuntime;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public class BootstrapMyxComponent extends AbstractMyxSimpleBrick
	implements IMyxDynamicBrick{
	
	public static final String ARCHITECTURE_NAME = "main";

	public static final IMyxName INTERFACE_NAME_OUT_MYX = MyxUtils.createName("myx");
	public static final IMyxName INTERFACE_NAME_OUT_AIM = MyxUtils.createName("aim");
	public static final IMyxName INTERFACE_NAME_OUT_XARCH = MyxUtils.createName("xarch");

	protected IMyxRuntime myx = null;
	protected IAIM aim = null;
	protected XArchFlatInterface xarch = null;

	public BootstrapMyxComponent(){
	}

	public synchronized void interfaceConnected(IMyxName interfaceName, Object serviceObject){
		if(interfaceName.equals(INTERFACE_NAME_OUT_MYX)){
			myx = (IMyxRuntime)serviceObject;
		}
		else if(interfaceName.equals(INTERFACE_NAME_OUT_AIM)){
			aim = (IAIM)serviceObject;
		}
		else if(interfaceName.equals(INTERFACE_NAME_OUT_XARCH)){
			xarch = (XArchFlatInterface)serviceObject;
		}
	}

	public synchronized void interfaceDisconnecting(IMyxName interfaceName, Object serviceObject){
		if(interfaceName.equals(INTERFACE_NAME_OUT_MYX)){
			myx = null;
		}
		else if(interfaceName.equals(INTERFACE_NAME_OUT_AIM)){
			aim = null;
		}
		else if(interfaceName.equals(INTERFACE_NAME_OUT_XARCH)){
			xarch = null;
		}
	}

	public void interfaceDisconnected(IMyxName interfaceName, Object serviceObject){
	}

	public Object getServiceObject(IMyxName interfaceName){
		return null;
	}

	public void begin(){
		String xadlFileContents = null;
		String xadlFileString = MyxUtils.getInitProperties(this).getProperty("file");
		if(xadlFileString == null){
			xadlFileContents = MyxUtils.getInitProperties(this).getProperty("contents");
			if(xadlFileContents == null){
				throw new IllegalArgumentException("File parameter missing to bootstrap component.");
			}
		}

		String structureDescription = MyxUtils.getInitProperties(this).getProperty("structure");
		if(structureDescription == null){
			structureDescription = "main";
		}

		ObjRef xArchRef = null;
		try{
			if(xadlFileString == null){
				xArchRef = xarch.parseFromString("urn:arch" + Math.random(), xadlFileContents);
			}
			else if(xadlFileString.toLowerCase().startsWith("http:")){
				xArchRef = xarch.parseFromURL(xadlFileString);
			}
			else if(xadlFileString.toLowerCase().startsWith("file:")){
				xArchRef = xarch.parseFromURL(xadlFileString);
			}
			else{
				xArchRef = xarch.parseFromFile(xadlFileString);
			}

			ObjRef typesContextRef = xarch.createContext(xArchRef, "types");
			ObjRef[] structureRefs = xarch.getAllElements(typesContextRef, "archStructure", xArchRef);

			if(structureRefs.length == 0){
				throw new RuntimeException("Architecture has no structures to instantiate");
			}

			ObjRef structureRef = null;
			for(int i = 0; i < structureRefs.length; i++){
				String description = XadlUtils.getDescription(xarch, structureRefs[i]);
				if((description != null) && (description.equals(structureDescription))){
					structureRef = structureRefs[i];
					break;
				}
			}
			if(structureRef == null){
				structureRef = structureRefs[0];
			}

			aim.instantiate(myx, ARCHITECTURE_NAME, xArchRef, structureRef);
			aim.begin(myx, ARCHITECTURE_NAME);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
