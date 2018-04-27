package com.carrotgarden.eclipse.space.console;

import org.eclipse.jface.action.Action;

import com.carrotgarden.eclipse.space.Plugin;

public class ActionOpenConsole extends Action {

	@Override
	public void run() {
		Plugin.instance().runnerConsole().showConsole();
	}

}
