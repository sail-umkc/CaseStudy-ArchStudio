package edu.uci.isr.archstudio4.comp.archipelago.types;

import org.eclipse.swt.graphics.RGB;

import edu.uci.isr.archstudio4.comp.archipelago.ArchipelagoServices;
import edu.uci.isr.archstudio4.comp.archipelago.util.AbstractEditColorLogic;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.bna4.assemblies.BoxAssembly;
import edu.uci.isr.bna4.facets.IHasAssemblyData;
import edu.uci.isr.bna4.facets.IHasMutableColor;
import edu.uci.isr.bna4.things.glass.BoxGlassThing;
import edu.uci.isr.bna4.things.shapes.BoxThing;

public class TypesEditColorLogic extends AbstractEditColorLogic{
	protected ArchipelagoServices AS = null;
	
	public TypesEditColorLogic(ArchipelagoServices services){
		super();
		this.AS = services;
	}

	protected boolean matches(IBNAView view, IThing t){
		if(t instanceof BoxGlassThing){
			IThing pt = view.getWorld().getBNAModel().getParentThing(t);
			if(pt != null){
				if(TypesMapper.isBrickTypeAssemblyRootThing(pt)){
					return true;
				}
			}
		}
		return false;
	}
	
	protected RGB getDefaultRGB(IBNAView view, IThing[] thingsToEdit){
		RGB defaultRGB = null;
		RGB defaultComponentTypeRGB = TypesMapper.getDefaultComponentTypeColor(AS);
		RGB defaultConnectorTypeRGB = TypesMapper.getDefaultConnectorTypeColor(AS);
		for(int i = 0; i < thingsToEdit.length; i++){
			IThing pt = view.getWorld().getBNAModel().getParentThing(thingsToEdit[i]);
			if(pt != null){
				if(TypesMapper.isComponentTypeAssemblyRootThing(pt)){
					if((defaultRGB == null) || (BNAUtils.nulleq(defaultRGB, defaultComponentTypeRGB))){
						defaultRGB = defaultComponentTypeRGB;
					}
					else{
						return null;
					}
				}
				else if(TypesMapper.isConnectorTypeAssemblyRootThing(pt)){
					if((defaultRGB == null) || (BNAUtils.nulleq(defaultRGB, defaultConnectorTypeRGB))){
						defaultRGB = defaultConnectorTypeRGB;
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
			if(TypesMapper.isBrickTypeAssemblyRootThing(pt)){
				BoxAssembly boxAssembly = BoxAssembly.attach(view.getWorld().getBNAModel(), (IHasAssemblyData)pt);
				BoxThing boxThing = boxAssembly.getBoxThing();
				if(boxThing != null){
					return boxThing;
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
