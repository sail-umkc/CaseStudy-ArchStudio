package edu.uci.isr.archstudio4.comp.resources;

import java.lang.reflect.Proxy;

import edu.uci.isr.myx.fw.AbstractMyxSimpleBrick;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.MyxUtils;

public class ResourcesMyxComponent extends AbstractMyxSimpleBrick{
  

	public static final IMyxName INTERFACE_NAME_IN_RESOURCES = MyxUtils.createName("resources");

	public ResourcesMyxComponent() {
	}
	
	IResources proxy = null;

	public void init() {
		proxy = (IResources) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { IResources.class }, new ResourcesProxy());
	}
	
	public void destroy(){
		proxy = null;
	}

	public Object getServiceObject(IMyxName interfaceName){
		if(interfaceName.equals(INTERFACE_NAME_IN_RESOURCES)){
			return proxy;
		}
		return null;
	}
	
}
