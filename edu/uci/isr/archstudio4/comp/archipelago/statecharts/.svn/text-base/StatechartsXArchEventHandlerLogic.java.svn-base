package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.IXArchEventHandlerLogic;
import edu.uci.isr.bna4.AbstractThingLogic;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.facets.IHasWorld;
import edu.uci.isr.bna4.logics.tracking.TypedThingSetTrackingLogic;
import edu.uci.isr.bna4.things.utility.EnvironmentPropertiesThing;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatEvent;
import edu.uci.isr.xarchflat.XArchPath;

public class StatechartsXArchEventHandlerLogic extends AbstractThingLogic implements IXArchEventHandlerLogic{
	protected ArchipelagoServices AS = null;
	protected TypedThingSetTrackingLogic<IHasWorld> ttstlView = null;
	
	public StatechartsXArchEventHandlerLogic(ArchipelagoServices AS, TypedThingSetTrackingLogic<IHasWorld> ttstlView){
		this.AS = AS;
		this.ttstlView = ttstlView;
	}
	
	public void handleXArchFlatEvent(XArchFlatEvent evt, IBNAWorld world){
		ArchipelagoUtils.sendEventToInnerViews(evt, world, ttstlView);
		
		IBNAModel model = world.getBNAModel();
		
		XArchPath sourcePath = evt.getSourcePath();
		String sourcePathString = null;
		if(sourcePath != null) sourcePathString = sourcePath.toTagsOnlyString();
		
		XArchPath targetPath = evt.getTargetPath();
		String targetPathString = null;
		if(targetPath != null) targetPathString = targetPath.toTagsOnlyString();

		//See if we're removing the currently editing archstructure;
		//if so, then clear the editor and display the default editor
		if(evt.getEventType() == XArchFlatEvent.REMOVE_EVENT){
			if((sourcePathString != null) && (sourcePathString.equals("xArch"))){
				if((targetPathString != null) && (targetPathString.equals("statechart"))){
					String targetID = XadlUtils.getID(AS.xarch, (ObjRef)evt.getTarget());
					if(targetID != null){
						EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(model);
						String editingStatechartID = (String)ept.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
						if((editingStatechartID != null) && (editingStatechartID.equals(targetID))){
							AS.editor.clearEditor();
							AS.editor.displayDefaultEditor();
							return;
						}
					}
				}
			}
		}
		
		//We only handle things that occur under xArch/archStructure
		if((sourcePathString == null) || (!sourcePathString.startsWith("xArch/statechart"))){
			return;
		}
		
		ObjRef sourceRef = evt.getSource();

		ObjRef[] srcAncestors = evt.getSourceAncestors();
		ObjRef[] targetAncestors;
		if(evt.getTarget() instanceof ObjRef){
			targetAncestors = new ObjRef[srcAncestors.length + 1];
			targetAncestors[0] = (ObjRef)evt.getTarget();
			System.arraycopy(srcAncestors, 0, targetAncestors, 1, srcAncestors.length);
		}
		else{
			targetAncestors = srcAncestors;
		}

		ObjRef statechartRef = srcAncestors[srcAncestors.length - 2];
		String statechartID = XadlUtils.getID(AS.xarch, statechartRef);
		
		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(model);
		String editingStatechartID = (String)ept.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
		
		if(!BNAUtils.nulleq(statechartID, editingStatechartID)){
			return;
		}
		
		
		//This is an event that occurred on the structure associated with the passed in model

		if((evt.getEventType() == XArchFlatEvent.CLEAR_EVENT) || (evt.getEventType() == XArchFlatEvent.REMOVE_EVENT)){
			if((sourcePathString != null) && (sourcePathString.equals("xArch/statechart"))){
				if((targetPathString != null) && (targetPathString.equals("state"))){
					StatechartsMapper.removeOrphanedStates(AS, world, statechartRef);
				}
				else if((targetPathString != null) && (targetPathString.equals("transition"))){
					StatechartsMapper.removeOrphanedTransitions(AS, world, statechartRef);
				}
			}
			else if((sourcePathString != null) && (sourcePathString.startsWith("xArch/statechart/state"))){
				StatechartsMapper.updateState(AS, world, srcAncestors[srcAncestors.length - 3]);
			}
			else if((sourcePathString != null) && (sourcePathString.startsWith("xArch/statechart/transition"))){
				StatechartsMapper.updateTransition(AS, world, srcAncestors[srcAncestors.length - 3]);
			}
		}
		
		if(targetPathString == null){
			targetPath = sourcePath;
		}
		
		if((evt.getTarget() == null) || (!(evt.getTarget() instanceof ObjRef))){
			return;
		}
		if((targetPathString != null) && (targetPathString.startsWith("xArch/statechart/state"))){
			StatechartsMapper.updateState(AS, world, targetAncestors[targetAncestors.length - 3]);
		}
		else if((targetPathString != null) && (targetPathString.startsWith("xArch/statechart/transition"))){
			StatechartsMapper.updateTransition(AS, world, targetAncestors[targetAncestors.length - 3]);
		}
	}
}
