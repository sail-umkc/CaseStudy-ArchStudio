package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.types.ArchipelagoTypesConstants;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.DistinguishedStateAssembly;
import edu.uci.isr.bna4.assemblies.IAssembly;
import edu.uci.isr.bna4.assemblies.StickySplineAssembly;
import edu.uci.isr.bna4.constants.ArrowheadShape;
import edu.uci.isr.bna4.facets.IHasAssemblyData;
import edu.uci.isr.bna4.things.glass.BoxGlassThing;
import edu.uci.isr.bna4.things.utility.AssemblyRootThing;
import edu.uci.isr.bna4.things.utility.EnvironmentPropertiesThing;
import edu.uci.isr.bna4.things.utility.NoThing;
import edu.uci.isr.widgets.swt.constants.FontStyle;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xadlutils.XadlUtils.LinkInfo;
import edu.uci.isr.xarchflat.ObjRef;

public class StatechartsMapper{
	private StatechartsMapper(){
	}

	public static final String ASSEMBLY_TYPE_PROPERTY_NAME = "#assemblyType";
	public static final String ASSEMBLY_TYPE_STATE = "state";
	public static final String ASSEMBLY_TYPE_TRANSITION = "transition";
	public static final String STATE_KIND_PROPERTY_NAME = "#stateKind";

	public static enum StateKind{
		INITIAL, NORMAL, FINAL
	}

	public static void updateStatechartInJob(ArchipelagoServices AS, IBNAWorld world, ObjRef statechartRef){
		final ArchipelagoServices fAS = AS;
		final IBNAWorld fbnaWorld = world;
		final ObjRef fstatechartRef = statechartRef;
		Job job = new Job("Updating Statechart"){
			protected IStatus run(IProgressMonitor monitor){
				updateStatechart(fAS, fbnaWorld, fstatechartRef);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		/*
		 * try{ job.join(); } catch(InterruptedException ie){}
		 */
	}

	public static void updateStatechart(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef statechartRef){
		removeOrphanedStates(AS, bnaWorld, statechartRef);

		ObjRef[] stateRefs = AS.xarch.getAll(statechartRef, "state");
		for(ObjRef stateRef : stateRefs){
			updateState(AS, bnaWorld, stateRef);
		}

		removeOrphanedTransitions(AS, bnaWorld, statechartRef);
		ObjRef[] transitionRefs = AS.xarch.getAll(statechartRef, "transition");
		for(ObjRef transitionRef : transitionRefs){
			updateTransition(AS, bnaWorld, transitionRef);
		}

		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaWorld.getBNAModel());
		if(!ept.hasProperty("hintsApplied")){
			StatechartsEditorSupport.readHints(AS, AS.xarch.getXArch(statechartRef), bnaWorld.getBNAModel(), statechartRef);
			ept.setProperty("hintsApplied", true);
		}
		AS.eventBus.fireArchipelagoEvent(new StatechartsEvents.StatechartUpdatedEvent(bnaWorld, statechartRef));
	}

	public synchronized static void removeOrphanedStates(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef statechartRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		IThing stateRootThing = getStateRootThing(bnaModel);
		IThing[] childThings = bnaModel.getChildThings(stateRootThing);
		for(IThing t : childThings){
			if(isStateAssemblyRootThing(t)){
				String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
				if(xArchID != null){
					ObjRef[] stateRefs = AS.xarch.getAll(statechartRef, "state");

					boolean found = false;
					for(ObjRef refToMatch : stateRefs){
						String idToMatch = XadlUtils.getID(AS.xarch, refToMatch);
						if((idToMatch != null) && (idToMatch.equals(xArchID))){
							found = true;
							break;
						}
					}
					if(!found){
						bnaModel.removeThingAndChildren(t);
					}
				}
			}
		}
	}

	public synchronized static void updateState(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef stateRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();

		String xArchID = XadlUtils.getID(AS.xarch, stateRef);
		if(xArchID == null)
			return;

		String description = XadlUtils.getDescription(AS.xarch, stateRef);
		if(description == null){
			description = "[No Description]";
		}
		
		StateKind stateKind = getStateKind(AS, stateRef);
		if(stateKind != null){
			if(stateKind.equals(StateKind.FINAL) || (stateKind.equals(StateKind.INITIAL))){
				IAssembly assembly = findStateAssembly(AS, bnaModel, stateRef);
				DistinguishedStateAssembly stateAssembly = null;
				boolean isNew = false;
				
				if((assembly == null) || (!(assembly instanceof DistinguishedStateAssembly)) || !isStateAssemblyRootThing(assembly.getRootThing())){
					isNew = true;
					
					stateAssembly = DistinguishedStateAssembly.create(bnaModel, getStateRootThing(bnaModel));
					stateAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);
					
					Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
					stateAssembly.getDistinguishedStateGlassThing().setAnchorPoint(new Point(p.x, p.y));
					
					stateAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_STATE);
					stateAssembly.getRootThing().setProperty(STATE_KIND_PROPERTY_NAME, stateKind);
				}
				else{
					stateAssembly = DistinguishedStateAssembly.attach(bnaModel, assembly.getRootThing());
				}
				if(stateKind.equals(StateKind.FINAL)){
					stateAssembly.getDistinguishedStateThing().setIsFinalState(true);
				}
				else{
					stateAssembly.getDistinguishedStateThing().setIsFinalState(false);
				}
				stateAssembly.getDistinguishedStateGlassThing().setToolTip(description);

				AS.eventBus.fireArchipelagoEvent(new StatechartsEvents.StateUpdatedEvent(bnaWorld, stateRef, stateKind, stateAssembly));
			}
			else if(stateKind.equals(StateKind.NORMAL)){
				IAssembly assembly = findStateAssembly(AS, bnaModel, stateRef);
				BoxAssembly stateAssembly = null;
				boolean isNew = false;
				
				if((assembly == null) || (!(assembly instanceof BoxAssembly)) || !isStateAssemblyRootThing(assembly.getRootThing())){
					isNew = true;
					
					stateAssembly = BoxAssembly.create(bnaModel, getStateRootThing(bnaModel));
					stateAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

					Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
					stateAssembly.getBoxGlassThing().setBoundingBox(p.x, p.y, 125, 100);
					stateAssembly.getBoxGlassThing().setStickyBoxMode(BoxGlassThing.StickyBoxMode.BOUNDING_BOX);
					
					stateAssembly.getBoxThing().setCornerHeight(15);
					stateAssembly.getBoxThing().setCornerWidth(15);
					stateAssembly.getBoxThing().setColor(new RGB(255,255,255));
					stateAssembly.getBoxThing().setGradientFilled(true);

					stateAssembly.getBoxBorderThing().setCornerHeight(15);
					stateAssembly.getBoxBorderThing().setCornerWidth(15);
					stateAssembly.getBoxBorderThing().setColor(new RGB(0,0,0));

					stateAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_STATE);
					stateAssembly.getRootThing().setProperty(STATE_KIND_PROPERTY_NAME, stateKind);
				}
				else{
					stateAssembly = BoxAssembly.attach(bnaModel, assembly.getRootThing());
				}
				FontData[] fd = PreferenceConverter.getFontDataArray(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_FONT);
				if(fd.length > 0){
					stateAssembly.getBoxedLabelThing().setFontName(fd[0].getName());
					stateAssembly.getBoxedLabelThing().setFontSize(fd[0].getHeight());
					stateAssembly.getBoxedLabelThing().setFontStyle(FontStyle.fromSWT(fd[0].getStyle()));
				}

				stateAssembly.getBoxedLabelThing().setText(description);
				stateAssembly.getBoxGlassThing().setToolTip(description);

				AS.eventBus.fireArchipelagoEvent(new StatechartsEvents.StateUpdatedEvent(bnaWorld, stateRef, stateKind, stateAssembly));
			}
		}
	}

	public synchronized static void removeOrphanedTransitions(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef statechartRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		IThing linkRootThing = getTransitionRootThing(bnaModel);
		IThing[] childThings = bnaModel.getChildThings(linkRootThing);
		for(IThing t : childThings){
			if((t instanceof IHasAssemblyData) && (((IHasAssemblyData)t).getAssemblyKind().equals(StickySplineAssembly.ASSEMBLY_KIND))){
				String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
				if(xArchID != null){
					ObjRef[] linkRefs = AS.xarch.getAll(statechartRef, "transition");
					boolean found = false;
					for(ObjRef refToMatch : linkRefs){
						String idToMatch = XadlUtils.getID(AS.xarch, refToMatch);
						if((idToMatch != null) && (idToMatch.equals(xArchID))){
							found = true;
							break;
						}
					}
					if(!found){
						bnaModel.removeThingAndChildren(t);
					}
				}
			}
		}
	}

	public synchronized static void updateTransition(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef transitionRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();

		String xArchID = XadlUtils.getID(AS.xarch, transitionRef);
		if(xArchID == null) return;

		StickySplineAssembly transitionAssembly = null;
		IThing transitionAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((transitionAssemblyRootThing == null) || (!(transitionAssemblyRootThing instanceof IHasAssemblyData))){
			transitionAssembly = StickySplineAssembly.create(bnaModel, getTransitionRootThing(bnaModel));
			transitionAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			transitionAssembly.getEndpoint2ArrowheadThing().setArrowheadShape(ArrowheadShape.WEDGE);
			
			Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
			transitionAssembly.getSplineGlassThing().setEndpoint1(new Point(p.x - 50, p.y - 50));
			transitionAssembly.getSplineGlassThing().setEndpoint2(new Point(p.x + 50, p.y + 50));
			transitionAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_TRANSITION);
		}
		else{
			transitionAssembly = StickySplineAssembly.attach(bnaModel, (IHasAssemblyData)transitionAssemblyRootThing);
		}
		bnaModel.fireStreamNotificationEvent("+updateTransition$" + transitionAssembly.getSplineGlassThing().getID());

		String description = XadlUtils.getDescription(AS.xarch, transitionRef);
		if(description == null)
			description = "[No Description]";
		transitionAssembly.getSplineGlassThing().setToolTip(description);

		LinkInfo linkInfo = XadlUtils.getTransitionInfo(AS.xarch, transitionRef, true);

		// Stick the endpoints
		boolean endpoint1Stuck = false;
		ObjRef endpoint1TargetRef = linkInfo.getPoint1Target();
		if(endpoint1TargetRef != null){
			IAssembly assembly = findStateAssembly(AS, bnaModel, endpoint1TargetRef);
			if(assembly != null){
				if(assembly instanceof BoxAssembly){
					transitionAssembly.getSplineGlassThing().setEndpoint1StuckToThingID(((BoxAssembly)assembly).getBoxGlassThing().getID());
					endpoint1Stuck = true;
				}
				else if(assembly instanceof DistinguishedStateAssembly){
					transitionAssembly.getSplineGlassThing().setEndpoint1StuckToThingID(((DistinguishedStateAssembly)assembly).getDistinguishedStateGlassThing().getID());
					endpoint1Stuck = true;
				}
			}
		}
		if(!endpoint1Stuck){
			transitionAssembly.getSplineGlassThing().clearEndpoint1StuckToThingID();
		}

		boolean endpoint2Stuck = false;
		ObjRef endpoint2TargetRef = linkInfo.getPoint2Target();
		if(endpoint2TargetRef != null){
			IAssembly assembly = findStateAssembly(AS, bnaModel, endpoint2TargetRef);
			if(assembly != null){
				if(assembly instanceof BoxAssembly){
					transitionAssembly.getSplineGlassThing().setEndpoint2StuckToThingID(((BoxAssembly)assembly).getBoxGlassThing().getID());
					endpoint2Stuck = true;
				}
				else if(assembly instanceof DistinguishedStateAssembly){
					transitionAssembly.getSplineGlassThing().setEndpoint2StuckToThingID(((DistinguishedStateAssembly)assembly).getDistinguishedStateGlassThing().getID());
					endpoint2Stuck = true;
				}
			}
		}
		if(!endpoint2Stuck){
			transitionAssembly.getSplineGlassThing().clearEndpoint2StuckToThingID();
		}
		bnaModel.fireStreamNotificationEvent("-updateTransition$" + transitionAssembly.getSplineGlassThing().getID());
		AS.eventBus.fireArchipelagoEvent(new StatechartsEvents.TransitionUpdatedEvent(bnaWorld, transitionRef, transitionAssembly));
	}

	public static StateKind getStateKind(ArchipelagoServices AS, ObjRef stateRef){
		String stateTypeString = (String)AS.xarch.get(stateRef, "stateType");
		if(stateTypeString == null){
			return null;
		}
		else if(stateTypeString.equals("initial")){
			return StateKind.INITIAL;
		}
		else if(stateTypeString.equals("final")){
			return StateKind.FINAL;
		}
		else if(stateTypeString.equals("state")){
			return StateKind.NORMAL;
		}
		return null;
	}
	
	public static boolean isStateAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_STATE)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isTransitionAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_TRANSITION)){
					return true;
				}
			}
		}
		return false;
	}

	/*
	public static RGB getDefaultComponentColor(ArchipelagoServices AS){
		if(AS.prefs.contains(ArchipelagoTypesConstants.PREF_DEFAULT_COMPONENT_COLOR)){
			return PreferenceConverter.getColor(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_COMPONENT_COLOR);
		}
		return ArchipelagoTypesConstants.DEFAULT_COMPONENT_RGB;
	}

	public static RGB getDefaultConnectorColor(ArchipelagoServices AS){
		if(AS.prefs.contains(ArchipelagoTypesConstants.PREF_DEFAULT_CONNECTOR_COLOR)){
			return PreferenceConverter.getColor(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_CONNECTOR_COLOR);
		}
		return ArchipelagoTypesConstants.DEFAULT_CONNECTOR_RGB;
	}
	*/
	
	// Functions to set up the BNA model with basic root things for the hierarchy
	public static final String BNA_STATE_ROOT_THING_ID = "$STATEROOT";

	public static final String BNA_TRANSITION_ROOT_THING_ID = "$TRANSITIONROOT";

	protected static void initBNAModel(IBNAModel m){
		IThing normalRootThing = ArchipelagoUtils.getNormalRootThing(m);

		if(m.getThing(BNA_TRANSITION_ROOT_THING_ID) == null){
			IThing linkRootThing = new NoThing(BNA_TRANSITION_ROOT_THING_ID);
			m.addThing(linkRootThing);
		}

		if(m.getThing(BNA_STATE_ROOT_THING_ID) == null){
			IThing brickRootThing = new NoThing(BNA_STATE_ROOT_THING_ID);
			m.addThing(brickRootThing);
		}
	}

	protected static IThing getRootThing(IBNAModel m, String id){
		IThing rootThing = m.getThing(id);
		if(rootThing == null){
			initBNAModel(m);
			return m.getThing(id);
		}
		return rootThing;
	}

	public static IThing getStateRootThing(IBNAModel m){
		return getRootThing(m, BNA_STATE_ROOT_THING_ID);
	}

	public static IThing getTransitionRootThing(IBNAModel m){
		return getRootThing(m, BNA_TRANSITION_ROOT_THING_ID);
	}

	public static IAssembly findStateAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef stateRef){
		String xArchID = XadlUtils.getID(AS.xarch, stateRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isStateAssemblyRootThing(t)){
				IHasAssemblyData art = (IHasAssemblyData)t;
				if(art.getAssemblyKind().equals(BoxAssembly.ASSEMBLY_KIND)){
					BoxAssembly assembly = BoxAssembly.attach(m, (IHasAssemblyData)t);
					return assembly;
				}
				else if(art.getAssemblyKind().equals(DistinguishedStateAssembly.ASSEMBLY_KIND)){
					DistinguishedStateAssembly assembly = DistinguishedStateAssembly.attach(m, art);
					return assembly;
				}
			}
		}
		return null;
	}

	public static StickySplineAssembly findTransitionAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef transitionRef){
		String xArchID = XadlUtils.getID(AS.xarch, transitionRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isTransitionAssemblyRootThing(t)){
				StickySplineAssembly assembly = StickySplineAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}

}
