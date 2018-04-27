package com.carrotgarden.eclipse.space.console;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.carrotgarden.eclipse.space.Plugin;

public class ActionConsoleRemove extends Action {

	public ActionConsoleRemove() {
		setToolTipText("tool tip");
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));
	}

	@Override
	public void run() {
		Plugin.instance().runnerConsole().closeConsole();
	}

}
