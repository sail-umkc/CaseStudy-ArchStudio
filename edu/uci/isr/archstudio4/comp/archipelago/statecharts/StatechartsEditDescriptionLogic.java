package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.util.AbstractEditDescriptionLogic;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.things.glass.BoxGlassThing;
import edu.uci.isr.bna4.things.glass.DistinguishedStateGlassThing;
import edu.uci.isr.bna4.things.glass.StickySplineGlassThing;
import edu.uci.isr.xarchflat.ObjRef;

public class StatechartsEditDescriptionLogic extends AbstractEditDescriptionLogic{
	
	public StatechartsEditDescriptionLogic(ArchipelagoServices AS, ObjRef xArchRef){
		super(AS, xArchRef);
	}
	
	public boolean matches(IBNAView view, IThing t){
		if(t instanceof BoxGlassThing){
			IThing pt = view.getWorld().getBNAModel().getParentThing(t);
			if(pt != null){
				return StatechartsMapper.isStateAssemblyRootThing(pt);
			}
		}
		else if(t instanceof DistinguishedStateGlassThing){
			IThing pt = view.getWorld().getBNAModel().getParentThing(t);
			if(pt != null){
				return StatechartsMapper.isStateAssemblyRootThing(pt);
			}
		}
		else if(t instanceof StickySplineGlassThing){
			IThing pt = view.getWorld().getBNAModel().getParentThing(t);
			if(pt != null){
				return StatechartsMapper.isTransitionAssemblyRootThing(pt);
			}
		}
		return false;
	}
	
	public String getXArchID(IBNAView view, IThing t){
		if(t instanceof BoxGlassThing){
			IThing parentThing = view.getWorld().getBNAModel().getParentThing(t);
			return parentThing.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
		}
		else if(t instanceof DistinguishedStateGlassThing){
			IThing parentThing = view.getWorld().getBNAModel().getParentThing(t);
			return parentThing.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
		}
		else if(t instanceof StickySplineGlassThing){
			IThing parentThing = view.getWorld().getBNAModel().getParentThing(t);
			return parentThing.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
		}
		return null;
	}
}
