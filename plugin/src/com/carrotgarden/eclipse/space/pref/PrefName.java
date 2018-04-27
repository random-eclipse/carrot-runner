package com.carrotgarden.eclipse.space.pref;

import com.carrotgarden.eclipse.space.Plugin;

public interface PrefName {

	String A = Plugin.PLUGIN_ID + ".";

	String PROJECT_NAME = A + "project-name";
	String PROJECT_TARGET = A + "project-target";

	String DEFAULT_CLASS = A + "default-class";
	String DEFAULT_INVOKE = A + "default-invoke";

	String LOGBACK_PATTERN = A + "logback-pattern";
	String LOGBACK_LEVEL = A + "logback-root-level";
//	String LOGBACK_ROOT_ATTACH = A + "logback-root-attach";

	String CONSOLE_DEBUG_OUTPUT = A + "console-debug-output";
	String CONSOLE_SHOW_ON_OUT = A + "console-show-on-out";
	String CONSOLE_SHOW_ON_ERR = A + "console-show-on-err";

}
