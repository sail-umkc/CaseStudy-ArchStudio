package edu.uci.isr.archstudio4.comp.archipelago.types;

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

public class StructureXArchEventHandlerLogic extends AbstractThingLogic implements IXArchEventHandlerLogic{
	protected ArchipelagoServices AS = null;
	protected TypedThingSetTrackingLogic<IHasWorld> ttstlView = null;
	
	public StructureXArchEventHandlerLogic(ArchipelagoServices AS, TypedThingSetTrackingLogic<IHasWorld> ttstlView){
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
				if((targetPathString != null) && (targetPathString.equals("archStructure"))){
					String targetID = XadlUtils.getID(AS.xarch, (ObjRef)evt.getTarget());
					if(targetID != null){
						EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(model);
						String editingArchStructureID = (String)ept.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
						if((editingArchStructureID != null) && (editingArchStructureID.equals(targetID))){
							AS.editor.clearEditor();
							AS.editor.displayDefaultEditor();
							return;
						}
					}
				}
			}
		}
		
		//We only handle things that occur under xArch/archStructure
		if((sourcePathString == null) || (!sourcePathString.startsWith("xArch/archStructure"))){
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

		ObjRef archStructureRef = srcAncestors[srcAncestors.length - 2];
		String archStructureID = XadlUtils.getID(AS.xarch, archStructureRef);
		
		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(model);
		String editingArchStructureID = (String)ept.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
		
		if(!BNAUtils.nulleq(archStructureID, editingArchStructureID)){
			return;
		}
		
		
		//This is an event that occurred on the structure associated with the passed in model

		if((evt.getEventType() == XArchFlatEvent.CLEAR_EVENT) || (evt.getEventType() == XArchFlatEvent.REMOVE_EVENT)){
			if((sourcePathString != null) && (sourcePathString.equals("xArch/archStructure"))){
				if((targetPathString != null) && (targetPathString.equals("component"))){
					StructureMapper.removeOrphanedBricks(AS, world, archStructureRef);
				}
				else if((targetPathString != null) && (targetPathString.equals("connector"))){
					StructureMapper.removeOrphanedBricks(AS, world, archStructureRef);
				}
				else if((targetPathString != null) && (targetPathString.equals("link"))){
					StructureMapper.removeOrphanedLinks(AS, world, archStructureRef);
				}
			}
			else if((sourcePathString != null) && (sourcePathString.startsWith("xArch/archStructure/component"))){
				StructureMapper.updateComponent(AS, world, srcAncestors[srcAncestors.length - 3]);
			}
			else if((sourcePathString != null) && (sourcePathString.startsWith("xArch/archStructure/connector"))){
				StructureMapper.updateConnector(AS, world, srcAncestors[srcAncestors.length - 3]);
			}
			else if((sourcePathString != null) && (sourcePathString.startsWith("xArch/archStructure/link"))){
				StructureMapper.updateLink(AS, world, srcAncestors[srcAncestors.length - 3]);
			}
		}
		
		if(targetPathString == null){
			targetPath = sourcePath;
		}
		
		if((evt.getTarget() == null) || (!(evt.getTarget() instanceof ObjRef))){
			return;
		}
		if((targetPathString != null) && (targetPathString.startsWith("xArch/archStructure/component"))){
			StructureMapper.updateComponent(AS, world, targetAncestors[targetAncestors.length - 3]);
		}
		else if((targetPathString != null) && (targetPathString.startsWith("xArch/archStructure/connector"))){
			StructureMapper.updateConnector(AS, world, targetAncestors[targetAncestors.length - 3]);
		}
		else if((targetPathString != null) && (targetPathString.startsWith("xArch/archStructure/link"))){
			StructureMapper.updateLink(AS, world, targetAncestors[targetAncestors.length - 3]);
		}
	}
	
}
