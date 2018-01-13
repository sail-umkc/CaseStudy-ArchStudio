package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import java.util.HashSet;
import java.util.Set;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.bna4.AbstractThingLogic;
import edu.uci.isr.bna4.BNAModelEvent;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAModelListener;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.ThingEvent;
import edu.uci.isr.bna4.facets.IHasStickyEndpoints;
import edu.uci.isr.bna4.things.glass.StickySplineGlassThing;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xarchflat.ObjRef;

public class StatechartsTransitionEndpointLogic extends AbstractThingLogic implements IBNAModelListener{
	protected ArchipelagoServices AS = null;
	protected ObjRef xArchRef = null;
	
	protected Set<String> transitionsBeingUpdated = new HashSet<String>();

	public StatechartsTransitionEndpointLogic(ArchipelagoServices services, ObjRef xArchRef){
		this.AS = services;
		this.xArchRef = xArchRef;
	}
	
	public void destroy(){
		transitionsBeingUpdated.clear();
	}
	
	public void bnaModelChanged(BNAModelEvent evt){
		if(evt.getEventType().equals(BNAModelEvent.EventType.STREAM_NOTIFICATION_EVENT)){
			String not = evt.getStreamNotification();
			if(not != null){
				if(not.startsWith("+updateTransition$")){
					transitionsBeingUpdated.add(not.substring(not.indexOf('$') + 1));
				}
				if(not.startsWith("-updateTransition$")){
					transitionsBeingUpdated.remove(not.substring(not.indexOf('$') + 1));
				}
			}
			return;
		}
		IBNAModel model = (IBNAModel)evt.getSource();
		if(model != null){
			if(evt.getEventType().equals(BNAModelEvent.EventType.THING_CHANGED)){
				IThing targetThing = evt.getTargetThing();
				if(targetThing instanceof StickySplineGlassThing){
					StickySplineGlassThing sgt = (StickySplineGlassThing)targetThing;
					if(transitionsBeingUpdated.contains(sgt.getID())){
						return;
					}
					ThingEvent tevt = evt.getThingEvent();
					if(tevt != null){
						int endpointNum = 0;
						String propertyName = tevt.getPropertyName();
						if((propertyName != null) && (propertyName.equals(IHasStickyEndpoints.ENDPOINT_1_STUCK_TO_THING_ID_PROPERTY_NAME))){
							endpointNum = 1;
						}
						else if((propertyName != null) && (propertyName.equals(IHasStickyEndpoints.ENDPOINT_2_STUCK_TO_THING_ID_PROPERTY_NAME))){
							endpointNum = 2;
						}
						else{
							return;
						}
						
						String newStuckToThingID = (String)tevt.getNewPropertyValue();
						IThing newStuckToThing = model.getThing(newStuckToThingID);
						if(newStuckToThing != null){
							IThing newStuckToParentThing = model.getParentThing(newStuckToThing);
							if(newStuckToParentThing != null){
								if(StatechartsMapper.isStateAssemblyRootThing(newStuckToParentThing)){
									String newStuckToXArchID = newStuckToParentThing.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
									if(newStuckToXArchID != null){
										ObjRef newStuckToRef = AS.xarch.getByID(xArchRef, newStuckToXArchID);
										if(newStuckToRef != null){
											if(AS.xarch.isInstanceOf(newStuckToRef, "statecharts#State")){
												IThing splineGlassThingParentThing = model.getParentThing(sgt);
												if(splineGlassThingParentThing != null){
													if(StatechartsMapper.isTransitionAssemblyRootThing(splineGlassThingParentThing)){
														String transitionXArchID = splineGlassThingParentThing.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
														if(transitionXArchID != null){
															ObjRef transitionRef = AS.xarch.getByID(xArchRef, transitionXArchID);
															if(transitionRef != null){
																String stateID = (String)AS.xarch.get(newStuckToRef, "id");
																if(stateID != null){
																	if(endpointNum == 1){
																		XadlUtils.setXLink(AS.xarch, transitionRef, "fromState", stateID);
																	}
																	else if(endpointNum == 2){
																		XadlUtils.setXLink(AS.xarch, transitionRef, "toState", stateID);
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
						else{
							//Disconnected the point
							IThing splineGlassThingParentThing = model.getParentThing(sgt);
							if(splineGlassThingParentThing != null){
								if(StatechartsMapper.isTransitionAssemblyRootThing(splineGlassThingParentThing)){
									String transitionXArchID = splineGlassThingParentThing.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
									if(transitionXArchID != null){
										ObjRef transitionRef = AS.xarch.getByID(xArchRef, transitionXArchID);
										if(transitionRef != null){
											if(endpointNum == 1){
												AS.xarch.clear(transitionRef, "fromState");
											}
											else if(endpointNum == 2){
												AS.xarch.clear(transitionRef, "toState");
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
