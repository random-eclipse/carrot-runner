package com.carrotgarden.eclipse.space.menu;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.util.BundleUtil;
import com.carrotgarden.eclipse.space.util.EclipseRunnable;
import com.carrotgarden.eclipse.space.util.JobUtil;

/**
 * build dynamic menu from discovered script actions
 */
public class RunnerMenuList extends CompoundContributionItem {

	static RunnerMenuItem buildErrorItem(Throwable e) {
		final String menuName = "Menu failure: " + e.getMessage();
		return new RunnerMenuItem("plugin-error", menuName, Invoker.empty, true, null);
	}

	static RunnerMenuItem buildMenuItem(ClassLoader scriptLoader, File classFile) throws Exception {
		final String className = classFile.getName().replaceAll(".class", "");
		final Class<?> scriptClass = scriptLoader.loadClass(className);
		final String menuName = className.replaceAll("_", " ");
		final boolean enabled = InvokerUtil.methodEnabled(scriptClass);
		final Image image = InvokerUtil.methodImage(scriptClass);
		return new RunnerMenuItem(className, menuName, Invoker.action, enabled, image);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final List<IContributionItem> itemList = new ArrayList<IContributionItem>();
		try {
			Bundle bundle = InvokerUtil.bundleAquire();
			final List<File> fileList = InvokerUtil.scriptClassList(bundle);
			try {
				ClassLoader scriptLoader = bundle.adapt(BundleWiring.class).getClassLoader();
				for (final File file : fileList) {
					// Plugin.logInfo("file=" + file);
					itemList.add(buildMenuItem(scriptLoader, file));
				}
			} catch (Throwable e) {
				throw e;
			} finally {
				BundleUtil.bundleRelease(bundle);
			}
		} catch (final Throwable e) {
			itemList.clear();
			itemList.add(buildErrorItem(e));
			Plugin.log.error("Menu failure", e);
		}
		return itemList.toArray(new IContributionItem[] {});
	}

}
