package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.util.HintSupport;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.DistinguishedStateAssembly;
import edu.uci.isr.bna4.assemblies.IAssembly;
import edu.uci.isr.bna4.assemblies.StickySplineAssembly;
import edu.uci.isr.xarchflat.ObjRef;

public class StatechartsHintSupport{

	public static void writeHintsForStatechart(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef statechartRef){
		ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
		ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
		
		HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, statechartRef);
		
		ObjRef[] stateRefs = AS.xarch.getAll(statechartRef, "state");
		for(int i = 0; i < stateRefs.length; i++){
			writeHintsForState(AS, xArchRef, eltRef, m, stateRefs[i]);
		}
		ObjRef[] transitionRefs = AS.xarch.getAll(statechartRef, "transition");
		for(int i = 0; i < transitionRefs.length; i++){
			writeHintsForTransition(AS, xArchRef, eltRef, m, transitionRefs[i]);
		}
		
		AS.xarch.add(rootRef, "hintedElement", eltRef);
	}
	
	public static void writeHintsForState(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef stateRef){
		IAssembly assembly = StatechartsMapper.findStateAssembly(AS, m, stateRef);
		
		if((assembly != null) && (assembly instanceof BoxAssembly)){
			BoxAssembly stateAssembly = (BoxAssembly)assembly;
			ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
			ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
			
			//xArchID
			HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, stateRef);
			
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassBoundingBox", stateAssembly.getBoxGlassThing().getBoundingBox());
			
			AS.xarch.add(rootRef, "hintedElement", eltRef);
		}
		if((assembly != null) && (assembly instanceof DistinguishedStateAssembly)){
			DistinguishedStateAssembly stateAssembly = (DistinguishedStateAssembly)assembly;
			
			ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
			ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
			
			//xArchID
			HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, stateRef);
			
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassAnchorPoint", stateAssembly.getDistinguishedStateGlassThing().getAnchorPoint());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassRadius", stateAssembly.getDistinguishedStateGlassThing().getRadius());
			
			AS.xarch.add(rootRef, "hintedElement", eltRef);
		}
	}
	
	public static void writeHintsForTransition(ArchipelagoServices AS, ObjRef xArchRef, ObjRef rootRef, IBNAModel m, ObjRef transitionRef){
		StickySplineAssembly transitionAssembly = StatechartsMapper.findTransitionAssembly(AS, m, transitionRef);
		if(transitionAssembly != null){
			ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
			ObjRef eltRef = AS.xarch.create(hintsContextRef, "hintedElement");
			
			//xArchID
			HintSupport.getInstance().writeTarget(AS, xArchRef, eltRef, transitionRef);
			
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassEndpoint1", transitionAssembly.getSplineGlassThing().getEndpoint1());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassEndpoint2", transitionAssembly.getSplineGlassThing().getEndpoint2());
			HintSupport.getInstance().writeProperty(AS, xArchRef, eltRef, 
				"glassMidpoints", transitionAssembly.getSplineGlassThing().getMidpoints());
			AS.xarch.add(rootRef, "hintedElement", eltRef);
		}
	}
	
	/* --------------------------------- */

	public static void readHintsForStatechart(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef statechartRef){
		ObjRef[] stateRefs = AS.xarch.getAll(statechartRef, "state");
		for(int i = 0; i < stateRefs.length; i++){
			readHintsForState(AS, xArchRef, bundleRef, m, stateRefs[i]);
		}
		ObjRef[] transitionRefs = AS.xarch.getAll(statechartRef, "transition");
		for(int i = 0; i < transitionRefs.length; i++){
			readHintsForTransition(AS, xArchRef, bundleRef, m, transitionRefs[i]);
		}
	}
	
	public static void readHintsForState(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef stateRef){
		IAssembly assembly = StatechartsMapper.findStateAssembly(AS, m, stateRef);
		ObjRef eltRef = HintSupport.findHintedElementRef(AS, xArchRef, bundleRef, stateRef);
		
		if((assembly != null) && (eltRef != null)){
			if(assembly instanceof BoxAssembly){
				BoxAssembly stateAssembly = (BoxAssembly)assembly;

				Rectangle glassBoundingBox = (Rectangle)HintSupport.getInstance().readProperty(AS, 
					xArchRef, eltRef, "glassBoundingBox");
				if(glassBoundingBox != null){
					stateAssembly.getBoxGlassThing().setBoundingBox(glassBoundingBox);
				}
			}
			else if(assembly instanceof DistinguishedStateAssembly){
				DistinguishedStateAssembly stateAssembly = (DistinguishedStateAssembly)assembly;

				Point glassAnchorPoint = (Point)HintSupport.getInstance().readProperty(AS, 
					xArchRef, eltRef, "glassAnchorPoint");
				if(glassAnchorPoint != null){
					stateAssembly.getDistinguishedStateGlassThing().setAnchorPoint(glassAnchorPoint);
				}
				
				Integer glassRadius = (Integer)HintSupport.getInstance().readProperty(AS, 
					xArchRef, eltRef, "glassRadius");
				if(glassRadius != null){
					stateAssembly.getDistinguishedStateGlassThing().setRadius(glassRadius.intValue());
					stateAssembly.getDistinguishedStateThing().setRadius(glassRadius.intValue());
				}
			}
		}
	}
	
	public static void readHintsForTransition(ArchipelagoServices AS, ObjRef xArchRef, ObjRef bundleRef, IBNAModel m, ObjRef transitionRef){
		StickySplineAssembly transitionAssembly = StatechartsMapper.findTransitionAssembly(AS, m, transitionRef);
		ObjRef eltRef = HintSupport.findHintedElementRef(AS, xArchRef, bundleRef, transitionRef);
		
		if((transitionAssembly != null) && (eltRef != null)){
			
			Point glassEndpoint1 = (Point)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassEndpoint1");
			if(glassEndpoint1 != null){
				transitionAssembly.getSplineGlassThing().setEndpoint1(glassEndpoint1);
			}
			
			Point glassEndpoint2 = (Point)HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassEndpoint2");
			if(glassEndpoint2 != null){
				transitionAssembly.getSplineGlassThing().setEndpoint2(glassEndpoint2);
			}
			
			Point[] glassMidpoints = (Point[])HintSupport.getInstance().readProperty(
				AS, xArchRef, eltRef, "glassMidpoints");
			if(glassMidpoints != null){
				transitionAssembly.getSplineGlassThing().setMidpoints(glassMidpoints);
			}
		}
	}
	
	
}
