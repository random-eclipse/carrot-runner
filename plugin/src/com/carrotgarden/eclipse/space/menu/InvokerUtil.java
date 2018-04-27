package com.carrotgarden.eclipse.space.menu;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.pref.MainPage;
import com.carrotgarden.eclipse.space.util.BundleUtil;
import com.carrotgarden.eclipse.space.util.ResourceUtil;

public class InvokerUtil {

	/**
	 * invoke default action on plugin activation
	 */
	public static void invokeDefaultAction() {
		String className = MainPage.defaultClass();
		boolean invokeAction = MainPage.defaultInvoke();
		if (invokeAction) {
			Plugin.log.info("Default action enabled: class=" + className);
			try {
				Bundle bundle = InvokerUtil.bundleAquire();
				try {
					ClassLoader scriptLoader = bundle.adapt(BundleWiring.class).getClassLoader();
					Class<?> scriptClass = scriptLoader.loadClass(className);
					methodInvoke(scriptClass);
				} catch (Throwable e) {
					throw e;
				} finally {
					BundleUtil.bundleRelease(bundle);
				}
			} catch (Throwable e) {
				Plugin.log.error("Default invoke failure. className=" + className, e);
			}
		} else {
			Plugin.log.info("Default action disabled");
		}
	}

	static FilenameFilter actionFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".class") && !name.contains("$"); // XXX
		}
	};

	static Comparator<? super File> filenameComparator = new Comparator<File>() {
		@Override
		public int compare(File f1, File f2) {
			return f1.getName().compareTo(f2.getName());
		}
	};

	/**
	 * runner/script method contract: static method "enable"
	 */
	static boolean methodEnabled(Class<?> scriptClass) {
		try {
			Method method = scriptClass.getMethod("enable");
			Boolean enable = (Boolean) method.invoke(null);
			return enable;
		} catch (Throwable e) {
			Plugin.log.error("Method 'enable' failure in script " + scriptClass, e);
			return false;
		}
	}

	/**
	 * runner/script method contract: static method "image"
	 */
	static Image methodImage(Class<?> scriptClass) {
		try {
			Method method = scriptClass.getMethod("image");
			InputStream input = (InputStream) method.invoke(null);
			Display display = Display.getDefault();
			Image image = new Image(display, input);
			return image;
		} catch (Throwable e) {
			Plugin.log.error("Method 'image' failure in  script " + scriptClass, e);
			return null; // valid value
		}
	}

	/**
	 * runner/script method contract: static method "invoke"
	 */
	static void methodInvoke(Class<?> scriptClass) {
		try {
			Method method = scriptClass.getMethod("invoke");
			method.invoke(null);
		} catch (Throwable e) {
			Plugin.log.error("Method 'invoke' failure in script " + scriptClass, e);
		}
	}

	/**
	 * script plugin 'action' classes only
	 */
	static List<File> scriptClassList(Bundle bundle) {
		List<File> scriptList = new ArrayList<>();
		Dictionary<String, String> dict = bundle.getHeaders();
		String classPath = dict.get("Bundle-ClassPath");
		if (classPath == null) {
			throw new RuntimeException("Script plugin missing 'Bundle-ClassPath'");
		}
		String[] entryList = classPath.split(",");
		File projectPath = scriptProjectFolder();
		for (String entry : entryList) {
			File entryPath = new File(projectPath, entry);
			if (entryPath.isDirectory()) {
				File[] classList = entryPath.listFiles(actionFilter);
				scriptList.addAll(Arrays.asList(classList));
			}
		}
		scriptList.sort(filenameComparator);
		return scriptList;
	}

	/**
	 * location of script plugin in the workspace
	 */
	static File scriptProjectFolder() {
		String projectName = MainPage.projectName();
		String projectPath = ResourceUtil.projectPath(projectName);
		File folder = new File(projectPath);
		return folder;
	}

	/**
	 * script plugin exploded directory bundle url
	 */
	static String bundleLocation() {
		return "reference:file:" + scriptProjectFolder().getAbsolutePath();
	}

	/**
	 * load script plugin bundle
	 */
	static Bundle bundleAquire() throws Exception {
		return BundleUtil.bundleAquire(bundleLocation());
	}

}
