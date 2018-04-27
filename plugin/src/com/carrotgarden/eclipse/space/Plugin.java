package com.carrotgarden.eclipse.space;

import java.io.PrintWriter;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import com.carrotgarden.eclipse.space.console.RunnerConsoleImpl;
import com.carrotgarden.eclipse.space.logger.ScriptLogging;
import com.carrotgarden.eclipse.space.menu.InvokerUtil;
import com.carrotgarden.eclipse.space.util.ConsoleUtil;

/** runner plugin, singleton */
public class Plugin extends AbstractUIPlugin implements IStartup {

	/** plugin identity */
	public static final String PLUGIN_ID = "com.carrotgarden.eclipse.space.plugin";
	public static final String PLUGIN_NAME = "Space Master";

	private static void render(final IStatus status) {
		final Plugin instance = instance();
		if (instance == null) {
			System.err.println(status);
		} else {
			instance.getLog().log(status);
		}
	}

	/** eclipse logger */
	public static interface log {

		static void debug(final String message) {
			final IStatus status = new Status(IStatus.OK, Plugin.PLUGIN_ID, 0, message, null);
			render(status);
		}

		static void info(final String message) {
			final IStatus status = new Status(IStatus.INFO, Plugin.PLUGIN_ID, 0, message, null);
			render(status);
		}

		static void warn(final String message) {
			final IStatus status = new Status(IStatus.WARNING, Plugin.PLUGIN_ID, 0, message, null);
			render(status);
		}

		static void error(final String message) {
			final IStatus status = new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 0, message, null);
			render(status);
		}

		static void error(final String message, final Throwable exception) {
			final IStatus status = new Status(IStatus.ERROR, Plugin.PLUGIN_ID, 0, message, exception);
			render(status);
		}

	}

	public static interface image {
		static ImageDescriptor descriptor(final String path) {
			return imageDescriptorFromPlugin(PLUGIN_ID, path);
		}
	}

	private static volatile Plugin instance;

	private volatile BundleContext context;

	public BundleContext context() {
		return context;
	}

	public static Plugin instance() {
		return instance;
	}

	public Version version() {
		return context.getBundle().getVersion();
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		this.context = context;
		instance = this;
		super.start(context);
		instance.getLog().addLogListener(eclipseListener);
		ScriptLogging.initialize();
		log.info(PLUGIN_NAME + " start. " + version());
		InvokerUtil.invokeDefaultAction();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		log.info(PLUGIN_NAME + " stop.");
		ScriptLogging.terminate();
		instance.getLog().removeLogListener(eclipseListener);
		instance = null;
		this.context = null;
	}

	private static final ConsoleUtil.Console console = ConsoleUtil.console(PLUGIN_NAME);

	/** plugin master console */
	public static ConsoleUtil.Console console() {
		return console;
	}

	private final PrintWriter eclipseWrier = new PrintWriter(console.out);

	/** forward runner plugin logs to master console */
	private final ILogListener eclipseListener = new ILogListener() {
		@Override
		public void logging(final IStatus status, final String plugin) {
			eclipseWrier.println(ConsoleUtil.render(status));
			eclipseWrier.flush();
		}
	};

	private static volatile RunnerConsoleImpl runnerConsole;

	/** plugin worker console */
	public RunnerConsoleImpl runnerConsole() {
		if (runnerConsole == null) {
			runnerConsole = new RunnerConsoleImpl(image.descriptor("/image/logo-16x16.png"));
		}
		return runnerConsole;
	}

	@Override
	public void earlyStartup() {
		// TODO Auto-generated method stub
	}

}
