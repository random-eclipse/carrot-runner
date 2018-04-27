package com.carrotgarden.eclipse.space.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.carrotgarden.eclipse.space.Plugin;

public class BundleUtil {

	/**
	 * load script plugin bundle
	 */
	public static Bundle bundleAquire(String location) throws Exception {
		BundleContext context = Plugin.instance().context();
		bundleRelease(context.getBundle(location));
		Bundle bundle = context.installBundle(location);
		bundle.update();
		bundle.start();
		return bundle;
	}

	/**
	 * unload script plugin bundle
	 */
	public static void bundleRelease(Bundle bundle) throws Exception {
		if (bundle == null) {
			return;
		}
		int state = bundle.getState();
		switch (state) {
		case Bundle.UNINSTALLED:
			return;
		case Bundle.RESOLVED:
		case Bundle.INSTALLED:
		case Bundle.STARTING:
		case Bundle.ACTIVE:
		case Bundle.STOPPING:
			bundle.uninstall();
			return;
		default:
			Plugin.log.error("Wrong bundle state=" + state);
			return;
		}
	}

}
