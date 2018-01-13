package edu.uci.isr.archstudio4.comp.archipelago.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoUtils;
import edu.uci.isr.archstudio4.comp.archipelago.types.StructureMapper;
import edu.uci.isr.archstudio4.comp.archipelago.types.TypesMapper;
import edu.uci.isr.archstudio4.util.ArchstudioResources;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.FlyToUtils;
import edu.uci.isr.bna4.IBNAModel;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.EndpointAssembly;
import edu.uci.isr.bna4.assemblies.StickySplineAssembly;
import edu.uci.isr.bna4.facets.IHasAssemblyData;
import edu.uci.isr.bna4.things.utility.WorldThing;
import edu.uci.isr.bna4.things.utility.WorldThingPeer;
import edu.uci.isr.widgets.swt.DefaultFindResult;
import edu.uci.isr.widgets.swt.IFindResult;
import edu.uci.isr.widgets.swt.IFinder;

public class ArchipelagoFinder implements IFinder<IBNAView>{

	protected ArchipelagoServices AS = null;
	
	public ArchipelagoFinder(ArchipelagoServices AS){
		this.AS = AS;
	}
	
	@SuppressWarnings("unchecked")
	public IFindResult[] find(IBNAView context, String search){
		List<IFindResult> resultList = new ArrayList<IFindResult>();
		find(context, context.getWorld().getBNAModel(), search, "", resultList);
		Collections.sort(resultList);
		return resultList.toArray(new IFindResult[resultList.size()]);
	}
	
	public void selected(IFindResult selectedResult){
		Object o = selectedResult.getData();
		if((o != null) && (o instanceof FindResultData)){
			final FindResultData frd = (FindResultData)o;
			FlyToUtils.flyTo(frd.view, frd.p.x, frd.p.y);
			
			IThing t = frd.t;
			ArchipelagoUtils.pulseNotify(frd.view.getWorld().getBNAModel(), t);
		}
	}
	
	protected void find(IBNAView context, IBNAModel m, String search, String prefix, List<IFindResult> resultList){
		IThing[] things = m.getAllThings();
		for(IThing t : things){
			IFindResult r = null;
			if(StructureMapper.isComponentAssemblyRootThing(t)){
				BoxAssembly a = BoxAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getBoxedLabelThing() != null){
					if(matches(search, a.getBoxedLabelThing().getText())){
						r = createFindResult(context, a.getBoxedLabelThing(), prefix, a.getBoxedLabelThing().getText(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_COMPONENT));
					}
				}
				find(context, a, search, resultList);
			}
			else if(StructureMapper.isConnectorAssemblyRootThing(t)){
				BoxAssembly a = BoxAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getBoxedLabelThing() != null){
					if(matches(search, a.getBoxedLabelThing().getText())){
						r = createFindResult(context, a.getBoxedLabelThing(), prefix, a.getBoxedLabelThing().getText(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_CONNECTOR));
					}
				}
				find(context, a, search, resultList);
			}
			else if(StructureMapper.isInterfaceAssemblyRootThing(t)){
				EndpointAssembly a = EndpointAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getEndpointGlassThing() != null){
					if(matches(search, a.getEndpointGlassThing().getToolTip())){
						r = createFindResult(context, a.getEndpointGlassThing(), prefix, a.getEndpointGlassThing().getToolTip(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_INTERFACE));
					}
				}
			}
			else if(StructureMapper.isLinkAssemblyRootThing(t)){
				StickySplineAssembly a = StickySplineAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getSplineGlassThing() != null){
					if(matches(search, a.getSplineGlassThing().getToolTip())){
						r = createFindResult(context, a.getSplineGlassThing(), prefix, a.getSplineGlassThing().getToolTip(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_LINK));
					}
				}
			}
			else if(TypesMapper.isComponentTypeAssemblyRootThing(t)){
				BoxAssembly a = BoxAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getBoxedLabelThing() != null){
					if(matches(search, a.getBoxedLabelThing().getText())){
						r = createFindResult(context, a.getBoxedLabelThing(), prefix, a.getBoxedLabelThing().getText(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_COMPONENT_TYPE));
					}
				}
				find(context, a, search, resultList);
			}
			else if(TypesMapper.isConnectorTypeAssemblyRootThing(t)){
				BoxAssembly a = BoxAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getBoxedLabelThing() != null){
					if(matches(search, a.getBoxedLabelThing().getText())){
						r = createFindResult(context, a.getBoxedLabelThing(), prefix, a.getBoxedLabelThing().getText(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_CONNECTOR_TYPE));
					}
				}
				find(context, a, search, resultList);
			}
			else if(TypesMapper.isSignatureAssemblyRootThing(t)){
				EndpointAssembly a = EndpointAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getEndpointGlassThing() != null){
					if(matches(search, a.getEndpointGlassThing().getToolTip())){
						r = createFindResult(context, a.getEndpointGlassThing(), prefix, a.getEndpointGlassThing().getToolTip(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_INTERFACE));
					}
				}
			}
			else if(TypesMapper.isInterfaceTypeAssemblyRootThing(t)){
				EndpointAssembly a = EndpointAssembly.attach(m, (IHasAssemblyData)t);
				if(a.getEndpointGlassThing() != null){
					if(matches(search, a.getEndpointGlassThing().getToolTip())){
						r = createFindResult(context, a.getEndpointGlassThing(), prefix, a.getEndpointGlassThing().getToolTip(), AS.resources.getImageDescriptor(ArchstudioResources.ICON_INTERFACE_TYPE));
					}
				}
			}
			
			if(r != null){
				resultList.add(r);
			}
		}
	}
	
	protected static boolean matches(String query, String target){
		if((query == null) || (target == null)) return false;
		query = query.toLowerCase();
		target = target.toLowerCase();
		return (target.indexOf(query) != -1);
	}
	
	protected void find(IBNAView context, BoxAssembly ba, String search, List<IFindResult> resultList){
		String prefix = "[No Description]/";
		if(ba.getBoxedLabelThing() != null){
			String text = ba.getBoxedLabelThing().getText();
			if(text != null){
				prefix = text + "/";
			}
		}
		
		WorldThing wt = ba.getWorldThing();
		
		WorldThingPeer wtp = (WorldThingPeer)context.getPeer(wt);
		
		if(wt != null){
			IBNAWorld world = wt.getWorld();
			if(world != null){
				IBNAModel internalModel = world.getBNAModel();
				find(wtp.getInnerView(), internalModel, search, prefix, resultList);
			}
		}
	}
	
	protected IFindResult createFindResult(IBNAView view, IThing t, String prefix, String text, ImageDescriptor image){
		if(text == null){
			return null;
		}
		Point p = BNAUtils.getCentralPoint(t);
		if(p == null){
			return null;
		}
		if(prefix == null) prefix = "";
		return new DefaultFindResult(new FindResultData(view, t, p), prefix + text, image);
	}
	
	class FindResultData{
		public IBNAView view;
		public IThing t;
		public Point p;
		
		public FindResultData(IBNAView view, IThing t, Point p){
			this.view = view;
			this.t = t;
			this.p = p;
		}
	}
	
}
