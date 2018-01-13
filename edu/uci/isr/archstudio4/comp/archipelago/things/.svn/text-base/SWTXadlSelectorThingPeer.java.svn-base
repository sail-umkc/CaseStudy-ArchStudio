package edu.uci.isr.archstudio4.comp.archipelago.things;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import edu.uci.isr.archstudio4.comp.resources.IResources;
import edu.uci.isr.archstudio4.util.XadlTreeContentProvider;
import edu.uci.isr.archstudio4.util.XadlTreeLabelProvider;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.things.swt.AbstractSWTTreeThingPeer;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public class SWTXadlSelectorThingPeer extends AbstractSWTTreeThingPeer{

	protected SWTXadlSelectorThing lt;
	
	public SWTXadlSelectorThingPeer(IThing t){
		super(t);
		if(!(t instanceof SWTXadlSelectorThing)){
			throw new IllegalArgumentException("SWTXadlSelectorThingPeer can only peer for SWTXadlSelectorThing");
		}
		this.lt = (SWTXadlSelectorThing)t;
	}
	
	protected Object getInput(){
		return lt.getContentProviderRootRef();
	}
	
	protected ITreeContentProvider getContentProvider(){
		XArchFlatInterface xarch = lt.getRepository();
		if(xarch != null){
			ObjRef rootRef = lt.getContentProviderRootRef();
			if(rootRef != null){
				int flags = lt.getContentProviderFlags();
				XadlTreeContentProvider contentProvider = new XadlTreeContentProvider(xarch, rootRef, flags);
				return contentProvider;
			}
		}
		return null;
	}
	
	protected ILabelProvider getLabelProvider(){
		XArchFlatInterface xarch = lt.getRepository();
		if(xarch != null){
			IResources resources = lt.getResources();
			if(resources != null){
				return new XadlTreeLabelProvider(xarch, resources);
			}
		}
		return null;
	}

}
