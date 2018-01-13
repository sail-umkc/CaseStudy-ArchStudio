package edu.uci.isr.archstudio4.comp.archipelago.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchActionConstants;

import edu.uci.isr.bna4.AbstractThingLogic;
import edu.uci.isr.bna4.BNAUtils;
import edu.uci.isr.bna4.IBNAMenuListener;
import edu.uci.isr.bna4.IBNAView;
import edu.uci.isr.bna4.IThing;
import edu.uci.isr.widgets.swt.ColorSelectorDialog;
import edu.uci.isr.widgets.swt.SWTWidgetUtils;

public abstract class AbstractEditColorLogic extends AbstractThingLogic implements IBNAMenuListener{
	protected static RGB copiedRGB = null;

	protected Color[] currentColors = null;
	protected ImageDescriptor currentSwatchDescriptor = null;
	protected Color copyColor = null;
	protected ImageDescriptor copySwatchDescriptor = null;
	protected Color defaultColor = null;
	protected ImageDescriptor defaultSwatchDescriptor = null;
	
	public AbstractEditColorLogic(){
	}
	
	public void destroy(){
		super.destroy();
		disposeResources();
	}
	
	protected abstract boolean matches(IBNAView view, IThing t);
	protected abstract RGB getDefaultRGB(IBNAView view, IThing[] thingsToEdit);
	protected abstract RGB getRGB(IBNAView view, IThing t);
	protected abstract void setRGB(IBNAView view, IThing t, RGB newRGB);
	
	protected void disposeResources(){
		if(currentColors != null){
			for(int i = 0; i < currentColors.length; i++){
				if((currentColors[i] != null) && (!currentColors[i].isDisposed())){
					currentColors[i].dispose();
				}
			}
			currentColors = null;
		}
		if((copyColor != null) && (!copyColor.isDisposed())){
			copyColor.dispose();
		}
		if((defaultColor != null) && (!defaultColor.isDisposed())){
			defaultColor.dispose();
		}
	}
	
	public void fillMenu(IBNAView view, IMenuManager m, int localX, int localY, IThing t, int worldX, int worldY){
		if(t == null) return;
		IThing[] selectedThings = BNAUtils.getSelectedThings(view.getWorld().getBNAModel());

		List<IThing> thingsToEditList = new ArrayList<IThing>();
		if(selectedThings.length > 0){
			for(int i = 0; i < selectedThings.length; i++){
				if(matches(view, selectedThings[i])){
					thingsToEditList.add(selectedThings[i]);
				}
			}
		}
		else{
			if(matches(view, t)){
				thingsToEditList.add(t);
			}
		}
		if(thingsToEditList.size() == 0){
			return;
		}
		IThing[] thingsToEdit = thingsToEditList.toArray(new IThing[thingsToEditList.size()]);
		
		for(IAction action : getActions(view, thingsToEdit, worldX, worldY)){
			m.add(action);
		}
		m.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	protected IAction[] getActions(IBNAView view, IThing[] thingsToEdit, int worldX, int worldY){
		Display d = BNAUtils.getParentComposite(view).getDisplay();
		final IBNAView fview = view;
		final IThing[] fthingsToEdit = thingsToEdit;
		final int fworldX = worldX;
		final int fworldY = worldY;
		
		disposeResources();
		
		List<IAction> actionList = new ArrayList<IAction>(10);
		
		List<Color> colorList = new ArrayList<Color>(thingsToEdit.length);
		for(int i = 0; i < thingsToEdit.length; i++){
			RGB rgb = getRGB(view, thingsToEdit[i]);
			if(rgb != null){
				Color c = new Color(d, rgb);
				colorList.add(c);
			}
		}
		
		currentColors = colorList.toArray(new Color[colorList.size()]);
		currentSwatchDescriptor = SWTWidgetUtils.createColorSwatch(d, currentColors, 16, 16, false, true);
		
		RGB initialRGB = null;
		if(currentColors.length == 1){
			initialRGB = currentColors[0].getRGB();
		}
		final RGB finitialRGB = initialRGB;
		Action assignColorAction = new Action("Assign Color..."){
			public void run(){
				chooseAndAssignColor(fview, fthingsToEdit, finitialRGB);
			}
		};
		assignColorAction.setImageDescriptor(currentSwatchDescriptor);
		actionList.add(assignColorAction);
		
		final RGB defaultRGB = getDefaultRGB(view, thingsToEdit);
		if(defaultRGB != null){
			Action resetToDefaultColorAction = new Action("Reset to Default Color"){
				public void run(){
					assignColor(fview, fthingsToEdit, defaultRGB);
				}
			};

			defaultColor = new Color(d, defaultRGB);
			defaultSwatchDescriptor = SWTWidgetUtils.createColorSwatch(d, defaultColor, 16, 16, false, true);
			resetToDefaultColorAction.setImageDescriptor(defaultSwatchDescriptor);
			actionList.add(resetToDefaultColorAction);
		}
		
		if(currentColors.length == 1){
			Action copyColorAction = new Action("Copy Color"){
				public void run(){
					copiedRGB = currentColors[0].getRGB();
				}
			};
			copyColorAction.setImageDescriptor(currentSwatchDescriptor);
			actionList.add(copyColorAction);
		}
		else{
			actionList.add(SWTWidgetUtils.createNoAction("Copy Color"));
		}
		
		if(copiedRGB != null){
			Action pasteColorAction = new Action("Paste Color"){
				public void run(){
					assignColor(fview, fthingsToEdit, copiedRGB);
				}
			};
			
			copyColor = new Color(d, copiedRGB);
			copySwatchDescriptor = SWTWidgetUtils.createColorSwatch(d, copyColor, 16, 16, false, true);
			pasteColorAction.setImageDescriptor(copySwatchDescriptor);
			actionList.add(pasteColorAction);
		}
		else{
			actionList.add(SWTWidgetUtils.createNoAction("Paste Color"));
		}
		
		return actionList.toArray(new IAction[actionList.size()]);
	}
	
	protected void chooseAndAssignColor(IBNAView view, IThing[] thingsToEdit, RGB initialRGB){
		ColorSelectorDialog csd = new ColorSelectorDialog(BNAUtils.getParentComposite(view).getShell());
		RGB rgb = csd.open(initialRGB);
		if(rgb != null){
			assignColor(view, thingsToEdit, rgb);
		}
	}
	
	protected void assignColor(IBNAView view, IThing[] thingsToEdit, RGB rgb){
		for(IThing t : thingsToEdit){
			setRGB(view, t, rgb);
		}
	}
}
