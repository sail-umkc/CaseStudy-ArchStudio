package edu.uci.isr.archstudio4.comp.resources;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class ResourcesProxy implements InvocationHandler {

	ResourcesImp resources = new ResourcesImp();
	private final Object lock = new Object();

	List<Runnable> toExec = new ArrayList<Runnable>();

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		synchronized (lock) {
			final Method fMethod = method;
			final Object[] fArgs = args;
			if (void.class.equals(method.getReturnType())) {
				if (resources.display == null) {
					toExec.add(new Runnable() {
						public void run() {
							try {
								fMethod.invoke(resources, fArgs);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					});
					return null;
				} else if (Display.getCurrent() == null) {
					resources.display.asyncExec(new Runnable() {
						public void run() {
							try {
								fMethod.invoke(resources, fArgs);
							} catch (Throwable t) {
								t.printStackTrace();
							}
						}
					});
					return null;
				}
			}
			if (Display.getCurrent() == null)
				SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
			if (resources.display == null) {
				resources.setDisplay(Display.getCurrent());
				for (Runnable runnable : toExec)
					runnable.run();
				toExec.clear();
			}
			return method.invoke(resources, args);
		}
	}
}
