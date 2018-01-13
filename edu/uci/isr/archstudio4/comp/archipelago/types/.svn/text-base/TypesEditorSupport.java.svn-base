package edu.uci.isr.archstudio4.comp.archipelago.types;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoConstants;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.util.ArchipelagoFinder;
import edu.uci.isr.archstudio4.comp.archipelago.util.FileDirtyLogic;
import edu.uci.isr.archstudio4.comp.archipelago.util.HintSupport;
import edu.uci.isr.bna4.BNAComposite;
import edu.uci.isr.bna4.BNARenderingSettings;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.DefaultBNAModel;
import edu.uci.isr.bna4.DefaultBNAView;
import edu.uci.isr.bna4.DefaultBNAWorld;
import edu.uci.isr.bna4.DefaultCoordinateMapper;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.IMutableCoordinateMapper;
import edu.uci.isr.bna4.IThingLogicManager;
import edu.uci.isr.bna4.facets.IHasMutableLife;
import edu.uci.isr.bna4.facets.IHasMutableRotatingOffset;
import edu.uci.isr.bna4.facets.IHasMutableSelected;
import edu.uci.isr.bna4.facets.IHasStickyBox;
import edu.uci.isr.bna4.facets.IHasStickyEndpoints;
import edu.uci.isr.bna4.facets.IHasWorld;
import edu.uci.isr.bna4.logics.background.LifeSapperLogic;
import edu.uci.isr.bna4.logics.background.RotatingOffsetLogic;
import edu.uci.isr.bna4.logics.coordinating.ArrowheadLogic;
import edu.uci.isr.bna4.logics.coordinating.AssemblyLogic;
import edu.uci.isr.bna4.logics.coordinating.BoundingBoxRailLogic;
import edu.uci.isr.bna4.logics.coordinating.EndpointFlowOrientationLogic;
import edu.uci.isr.bna4.logics.coordinating.MaintainIndicatorLogic;
import edu.uci.isr.bna4.logics.coordinating.MaintainMappingEndpointsLogic;
import edu.uci.isr.bna4.logics.coordinating.MaintainStickyEndpointsLogic;
import edu.uci.isr.bna4.logics.coordinating.MirrorAnchorPointLogic;
import edu.uci.isr.bna4.logics.coordinating.MirrorBoundingBoxLogic;
import edu.uci.isr.bna4.logics.coordinating.MirrorEndpointLogic;
import edu.uci.isr.bna4.logics.coordinating.MirrorEndpointsLogic;
import edu.uci.isr.bna4.logics.coordinating.MirrorMidpointsLogic;
import edu.uci.isr.bna4.logics.coordinating.MirrorPathDataLogic;
import edu.uci.isr.bna4.logics.coordinating.MoveWithLogic;
import edu.uci.isr.bna4.logics.coordinating.WorldThingExternalEventsLogic;
import edu.uci.isr.bna4.logics.coordinating.WorldThingInternalEventsLogic;
import edu.uci.isr.bna4.logics.editing.AlignAndDistributeLogic;
import edu.uci.isr.bna4.logics.editing.BoxReshapeHandleLogic;
import edu.uci.isr.bna4.logics.editing.ClickSelectionLogic;
import edu.uci.isr.bna4.logics.editing.DragMovableLogic;
import edu.uci.isr.bna4.logics.editing.DragMovableSelectionLogic;
import edu.uci.isr.bna4.logics.editing.KeyNudgerLogic;
import edu.uci.isr.bna4.logics.editing.MarqueeSelectionLogic;
import edu.uci.isr.bna4.logics.editing.RectifyToGridLogic;
import edu.uci.isr.bna4.logics.editing.RotateTagsLogic;
import edu.uci.isr.bna4.logics.editing.RotaterLogic;
import edu.uci.isr.bna4.logics.editing.ShowHideTagsLogic;
import edu.uci.isr.bna4.logics.editing.SnapToGridLogic;
import edu.uci.isr.bna4.logics.editing.SplineBreakLogic;
import edu.uci.isr.bna4.logics.editing.SplineReshapeHandleLogic;
import edu.uci.isr.bna4.logics.editing.StandardCursorLogic;
import edu.uci.isr.bna4.logics.editing.StickySplineEndpointsColorLogic;
import edu.uci.isr.bna4.logics.editing.StickySplineEndpointsLogic;
import edu.uci.isr.bna4.logics.information.ToolTipLogic;
import edu.uci.isr.bna4.logics.navigating.FindDialogLogic;
import edu.uci.isr.bna4.logics.navigating.MousePanningLogic;
import edu.uci.isr.bna4.logics.navigating.MouseWheelZoomingLogic;
import edu.uci.isr.bna4.logics.tracking.AnchorPointTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.BoundingBoxTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.EndpointsTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.MidpointsTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.ModelBoundsTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.MouseLocationTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.SelectionTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.StickyBoxTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.ThingRefTrackingLogic;
import edu.uci.isr.bna4.logics.tracking.TypedThingSetTrackingLogic;
import edu.uci.isr.bna4.logics.util.ExportBitmapLogic;
import edu.uci.isr.bna4.things.glass.MappingGlassThing;
import edu.uci.isr.bna4.things.utility.EnvironmentPropertiesThing;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xarchflat.ObjRef;

public class TypesEditorSupport{
	//For tree node cache
	public static final String BNA_WORLD_KEY = "bnaWorld";
	
	//For editor pane properties
	public static final String EDITING_BNA_COMPOSITE_KEY = "bnaComposite";
	
	public static void setupEditor(ArchipelagoServices AS, ObjRef typeRef){
		ObjRef xArchRef = AS.xarch.getXArch(typeRef);

		String typeID = XadlUtils.getID(AS.xarch, typeRef);
		if(typeID == null) return;

		IBNAWorld bnaWorld = setupWorld(AS, xArchRef, typeRef);
		
		IBNAView bnaView = new DefaultBNAView(null, bnaWorld, new DefaultCoordinateMapper());

		AS.editor.clearEditor();
		Composite parentComposite = AS.editor.getParentComposite();
		FillLayout fl = new FillLayout();
		fl.type = SWT.HORIZONTAL;
		parentComposite.setLayout(fl);
		
		final BNAComposite bnaComposite = new BNAComposite(parentComposite, SWT.V_SCROLL
			| SWT.H_SCROLL | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED, bnaView);
		bnaComposite.setBackground(parentComposite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		bnaComposite.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e){
				EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaComposite.getView().getWorld().getBNAModel());
				BNAUtils.saveCoordinateMapperData(bnaComposite.getView().getCoordinateMapper(), ept);
				bnaComposite.removeDisposeListener(this);
			}
		});
		
		BNARenderingSettings.setAntialiasGraphics(bnaComposite, AS.prefs.getBoolean(ArchipelagoConstants.PREF_ANTIALIAS_GRAPHICS));
		BNARenderingSettings.setAntialiasText(bnaComposite, AS.prefs.getBoolean(ArchipelagoConstants.PREF_ANTIALIAS_TEXT));
		BNARenderingSettings.setDecorativeGraphics(bnaComposite, AS.prefs.getBoolean(ArchipelagoConstants.PREF_DECORATIVE_GRAPHICS));

		ArchipelagoUtils.addZoomWidget(bnaComposite, bnaView);

		bnaComposite.pack();
		parentComposite.layout(true);
		
		EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaComposite.getView().getWorld().getBNAModel());
		BNAUtils.restoreCoordinateMapperData((IMutableCoordinateMapper)bnaComposite.getView().getCoordinateMapper(), ept);

		ArchipelagoUtils.setBNAComposite(AS.editor, bnaComposite);
		bnaComposite.setFocus();
	}

	public static IBNAWorld setupWorld(ArchipelagoServices AS, ObjRef xArchRef, ObjRef typeRef){
		IBNAWorld bnaWorld = (IBNAWorld)AS.treeNodeDataCache.getData(xArchRef, typeRef, BNA_WORLD_KEY);
		IBNAModel bnaModel = null;

		String typeID = XadlUtils.getID(AS.xarch, typeRef);
		if(typeID == null) return null;

		if(bnaWorld != null){
			bnaModel = bnaWorld.getBNAModel();
		}
		else{
			bnaModel = new DefaultBNAModel();
			EnvironmentPropertiesThing ept = BNAUtils.getEnvironmentPropertiesThing(bnaModel);
			ept.setProperty(ArchipelagoUtils.XARCH_ID_PROPERTY_NAME, typeID);
			
			bnaWorld = new DefaultBNAWorld("archTypes", bnaModel);
			AS.treeNodeDataCache.setData(xArchRef, typeRef, BNA_WORLD_KEY, bnaWorld);

			setupWorld(AS, AS.xarch.getXArch(typeRef), bnaWorld);
			AS.eventBus.fireArchipelagoEvent(new TypesEvents.WorldCreatedEvent(typeRef, bnaWorld));
		}
		
		ArchipelagoUtils.applyGridPreferences(AS, bnaModel);

		TypesMapper.updateType(AS, bnaWorld, typeRef);
		return bnaWorld;
	}
	
	static void setupWorld(ArchipelagoServices AS, ObjRef xArchRef, IBNAWorld bnaWorld){
		IThingLogicManager logicManager = bnaWorld.getThingLogicManager();

		logicManager.addThingLogic(new MouseWheelZoomingLogic());
		logicManager.addThingLogic(new MousePanningLogic());

		MouseLocationTrackingLogic mltl = new MouseLocationTrackingLogic();
		logicManager.addThingLogic(mltl);
		ThingRefTrackingLogic trtl = new ThingRefTrackingLogic();
		logicManager.addThingLogic(trtl);
		BoundingBoxTrackingLogic bbtl = new BoundingBoxTrackingLogic();
		logicManager.addThingLogic(bbtl);
		AnchorPointTrackingLogic aptl = new AnchorPointTrackingLogic();
		logicManager.addThingLogic(aptl);
		SelectionTrackingLogic stl = new SelectionTrackingLogic();
		logicManager.addThingLogic(stl);
		EndpointsTrackingLogic epstl = new EndpointsTrackingLogic();
		logicManager.addThingLogic(epstl);
		MidpointsTrackingLogic mpstl = new MidpointsTrackingLogic();
		logicManager.addThingLogic(mpstl);
		StickyBoxTrackingLogic sbtl = new StickyBoxTrackingLogic();
		logicManager.addThingLogic(sbtl);

		TypedThingSetTrackingLogic<IHasMutableRotatingOffset> ttstlOffset = new TypedThingSetTrackingLogic<IHasMutableRotatingOffset>(
			IHasMutableRotatingOffset.class);
		logicManager.addThingLogic(ttstlOffset);

		TypedThingSetTrackingLogic<IHasMutableLife> ttstlLife = new TypedThingSetTrackingLogic<IHasMutableLife>(
			IHasMutableLife.class);
		logicManager.addThingLogic(ttstlLife);
		
		TypedThingSetTrackingLogic<IHasMutableSelected> ttstlSelected = new TypedThingSetTrackingLogic<IHasMutableSelected>(
			IHasMutableSelected.class);
		logicManager.addThingLogic(ttstlSelected);

		TypedThingSetTrackingLogic<IHasStickyBox> ttstlSticky = new TypedThingSetTrackingLogic<IHasStickyBox>(
			IHasStickyBox.class);
		logicManager.addThingLogic(ttstlSticky);
		
		TypedThingSetTrackingLogic<IHasStickyEndpoints> ttstlStickyEndpoints = new TypedThingSetTrackingLogic<IHasStickyEndpoints>(
			IHasStickyEndpoints.class);
		logicManager.addThingLogic(ttstlStickyEndpoints);
		
		TypedThingSetTrackingLogic<IHasWorld> ttstlView = new TypedThingSetTrackingLogic<IHasWorld>(
			IHasWorld.class);
		logicManager.addThingLogic(ttstlView);
		
		logicManager.addThingLogic(new AssemblyLogic());
		logicManager.addThingLogic(new MoveWithLogic(trtl, bbtl, aptl));
		logicManager.addThingLogic(new MirrorAnchorPointLogic(trtl, aptl));
		logicManager.addThingLogic(new MirrorBoundingBoxLogic(trtl, bbtl));
		logicManager.addThingLogic(new MirrorEndpointsLogic(trtl, epstl));
		logicManager.addThingLogic(new MirrorMidpointsLogic(trtl, mpstl));
		logicManager.addThingLogic(new MirrorEndpointLogic(trtl, epstl));
		logicManager.addThingLogic(new MaintainIndicatorLogic(trtl, bbtl, aptl));
		logicManager.addThingLogic(new ArrowheadLogic(trtl, epstl, mpstl));
		logicManager.addThingLogic(new MirrorPathDataLogic(trtl));
		DragMovableLogic dml = new DragMovableLogic();
		logicManager.addThingLogic(dml);
		logicManager.addThingLogic(new SnapToGridLogic(dml, stl));
		logicManager.addThingLogic(new KeyNudgerLogic());
		logicManager.addThingLogic(new RotatingOffsetLogic(ttstlOffset));
		logicManager.addThingLogic(new LifeSapperLogic(ttstlLife));
		logicManager.addThingLogic(new ClickSelectionLogic(ttstlSelected));
		logicManager.addThingLogic(new MarqueeSelectionLogic(ttstlSelected));
		logicManager.addThingLogic(new DragMovableSelectionLogic(dml, stl));
		logicManager.addThingLogic(new BoxReshapeHandleLogic(stl, bbtl, dml));
		SplineReshapeHandleLogic srhl = new SplineReshapeHandleLogic(stl, epstl, mpstl, dml);
		logicManager.addThingLogic(srhl);
		logicManager.addThingLogic(new StickySplineEndpointsLogic(ttstlSticky, srhl));
		logicManager.addThingLogic(new StickySplineEndpointsColorLogic(stl, srhl));
		logicManager.addThingLogic(new MaintainStickyEndpointsLogic(trtl, ttstlStickyEndpoints, sbtl));
		logicManager.addThingLogic(new SplineBreakLogic());
		logicManager.addThingLogic(new BoundingBoxRailLogic(trtl, bbtl, aptl));
		logicManager.addThingLogic(new EndpointFlowOrientationLogic(aptl));
		logicManager.addThingLogic(new StandardCursorLogic());
		logicManager.addThingLogic(new ToolTipLogic());
		logicManager.addThingLogic(new RotaterLogic());

		ModelBoundsTrackingLogic mbtl = new ModelBoundsTrackingLogic(bbtl, aptl);
		logicManager.addThingLogic(mbtl);
		
		WorldThingInternalEventsLogic vtiel = new WorldThingInternalEventsLogic(ttstlView);
		logicManager.addThingLogic(vtiel);
		logicManager.addThingLogic(new WorldThingExternalEventsLogic(ttstlView));
		//logicManager.addThingLogic(new WorldThingDestroyLogic(true));

		//(signature-interface) mapping handling logics
		TypedThingSetTrackingLogic<MappingGlassThing> ttstlMapping = new TypedThingSetTrackingLogic<MappingGlassThing>(
			MappingGlassThing.class);
		logicManager.addThingLogic(ttstlMapping);
		
		logicManager.addThingLogic(new MaintainMappingEndpointsLogic(trtl, ttstlMapping, vtiel));
		
		//XArchEvent handler logics
		logicManager.addThingLogic(new TypesXArchEventHandlerLogic(AS, xArchRef, ttstlView));

		logicManager.addThingLogic(new FileDirtyLogic(AS, xArchRef));

		//Drag and drop logics
		logicManager.addThingLogic(new TypesStructureDropLogic(AS, xArchRef));
		logicManager.addThingLogic(new TypesTypeDropLogic(AS, xArchRef));

		//Menu logics
		logicManager.addThingLogic(new FindDialogLogic(new ArchipelagoFinder(AS)));
		logicManager.addThingLogic(new AlignAndDistributeLogic());
		logicManager.addThingLogic(new RectifyToGridLogic());
		logicManager.addThingLogic(new TypesEditDescriptionLogic(AS, xArchRef));
		logicManager.addThingLogic(new TypesEditDirectionLogic(AS, xArchRef));
		logicManager.addThingLogic(new TypesNewSignatureLogic(AS, xArchRef));
		logicManager.addThingLogic(new ShowHideTagsLogic());
		logicManager.addThingLogic(new RotateTagsLogic());
		logicManager.addThingLogic(new TypesAssignStructureLogic(AS, xArchRef));
		logicManager.addThingLogic(new TypesCreateSIMLogic(AS, xArchRef));
		logicManager.addThingLogic(new TypesRemoveDeadSIMLogic(AS, xArchRef));
		logicManager.addThingLogic(new TypesEditColorLogic(AS));
		logicManager.addThingLogic(new TypesRemoveLogic(AS, xArchRef));
		logicManager.addThingLogic(new ExportBitmapLogic(mbtl));
	}
	
	public static void readHints(ArchipelagoServices AS, ObjRef xArchRef, IBNAModel modelToPopulate, ObjRef typeRef){
		ObjRef bundleRef = HintSupport.getArchipelagoHintsBundleRef(AS, xArchRef);
		if(bundleRef != null){
			ObjRef typeBundleRef = HintSupport.findChildHintedElementRef(AS, xArchRef, bundleRef, typeRef);
			if(typeBundleRef != null){
				if(AS.xarch.isInstanceOf(typeRef, "types#ComponentType") || AS.xarch.isInstanceOf(typeRef, "types#ConnectorType")){
					TypesHintSupport.readHintsForBrickType(AS, xArchRef, typeBundleRef, modelToPopulate, typeRef);
				}
				else if(AS.xarch.isInstanceOf(typeRef, "types#InterfaceType")){
					TypesHintSupport.readHintsForInterfaceType(AS, xArchRef, typeBundleRef, modelToPopulate, typeRef);
				}
			}
		}
	}
	
	public static void writeHints(ArchipelagoServices AS, ObjRef xArchRef, IProgressMonitor monitor){
		ObjRef typesContextRef = AS.xarch.createContext(xArchRef, "types");
		
		ObjRef archTypesRef = XadlUtils.getArchTypes(AS.xarch, xArchRef);
		if(archTypesRef != null){
			ObjRef[] componentTypeRefs = AS.xarch.getAll(archTypesRef, "componentType");
			ObjRef[] connectorTypeRefs = AS.xarch.getAll(archTypesRef, "connectorType");
			ObjRef[] interfaceTypeRefs = AS.xarch.getAll(archTypesRef, "interfaceType");
			
			ObjRef[] allTypeRefs = new ObjRef[componentTypeRefs.length + connectorTypeRefs.length + interfaceTypeRefs.length];
			System.arraycopy(componentTypeRefs, 0, allTypeRefs, 0, componentTypeRefs.length);
			System.arraycopy(connectorTypeRefs, 0, allTypeRefs, componentTypeRefs.length, connectorTypeRefs.length);
			System.arraycopy(interfaceTypeRefs, 0, allTypeRefs, componentTypeRefs.length + connectorTypeRefs.length, interfaceTypeRefs.length);
			
			if(allTypeRefs.length > 0){
				ObjRef hintsContextRef = AS.xarch.createContext(xArchRef, "hints3");
				ObjRef bundleRef = HintSupport.getArchipelagoHintsBundleRef(AS, xArchRef);
				AS.xarch.set(bundleRef, "type", "XML");
				for(int i = 0; i < allTypeRefs.length; i++){
					String typeDescription = XadlUtils.getDescription(AS.xarch, allTypeRefs[i]);
					if(typeDescription == null) typeDescription = "[No Description]";
					monitor.setTaskName("Storing Hints for " + typeDescription);
					
					//Find the model for this structure, if there isn't one we do nothing.
					//If there were already hints in the model, they'll be left alone
					//since they were never loaded out.  If there are new hints,
					//they will overwrite the ones in the xADL model
					IBNAWorld world = (IBNAWorld)AS.treeNodeDataCache.getData(xArchRef, allTypeRefs[i], BNA_WORLD_KEY);
					if(world != null){
						IBNAModel model = world.getBNAModel();
						if(model != null){
							//We have new hints for this structure
							//Remove the old set of hints for this structure, if they exist
							ObjRef hintedElementRef = HintSupport.findChildHintedElementRef(AS, xArchRef, bundleRef, allTypeRefs[i]);
							if(hintedElementRef != null){
								AS.xarch.remove(bundleRef, "hintedElement", hintedElementRef);
							}
							if(i < (componentTypeRefs.length + connectorTypeRefs.length)){
								TypesHintSupport.writeHintsForBrickType(AS, xArchRef, bundleRef, model, allTypeRefs[i]);
							}
							else{
								TypesHintSupport.writeHintsForInterfaceType(AS, xArchRef, bundleRef, model, allTypeRefs[i]);
							}
						}
					}
				}
				//AS.xarch.cleanup(xArchRef);
			}
		}
	}

}
