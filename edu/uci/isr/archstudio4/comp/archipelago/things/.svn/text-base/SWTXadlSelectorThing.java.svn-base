package edu.uci.isr.archstudio4.comp.archipelago.things;

import edu.uci.isr.archstudio4.comp.resources.IResources;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.things.swt.AbstractSWTTreeThing;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public class SWTXadlSelectorThing extends AbstractSWTTreeThing{

	public SWTXadlSelectorThing(){
		this(BNAUtils.generateUID(SWTXadlSelectorThing.class.getName()));
	}
	
	public SWTXadlSelectorThing(String id){
		super(id);
	}
	
	protected void initProperties(){
		super.initProperties();
		setContentProviderFlags(0);
	}
	
	public void setRepository(XArchFlatInterface xarch){
		setProperty("$xarch", xarch);
	}
	
	public XArchFlatInterface getRepository(){
		return getProperty("$xarch");
	}
	
	public void setResources(IResources resources){
		setProperty("$resources", resources);
	}
	
	public IResources getResources(){
		return getProperty("$resources");
	}
	
	public void setContentProviderRootRef(ObjRef rootRef){
		setProperty("rootRef", rootRef);
	}
	
	public ObjRef getContentProviderRootRef(){
		return getProperty("rootRef");
	}
	
	public void setContentProviderFlags(int contentProviderFlags){
		setProperty("contentProviderFlags", contentProviderFlags);
	}
	
	public int getContentProviderFlags(){
		return getProperty("contentProviderFlags");
	}
}
