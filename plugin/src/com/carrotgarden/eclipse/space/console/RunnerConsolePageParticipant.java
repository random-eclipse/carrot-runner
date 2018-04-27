package com.carrotgarden.eclipse.space.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import com.carrotgarden.eclipse.space.pref.PrefName;

public class RunnerConsolePageParticipant implements IConsolePageParticipant {

	private IAction consoleRemoveAction;

	private IAction debugAction;

	private IAction showOnErrAction;

	private IAction showOnOutAction;

	private static final String SHOW_ON_OUT_LBL = "Show on Out.";

	private static final String SHOW_ON_ERR_LBL = "Show on Err.";

	@Override
	public void init(final IPageBookViewPage page, final IConsole console) {

		this.consoleRemoveAction = new ActionConsoleRemove();
		this.debugAction = new ActionDebugOutput();

		showOnOutAction = new ShowOnOutAction(console, SHOW_ON_OUT_LBL);
		showOnErrAction = new ShowOnErrAction(console, SHOW_ON_ERR_LBL);

		final IActionBars actionBars = page.getSite().getActionBars();
		configureToolBar(actionBars.getToolBarManager());
	}

	private void configureToolBar(final IToolBarManager mgr) {
		mgr.appendToGroup(IConsoleConstants.LAUNCH_GROUP, consoleRemoveAction);
		mgr.prependToGroup(IConsoleConstants.OUTPUT_GROUP, debugAction);
		mgr.appendToGroup(IConsoleConstants.OUTPUT_GROUP, showOnOutAction);
		mgr.appendToGroup(IConsoleConstants.OUTPUT_GROUP, showOnErrAction);
	}

	@Override
	public void dispose() {
		this.consoleRemoveAction = null;
		this.debugAction = null;
	}

	@Override
	public void activated() {
	}

	@Override
	public void deactivated() {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter) {
		return null;
	}

	class ShowOnErrAction extends ActionShowConsole {
		public ShowOnErrAction(final IConsole console, final String name) {
			super(name);
			// setImageDescriptor(MavenImages.SHOW_CONSOLE_ERR);
		}

		@Override
		protected String getKey() {
			return PrefName.CONSOLE_SHOW_ON_ERR;
		}
	}

	class ShowOnOutAction extends ActionShowConsole {

		public ShowOnOutAction(final IConsole console, final String name) {
			super(name);
			// setImageDescriptor(MavenImages.SHOW_CONSOLE_OUT);
		}

		@Override
		protected String getKey() {
			return PrefName.CONSOLE_SHOW_ON_OUT;
		}

	}
}
