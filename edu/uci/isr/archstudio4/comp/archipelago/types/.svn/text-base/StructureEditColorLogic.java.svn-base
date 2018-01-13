package edu.uci.isr.archstudio4.comp.archipelago.types;

import org.eclipse.swt.graphics.RGB;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.util.AbstractEditColorLogic;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.assemblies.SplineAssembly;
import edu.uci.isr.bna4.facets.IHasAssemblyData;
import edu.uci.isr.bna4.facets.IHasMutableColor;
import edu.uci.isr.bna4.things.glass.BoxGlassThing;
import edu.uci.isr.bna4.things.shapes.BoxThing;
import edu.uci.isr.bna4.things.shapes.SplineThing;

public class StructureEditColorLogic extends AbstractEditColorLogic{
	protected ArchipelagoServices AS = null;
	
	public StructureEditColorLogic(ArchipelagoServices services){
		super();
		this.AS = services;
	}
	
	protected boolean matches(IBNAView view, IThing t){
		IThing pt = view.getWorld().getBNAModel().getParentThing(t);
		if(pt != null){
			if((t instanceof BoxGlassThing) && StructureMapper.isBrickAssemblyRootThing(pt)){
				return true;
			}
			else if(StructureMapper.isLinkAssemblyRootThing(pt)){
				return true;
			}
		}
		return false;
	}
	
	protected RGB getDefaultRGB(IBNAView view, IThing[] thingsToEdit){
		RGB defaultRGB = null;
		RGB defaultComponentRGB = StructureMapper.getDefaultComponentColor(AS);
		RGB defaultConnectorRGB = StructureMapper.getDefaultConnectorColor(AS);
		for(int i = 0; i < thingsToEdit.length; i++){
			IThing pt = view.getWorld().getBNAModel().getParentThing(thingsToEdit[i]);
			if(pt != null){
				if(StructureMapper.isComponentAssemblyRootThing(pt)){
					if((defaultRGB == null) || (BNAUtils.nulleq(defaultRGB, defaultComponentRGB))){
						defaultRGB = defaultComponentRGB;
					}
					else{
						return null;
					}
				}
				else if(StructureMapper.isConnectorAssemblyRootThing(pt)){
					if((defaultRGB == null) || (BNAUtils.nulleq(defaultRGB, defaultConnectorRGB))){
						defaultRGB = defaultConnectorRGB;
					}
					else{
						return null;
					}
				}
			}
		}
		return defaultRGB;
	}
	
	protected IHasMutableColor getColoredThing(IBNAView view, IThing t){
		IThing pt = view.getWorld().getBNAModel().getParentThing(t);
		if(pt != null){
			if(StructureMapper.isBrickAssemblyRootThing(pt)){
				BoxAssembly boxAssembly = BoxAssembly.attach(view.getWorld().getBNAModel(), (IHasAssemblyData)pt);
				BoxThing boxThing = boxAssembly.getBoxThing();
				if(boxThing != null){
					return boxThing;
				}
			}
			else if(StructureMapper.isLinkAssemblyRootThing(pt)){
				SplineAssembly splineAssembly = SplineAssembly.attach(view.getWorld().getBNAModel(), (IHasAssemblyData)pt);
				SplineThing splineThing = splineAssembly.getSplineThing();
				if(splineThing != null){
					return splineThing;
				}
			}
		}
		return null;
	}
	
	protected RGB getRGB(IBNAView view, IThing t){
		IHasMutableColor ct = getColoredThing(view, t);
		if(ct != null){
			return ct.getColor();
		}
		return null;
	}
	
	protected void setRGB(IBNAView view, IThing t, RGB newRGB){
		IHasMutableColor ct = getColoredThing(view, t);
		if(ct != null){
			ct.setColor(newRGB);
		}
	}
}
