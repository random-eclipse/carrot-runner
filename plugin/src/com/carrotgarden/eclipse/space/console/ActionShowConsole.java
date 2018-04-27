package com.carrotgarden.eclipse.space.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.carrotgarden.eclipse.space.Plugin;

public abstract class ActionShowConsole extends Action implements IPropertyChangeListener {

	public ActionShowConsole(final String name) {
		super(name, IAction.AS_CHECK_BOX);
		setToolTipText(name);
		getPreferenceStore().addPropertyChangeListener(this);
		update();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final String property = event.getProperty();
		if (property.equals(getKey())) {
			update();
		}
	}

	protected abstract String getKey();

	private void update() {
		final IPreferenceStore store = getPreferenceStore();
		if (store.getBoolean(getKey())) {
			// on
			setChecked(true);
		} else {
			// off
			setChecked(false);
		}
	}

	private IPreferenceStore getPreferenceStore() {
		return Plugin.instance().getPreferenceStore();
	}

	@Override
	public void run() {
		final IPreferenceStore store = getPreferenceStore();
		final boolean show = isChecked();
		store.removePropertyChangeListener(this);
		store.setValue(getKey(), show);
		store.addPropertyChangeListener(this);
	}

	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(this);
	}
}
