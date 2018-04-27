package com.carrotgarden.eclipse.space.menu;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * menu entry representing script action
 */
public class RunnerMenuItem extends ContributionItem {

	final String name;
	final Invoker invoker;
	final boolean enabled;
	final Image image;

	public RunnerMenuItem(final String id, final String name, final Invoker invoker, final boolean enabled,
			final Image image) {
		super(id);
		this.name = name;
		this.invoker = invoker;
		this.enabled = enabled;
		this.image = image;
	}

	@Override
	public void fill(final Menu menu, final int index) {

		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);

		menuItem.setText(name);
		menuItem.setEnabled(enabled);
		menuItem.setImage(image);

		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// System.err.println("event=" + event);
				invoker.invoke(getId());
			}
		});

		// final ToolTip tip = new ToolTip(menu.getShell(), SWT.BALLOON);
		// tip.setMessage("tip-tip-tip-tip-tip");
		// tip.setAutoHide(true);
		// tip.setVisible(true);

		menuItem.addArmListener(new ArmListener() {
			@Override
			public void widgetArmed(final ArmEvent event) {
				// System.err.println("event=" + event);
			}
		});

	}

}
