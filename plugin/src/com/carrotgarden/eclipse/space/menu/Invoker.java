package com.carrotgarden.eclipse.space.menu;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.carrotgarden.eclipse.space.util.BundleUtil;
import com.carrotgarden.eclipse.space.util.EclipseRunnable;
import com.carrotgarden.eclipse.space.util.JobUtil;

/**
 * menu action invocation provider
 */
public interface Invoker {

	/**
	 * invoke menu action by congigured id
	 */
	void invoke(String id);

	/**
	 * no-op invocaton
	 */
	Invoker empty = new Invoker() {
		@Override
		public void invoke(String id) {
		}
	};

	/**
	 * scheduled background invocation
	 */
	Invoker action = new Invoker() {
		@Override
		public void invoke(final String className) {
			final EclipseRunnable runable = new EclipseRunnable(className) {
				@Override
				public void doit(final IProgressMonitor monitor) throws Throwable {
					Bundle bundle = InvokerUtil.bundleAquire();
					try {
						ClassLoader scriptLoader = bundle.adapt(BundleWiring.class).getClassLoader();
						final Class<?> scriptClass = scriptLoader.loadClass(className);
						InvokerUtil.methodInvoke(scriptClass);
					} catch (Throwable e) {
						throw e;
					} finally {
						BundleUtil.bundleRelease(bundle);
					}
				}
			};
			JobUtil.schedule(runable);
		}
	};

}
