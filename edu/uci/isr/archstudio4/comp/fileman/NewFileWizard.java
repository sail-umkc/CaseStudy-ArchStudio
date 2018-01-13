package edu.uci.isr.archstudio4.comp.fileman;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import edu.uci.isr.myx.fw.MyxRegistry;
import edu.uci.isr.sysutils.UIDGenerator;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public class NewFileWizard extends Wizard implements INewWizard{
	private MyxRegistry er = MyxRegistry.getSharedInstance();
	private FileManagerMyxComponent comp = null;

	protected IWorkbench workbench;
	protected IStructuredSelection selection;

	protected NewFileCreationPage mainPage;

	protected XArchFlatInterface xarch = null;

	public NewFileWizard(){
		comp = (FileManagerMyxComponent)er.waitForBrick(FileManagerMyxComponent.class);
		er.map(comp, this);
		xarch = comp.xarch;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection){
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("New Architecture Description");
		//setDefaultPageImageDescriptor(ReadmeImages.README_WIZARD_BANNER);
	}

	public void addPages() {
		mainPage = new NewFileCreationPage(workbench, selection);
		addPage(mainPage);
	}


	public boolean performFinish(){
		IPath containerPath = mainPage.getContainerFullPath();
		String fileName = mainPage.getFileName();

		if(!fileName.toLowerCase().endsWith(".xml")){
			fileName += ".xml";
		}

		IPath filePath = containerPath.append(fileName);

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFile(filePath);
		if(file.exists()){
			MessageDialog.openError(
				getShell(),
				"Error",
			"File already exists.");
			return false;
		}
		String uri = UIDGenerator.generateUID("urn:");
		ObjRef ref = xarch.createXArch(uri);
		String fileContents = xarch.serialize(ref);
		xarch.close(ref);

		InputStream is = new ByteArrayInputStream(fileContents.getBytes());
		try{
			file.create(is, false, null);
		}
		catch(CoreException ce){
			MessageDialog.openError(
				getShell(),
				"Error",
				ce.getMessage());
			return false;
		}
		finally{
			try{
				is.close();
			}
			catch(IOException ioe){}
		}

		return true;
	}

	static class NewFileCreationPage extends WizardNewFileCreationPage{
		public NewFileCreationPage(IWorkbench workbench, IStructuredSelection selection){
			super("New Architecture Description", selection);
			setTitle("New Architecture Description");
		}

		public void createControl(Composite parent){
			super.createControl(parent);
		}
	}

	public static void showWizard(Shell shell, IWorkbench workbench){
		NewFileWizard wizard = new NewFileWizard();
		wizard.init(workbench, StructuredSelection.EMPTY);
		WizardDialog dialog = new WizardDialog(shell, wizard);

		int result = dialog.open();
	}

}
