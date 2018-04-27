package com.carrotgarden.eclipse.space.console;

import org.eclipse.ui.console.IConsoleFactory;

import com.carrotgarden.eclipse.space.Plugin;


/**
 */
public class RunnerConsoleFactory implements IConsoleFactory {

  @Override
public void openConsole() {
    Plugin.instance().runnerConsole().showConsole();
  }

}
