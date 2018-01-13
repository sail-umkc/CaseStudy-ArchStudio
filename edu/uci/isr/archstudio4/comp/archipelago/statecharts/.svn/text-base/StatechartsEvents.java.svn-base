package edu.uci.isr.archstudio4.comp.archipelago.statecharts;

import edu.uci.isr.archstudio4.comp.archipelago.IArchipelagoEvent;
import edu.uci.isr.archstudio4.comp.archipelago.statecharts.StatechartsMapper.StateKind;
import edu.uci.isr.bna4.IBNAWorld;
import edu.uci.isr.bna4.assemblies.IAssembly;
import edu.uci.isr.bna4.assemblies.StickySplineAssembly;
import edu.uci.isr.xarchflat.ObjRef;

public class StatechartsEvents{
	private StatechartsEvents(){}
	
	public static class WorldCreatedEvent implements IArchipelagoEvent{
		protected ObjRef statechartRef;
		protected IBNAWorld world;

		public WorldCreatedEvent(ObjRef statechartRef, IBNAWorld world){
			this.statechartRef = statechartRef;
			this.world = world;
		}

		public ObjRef getStatechartRef(){
			return statechartRef;
		}

		public IBNAWorld getWorld(){
			return world;
		}
	}
	
	public static class StatechartUpdatedEvent implements IArchipelagoEvent{
		protected IBNAWorld bnaWorld;
		protected ObjRef statechartRef;

		public StatechartUpdatedEvent(IBNAWorld bnaWorld, ObjRef statechartRef){
			this.bnaWorld = bnaWorld;
			this.statechartRef = statechartRef;
		}

		public ObjRef getStatechartRef(){
			return statechartRef;
		}

		public IBNAWorld getBNAWorld(){
			return bnaWorld;
		}
	}

	public static class StateUpdatedEvent implements IArchipelagoEvent{
		protected IBNAWorld bnaWorld;
		protected ObjRef stateRef;
		protected StateKind kind;
		protected IAssembly stateAssembly;
		
		public StateUpdatedEvent(IBNAWorld bnaWorld, ObjRef stateRef, StateKind kind, IAssembly stateAssembly){
			this.bnaWorld = bnaWorld;
			this.stateRef = stateRef;
			this.kind = kind;
			this.stateAssembly = stateAssembly;
		}

		public IBNAWorld getBNAWorld(){
			return bnaWorld;
		}

		public ObjRef getStateRef(){
			return stateRef;
		}

		public StateKind getStateKind(){
			return kind;
		}
		
		public IAssembly getStateAssembly(){
			return stateAssembly;
		}
	}
	
	public static class TransitionUpdatedEvent implements IArchipelagoEvent{
		protected IBNAWorld bnaWorld;
		protected ObjRef transitionRef;
		protected StickySplineAssembly transitionAssembly;
		
		public TransitionUpdatedEvent(IBNAWorld bnaWorld, ObjRef transitionRef, StickySplineAssembly transitionAssembly){
			this.bnaWorld = bnaWorld;
			this.transitionRef = transitionRef;
			this.transitionAssembly = transitionAssembly;
		}

		public IBNAWorld getBNAWorld(){
			return bnaWorld;
		}

		public ObjRef getTransitionRef(){
			return transitionRef;
		}
		
		public StickySplineAssembly getTransitionAssembly(){
			return transitionAssembly;
		}
	}
}
