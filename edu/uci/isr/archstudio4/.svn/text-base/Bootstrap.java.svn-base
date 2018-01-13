package edu.uci.isr.archstudio4;

import java.util.Properties;

import edu.uci.isr.archstudio4.comp.aim.AIMMyxComponent;
import edu.uci.isr.archstudio4.comp.aim.IAIM;
import edu.uci.isr.archstudio4.comp.bootstrap.BootstrapMyxComponent;
import edu.uci.isr.archstudio4.comp.xarchadt.XArchADTMyxComponent;
import edu.uci.isr.myx.comp.MyxRuntimeComponent;
import edu.uci.isr.myx.fw.IMyxImplementation;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.IMyxRuntime;
import edu.uci.isr.myx.fw.MyxConstants;
import edu.uci.isr.myx.fw.MyxJavaClassBrickDescription;
import edu.uci.isr.myx.fw.MyxJavaClassInterfaceDescription;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.xarchflat.XArchFileListener;
import edu.uci.isr.xarchflat.XArchFlatInterface;
import edu.uci.isr.xarchflat.XArchFlatListener;

public class Bootstrap{

	public static final IMyxName XARCHADT_NAME = MyxUtils.createName("xArchADT");
	public static final IMyxName AIM_NAME = MyxUtils.createName("AIM");
	public static final IMyxName MYXRUNTIME_NAME = MyxUtils.createName("myxruntime");
	public static final IMyxName BOOTSTRAP_NAME = MyxUtils.createName("bootstrap");
	
	protected IMyxImplementation myximpl;

	public static void main(String[] args){
		printHeader();
		Properties p = parseArgs(args);
		new Bootstrap(p);
	}
	
	private static void printHeader(){
		System.out.println();
		System.out.println("------------------------------------------------------------------------");
		System.out.println("ArchStudio 4 Bootstrapper");
		System.out.println("Copyright (C) 2006 The Regents of the University of California.");
		System.out.println("All rights reserved worldwide.");
		System.out.println();
	}
	
	private static Properties parseArgs(String[] args){
		String xadlFile = null;
		String structureName = null;
		
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-s")){
				if(++i == args.length) argError();
				if(structureName != null) argError();
				structureName = args[i];
			}
			else{
				if(xadlFile != null) argError();
				xadlFile = args[i];
			}
		}
		if(xadlFile == null) argError();
		
		Properties p = new Properties();
		p.setProperty("file", xadlFile);
		if(structureName != null){
			p.setProperty("structure", structureName);
		}
		return p;
	}
	
	private static void argError(){
		System.err.println();
		System.err.println("Argument error. Usage is:");
		System.err.println("  java " + Bootstrap.class.getName() + " file|URL [-s structureName]");
		System.err.println();
		System.err.println("  where:");
		System.err.println("    file: the name of the xADL file to bootstrap");
		System.err.println("    URL: the URL of the xADL file to bootstrap");
		System.err.println("    structureName: the name of the structure to bootstrap");
		System.err.println();
		System.exit(-2);
	}

	public Bootstrap(Properties props){
		myximpl = MyxUtils.getDefaultImplementation();
		doBootstrap(props);
	}

	private void doBootstrap(Properties p){
		//System.err.println("in dobootstrap");
		IMyxRuntime myx = myximpl.createRuntime();
		try{
			MyxJavaClassBrickDescription xarchadtDesc = new MyxJavaClassBrickDescription(null, XArchADTMyxComponent.class.getName());
			MyxJavaClassBrickDescription aimDesc = new MyxJavaClassBrickDescription(null, AIMMyxComponent.class.getName());
			MyxJavaClassBrickDescription myxruntimeDesc = new MyxJavaClassBrickDescription(null, MyxRuntimeComponent.class.getName());
			MyxJavaClassBrickDescription bootstrapDesc = new MyxJavaClassBrickDescription(p, BootstrapMyxComponent.class.getName());

			myx.addBrick(null, XARCHADT_NAME, xarchadtDesc);
			myx.addBrick(null, AIM_NAME, aimDesc);
			myx.addBrick(null, MYXRUNTIME_NAME, myxruntimeDesc);
			myx.addBrick(null, BOOTSTRAP_NAME, bootstrapDesc);

			MyxJavaClassInterfaceDescription aimIfaceDesc = new MyxJavaClassInterfaceDescription(new String[]{IAIM.class.getName()});
			MyxJavaClassInterfaceDescription xarchIfaceDesc = new MyxJavaClassInterfaceDescription(new String[]{XArchFlatInterface.class.getName()});
			MyxJavaClassInterfaceDescription xarchFileEventsIfaceDesc = new MyxJavaClassInterfaceDescription(new String[]{XArchFileListener.class.getName()});
			MyxJavaClassInterfaceDescription xarchFlatEventsIfaceDesc = new MyxJavaClassInterfaceDescription(new String[]{XArchFlatListener.class.getName()});
			MyxJavaClassInterfaceDescription myxIfaceDesc = new MyxJavaClassInterfaceDescription(new String[]{IMyxRuntime.class.getName()});
			
			myx.addInterface(null, BOOTSTRAP_NAME, BootstrapMyxComponent.INTERFACE_NAME_OUT_MYX, aimIfaceDesc, MyxConstants.REQUIRED);
			myx.addInterface(null, BOOTSTRAP_NAME, BootstrapMyxComponent.INTERFACE_NAME_OUT_AIM, aimIfaceDesc, MyxConstants.REQUIRED);
			myx.addInterface(null, BOOTSTRAP_NAME, BootstrapMyxComponent.INTERFACE_NAME_OUT_XARCH, xarchIfaceDesc, MyxConstants.REQUIRED);
			
			myx.addInterface(null, AIM_NAME, AIMMyxComponent.INTERFACE_NAME_OUT_XARCH, xarchIfaceDesc, MyxConstants.REQUIRED);
			myx.addInterface(null, AIM_NAME, AIMMyxComponent.INTERFACE_NAME_IN_AIM, aimIfaceDesc, MyxConstants.PROVIDED);
			
			myx.addInterface(null, MYXRUNTIME_NAME, MyxRuntimeComponent.INTERFACE_NAME_IN_MYXRUNTIME, myxIfaceDesc, MyxConstants.PROVIDED);
			
			myx.addInterface(null, XARCHADT_NAME, XArchADTMyxComponent.INTERFACE_NAME_IN_XARCH, xarchIfaceDesc, MyxConstants.PROVIDED);
			myx.addInterface(null, XARCHADT_NAME, XArchADTMyxComponent.INTERFACE_NAME_OUT_FILEEVENTS, xarchFileEventsIfaceDesc, MyxConstants.REQUIRED);
			myx.addInterface(null, XARCHADT_NAME, XArchADTMyxComponent.INTERFACE_NAME_OUT_FLATEVENTS, xarchFlatEventsIfaceDesc, MyxConstants.REQUIRED);

			myx.init(null, null);

			//Bootstrap->xArchADT
			myx.addWeld(myx.createWeld(
				null, BOOTSTRAP_NAME, BootstrapMyxComponent.INTERFACE_NAME_OUT_XARCH, 
				null, XARCHADT_NAME, XArchADTMyxComponent.INTERFACE_NAME_IN_XARCH));
			
			//Bootstrap->AIM
			myx.addWeld(myx.createWeld(
				null, BOOTSTRAP_NAME, BootstrapMyxComponent.INTERFACE_NAME_OUT_AIM, 
				null, AIM_NAME, AIMMyxComponent.INTERFACE_NAME_IN_AIM));
			
			//Bootstrap->MyxRuntime
			myx.addWeld(myx.createWeld(
				null, BOOTSTRAP_NAME, BootstrapMyxComponent.INTERFACE_NAME_OUT_MYX, 
				null, MYXRUNTIME_NAME, MyxRuntimeComponent.INTERFACE_NAME_IN_MYXRUNTIME));
			
			//AIM->xArchADT
			myx.addWeld(myx.createWeld(
				null, AIM_NAME, AIMMyxComponent.INTERFACE_NAME_OUT_XARCH, 
				null, XARCHADT_NAME, XArchADTMyxComponent.INTERFACE_NAME_IN_XARCH));
			
			myx.begin(null, null);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-3);
		}
	}

}
