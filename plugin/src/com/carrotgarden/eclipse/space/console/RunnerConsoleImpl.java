package com.carrotgarden.eclipse.space.console;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.pref.PrefName;

public class RunnerConsoleImpl extends IOConsole implements RunnerConsole, IPropertyChangeListener, PrefName {

	private boolean initialized = false;

	// console is visible in the Console view
	private boolean visible = false;

	private RunnerConsoleDocument consoleDocument;

	// created colors for each line type - must be disposed at shutdown
	private Color commandColor;

	private Color messageColor;

	private Color errorColor;

	// streams for each command type - each stream has its own color
	private IOConsoleOutputStream commandStream;

	private IOConsoleOutputStream messageStream;

	private IOConsoleOutputStream errorStream;

	private static final String TITLE = Plugin.PLUGIN_NAME;

	private final List<RunnerConsoleListener> listeners = new CopyOnWriteArrayList<RunnerConsoleListener>();

	public RunnerConsoleImpl(final ImageDescriptor imageDescriptor) {
		super(TITLE, imageDescriptor);
		this.setConsoleDocument(new RunnerConsoleDocument());
	}

	@Override
	protected void init() {
		super.init();

		// Ensure that initialization occurs in the UI thread
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				JFaceResources.getFontRegistry().addListener(RunnerConsoleImpl.this);
				initializeConsoleStreams(Display.getDefault());
				dumpConsole();
			}
		});
	}

	/**
	 * Initialize three streams of the console. Must be called from the UI thread,
	 * so synchronization is unnecessary.
	 */
	protected void initializeConsoleStreams(final Display display) {
		if (!initialized) {
			setCommandStream(newOutputStream());
			setErrorStream(newOutputStream());
			setMessageStream(newOutputStream());

			// TODO convert this to use themes
			// install colors
			commandColor = new Color(display, new RGB(0, 0, 0));
			messageColor = new Color(display, new RGB(0, 0, 255));
			errorColor = new Color(display, new RGB(255, 0, 0));

			getCommandStream().setColor(commandColor);
			getMessageStream().setColor(messageColor);
			getErrorStream().setColor(errorColor);

			// install font
			setFont(JFaceResources.getFontRegistry().get("pref_console_font")); //$NON-NLS-1$

			initialized = true;
		}
	}

	/**
	 * Is always called from main thread, so synchronization not necessary
	 */
	protected void dumpConsole() {
		setVisible(true);
		final RunnerConsoleDocument.ConsoleLine[] lines = getConsoleDocument().getLines();
		for (int i = 0; i < lines.length; i++) {
			final RunnerConsoleDocument.ConsoleLine line = lines[i];
			appendLine(line.type, line.line);
		}
		getConsoleDocument().clear();
	}

	private void appendLine(final int type, final String line) {
		show(false);
		// the synchronization here caused a deadlock. since the writes are
		// simply appending to the output stream
		// or the document, just doing it on the main thread to avoid deadlocks
		// and or corruption of the
		// document or output stream
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (isVisible()) {
					try {
						switch (type) {
						case RunnerConsoleDocument.COMMAND:
							getCommandStream().write(line);
							getCommandStream().write('\n');
							break;
						case RunnerConsoleDocument.MESSAGE:
							getMessageStream().write(line);
							getMessageStream().write('\n');
							break;
						case RunnerConsoleDocument.ERROR:
							getErrorStream().write(line);
							getErrorStream().write('\n');
							break;
						}
					} catch (final IOException ex) {
						// Don't log using slf4j - it will cause a cycle
						ex.printStackTrace();
					}
				} else {
					getConsoleDocument().appendConsoleLine(type, line);
				}
			}
		});
	}

	/**
	 * Show the console.
	 * 
	 * @param showNoMatterWhat
	 *            ignore preferences if <code>true</code>
	 */
	public void show(final boolean showNoMatterWhat) {
		if (showNoMatterWhat) {
			if (!isVisible()) {
				showConsole();
			} else {
				ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this);
			}
		}
	}

	public void showConsole() {
		boolean exists = false;
		final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		for (final IConsole element : manager.getConsoles()) {
			if (this == element) {
				exists = true;
			}
		}
		if (!exists) {
			manager.addConsoles(new IConsole[] { this });
		}
		manager.showConsoleView(this);
	}

	public void closeConsole() {
		final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		manager.removeConsoles(new IConsole[] { this });
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(this.newLifecycle());
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// font changed
		setFont(JFaceResources.getFontRegistry().get("pref_console_font")); //$NON-NLS-1$
	}

	private void bringConsoleToFront() {
		if (PlatformUI.isWorkbenchRunning()) {
			final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			if (!isVisible()) {
				manager.addConsoles(new IConsole[] { this });
			}
			manager.showConsoleView(this);
		}
	}

	// Called when console is removed from the console view
	@Override
	protected void dispose() {
		// Here we can't call super.dispose() because we actually want the
		// partitioner to remain
		// connected, but we won't show lines until the console is added to the
		// console manager
		// again.
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setVisible(false);
				JFaceResources.getFontRegistry().removeListener(RunnerConsoleImpl.this);
			}
		});
	}

	public void shutdown() {
		// Call super dispose because we want the partitioner to be
		// disconnected.
		super.dispose();
		if (commandColor != null) {
			commandColor.dispose();
		}
		if (messageColor != null) {
			messageColor.dispose();
		}
		if (errorColor != null) {
			errorColor.dispose();
		}
	}

	static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

	private DateFormat dateFormat() {
		return FORMAT;
	}

	// MavenConsole

	@Override
	public void debug(final String message) {
		if (!Plugin.instance().getPreferenceStore().getBoolean(CONSOLE_DEBUG_OUTPUT)) {
			return;
		}
		if (showConsoleOnOutput()) {
			bringConsoleToFront();
		}
		appendLine(RunnerConsoleDocument.MESSAGE, dateFormat().format(new Date()) + ": " + message);

		for (final RunnerConsoleListener listener : listeners) {
			try {
				listener.loggingNormal(message);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void info(final String message) {
		if (showConsoleOnOutput()) {
			bringConsoleToFront();
		}
		appendLine(RunnerConsoleDocument.MESSAGE, dateFormat().format(new Date()) + ": " + message);

		for (final RunnerConsoleListener listener : listeners) {
			try {
				listener.loggingNormal(message);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void error(final String message) {
		if (showConsoleOnError()) {
			bringConsoleToFront();
		}
		appendLine(RunnerConsoleDocument.ERROR, dateFormat().format(new Date()) + ": " + message); //$NON-NLS-1$

		for (final RunnerConsoleListener listener : listeners) {
			try {
				listener.loggingFailed(message);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean showConsoleOnError() {
		return Plugin.instance().getPreferenceStore().getBoolean(CONSOLE_SHOW_ON_ERR);
	}

	public boolean showConsoleOnOutput() {
		return Plugin.instance().getPreferenceStore().getBoolean(CONSOLE_SHOW_ON_OUT);
	}

	public IConsoleListener newLifecycle() {
		return new MavenConsoleLifecycle();
	}

	protected void setCommandStream(final IOConsoleOutputStream commandStream) {
		this.commandStream = commandStream;
	}

	protected IOConsoleOutputStream getCommandStream() {
		return commandStream;
	}

	protected void setMessageStream(final IOConsoleOutputStream messageStream) {
		this.messageStream = messageStream;
	}

	protected IOConsoleOutputStream getMessageStream() {
		return messageStream;
	}

	protected void setErrorStream(final IOConsoleOutputStream errorStream) {
		this.errorStream = errorStream;
	}

	protected IOConsoleOutputStream getErrorStream() {
		return errorStream;
	}

	protected void setVisible(final boolean visible) {
		this.visible = visible;
	}

	protected boolean isVisible() {
		return visible;
	}

	private void setConsoleDocument(final RunnerConsoleDocument consoleDocument) {
		this.consoleDocument = consoleDocument;
	}

	protected RunnerConsoleDocument getConsoleDocument() {
		return consoleDocument;
	}

	public class MavenConsoleLifecycle implements org.eclipse.ui.console.IConsoleListener {

		@Override
		public void consolesAdded(final IConsole[] consoles) {
			for (int i = 0; i < consoles.length; i++) {
				final IConsole console = consoles[i];
				if (console == RunnerConsoleImpl.this) {
					init();
				}
			}

		}

		@Override
		public void consolesRemoved(final IConsole[] consoles) {
			for (int i = 0; i < consoles.length; i++) {
				final IConsole console = consoles[i];
				if (console == RunnerConsoleImpl.this) {
					ConsolePlugin.getDefault().getConsoleManager().removeConsoleListener(this);
					dispose();
				}
			}
		}

	}

	public void addMavenConsoleListener(final RunnerConsoleListener listener) {
		listeners.remove(listener);
		listeners.add(listener);
	}

	public void removeMavenConsoleListener(final RunnerConsoleListener listener) {
		listeners.remove(listener);
	}

}
