package com.carrotgarden.eclipse.space;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * runner plugin project nature
 */
public class Nature implements IProjectNature {

	public static final String NATURE_ID = "com.carrotgarden.eclipse.space.nature";

	/** adapted resource project */
	private volatile IProject project;

	@Override
	public void configure() throws CoreException {
		Plugin.log.info("Nature#configure  : " + getProject());
	}

	@Override
	public void deconfigure() throws CoreException {
		Plugin.log.info("Nature#deconfigure : " + getProject());
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

}
