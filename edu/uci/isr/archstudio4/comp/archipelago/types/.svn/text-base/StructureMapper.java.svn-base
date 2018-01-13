package edu.uci.isr.archstudio4.comp.archipelago.types;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.util.InterfaceLocationSyncUtils;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.EndpointAssembly;
import edu.uci.isr.bna4.assemblies.MappingAssembly;
import edu.uci.isr.bna4.assemblies.StickySplineAssembly;
import edu.uci.isr.bna4.facets.IHasAssemblyData;
import edu.uci.isr.bna4.things.utility.AssemblyRootThing;
import edu.uci.isr.bna4.things.utility.EnvironmentPropertiesThing;
import edu.uci.isr.bna4.things.utility.NoThing;
import edu.uci.isr.widgets.swt.constants.Flow;
import edu.uci.isr.widgets.swt.constants.FontStyle;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xadlutils.XadlUtils.LinkInfo;
import edu.uci.isr.xarchflat.ObjRef;

public class StructureMapper{
	private StructureMapper(){
	}

	public static final String ASSEMBLY_TYPE_PROPERTY_NAME = "#assemblyType";
	public static final String ASSEMBLY_TYPE_COMPONENT = "component";
	public static final String ASSEMBLY_TYPE_CONNECTOR = "connector";
	public static final String ASSEMBLY_TYPE_INTERFACE = "interface";
	public static final String ASSEMBLY_TYPE_IIM = "interfaceInterfaceMapping";
	public static final String ASSEMBLY_TYPE_LINK = "link";
	public static final String BRICK_KIND_PROPERTY_NAME = "#brickKind";

	public static enum BrickKind{
		COMPONENT, CONNECTOR
	}

	public static void updateStructureInJob(ArchipelagoServices AS, IBNAWorld world, ObjRef archStructureRef){
		final ArchipelagoServices fAS = AS;
		final IBNAWorld fbnaWorld = world;
		final ObjRef farchStructureRef = archStructureRef;
		Job job = new Job("Updating Structure"){
			protected IStatus run(IProgressMonitor monitor){
				updateStructure(fAS, fbnaWorld, farchStructureRef);
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

	public static void updateStructure(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef archStructureRef){
		removeOrphanedBricks(AS, bnaWorld, archStructureRef);

		ObjRef[] componentRefs = AS.xarch.getAll(archStructureRef, "component");
		for(ObjRef componentRef : componentRefs){
			updateComponent(AS, bnaWorld, componentRef);
		}

		ObjRef[] connectorRefs = AS.xarch.getAll(archStructureRef, "connector");
		for(ObjRef connectorRef : connectorRefs){
			updateConnector(AS, bnaWorld, connectorRef);
		}

		removeOrphanedLinks(AS, bnaWorld, archStructureRef);
		ObjRef[] linkRefs = AS.xarch.getAll(archStructureRef, "link");
		for(ObjRef linkRef : linkRefs){
			updateLink(AS, bnaWorld, linkRef);
		}

		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaWorld.getBNAModel());
		if(!ept.hasProperty("hintsApplied")){
			StructureEditorSupport.readHints(AS, AS.xarch.getXArch(archStructureRef), bnaWorld.getBNAModel(), archStructureRef);
			ept.setProperty("hintsApplied", true);
		}
		AS.eventBus.fireArchipelagoEvent(new StructureEvents.StructureUpdatedEvent(bnaWorld, archStructureRef));
	}

	public synchronized static void removeOrphanedBricks(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef archStructureRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		IThing brickRootThing = getBrickRootThing(bnaModel);
		IThing[] childThings = bnaModel.getChildThings(brickRootThing);
		for(IThing t : childThings){
			if(isBrickAssemblyRootThing(t)){
				String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
				if(xArchID != null){
					BrickKind kind = t.getProperty(BRICK_KIND_PROPERTY_NAME);
					if(kind == null)
						continue;

					ObjRef[] brickRefs = null;
					switch(kind){
						case COMPONENT:
							brickRefs = AS.xarch.getAll(archStructureRef, "component");
							break;
						case CONNECTOR:
							brickRefs = AS.xarch.getAll(archStructureRef, "connector");
							break;
						default:
							continue;
					}

					boolean found = false;
					for(ObjRef refToMatch : brickRefs){
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

	public static void updateComponent(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef componentRef){
		updateBrick(AS, bnaWorld, componentRef, BrickKind.COMPONENT);
	}

	public static void updateConnector(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef connectorRef){
		updateBrick(AS, bnaWorld, connectorRef, BrickKind.CONNECTOR);
	}

	public synchronized static void updateBrick(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef brickRef, BrickKind kind){
		IBNAModel bnaModel = bnaWorld.getBNAModel();

		String xArchID = XadlUtils.getID(AS.xarch, brickRef);
		if(xArchID == null)
			return;

		BoxAssembly brickAssembly = null;
		IThing brickAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);

		boolean isNew = false;

		if((brickAssemblyRootThing == null) || (!isBrickAssemblyRootThing(brickAssemblyRootThing))){
			isNew = true;

			brickAssembly = BoxAssembly.create(bnaModel, getBrickRootThing(bnaModel));
			brickAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
			brickAssembly.getBoxGlassThing().setBoundingBox(p.x, p.y, 100, 100);

			brickAssembly.getBoxThing().setGradientFilled(true);
			brickAssembly.getBoxBorderThing().setLineWidth(1);
			brickAssembly.getBoxBorderThing().setLineStyle(SWT.LINE_SOLID);

			switch(kind){
				case COMPONENT:
					brickAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_COMPONENT);
					brickAssembly.getRootThing().setProperty(BRICK_KIND_PROPERTY_NAME, BrickKind.COMPONENT);
					brickAssembly.getBoxThing().setColor(getDefaultComponentColor(AS));
					brickAssembly.getBoxBorderThing().setCount(2);
					break;
				case CONNECTOR:
					brickAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_CONNECTOR);
					brickAssembly.getRootThing().setProperty(BRICK_KIND_PROPERTY_NAME, BrickKind.CONNECTOR);
					brickAssembly.getBoxThing().setColor(getDefaultConnectorColor(AS));
					brickAssembly.getBoxBorderThing().setCount(1);
					break;
			}
		}
		else{
			brickAssembly = BoxAssembly.attach(bnaModel, (IHasAssemblyData)brickAssemblyRootThing);
		}
		String description = XadlUtils.getDescription(AS.xarch, brickRef);
		if(description == null)
			description = "[No Description]";

		brickAssembly.getBoxedLabelThing().setText(description);
		brickAssembly.getBoxGlassThing().setToolTip(description);

		// update the interfaces
		removeOrphanedInterfaces(AS, bnaWorld, brickAssembly, brickRef);
		ObjRef[] interfaceRefs = AS.xarch.getAll(brickRef, "interface");
		for(ObjRef interfaceRef : interfaceRefs){
			updateInterface(AS, bnaWorld, brickAssembly, interfaceRef);
		}

		if(isNew){
			InterfaceLocationSyncUtils.syncInterfaceLocations(AS, AS.xarch.getXArch(brickRef), bnaWorld, brickAssembly.getRootThing(), false);
		}

		FontData[] fd = PreferenceConverter.getFontDataArray(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_FONT);
		if(fd.length > 0){
			brickAssembly.getBoxedLabelThing().setFontName(fd[0].getName());
			brickAssembly.getBoxedLabelThing().setFontSize(fd[0].getHeight());
			brickAssembly.getBoxedLabelThing().setFontStyle(FontStyle.fromSWT(fd[0].getStyle()));
		}

		updateSubarchitecture(AS, bnaWorld, brickAssembly, brickRef);

		AS.eventBus.fireArchipelagoEvent(new StructureEvents.BrickUpdatedEvent(bnaWorld, brickRef, kind, brickAssembly));
	}

	public synchronized static void updateSubarchitecture(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickAssembly, ObjRef brickRef){
		ObjRef brickTypeRef = XadlUtils.resolveXLink(AS.xarch, brickRef, "type");
		if(brickTypeRef != null){
			ObjRef subarchitectureRef = (ObjRef)AS.xarch.get(brickTypeRef, "subArchitecture");
			if(subarchitectureRef != null){
				ObjRef internalStructureRef = XadlUtils.resolveXLink(AS.xarch, subarchitectureRef, "archStructure");
				if(internalStructureRef != null){
					ObjRef xArchRef = AS.xarch.getXArch(brickTypeRef);
					IBNAWorld internalWorld = StructureEditorSupport.setupWorld(AS, xArchRef, internalStructureRef);
					if(internalWorld != null){
						StructureMapper.updateStructure(AS, internalWorld, internalStructureRef);
						brickAssembly.getWorldThing().setWorld(internalWorld);

						removeOrphanedInterfaceInterfaceMappings(AS, bnaWorld, brickAssembly, brickRef, internalWorld);
						ObjRef[] simRefs = AS.xarch.getAll(subarchitectureRef, "signatureInterfaceMapping");
						for(ObjRef simRef : simRefs){
							updateInterfaceInterfaceMapping(AS, bnaWorld, brickAssembly, brickRef, brickTypeRef, internalWorld, simRef);
						}
						AS.eventBus.fireArchipelagoEvent(new StructureEvents.SubarchitectureUpdatedEvent(bnaWorld, brickAssembly, brickRef));
						return;
					}
				}
			}
		}
		// It doesn't have a valid subarchitecture, clear it
		brickAssembly.getWorldThing().clearWorld();

		AS.eventBus.fireArchipelagoEvent(new StructureEvents.SubarchitectureUpdatedEvent(bnaWorld, brickAssembly, brickRef));
	}

	public static void removeOrphanedInterfaceInterfaceMappings(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickAssembly, ObjRef brickRef, IBNAWorld innerWorld){
		ObjRef[] interfaceRefs = AS.xarch.getAll(brickRef, "interface");
		ObjRef[] signatureRefs = new ObjRef[interfaceRefs.length];
		for(int i = 0; i < interfaceRefs.length; i++){
			signatureRefs[i] = XadlUtils.resolveXLink(AS.xarch, interfaceRefs[i], "signature");
		}

		ObjRef brickTypeRef = XadlUtils.resolveXLink(AS.xarch, brickRef, "type");
		if(brickTypeRef != null){
			IBNAModel bnaModel = bnaWorld.getBNAModel();
			ObjRef subArchitectureRef = (ObjRef)AS.xarch.get(brickTypeRef, "subArchitecture");
			if(subArchitectureRef != null){
				ObjRef[] simRefs = AS.xarch.getAll(subArchitectureRef, "signatureInterfaceMapping");

				IThing[] childThings = bnaModel.getChildThings(brickAssembly.getMappingRootThing());

				for(IThing t : childThings){
					if((t instanceof IHasAssemblyData) && (((IHasAssemblyData)t).getAssemblyKind().equals(MappingAssembly.ASSEMBLY_KIND))){
						String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
						if(xArchID != null){
							boolean found = false;
							for(ObjRef refToMatch : simRefs){
								String idToMatch = XadlUtils.getID(AS.xarch, refToMatch);
								if((idToMatch != null) && (idToMatch.equals(xArchID))){
									ObjRef simSignature = XadlUtils.resolveXLink(AS.xarch, refToMatch, "outerSignature");
									for(int i = 0; i < signatureRefs.length; i++){
										if(BNAUtils.nulleq(simSignature, signatureRefs[i])){
											found = true;
											break;
										}
									}
									if(found){
										break;
									}
								}
							}
							if(!found){
								bnaModel.removeThingAndChildren(t);
							}
						}
					}
				}
			}
		}
	}

	public static void updateInterfaceInterfaceMapping(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickAssembly, ObjRef brickRef, ObjRef brickTypeRef,
	IBNAWorld innerWorld, ObjRef simRef){
		ObjRef[] interfaceRefs = AS.xarch.getAll(brickRef, "interface");
		ObjRef[] signatureRefs = new ObjRef[interfaceRefs.length];
		for(int i = 0; i < interfaceRefs.length; i++){
			signatureRefs[i] = XadlUtils.resolveXLink(AS.xarch, interfaceRefs[i], "signature");
		}
		if(brickTypeRef != null){
			IBNAModel bnaModel = bnaWorld.getBNAModel();
			String xArchID = XadlUtils.getID(AS.xarch, simRef);
			if(xArchID == null)
				return;

			ObjRef outerSignatureRef = XadlUtils.resolveXLink(AS.xarch, simRef, "outerSignature");
			if((outerSignatureRef == null) || (!AS.xarch.isInstanceOf(outerSignatureRef, "types#Signature"))){
				return;
			}
			String outerSignatureID = XadlUtils.getID(AS.xarch, outerSignatureRef);
			if(outerSignatureID == null){
				return;
			}

			ObjRef innerInterfaceRef = XadlUtils.resolveXLink(AS.xarch, simRef, "innerInterface");
			if((innerInterfaceRef == null) || (!AS.xarch.isInstanceOf(innerInterfaceRef, "types#Interface"))){
				return;
			}
			String innerInterfaceID = XadlUtils.getID(AS.xarch, innerInterfaceRef);
			if(innerInterfaceID == null){
				return;
			}

			ObjRef outerInterfaceRef = null;
			for(int i = 0; i < signatureRefs.length; i++){
				if((outerSignatureRef != null) && (signatureRefs[i] != null) && AS.xarch.isEqual(outerSignatureRef, signatureRefs[i])){
					outerInterfaceRef = interfaceRefs[i];
					break;
				}
			}
			String outerInterfaceXArchID = XadlUtils.getID(AS.xarch, outerInterfaceRef);

			if((outerInterfaceRef != null) && (outerInterfaceXArchID != null)){
				MappingAssembly iimAssembly = null;
				IThing iimAssemblyRootThing = null;
				IThing[] iimAssemblyRootThings = ArchipelagoUtils.findAllThings(bnaModel, xArchID);
				for(int i = 0; i < iimAssemblyRootThings.length; i++){
					String iimAssemblyRootThingBrickXArchID = (String)iimAssemblyRootThings[i].getProperty("interfaceXArchID");
					if((iimAssemblyRootThingBrickXArchID != null) && (iimAssemblyRootThingBrickXArchID.equals(outerInterfaceXArchID))){
						iimAssemblyRootThing = iimAssemblyRootThings[i];
					}
				}

				if((iimAssemblyRootThing == null) || (!(iimAssemblyRootThing instanceof IHasAssemblyData))){
					iimAssembly = MappingAssembly.create(bnaModel, brickAssembly.getMappingRootThing());
					iimAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);
					iimAssembly.getRootThing().setProperty("interfaceXArchID", outerInterfaceXArchID);

					iimAssembly.getMappingThing().setColor(new RGB(0, 0, 0));
					iimAssembly.getMappingThing().setLineWidth(2);

					iimAssembly.getMappingGlassThing().setUserEditable(false);

					iimAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_IIM);
				}
				else{
					iimAssembly = MappingAssembly.attach(bnaModel, (IHasAssemblyData)iimAssemblyRootThing);
				}
				String description = XadlUtils.getDescription(AS.xarch, simRef);
				if(description == null)
					description = "[No Description]";

				iimAssembly.getMappingGlassThing().setToolTip(description);

				IThing outerInterfaceAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, outerInterfaceXArchID);
				if((outerInterfaceAssemblyRootThing != null) && (outerInterfaceAssemblyRootThing instanceof IHasAssemblyData)){
					EndpointAssembly outerInterfaceAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)outerInterfaceAssemblyRootThing);
					if(outerInterfaceAssembly.getEndpointGlassThing() != null){
						iimAssembly.getMappingGlassThing().setExternalEndpointStuckToID(outerInterfaceAssembly.getEndpointGlassThing().getID());
					}
				}
				if(brickAssembly.getWorldThing() != null){
					iimAssembly.getMappingGlassThing().setWorldThingID(brickAssembly.getWorldThing().getID());
					iimAssembly.getMappingThing().setWorldThingID(brickAssembly.getWorldThing().getID());
				}
				IBNAModel innerModel = innerWorld.getBNAModel();
				IThing interfaceAssemblyRootThing = ArchipelagoUtils.findThing(innerModel, innerInterfaceID);
				if((interfaceAssemblyRootThing != null) && (interfaceAssemblyRootThing instanceof IHasAssemblyData)){
					EndpointAssembly interfaceAssembly = EndpointAssembly.attach(innerModel, (IHasAssemblyData)interfaceAssemblyRootThing);
					if(interfaceAssembly.getEndpointGlassThing() != null){
						iimAssembly.getMappingGlassThing().setInternalEndpointStuckToThingID(interfaceAssembly.getEndpointGlassThing().getID());
						iimAssembly.getMappingGlassThing().setEndpoint2(interfaceAssembly.getEndpointGlassThing().getAnchorPoint());
					}
				}
				AS.eventBus
				.fireArchipelagoEvent(new StructureEvents.InterfaceInterfaceMappingUpdatedEvent(bnaWorld, brickAssembly, brickRef, brickTypeRef, innerWorld, simRef, iimAssembly));
			}
		}
	}

	public synchronized static void removeOrphanedInterfaces(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickAssembly, ObjRef brickRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String brickXArchID = XadlUtils.getID(AS.xarch, brickRef);
		if(brickXArchID == null)
			return;

		IThing[] childThings = bnaModel.getChildThings(brickAssembly.getEndpointRootThing());

		for(IThing t : childThings){
			if((t instanceof IHasAssemblyData) && (((IHasAssemblyData)t).getAssemblyKind().equals(EndpointAssembly.ASSEMBLY_KIND))){
				String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
				if(xArchID != null){
					ObjRef[] interfaceRefs = AS.xarch.getAll(brickRef, "interface");

					boolean found = false;
					for(ObjRef refToMatch : interfaceRefs){
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

	public synchronized static void updateInterface(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickAssembly, ObjRef interfaceRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String xArchID = XadlUtils.getID(AS.xarch, interfaceRef);
		if(xArchID == null)
			return;

		EndpointAssembly endpointAssembly = null;
		IThing endpointAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((endpointAssemblyRootThing == null) || (!(endpointAssemblyRootThing instanceof IHasAssemblyData))){
			endpointAssembly = EndpointAssembly.create(bnaModel, brickAssembly.getEndpointRootThing());
			endpointAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			endpointAssembly.getEndpointGlassThing().setAnchorPoint(new Point(brickAssembly.getBoxGlassThing().getBoundingBox().x, brickAssembly.getBoxGlassThing().getBoundingBox().y));

			endpointAssembly.getBoxThing().setColor(new RGB(0xff, 0xff, 0xff));

			endpointAssembly.getEndpointGlassThing().setBoundingBoxRailMasterThingID(brickAssembly.getBoxGlassThing().getID());

			endpointAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_INTERFACE);
		}
		else{
			endpointAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)endpointAssemblyRootThing);
		}
		String description = XadlUtils.getDescription(AS.xarch, interfaceRef);
		if(description == null)
			description = "[No Description]";

		endpointAssembly.getEndpointGlassThing().setToolTip(description);
		endpointAssembly.getTagThing().setText(description);

		String direction = XadlUtils.getDirection(AS.xarch, interfaceRef);
		Flow flow = Flow.NONE;
		if(direction != null){
			if(direction.equals("in")){
				flow = Flow.IN;
			}
			else if(direction.equals("out")){
				flow = Flow.OUT;
			}
			else if(direction.equals("inout")){
				flow = Flow.INOUT;
			}
		}
		endpointAssembly.getDirectionalLabelThing().setFlow(flow);
		AS.eventBus.fireArchipelagoEvent(new StructureEvents.InterfaceUpdatedEvent(bnaWorld, brickAssembly, interfaceRef, endpointAssembly));
	}

	public synchronized static void removeOrphanedLinks(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef archStructureRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		IThing linkRootThing = getLinkRootThing(bnaModel);
		IThing[] childThings = bnaModel.getChildThings(linkRootThing);
		for(IThing t : childThings){
			if((t instanceof IHasAssemblyData) && (((IHasAssemblyData)t).getAssemblyKind().equals(StickySplineAssembly.ASSEMBLY_KIND))){
				String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
				if(xArchID != null){
					ObjRef[] linkRefs = AS.xarch.getAll(archStructureRef, "link");
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

	public synchronized static void updateLink(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef linkRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();

		String xArchID = XadlUtils.getID(AS.xarch, linkRef);
		if(xArchID == null)
			return;

		StickySplineAssembly linkAssembly = null;
		IThing linkAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((linkAssemblyRootThing == null) || (!(linkAssemblyRootThing instanceof IHasAssemblyData))){
			linkAssembly = StickySplineAssembly.create(bnaModel, getLinkRootThing(bnaModel));
			linkAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
			linkAssembly.getSplineGlassThing().setEndpoint1(new Point(p.x - 50, p.y - 50));
			linkAssembly.getSplineGlassThing().setEndpoint2(new Point(p.x + 50, p.y + 50));
			linkAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_LINK);
		}
		else{
			linkAssembly = StickySplineAssembly.attach(bnaModel, (IHasAssemblyData)linkAssemblyRootThing);
		}
		bnaModel.fireStreamNotificationEvent("+updateLink$" + linkAssembly.getSplineGlassThing().getID());

		String description = XadlUtils.getDescription(AS.xarch, linkRef);
		if(description == null)
			description = "[No Description]";
		linkAssembly.getSplineGlassThing().setToolTip(description);

		LinkInfo linkInfo = XadlUtils.getLinkInfo(AS.xarch, linkRef, true);

		// Stick the endpoints
		boolean endpoint1Stuck = false;
		ObjRef endpoint1TargetRef = linkInfo.getPoint1Target();
		if(endpoint1TargetRef != null){
			String endpoint1TargetID = XadlUtils.getID(AS.xarch, endpoint1TargetRef);
			if(endpoint1TargetID != null){
				IThing endpoint1TargetThing = ArchipelagoUtils.findThing(bnaModel, endpoint1TargetID);
				EndpointAssembly endpointAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)endpoint1TargetThing);
				if(endpoint1TargetThing != null){
					linkAssembly.getSplineGlassThing().setEndpoint1StuckToThingID(endpointAssembly.getEndpointGlassThing().getID());
					endpoint1Stuck = true;
				}
			}
		}
		if(!endpoint1Stuck){
			linkAssembly.getSplineGlassThing().clearEndpoint1StuckToThingID();
		}

		boolean endpoint2Stuck = false;
		ObjRef endpoint2TargetRef = linkInfo.getPoint2Target();
		if(endpoint2TargetRef != null){
			String endpoint2TargetID = XadlUtils.getID(AS.xarch, endpoint2TargetRef);
			if(endpoint2TargetID != null){
				IThing endpoint2TargetThing = ArchipelagoUtils.findThing(bnaModel, endpoint2TargetID);
				EndpointAssembly endpointAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)endpoint2TargetThing);
				if(endpoint2TargetThing != null){
					linkAssembly.getSplineGlassThing().setEndpoint2StuckToThingID(endpointAssembly.getEndpointGlassThing().getID());
					endpoint2Stuck = true;
				}
			}
		}
		if(!endpoint2Stuck){
			linkAssembly.getSplineGlassThing().clearEndpoint2StuckToThingID();
		}
		bnaModel.fireStreamNotificationEvent("-updateLink$" + linkAssembly.getSplineGlassThing().getID());
		AS.eventBus.fireArchipelagoEvent(new StructureEvents.LinkUpdatedEvent(bnaWorld, linkRef, linkAssembly));
	}

	public static boolean isComponentAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_COMPONENT)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isConnectorAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_CONNECTOR)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isBrickAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_COMPONENT)){
					return true;
				}
				if(assemblyType.equals(ASSEMBLY_TYPE_CONNECTOR)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isInterfaceAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_INTERFACE)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isLinkAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_LINK)){
					return true;
				}
			}
		}
		return false;
	}

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

	// Functions to set up the BNA model with basic root things for the hierarchy
	public static final String BNA_BRICK_ROOT_THING_ID = "$BRICKROOT";

	public static final String BNA_LINK_ROOT_THING_ID = "$LINKROOT";

	protected static void initBNAModel(IBNAModel m){
		IThing normalRootThing = ArchipelagoUtils.getNormalRootThing(m);

		if(m.getThing(BNA_LINK_ROOT_THING_ID) == null){
			IThing linkRootThing = new NoThing(BNA_LINK_ROOT_THING_ID);
			m.addThing(linkRootThing);
		}

		if(m.getThing(BNA_BRICK_ROOT_THING_ID) == null){
			IThing brickRootThing = new NoThing(BNA_BRICK_ROOT_THING_ID);
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

	public static IThing getBrickRootThing(IBNAModel m){
		return getRootThing(m, BNA_BRICK_ROOT_THING_ID);
	}

	public static IThing getLinkRootThing(IBNAModel m){
		return getRootThing(m, BNA_LINK_ROOT_THING_ID);
	}

	public static BoxAssembly findBrickAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef brickRef){
		String xArchID = XadlUtils.getID(AS.xarch, brickRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isBrickAssemblyRootThing(t)){
				BoxAssembly assembly = BoxAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}

	public static EndpointAssembly findInterfaceAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef interfaceRef){
		String xArchID = XadlUtils.getID(AS.xarch, interfaceRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isInterfaceAssemblyRootThing(t)){
				EndpointAssembly assembly = EndpointAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}

	public static StickySplineAssembly findLinkAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef linkRef){
		String xArchID = XadlUtils.getID(AS.xarch, linkRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isLinkAssemblyRootThing(t)){
				StickySplineAssembly assembly = StickySplineAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}

}
