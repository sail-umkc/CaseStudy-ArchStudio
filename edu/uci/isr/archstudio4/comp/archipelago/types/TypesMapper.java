package edu.uci.isr.archstudio4.comp.archipelago.types;

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
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.EndpointAssembly;
import edu.uci.isr.bna4.assemblies.MappingAssembly;
import edu.uci.isr.bna4.facets.IHasAssemblyData;
import edu.uci.isr.bna4.things.utility.AssemblyRootThing;
import edu.uci.isr.bna4.things.utility.EnvironmentPropertiesThing;
import edu.uci.isr.bna4.things.utility.NoThing;
import edu.uci.isr.widgets.swt.constants.Flow;
import edu.uci.isr.widgets.swt.constants.FontStyle;
import edu.uci.isr.widgets.swt.constants.Orientation;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xarchflat.ObjRef;

public class TypesMapper{
	private TypesMapper(){}
	
	public static final String ASSEMBLY_TYPE_PROPERTY_NAME = "#assemblyType";
	public static final String ASSEMBLY_TYPE_COMPONENT_TYPE = "componentType";
	public static final String ASSEMBLY_TYPE_CONNECTOR_TYPE = "connectorType";
	public static final String ASSEMBLY_TYPE_SIGNATURE = "signature";
	public static final String ASSEMBLY_TYPE_INTERFACE_TYPE = "interfaceType";
	public static final String ASSEMBLY_TYPE_SIM = "signatureInterfaceMapping";
	
	public static final String BRICK_TYPE_KIND_PROPERTY_NAME = "#brickTypeKind";
	
	public static enum TypeKind{
		COMPONENT_TYPE, CONNECTOR_TYPE, INTERFACE_TYPE
	}
	
	public static void updateTypeInJob(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef typeRef){
		final ArchipelagoServices fAS = AS;
		final IBNAWorld fbnaWorld = bnaWorld;
		final ObjRef ftypeRef = typeRef;
		
		Job job = new Job("Updating Type"){
			protected IStatus run(IProgressMonitor monitor){
				updateType(fAS, fbnaWorld, ftypeRef);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		/*
		try{
			job.join();
		}
		catch(InterruptedException ie){}
		*/
	}
	
	public static void updateType(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef typeRef){
		final ArchipelagoServices fAS = AS;
		final IBNAWorld fbnaWorld = bnaWorld;
		final ObjRef ftypeRef = typeRef;
		
		String elementName = AS.xarch.getElementName(typeRef);
		TypeKind tk = null;
		if((elementName != null) && (elementName.equals("componentType"))){
			tk = TypeKind.COMPONENT_TYPE;
		}
		else if((elementName != null) && (elementName.equals("connectorType"))){
			tk = TypeKind.CONNECTOR_TYPE;
		}
		else if((elementName != null) && (elementName.equals("interfaceType"))){
			tk = TypeKind.INTERFACE_TYPE;
		}
		else{
			return;
		}
		
		if(tk.equals(TypeKind.COMPONENT_TYPE) || tk.equals(TypeKind.CONNECTOR_TYPE)){
			updateBrickType(fAS, fbnaWorld, ftypeRef, tk);
		}
		else if(tk.equals(TypeKind.INTERFACE_TYPE)){
			updateInterfaceType(fAS, fbnaWorld, ftypeRef);
		}
		
		AS.eventBus.fireArchipelagoEvent(new TypesEvents.TypeUpdatedEvent(bnaWorld, typeRef));
	}
	
	public static void updateComponentType(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef componentTypeRef){
		updateBrickType(AS, bnaWorld, componentTypeRef, TypeKind.COMPONENT_TYPE);
	}
	
	public static void updateConnectorType(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef connectorTypeRef){
		updateBrickType(AS, bnaWorld, connectorTypeRef, TypeKind.CONNECTOR_TYPE);
	}
	
	public synchronized static void updateBrickType(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef brickTypeRef, TypeKind kind){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String xArchID = XadlUtils.getID(AS.xarch, brickTypeRef);
		if(xArchID == null) return;
		
		BoxAssembly brickTypeAssembly = null;
		IThing brickTypeAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((brickTypeAssemblyRootThing == null) || (!(brickTypeAssemblyRootThing instanceof IHasAssemblyData))){
			brickTypeAssembly = BoxAssembly.create(bnaModel, getBrickTypeRootThing(bnaModel, kind));
			brickTypeAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
			brickTypeAssembly.getBoxGlassThing().setBoundingBox(p.x, p.y, 100, 100);
			
			brickTypeAssembly.getBoxThing().setGradientFilled(true);
			
			switch(kind){
				case COMPONENT_TYPE:
					brickTypeAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_COMPONENT_TYPE);
					brickTypeAssembly.getRootThing().setProperty(BRICK_TYPE_KIND_PROPERTY_NAME, TypeKind.COMPONENT_TYPE);
					brickTypeAssembly.getBoxThing().setColor(getDefaultComponentTypeColor(AS));
					brickTypeAssembly.getBoxBorderThing().setCount(2);
					break;
				case CONNECTOR_TYPE:
					brickTypeAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_CONNECTOR_TYPE);
					brickTypeAssembly.getRootThing().setProperty(BRICK_TYPE_KIND_PROPERTY_NAME, TypeKind.CONNECTOR_TYPE);
					brickTypeAssembly.getBoxThing().setColor(getDefaultComponentTypeColor(AS));
					brickTypeAssembly.getBoxBorderThing().setCount(1);
					break;
			}
		}
		else{
			brickTypeAssembly = BoxAssembly.attach(bnaModel, (IHasAssemblyData)brickTypeAssemblyRootThing);
		}
		String description = XadlUtils.getDescription(AS.xarch, brickTypeRef);
		if(description == null) description = "[No Description]";
		
		brickTypeAssembly.getBoxedLabelThing().setText(description);
		brickTypeAssembly.getBoxGlassThing().setToolTip(description);
		
		//update the signatures
		removeOrphanedSignatures(AS, bnaWorld, brickTypeAssembly, brickTypeRef);
		ObjRef[] signatureRefs = AS.xarch.getAll(brickTypeRef, "signature");
		for(ObjRef signatureRef : signatureRefs){
			updateSignature(AS, bnaWorld, brickTypeAssembly, signatureRef);
		}
		
		FontData[] fd = PreferenceConverter.getFontDataArray(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_FONT);
		if(fd.length > 0){
			brickTypeAssembly.getBoxedLabelThing().setFontName(fd[0].getName());
			brickTypeAssembly.getBoxedLabelThing().setFontSize(fd[0].getHeight());
			brickTypeAssembly.getBoxedLabelThing().setFontStyle(FontStyle.fromSWT(fd[0].getStyle()));
		}

		updateSubarchitecture(AS, bnaWorld, brickTypeAssembly, brickTypeRef);
		
		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaWorld.getBNAModel());
		if(!ept.hasProperty("hintsApplied")){
			TypesEditorSupport.readHints(AS, AS.xarch.getXArch(brickTypeRef), bnaWorld.getBNAModel(), brickTypeRef);
			ept.setProperty("hintsApplied", true);
		}
		
		AS.eventBus.fireArchipelagoEvent(new TypesEvents.BrickTypeUpdatedEvent(bnaWorld, brickTypeRef, kind, brickTypeAssembly));
	}
	
	public synchronized static void updateSubarchitecture(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickTypeAssembly, ObjRef brickTypeRef){
		ObjRef subarchitectureRef = (ObjRef)AS.xarch.get(brickTypeRef, "subArchitecture");
		if(subarchitectureRef != null){
			ObjRef internalStructureRef = XadlUtils.resolveXLink(AS.xarch, subarchitectureRef, "archStructure");
			if(internalStructureRef != null){
				ObjRef xArchRef = AS.xarch.getXArch(brickTypeRef);
				IBNAWorld internalWorld = StructureEditorSupport.setupWorld(AS, xArchRef, internalStructureRef);
				if(internalWorld != null){
					//See if we're already visualizing this model; if so, just use that.
					//If not, create a world for it (and destroy the old world if necessary);
					StructureMapper.updateStructure(AS, internalWorld, internalStructureRef);
					brickTypeAssembly.getWorldThing().setWorld(internalWorld);
					
					removeOrphanedSignatureInterfaceMappings(AS, bnaWorld, brickTypeAssembly, brickTypeRef, internalWorld);
					ObjRef[] simRefs = AS.xarch.getAll(subarchitectureRef, "signatureInterfaceMapping");
					for(ObjRef simRef : simRefs){
						updateSignatureInterfaceMapping(AS, bnaWorld, brickTypeAssembly, brickTypeRef, internalWorld, simRef);
					}
					AS.eventBus.fireArchipelagoEvent(new TypesEvents.SubarchitectureUpdatedEvent(bnaWorld, brickTypeAssembly, brickTypeRef));
					return;
				}
			}
		}
		//It doesn't have a valid subarchitecture, clear it
		brickTypeAssembly.getWorldThing().clearWorld();
		AS.eventBus.fireArchipelagoEvent(new TypesEvents.SubarchitectureUpdatedEvent(bnaWorld, brickTypeAssembly, brickTypeRef));
	}
	
	public synchronized static void updateInterfaceType(ArchipelagoServices AS, IBNAWorld bnaWorld, ObjRef interfaceTypeRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String xArchID = XadlUtils.getID(AS.xarch, interfaceTypeRef);
		if(xArchID == null) return;
		
		EndpointAssembly interfaceTypeAssembly = null;
		IThing interfaceTypeAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((interfaceTypeAssemblyRootThing == null) || (!(interfaceTypeAssemblyRootThing instanceof IHasAssemblyData))){
			interfaceTypeAssembly = EndpointAssembly.create(bnaModel, getInterfaceTypeRootThing(bnaModel));
			interfaceTypeAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			Point p = ArchipelagoUtils.findOpenSpotForNewThing(bnaModel);
			interfaceTypeAssembly.getEndpointGlassThing().setAnchorPoint(p);
			
			interfaceTypeAssembly.getBoxThing().setGradientFilled(true);
			
			interfaceTypeAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_INTERFACE_TYPE);
			//brickTypeAssembly.getRootThing().setProperty(BRICK_TYPE_KIND_PROPERTY_NAME, TypeKind.COMPONENT_TYPE);
			interfaceTypeAssembly.getBoxThing().setColor(new RGB(255,255,255));
			interfaceTypeAssembly.getBoxBorderThing().setLineWidth(1);
			interfaceTypeAssembly.getDirectionalLabelThing().setFlow(Flow.INOUT);
			interfaceTypeAssembly.getDirectionalLabelThing().setOrientation(Orientation.NORTH);
		}
		else{
			interfaceTypeAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)interfaceTypeAssemblyRootThing);
		}
		String description = XadlUtils.getDescription(AS.xarch, interfaceTypeRef);
		if(description == null) description = "[No Description]";
		
		interfaceTypeAssembly.getEndpointGlassThing().setToolTip(description);

		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaWorld.getBNAModel());
		if(!ept.hasProperty("hintsApplied")){
			TypesEditorSupport.readHints(AS, AS.xarch.getXArch(interfaceTypeRef), bnaWorld.getBNAModel(), interfaceTypeRef);
			ept.setProperty("hintsApplied", true);
		}
		AS.eventBus.fireArchipelagoEvent(new TypesEvents.InterfaceTypeUpdatedEvent(bnaWorld, interfaceTypeRef, interfaceTypeAssembly));
	}
	
	public static void removeOrphanedSignatureInterfaceMappings(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickTypeAssembly, ObjRef brickTypeRef, IBNAWorld innerWorld){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		ObjRef subArchitectureRef = (ObjRef)AS.xarch.get(brickTypeRef, "subArchitecture");
		if(subArchitectureRef != null){
			IThing[] childThings = bnaModel.getChildThings(brickTypeAssembly.getMappingRootThing());
			
			for(IThing t : childThings){
				if((t instanceof IHasAssemblyData) && (((IHasAssemblyData)t).getAssemblyKind().equals(MappingAssembly.ASSEMBLY_KIND))){
					String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
					if(xArchID != null){
						ObjRef[] simRefs = AS.xarch.getAll(subArchitectureRef, "signatureInterfaceMapping");
	
						boolean found = false;
						for(ObjRef refToMatch : simRefs){
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
	}
	
	public static void updateSignatureInterfaceMapping(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickTypeAssembly, ObjRef brickTypeRef, IBNAWorld innerWorld, ObjRef simRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String xArchID = XadlUtils.getID(AS.xarch, simRef);
		if(xArchID == null) return;
		
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

		MappingAssembly simAssembly = null;
		IThing simAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((simAssemblyRootThing == null) || (!(simAssemblyRootThing instanceof IHasAssemblyData))){
			simAssembly = MappingAssembly.create(bnaModel, brickTypeAssembly.getMappingRootThing());
			simAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);
			
			simAssembly.getMappingThing().setColor(new RGB(0,0,0));
			simAssembly.getMappingThing().setLineWidth(2);
			
			simAssembly.getMappingGlassThing().setUserEditable(false);

			simAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_SIM);
		}
		else{
			simAssembly = MappingAssembly.attach(bnaModel, (IHasAssemblyData)simAssemblyRootThing);
		}
		String description = XadlUtils.getDescription(AS.xarch, simRef);
		if(description == null) description = "[No Description]";

		simAssembly.getMappingGlassThing().setToolTip(description);
		
		IThing signatureAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, outerSignatureID);
		if((signatureAssemblyRootThing != null) && (signatureAssemblyRootThing instanceof IHasAssemblyData)){
			EndpointAssembly signatureAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)signatureAssemblyRootThing);
			if(signatureAssembly.getEndpointGlassThing() != null){
				simAssembly.getMappingGlassThing().setExternalEndpointStuckToID(signatureAssembly.getEndpointGlassThing().getID());
			}
		}
		if(brickTypeAssembly.getWorldThing() != null){
			simAssembly.getMappingGlassThing().setWorldThingID(brickTypeAssembly.getWorldThing().getID());
			simAssembly.getMappingThing().setWorldThingID(brickTypeAssembly.getWorldThing().getID());
		}
		IBNAModel innerModel = innerWorld.getBNAModel();
		IThing interfaceAssemblyRootThing = ArchipelagoUtils.findThing(innerModel, innerInterfaceID);
		if((interfaceAssemblyRootThing != null) && (interfaceAssemblyRootThing instanceof IHasAssemblyData)){
			EndpointAssembly interfaceAssembly = EndpointAssembly.attach(innerModel, (IHasAssemblyData)interfaceAssemblyRootThing);
			if(interfaceAssembly.getEndpointGlassThing() != null){
				simAssembly.getMappingGlassThing().setInternalEndpointStuckToThingID(interfaceAssembly.getEndpointGlassThing().getID());
				simAssembly.getMappingGlassThing().setEndpoint2(interfaceAssembly.getEndpointGlassThing().getAnchorPoint());
			}
		}
		AS.eventBus.fireArchipelagoEvent(new TypesEvents.SignatureInterfaceMappingUpdatedEvent(bnaWorld, brickTypeAssembly, brickTypeRef, innerWorld, simRef, simAssembly));
	}

	public synchronized static void removeOrphanedSignatures(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickTypeAssembly, ObjRef brickTypeRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String brickTypeXArchID = XadlUtils.getID(AS.xarch, brickTypeRef);
		if(brickTypeXArchID == null) return;

		IThing[] childThings = bnaModel.getChildThings(brickTypeAssembly.getEndpointRootThing());
		
		for(IThing t : childThings){
			if((t instanceof IHasAssemblyData) && (((IHasAssemblyData)t).getAssemblyKind().equals(EndpointAssembly.ASSEMBLY_KIND))){
				String xArchID = t.getProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME);
				if(xArchID != null){
					ObjRef[] signatureRefs = AS.xarch.getAll(brickTypeRef, "signature");

					boolean found = false;
					for(ObjRef refToMatch : signatureRefs){
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
	
	public synchronized static void updateSignature(ArchipelagoServices AS, IBNAWorld bnaWorld, BoxAssembly brickTypeAssembly, ObjRef signatureRef){
		IBNAModel bnaModel = bnaWorld.getBNAModel();
		String xArchID = XadlUtils.getID(AS.xarch, signatureRef);
		if(xArchID == null) return;
		
		EndpointAssembly endpointAssembly = null;
		IThing endpointAssemblyRootThing = ArchipelagoUtils.findThing(bnaModel, xArchID);
		if((endpointAssemblyRootThing == null) || (!(endpointAssemblyRootThing instanceof IHasAssemblyData))){
			endpointAssembly = EndpointAssembly.create(bnaModel, brickTypeAssembly.getEndpointRootThing());
			endpointAssembly.getRootThing().setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, xArchID);

			endpointAssembly.getEndpointGlassThing().setAnchorPoint(new Point(
				brickTypeAssembly.getBoxGlassThing().getBoundingBox().x, 
				brickTypeAssembly.getBoxGlassThing().getBoundingBox().y));
			
			endpointAssembly.getBoxThing().setColor(new RGB(0xff, 0xff, 0xff));

			endpointAssembly.getEndpointGlassThing().setBoundingBoxRailMasterThingID(brickTypeAssembly.getBoxGlassThing().getID());

			endpointAssembly.getRootThing().setProperty(ASSEMBLY_TYPE_PROPERTY_NAME, ASSEMBLY_TYPE_SIGNATURE);
		}
		else{
			endpointAssembly = EndpointAssembly.attach(bnaModel, (IHasAssemblyData)endpointAssemblyRootThing);
		}
		String description = XadlUtils.getDescription(AS.xarch, signatureRef);
		if(description == null) description = "[No Description]";

		endpointAssembly.getEndpointGlassThing().setToolTip(description);
		endpointAssembly.getTagThing().setText(description);
		
		String direction = XadlUtils.getDirection(AS.xarch, signatureRef);
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
		AS.eventBus.fireArchipelagoEvent(new TypesEvents.SignatureUpdatedEvent(bnaWorld, brickTypeAssembly, signatureRef, endpointAssembly));
	}
	
	public static boolean isBrickTypeAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_COMPONENT_TYPE)){
					return true;
				}
				if(assemblyType.equals(ASSEMBLY_TYPE_CONNECTOR_TYPE)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isComponentTypeAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_COMPONENT_TYPE)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isConnectorTypeAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_CONNECTOR_TYPE)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isSignatureAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_SIGNATURE)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isInterfaceTypeAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_INTERFACE_TYPE)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isSignatureInterfaceMappingAssemblyRootThing(IThing t){
		if(t instanceof AssemblyRootThing){
			String assemblyType = t.getProperty(ASSEMBLY_TYPE_PROPERTY_NAME);
			if(assemblyType != null){
				if(assemblyType.equals(ASSEMBLY_TYPE_SIM)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static RGB getDefaultComponentTypeColor(ArchipelagoServices AS){
		if(AS.prefs.contains(ArchipelagoTypesConstants.PREF_DEFAULT_COMPONENT_TYPE_COLOR)){
			return PreferenceConverter.getColor(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_COMPONENT_TYPE_COLOR);
		}
		return ArchipelagoTypesConstants.DEFAULT_COMPONENT_TYPE_RGB;
	}
	
	public static RGB getDefaultConnectorTypeColor(ArchipelagoServices AS){
		if(AS.prefs.contains(ArchipelagoTypesConstants.PREF_DEFAULT_CONNECTOR_TYPE_COLOR)){
			return PreferenceConverter.getColor(AS.prefs, ArchipelagoTypesConstants.PREF_DEFAULT_CONNECTOR_TYPE_COLOR);
		}
		return ArchipelagoTypesConstants.DEFAULT_CONNECTOR_TYPE_RGB;
	}
	
	//Functions to set up the BNA model with basic root things for the hierarchy
	public static final String BNA_BRICK_TYPE_ROOT_THING_ID = "$BRICKTYPEROOT";
	public static final String BNA_INTERFACE_TYPE_ROOT_THING_ID = "$INTERFACETYPEROOT";

	protected static void initBNAModel(IBNAModel m, TypeKind tk){
		IThing normalRootThing = ArchipelagoUtils.getNormalRootThing(m);
		
		if(tk.equals(TypeKind.COMPONENT_TYPE) || (tk.equals(TypeKind.CONNECTOR_TYPE))){
			if(m.getThing(BNA_BRICK_TYPE_ROOT_THING_ID) == null){
				IThing brickTypeRootThing = new NoThing(BNA_BRICK_TYPE_ROOT_THING_ID);
				m.addThing(brickTypeRootThing);
			}
		}
		else if(tk.equals(TypeKind.INTERFACE_TYPE)){
			if(m.getThing(BNA_INTERFACE_TYPE_ROOT_THING_ID) == null){
				IThing interfaceTypeRootThing = new NoThing(BNA_INTERFACE_TYPE_ROOT_THING_ID);
				m.addThing(interfaceTypeRootThing);
			}
		}
	}
	
	protected static IThing getRootThing(IBNAModel m, String id, TypeKind tk){
		IThing rootThing = m.getThing(id);
		if(rootThing == null){
			initBNAModel(m, tk);
			return m.getThing(id);
		}
		return rootThing;
	}
	
	public static IThing getBrickTypeRootThing(IBNAModel m, TypeKind tk){
		return getRootThing(m, BNA_BRICK_TYPE_ROOT_THING_ID, tk);
	}
	
	public static IThing getInterfaceTypeRootThing(IBNAModel m){
		return getRootThing(m, BNA_INTERFACE_TYPE_ROOT_THING_ID, TypeKind.INTERFACE_TYPE);
	}
	
	public static BoxAssembly findBrickTypeAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef brickTypeRef){
		String xArchID = XadlUtils.getID(AS.xarch, brickTypeRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isBrickTypeAssemblyRootThing(t)){
				BoxAssembly assembly = BoxAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}
	
	public static EndpointAssembly findSignatureAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef signatureRef){
		String xArchID = XadlUtils.getID(AS.xarch, signatureRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isSignatureAssemblyRootThing(t)){
				EndpointAssembly assembly = EndpointAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}

	public static EndpointAssembly findInterfaceTypeAssembly(ArchipelagoServices AS, IBNAModel m, ObjRef interfaceRef){
		String xArchID = XadlUtils.getID(AS.xarch, interfaceRef);
		if(xArchID != null){
			IThing t = ArchipelagoUtils.findThing(m, xArchID);
			if((t != null) && isInterfaceTypeAssemblyRootThing(t)){
				EndpointAssembly assembly = EndpointAssembly.attach(m, (IHasAssemblyData)t);
				return assembly;
			}
		}
		return null;
	}

}
