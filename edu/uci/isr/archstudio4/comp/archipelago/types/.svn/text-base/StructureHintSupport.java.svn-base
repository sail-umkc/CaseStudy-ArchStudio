package edu.uci.isr.archstudio4.comp.archipelago.types;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.util.HintSupport;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.EndpointAssembly;
import edu.uci.isr.bna4.assemblies.StickySplineAssembly;
//import edu.uci.isr.widgets.swt.constants.FontStyle;
import edu.uci.isr.xarchflat.ObjRef;

public class StructureHintSupport{

	public static void writeHintsForStructure(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef archStructureRef){
		ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
		ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
		
		HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, archStructureRef);
		
		ObjRef[] componentRefs = AS.xarch.getAll(archStructureRef, "component");
		for(int i = 0; i < componentRefs.length; i++){
			writeHintsForBrick(AS, xArchRef, eltRef, m, componentRefs[i]);
		}
		ObjRef[] connectorRefs = AS.xarch.getAll(archStructureRef, "connector");
		for(int i = 0; i < connectorRefs.length; i++){
			writeHintsForBrick(AS, xArchRef, eltRef, m, connectorRefs[i]);
		}
		ObjRef[] linkRefs = AS.xarch.getAll(archStructureRef, "link");
		for(int i = 0; i < linkRefs.length; i++){
			writeHintsForLink(AS, xArchRef, eltRef, m, linkRefs[i]);
		}
		
		AS.xarch.add(rootRef, "hintedElement", eltRef);
	}
	
	public static void writeHintsForBrick(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef brickRef){
		BoxAssembly brickAssembly = StructureMapper.findBrickAssembly(AS, m, brickRef);
		if(brickAssembly != null){
			ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
			ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
			
			//xArchID
			HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, brickRef);
			
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassBoundingBox", brickAssembly.getBoxGlassThing().getBoundingBox());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"boxColor", brickAssembly.getBoxThing().getColor());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"labelColor", brickAssembly.getBoxedLabelThing().getColor());
			/*
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"labelFontName", brickAssembly.getBoxedLabelThing().getFontName());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"labelFontSize", brickAssembly.getBoxedLabelThing().getFontSize());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"labelFontStyle", brickAssembly.getBoxedLabelThing().getFontStyle());
			*/
			
			ObjRef[] interfaceRefs = AS.xarch.getAll(brickRef, "interface");
			for(int i = 0; i < interfaceRefs.length; i++){
				writeHintsForInterface(AS, xArchRef, eltRef, m, interfaceRefs[i]);
			}
			AS.xarch.add(rootRef, "hintedElement", eltRef);
		}
	}
	
	public static void writeHintsForInterface(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef interfaceRef){
		EndpointAssembly endpointAssembly = StructureMapper.findInterfaceAssembly(AS, m, interfaceRef);
		if(endpointAssembly != null){
			ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
			ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
			
			//xArchID
			HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, interfaceRef);
			
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassAnchorPoint", endpointAssembly.getEndpointGlassThing().getAnchorPoint());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef,
				"tagAnchorPoint", endpointAssembly.getTagThing().getAnchorPoint());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef,
				"tagVisible", endpointAssembly.getTagThing().isVisible());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef,
				"tagAngle", endpointAssembly.getTagThing().getAngle());
			
			AS.xarch.add(rootRef, "hintedElement", eltRef);
		}
	}
	
	public static void writeHintsForLink(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef linkRef){
		StickySplineAssembly linkAssembly = StructureMapper.findLinkAssembly(AS, m, linkRef);
		if(linkAssembly != null){
			ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
			ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
			
			//xArchID
			HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, linkRef);
			
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassEndpoint1", linkAssembly.getSplineGlassThing().getEndpoint1());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassEndpoint2", linkAssembly.getSplineGlassThing().getEndpoint2());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassMidpoints", linkAssembly.getSplineGlassThing().getMidpoints());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"splineColor", linkAssembly.getSplineThing().getColor());
			AS.xarch.add(rootRef, "hintedElement", eltRef);
		}
	}
	
	/* --------------------------------- */

	public static void readHintsForStructure(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef archStructureRef){
		ObjRef[] componentRefs = AS.xarch.getAll(archStructureRef, "component");
		for(int i = 0; i < componentRefs.length; i++){
			readHintsForBrick(AS, xArchRef, bundleRef, m, componentRefs[i]);
		}
		ObjRef[] connectorRefs = AS.xarch.getAll(archStructureRef, "connector");
		for(int i = 0; i < connectorRefs.length; i++){
			readHintsForBrick(AS, xArchRef, bundleRef, m, connectorRefs[i]);
		}
		ObjRef[] linkRefs = AS.xarch.getAll(archStructureRef, "link");
		for(int i = 0; i < linkRefs.length; i++){
			readHintsForLink(AS, xArchRef, bundleRef, m, linkRefs[i]);
		}
	}
	
	public static void readHintsForBrick(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef brickRef){
		BoxAssembly brickAssembly = StructureMapper.findBrickAssembly(AS, m, brickRef);
		ObjRef eltRef = HintSupport.findHintedElementRef(AS, xArchRef, bundleRef, brickRef);
		
		if((brickAssembly != null) && (eltRef != null)){

			Rectangle glassBoundingBox = (Rectangle)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassBoundingBox");
			if(glassBoundingBox != null){
				brickAssembly.getBoxGlassThing().setBoundingBox(glassBoundingBox);
			}

			RGB boxColor = (RGB)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "boxColor");
			if(boxColor != null){
				brickAssembly.getBoxThing().setColor(boxColor);
			}
			
			RGB labelColor = (RGB)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "labelColor");
			if(labelColor != null){
				brickAssembly.getBoxedLabelThing().setColor(labelColor);
			}
			/*
			String labelFontName = (String)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "labelFontName");
			if(labelFontName != null){
				brickAssembly.getBoxedLabelThing().setFontName(labelFontName);
			}

			Integer labelFontSize = (Integer)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "labelFontSize");
			if((labelFontSize != null) && (labelFontSize instanceof Integer)){
				brickAssembly.getBoxedLabelThing().setFontSize(((Integer)labelFontSize).intValue());
			}
			
			FontStyle labelFontStyle = (FontStyle)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "labelFontStyle");
			if(labelFontStyle != null){
				brickAssembly.getBoxedLabelThing().setFontStyle(labelFontStyle);
			}
			*/
			
			ObjRef[] interfaceRefs = AS.xarch.getAll(brickRef, "interface");
			for(int i = 0; i < interfaceRefs.length; i++){
				readHintsForInterface(AS, xArchRef, bundleRef, m, interfaceRefs[i]);
			}
		}
	}
	
	public static void readHintsForInterface(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef interfaceRef){
		EndpointAssembly endpointAssembly = StructureMapper.findInterfaceAssembly(AS, m, interfaceRef);
		ObjRef eltRef = HintSupport.findHintedElementRef(AS, xArchRef, bundleRef, interfaceRef);
		
		if((endpointAssembly != null) && (eltRef != null)){

			Point glassAnchorPoint = (Point)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassAnchorPoint");
			if(glassAnchorPoint != null){
				endpointAssembly.getEndpointGlassThing().setAnchorPoint(glassAnchorPoint);
			}
			
			Point tagAnchorPoint = (Point)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "tagAnchorPoint");
			if(tagAnchorPoint != null){
				endpointAssembly.getTagThing().setAnchorPoint(tagAnchorPoint);
			}
			
			Boolean tagVisible = (Boolean)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "tagVisible");
			if(tagVisible != null){
				endpointAssembly.getTagThing().setVisible(tagVisible);
			}
			
			Integer tagAngle = (Integer)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "tagAngle");
			if(tagAngle != null){
				endpointAssembly.getTagThing().setAngle(tagAngle);
			}
		}
	}
	
	public static void readHintsForLink(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef linkRef){
		StickySplineAssembly linkAssembly = StructureMapper.findLinkAssembly(AS, m, linkRef);
		ObjRef eltRef = HintSupport.findHintedElementRef(AS, xArchRef, bundleRef, linkRef);
		
		if((linkAssembly != null) && (eltRef != null)){
			
			Point glassEndpoint1 = (Point)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassEndpoint1");
			if(glassEndpoint1 != null){
				linkAssembly.getSplineGlassThing().setEndpoint1(glassEndpoint1);
			}
			
			Point glassEndpoint2 = (Point)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassEndpoint2");
			if(glassEndpoint2 != null){
				linkAssembly.getSplineGlassThing().setEndpoint2(glassEndpoint2);
			}
			
			Point[] glassMidpoints = (Point[])HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassMidpoints");
			if(glassMidpoints != null){
				linkAssembly.getSplineGlassThing().setMidpoints(glassMidpoints);
			}
			
			RGB splineColor = (RGB)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "splineColor");
			if(splineColor != null){
				linkAssembly.getSplineThing().setColor(splineColor);
			}
		}
	}
	
	
}
