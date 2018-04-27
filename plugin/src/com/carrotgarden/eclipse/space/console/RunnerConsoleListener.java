package com.carrotgarden.eclipse.space.console;

import java.util.EventListener;

public interface RunnerConsoleListener extends EventListener {

	void loggingNormal(String msg);

	void loggingFailed(String msg);

}
