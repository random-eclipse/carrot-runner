package com.carrotgarden.eclipse.space.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * eclipse resource helper
 */
public class ResourceUtil {

	public static String workspacePath() {
		return workspaceRoot().getLocation().toFile().getAbsolutePath();
	}

	public static IWorkspaceRoot workspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public static IProject project(final String name) {
		return workspaceRoot().getProject(name);
	}

	public static String projectPath(final String name) {
		return project(name).getLocation().toFile().getAbsolutePath();
	}

	public static boolean hasProject(final String name) {
		return project(name).exists();
	}

	public static boolean hasProjectOpen(final String name) {
		final IProject project = project(name);
		return project.exists() && project.isOpen();
	}

}
