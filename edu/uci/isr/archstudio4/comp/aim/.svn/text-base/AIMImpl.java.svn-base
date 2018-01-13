package edu.uci.isr.archstudio4.comp.aim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.uci.isr.myx.fw.IMyxBrickDescription;
import edu.uci.isr.myx.fw.IMyxName;
import edu.uci.isr.myx.fw.IMyxRuntime;
import edu.uci.isr.myx.fw.IMyxWeld;
import edu.uci.isr.myx.fw.MyxBrickCreationException;
import edu.uci.isr.myx.fw.MyxBrickLoadException;
import edu.uci.isr.myx.fw.MyxConstants;
import edu.uci.isr.myx.fw.MyxJavaClassBrickDescription;
import edu.uci.isr.myx.fw.MyxJavaClassInterfaceDescription;
import edu.uci.isr.myx.fw.MyxUtils;
import edu.uci.isr.xadlutils.XadlUtils;
import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public class AIMImpl implements IAIM{
	protected XArchFlatInterface xarch = null;
	
	protected IMyxBrickDescription containerBrickDescription;
	
	public AIMImpl(XArchFlatInterface xarch){
		this.xarch = xarch;
		containerBrickDescription = MyxUtils.getContainerBrickDescription();
	}
	
	public synchronized void setXArch(XArchFlatInterface xarch){
		this.xarch = xarch;
	}
	
	public synchronized void instantiate(IMyxRuntime myx, String name, ObjRef xArchRef, ObjRef structureRef) throws ArchitectureInstantiationException{
		IMyxName containerName = MyxUtils.createName(name);
		
		try{
			myx.addBrick(new IMyxName[0], containerName, containerBrickDescription);
			instantiate(myx, xArchRef, structureRef, new IMyxName[]{containerName});
		}
		catch(MyxBrickLoadException mble){
			throw new ArchitectureInstantiationException("Myx cannot load brick", mble);
		}
		catch(MyxBrickCreationException mbce){
			throw new ArchitectureInstantiationException("Myx cannot create brick", mbce);
		}
	}
	
	public synchronized void begin(IMyxRuntime myx, String name){
		IMyxName containerName = MyxUtils.createName(name);
		myx.begin(new IMyxName[0], containerName);
	}
	
	public synchronized void end(IMyxRuntime myx, String name){
		IMyxName containerName = MyxUtils.createName(name);
		myx.end(new IMyxName[0], containerName);
	}

	public synchronized void destroy(IMyxRuntime myx, String name){
		IMyxName containerName = MyxUtils.createName(name);
		try{
			myx.destroy(new IMyxName[0], containerName);
		}finally{
			myx.removeBrick(new IMyxName[0], containerName);
		}
	}
	
	private static class AIMInterfaceData{
		public int myxDirection;
		public IMyxName[] containerPath;
		public IMyxName brickName;
		public IMyxName interfaceName;
		public String[] serviceObjectInterfaceNames;
		
		/* Following are not used for non-mapped interfaces */

		public IMyxName internalBrickName;
		public IMyxName internalBrickInterfaceName;
	}
	
	public void instantiate(IMyxRuntime myx, ObjRef xArchRef, ObjRef structureRef, IMyxName[] containerPath) throws ArchitectureInstantiationException{
		//Let's do the components+connectors
		ObjRef[] componentRefs = xarch.getAll(structureRef, "component");
		ObjRef[] connectorRefs = xarch.getAll(structureRef, "connector");
		
		//Iterate through both arrays and instantiate.
		for(int i = 0; i < componentRefs.length + connectorRefs.length; i++){
			ObjRef brickRef = (i < componentRefs.length) ? componentRefs[i] : connectorRefs[i - componentRefs.length];
			String brickID = XadlUtils.getID(xarch, brickRef);
			if(brickID == null){
				throw new ArchitectureInstantiationException("Brick missing ID: " + brickRef);
			}
			
			ObjRef brickTypeRef = XadlUtils.resolveXLink(xarch, brickRef, "type");
			if(brickTypeRef == null){
				throw new ArchitectureInstantiationException("Brick type link missing or invalid: " + brickID);
			}
			
			ObjRef subArchitectureRef = (ObjRef)xarch.get(brickTypeRef, "subArchitecture");
			if(subArchitectureRef != null){
				//Process subarchitecture
				//Create the container:
				IMyxName containerName = MyxUtils.createName(brickID);
				try{
					myx.addBrick(containerPath, containerName, containerBrickDescription);
				}
				catch(MyxBrickLoadException mble){
					throw new ArchitectureInstantiationException("Myx cannot load brick", mble);
				}
				catch(MyxBrickCreationException mbce){
					throw new ArchitectureInstantiationException("Myx cannot create brick", mbce);
				}
				
				//Instantiate the substructure into that container.
				ObjRef innerStructureRef = XadlUtils.resolveXLink(xarch, subArchitectureRef, "archStructure");
				if(innerStructureRef == null){
					throw new ArchitectureInstantiationException("Brick type " + XadlUtils.getDescription(xarch, brickTypeRef) + " has invalid subarchitecture structure link.");
				}
				//This is easy enough; we just recurse.
				IMyxName[] innerContainerPath = new IMyxName[containerPath.length + 1];
				innerContainerPath[containerPath.length] = containerName;
				instantiate(myx, xArchRef, innerStructureRef, innerContainerPath);
				
				//Okay, the container is created and added; its inner structure is all
				//set up, now we have to go about creating and mapping all its interfaces.
				ObjRef[] interfaceRefs = xarch.getAll(brickRef, "interface");
				for(int k = 0; k < interfaceRefs.length; k++){
					AIMInterfaceData aid = parseAndValidateMappedInterfaceData(containerPath, brickRef, interfaceRefs[k], brickTypeRef);
					MyxJavaClassInterfaceDescription aidDesc = new MyxJavaClassInterfaceDescription(aid.serviceObjectInterfaceNames); 
					myx.addContainerInterface(aid.containerPath, aid.brickName, aid.interfaceName, aidDesc, aid.myxDirection, aid.internalBrickName, aid.internalBrickInterfaceName);
				}
			}
			else{
				//Process atomic type
				ObjRef[] implementationRefs = xarch.getAll(brickTypeRef, "implementation");
				if((implementationRefs == null) || (implementationRefs.length == 0)){
					throw new ArchitectureInstantiationException("Brick type " + XadlUtils.getDescription(xarch, brickTypeRef) + " must have either a subarchitecture or an implementation.");
				}
				ObjRef javaImplementationRef = null;
				for(int j = 0; j < implementationRefs.length; j++){
					if(xarch.isInstanceOf(implementationRefs[j], "javaimplementation#JavaImplementation")){
						javaImplementationRef = implementationRefs[j];
						break;
					}
				}
				if(javaImplementationRef == null){
					throw new ArchitectureInstantiationException("Could not find Java implementation on brick type: " + XadlUtils.getDescription(xarch, brickTypeRef));
				}
				ObjRef mainClassRef = (ObjRef)xarch.get(javaImplementationRef, "mainClass");
				if(mainClassRef == null){
					throw new ArchitectureInstantiationException("Java implementation lacks main class on brick type: " + XadlUtils.getDescription(xarch, brickTypeRef));
				}
				ObjRef mainClassNameRef = (ObjRef)xarch.get(mainClassRef, "javaClassName");
				if(mainClassNameRef == null){
					throw new ArchitectureInstantiationException("Java implementation lacks main class name on brick type: " + XadlUtils.getDescription(xarch, brickTypeRef));
				}
				String mainClassName = (String)xarch.get(mainClassNameRef, "value");
				if(mainClassName == null){
					throw new ArchitectureInstantiationException("Java implementation lacks main class name on brick type: " + XadlUtils.getDescription(xarch, brickTypeRef));
				}
				//We have the main class name; let's get the properties (if any)
				Properties initializationParameters = getProperties(mainClassRef);
				
				IMyxBrickDescription myxBrickDescription = new MyxJavaClassBrickDescription(initializationParameters, mainClassName);
				IMyxName myxBrickName = MyxUtils.createName(brickID);
				try{
					myx.addBrick(containerPath, myxBrickName, myxBrickDescription);
				}
				catch(MyxBrickLoadException mble){
					throw new ArchitectureInstantiationException("Myx cannot load brick", mble);
				}
				catch(MyxBrickCreationException mbce){
					throw new ArchitectureInstantiationException("Myx cannot create brick", mbce);
				}

				//Okay, the brick is created and added; now we have to go about
				//creating all its interfaces.
				ObjRef[] interfaceRefs = xarch.getAll(brickRef, "interface");
				for(int k = 0; k < interfaceRefs.length; k++){
					AIMInterfaceData aid = parseAndValidateInterfaceData(containerPath, brickRef, interfaceRefs[k]);
					MyxJavaClassInterfaceDescription aidDesc = new MyxJavaClassInterfaceDescription(aid.serviceObjectInterfaceNames); 
					myx.addInterface(aid.containerPath, aid.brickName, aid.interfaceName, aidDesc, aid.myxDirection);
				}
				//System.err.println("initing " + myxBrickName);
				myx.init(containerPath, myxBrickName);
			}
		}
		
		//Process the links
		ObjRef[] linkRefs = xarch.getAll(structureRef, "link");
		for(int i = 0; i < linkRefs.length; i++){
			ObjRef[] pointRefs = xarch.getAll(linkRefs[i], "point");
			if(pointRefs.length != 2){
				throw new ArchitectureInstantiationException("Link " + XadlUtils.getDescription(xarch, linkRefs[i]) + " must have exactly two points.");
			}
			
			ObjRef providedInterfaceRef = null;
			ObjRef requiredInterfaceRef = null;
			for(int p = 0; p < pointRefs.length; p++){
				ObjRef interfaceRef = XadlUtils.resolveXLink(xarch, pointRefs[p], "anchorOnInterface");
				if(interfaceRef == null){
					throw new ArchitectureInstantiationException("Link " + XadlUtils.getDescription(xarch, linkRefs[i]) + " has an invalid endpoint (" + p + ") link.");
				}
				String direction = XadlUtils.getDirection(xarch, interfaceRef);
				if(direction == null){
					throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, interfaceRef) + " has an invalid direction.");
				}
				else if(direction.equals("in")){
					if(providedInterfaceRef != null){
						throw new ArchitectureInstantiationException("Link " + XadlUtils.getDescription(xarch, linkRefs[i]) + " connects two provided interfaces.");
					}
					providedInterfaceRef = interfaceRef;
				}
				else if(direction.equals("out")){
					if(requiredInterfaceRef != null){
						throw new ArchitectureInstantiationException("Link " + XadlUtils.getDescription(xarch, linkRefs[i]) + " connects two required interfaces.");
					}
					requiredInterfaceRef = interfaceRef;
				}
				else{
					throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, interfaceRef) + " has an invalid (non in/out) direction.");
				}
			}
			//Okay, we have both the provided and required interface refs.
			ObjRef providedBrickRef = xarch.getParent(providedInterfaceRef);
			ObjRef requiredBrickRef = xarch.getParent(requiredInterfaceRef);

			String providedBrickID = XadlUtils.getID(xarch, providedBrickRef);
			if(providedBrickID == null){
				throw new ArchitectureInstantiationException("Brick " + XadlUtils.getDescription(xarch, providedBrickRef) + " missing ID.");
			}
			
			String requiredBrickID = XadlUtils.getID(xarch, requiredBrickRef);
			if(requiredBrickID == null){
				throw new ArchitectureInstantiationException("Brick " + XadlUtils.getDescription(xarch, requiredBrickRef) + " missing ID.");
			}
			
			String providedInterfaceID = XadlUtils.getID(xarch, providedInterfaceRef);
			if(providedInterfaceID == null){
				throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, providedInterfaceRef) + " on brick " + XadlUtils.getDescription(xarch, providedBrickRef) + " missing ID.");
			}
			
			String requiredInterfaceID = XadlUtils.getID(xarch, requiredInterfaceRef);
			if(requiredInterfaceID == null){
				throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, requiredInterfaceRef) + " on brick " + XadlUtils.getDescription(xarch, requiredBrickRef) + " missing ID.");
			}
			
			ObjRef requiredSignatureRef = XadlUtils.resolveXLink(xarch, requiredInterfaceRef, "signature");
			if(requiredSignatureRef == null){
				throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, requiredInterfaceRef) + " on brick " + XadlUtils.getDescription(xarch, requiredBrickRef) + " has a missing or invalid signature.");
			}

			ObjRef providedSignatureRef = XadlUtils.resolveXLink(xarch, providedInterfaceRef, "signature");
			if(providedSignatureRef == null){
				throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, providedInterfaceRef) + " on brick " + XadlUtils.getDescription(xarch, providedBrickRef) + " has a missing or invalid signature.");
			}
			
			String requiredSignatureImplementationName = getLookupImplementationName(requiredSignatureRef);
			if(requiredSignatureImplementationName == null){
				throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, requiredSignatureRef) + " on brick " + XadlUtils.getDescription(xarch, requiredBrickRef) + " has no lookup implementation for its corresponding signature.");
			}
			
			String providedSignatureImplementationName = getLookupImplementationName(providedSignatureRef);
			if(providedSignatureImplementationName == null){
				throw new ArchitectureInstantiationException("Interface " + XadlUtils.getDescription(xarch, providedSignatureRef) + " on brick " + XadlUtils.getDescription(xarch, providedBrickRef) + " has no lookup implementation for its corresponding signature.");
			}
			
			IMyxWeld weld = myx.createWeld(
				/* Required */
				containerPath, 
				MyxUtils.createName(requiredBrickID), 
				MyxUtils.createName(requiredSignatureImplementationName), 
				/* Provided */
				containerPath, 
				MyxUtils.createName(providedBrickID), 
				MyxUtils.createName(providedSignatureImplementationName)); 

			try{
				myx.addWeld(weld);
			}
			catch(Exception e){
				throw new ArchitectureInstantiationException("Problem adding weld or invalid weld: " + weld.toString(), e);
			}
		}
	}
	
	private AIMInterfaceData parseAndValidateInterfaceData(IMyxName[] containerPath, ObjRef brickRef, ObjRef interfaceRef) throws ArchitectureInstantiationException{
		String interfaceID = XadlUtils.getID(xarch, interfaceRef);
		if(interfaceID == null){
			throw new ArchitectureInstantiationException("Missing ID on interface: " + XadlUtils.getDescription(xarch, interfaceRef) + " on brick " + XadlUtils.getDescription(xarch, brickRef));
		}
		//We have to get the implementation lookup name for the interface,
		//which should be hiding on its type.
		ObjRef signatureRef = XadlUtils.resolveXLink(xarch, interfaceRef, "signature");
		if(signatureRef == null){
			throw new ArchitectureInstantiationException("Missing signature for interface " + XadlUtils.getDescription(xarch, interfaceRef) + " on brick " + XadlUtils.getDescription(xarch, brickRef));
		}
		String lookupImplementationName = getLookupImplementationName(signatureRef);
		if(lookupImplementationName == null){
			throw new ArchitectureInstantiationException("Missing lookup implementation name on signature " + XadlUtils.getDescription(xarch, signatureRef));
		}
		String direction = XadlUtils.getDirection(xarch, interfaceRef);
		if(direction == null){
			throw new ArchitectureInstantiationException("Missing direction on interface " + XadlUtils.getDescription(xarch, interfaceRef));
		}
		int myxDirection = -1;
		if(direction.equals("in")){
			myxDirection = MyxConstants.PROVIDED;
		}
		else if(direction.equals("out")){
			myxDirection = MyxConstants.REQUIRED;
		}
		else{
			throw new ArchitectureInstantiationException("Invalid direction (not in/out) on interface " + XadlUtils.getDescription(xarch, interfaceRef));
		}
		List<String> serviceObjectInterfaceNames = new ArrayList<String>();
		ObjRef ifaceTypeRef = XadlUtils.resolveXLink(xarch, interfaceRef, "type");
		ObjRef[] implementationRefs = xarch.getAll(ifaceTypeRef, "implementation");
		for(int j = 0; j < implementationRefs.length; j++){
			if(xarch.isInstanceOf(implementationRefs[j], "javaimplementation#JavaImplementation")){
				ObjRef javaImplementationRef = implementationRefs[j];
				ObjRef mainClassRef = (ObjRef)xarch.get(javaImplementationRef, "mainClass");
				if(mainClassRef != null){
					ObjRef mainClassNameRef = (ObjRef)xarch.get(mainClassRef, "javaClassName");
					if(mainClassNameRef != null){
						String mainClassName = (String)xarch.get(mainClassNameRef, "value");
						if(mainClassName != null){
							serviceObjectInterfaceNames.add(mainClassName);
						}
					}
				}
				for (ObjRef auxClassRef : xarch.getAll(javaImplementationRef, "auxClass")){
					if(auxClassRef != null){
						ObjRef auxClassNameRef = (ObjRef)xarch.get(auxClassRef, "javaClassName");
						if(auxClassNameRef != null){
							String auxClassName = (String)xarch.get(auxClassNameRef, "value");
							if(auxClassName != null){
								serviceObjectInterfaceNames.add(auxClassName);
							}
						}
					}
				}
			}
		}
		
		AIMInterfaceData aid = new AIMInterfaceData();
		aid.myxDirection = myxDirection;
		aid.containerPath = containerPath;
		aid.brickName = MyxUtils.createName(XadlUtils.getID(xarch, brickRef));
		aid.interfaceName = MyxUtils.createName(lookupImplementationName);
		aid.serviceObjectInterfaceNames = serviceObjectInterfaceNames.toArray(new String[serviceObjectInterfaceNames.size()]);
		
		return aid;
	}
	
	
	private AIMInterfaceData parseAndValidateMappedInterfaceData(IMyxName[] containerPath, ObjRef brickRef, ObjRef interfaceRef, ObjRef brickTypeRef) throws ArchitectureInstantiationException{
		AIMInterfaceData aid = parseAndValidateInterfaceData(containerPath, brickRef, interfaceRef);
		ObjRef subArchitectureRef = (ObjRef)xarch.get(brickTypeRef, "subArchitecture");
		if(subArchitectureRef == null){
			throw new ArchitectureInstantiationException("Can't find subarchitecture for brick " + XadlUtils.getDescription(xarch, brickRef));
		}
		ObjRef interfacesSignatureRef = XadlUtils.resolveXLink(xarch, interfaceRef, "signature");
		if(interfacesSignatureRef == null){
			throw new ArchitectureInstantiationException("Invalid or missing signature link on interface " + XadlUtils.getDescription(xarch, interfaceRef));
		}

		ObjRef[] simRefs = xarch.getAll(subArchitectureRef, "signatureInterfaceMapping");
		for(int i = 0; i < simRefs.length; i++){
			ObjRef outerSignatureRef = XadlUtils.resolveXLink(xarch, simRefs[i], "outerSignature");
			if(outerSignatureRef == null){
				throw new ArchitectureInstantiationException("Invalid or missing outerSignature link on signature-interface mapping " + XadlUtils.getDescription(xarch, simRefs[i]));
			}
			ObjRef innerInterfaceRef = XadlUtils.resolveXLink(xarch, simRefs[i], "innerInterface");
			if(innerInterfaceRef == null){
				throw new ArchitectureInstantiationException("Invalid or missing innerInterface link on signature-interface mapping " + XadlUtils.getDescription(xarch, simRefs[i]));
			}
			ObjRef innerInterfaceTypeRef = XadlUtils.resolveXLink(xarch, innerInterfaceRef, "type");
			if(innerInterfaceTypeRef == null){
				throw new ArchitectureInstantiationException("Missing interface type on interface " + XadlUtils.getDescription(xarch, innerInterfaceRef));
			}
			String innerInterfaceLookupImplementationName = getLookupImplementationName(innerInterfaceTypeRef);
			if(innerInterfaceLookupImplementationName == null){
				throw new ArchitectureInstantiationException("Missing lookup implementation on interface type " + XadlUtils.getDescription(xarch, innerInterfaceTypeRef));
			}
			
			//Is this a SIM to our target signature?
			if((interfacesSignatureRef.equals(outerSignatureRef)) || (xarch.isEqual(interfacesSignatureRef, outerSignatureRef))){
				ObjRef innerBrickRef = xarch.getParent(innerInterfaceRef);
				if(innerBrickRef == null){
					throw new ArchitectureInstantiationException("Invalid or missing parent on interface " + XadlUtils.getDescription(xarch, innerInterfaceRef));
				}
				String innerBrickID = XadlUtils.getID(xarch, innerBrickRef);
				if(innerBrickID == null){
					throw new ArchitectureInstantiationException("Missing ID on brick " + XadlUtils.getDescription(xarch, innerBrickRef));
				}
				String innerInterfaceID = XadlUtils.getID(xarch, innerInterfaceRef);
				if(innerInterfaceID == null){
					throw new ArchitectureInstantiationException("Missing ID on interface " + XadlUtils.getDescription(xarch, innerInterfaceRef));
				}
				aid.internalBrickName = MyxUtils.createName(innerBrickID);
				//aid.internalBrickInterfaceName = MyxUtils.createName(innerInterfaceID);
				aid.internalBrickInterfaceName = MyxUtils.createName(innerInterfaceLookupImplementationName);
				return aid;
			}
		}
		throw new ArchitectureInstantiationException("Could not find matching signature-interface mapping for interface " + XadlUtils.getDescription(xarch, interfaceRef) + " on brick " + XadlUtils.getDescription(xarch, brickRef));
	}
	
	private Properties getProperties(ObjRef javaClassFileRef){
		if(xarch.isInstanceOf(javaClassFileRef, "javainitparams#JavaClassFileParams")){
			Properties p = new Properties();
			ObjRef[] initializationParameterRefs = xarch.getAll(javaClassFileRef, "initializationParameter");
			for(int i = 0; i < initializationParameterRefs.length; i++){
				String name = (String)xarch.get(initializationParameterRefs[i], "name");
				String value = (String)xarch.get(initializationParameterRefs[i], "value");
				p.put(name, value);
			}
			return p;
		}
		else{
			return null;
		}
	}
	
	private String getLookupImplementationName(ObjRef typeRef){
		ObjRef[] implementationRefs = xarch.getAll(typeRef, "implementation");
		if((implementationRefs == null) || (implementationRefs.length == 0)){
			return null;
		}
		ObjRef lookupImplementationRef = null;
		for(int j = 0; j < implementationRefs.length; j++){
			if(xarch.isInstanceOf(implementationRefs[j], "lookupimplementation#LookupImplementation")){
				lookupImplementationRef = implementationRefs[j];
				break;
			}
		}
		if(lookupImplementationRef == null){
			return null;
		}
		ObjRef lookupNameRef = (ObjRef)xarch.get(lookupImplementationRef, "name");
		if(lookupNameRef == null){
			return null;
		}
		return (String)xarch.get(lookupNameRef, "value");
	}
}
