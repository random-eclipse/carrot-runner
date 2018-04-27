package com.carrotgarden.eclipse.space.pref;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.carrotgarden.eclipse.space.Plugin;

public class Initializer extends AbstractPreferenceInitializer implements PrefName {

	public static IPreferenceStore preferenceStore() {
		return Plugin.instance().getPreferenceStore();
	}

	@Override
	public void initializeDefaultPreferences() {

		final IPreferenceStore store = preferenceStore();

		store.setDefault(PROJECT_NAME, "space-worker");
		// store.setDefault(PROJECT_TARGET, "target/classes");

		store.setDefault(DEFAULT_CLASS, "Arkon");
		store.setDefault(DEFAULT_INVOKE, true);

		store.setDefault(LOGBACK_PATTERN, "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg %n");
		store.setDefault(LOGBACK_LEVEL, "INFO");
		// store.setDefault(LOGBACK_ROOT_ATTACH, true);

		store.setDefault(CONSOLE_SHOW_ON_ERR, true);
		store.setDefault(CONSOLE_SHOW_ON_OUT, true);

	}

}
