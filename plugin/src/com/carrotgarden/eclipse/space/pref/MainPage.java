package com.carrotgarden.eclipse.space.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.carrotgarden.eclipse.space.Plugin;

public class MainPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage, PrefName {

	public MainPage() {
		super(FieldEditorPreferencePage.FLAT);
	}

	@Override
	protected void createFieldEditors() {

		addField(new StringFieldEditor(PROJECT_NAME, "Worker Project Name", getFieldEditorParent()));

		// addField(new StringFieldEditor(PROJECT_TARGET, "Project Classes",
		// getFieldEditorParent()));

		addField(new StringFieldEditor(DEFAULT_CLASS, "Default Class Name", getFieldEditorParent()));
		addField(new BooleanFieldEditor(DEFAULT_INVOKE, "Invoke Default on Activate", getFieldEditorParent()));

		addField(new StringFieldEditor(LOGBACK_PATTERN, "Logback Render Pattern", getFieldEditorParent()));
		addField(new StringFieldEditor(LOGBACK_LEVEL, "Logback Logging Level", getFieldEditorParent()));

		// addField(new BooleanFieldEditor(LOGBACK_ROOT_ATTACH, "Logback Root Attach.",
		// getFieldEditorParent()));

		addField(new BooleanFieldEditor(CONSOLE_SHOW_ON_ERR, "Show Console on ERR", getFieldEditorParent()));
		addField(new BooleanFieldEditor(CONSOLE_SHOW_ON_OUT, "Show Console on OUT.", getFieldEditorParent()));

	}

	public static IPreferenceStore preferenceStore() {
		return Plugin.instance().getPreferenceStore();
	}

	public static String projectName() {
		return preferenceStore().getString(PROJECT_NAME);
	}

	// public static String projectTarget() {
	// return preferenceStore().getString(PROJECT_TARGET);
	// }

	public static String defaultClass() {
		return preferenceStore().getString(DEFAULT_CLASS);
	}

	public static boolean defaultInvoke() {
		return preferenceStore().getBoolean(DEFAULT_INVOKE);
	}

	public static boolean showConsoleOnErr() {
		return preferenceStore().getBoolean(CONSOLE_SHOW_ON_ERR);
	}

	public static boolean showConsoleOnOut() {
		return preferenceStore().getBoolean(CONSOLE_SHOW_ON_OUT);
	}

	public static String logbackPattern() {
		return preferenceStore().getString(LOGBACK_PATTERN);
	}

	// public static boolean logbackRootAttach() {
	// return preferenceStore().getBoolean(LOGBACK_ROOT_ATTACH);
	// }

	public static String logbackLevel() {
		return preferenceStore().getString(LOGBACK_LEVEL);
	}

	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(preferenceStore());
	}

}
