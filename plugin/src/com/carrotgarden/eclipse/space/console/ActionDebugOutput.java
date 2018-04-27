package com.carrotgarden.eclipse.space.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.pref.PrefName;

public class ActionDebugOutput extends Action implements PrefName {

	private final IPropertyChangeListener listener = new IPropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent event) {
			if (CONSOLE_DEBUG_OUTPUT.equals(event.getProperty())) {
				setChecked(isDebug());
			}
		}
	};

	public ActionDebugOutput() {
		setToolTipText("tool tip");
		// setImageDescriptor(MavenImages.DEBUG);

		getPreferenceStore().addPropertyChangeListener(listener);
		setChecked(isDebug());
	}

	@Override
	public void run() {
		getPreferenceStore().setValue(CONSOLE_DEBUG_OUTPUT, isChecked());
	}

	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(listener);
	}

	IPreferenceStore getPreferenceStore() {
		return Plugin.instance().getPreferenceStore();
	}

	boolean isDebug() {
		return getPreferenceStore().getBoolean(CONSOLE_DEBUG_OUTPUT);
	}

}
