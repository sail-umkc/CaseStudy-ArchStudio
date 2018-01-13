package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.IXArchEventHandlerLogic;
import edu.uci.isr.bna4.BNAComposite;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.IThingLogic;
import edu.uci.isr.xarchflat.XArchFlatEvent;
import edu.uci.isr.xarchflat.XArchFlatListener;

public class StatechartsXArchEventHandler implements XArchFlatListener{

	protected ArchipelagoServices AS = null;
	
	public StatechartsXArchEventHandler(ArchipelagoServices AS){
		this.AS = AS;
	}
	
	public synchronized void handleXArchFlatEvent(XArchFlatEvent evt){
		BNAComposite currentlyEditingComposite = ArchipelagoUtils.getBNAComposite(AS.editor);
		if(currentlyEditingComposite != null){
			IBNAView view = currentlyEditingComposite.getView();
			if(view != null){
				IBNAWorld world = view.getWorld();
				if(world != null){
					String worldID = world.getID();
					if(worldID.equals("statechart")){
						IThingLogic[] tls = world.getThingLogicManager().getAllThingLogics();
						for(IThingLogic tl : tls){
							if(tl instanceof IXArchEventHandlerLogic){
								((IXArchEventHandlerLogic)tl).handleXArchFlatEvent(evt, world);
							}
						}
					}
				}
			}
		}
	}
	
}
