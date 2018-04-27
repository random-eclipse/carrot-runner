package com.carrotgarden.eclipse.space.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;

import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.util.EclipseRunnable;
import com.carrotgarden.eclipse.space.util.JobUtil;

/**
 * @see plugin.xml
 */
public class ProjectUpdateAction extends BaseAction {

	public ProjectUpdateAction() {
	}

	@Override
	public void run(final IAction action) {

		final IProject project = getProject();

		JobUtil.schedule(new EclipseRunnable("Project update.") {
			@Override
			public void doit(final IProgressMonitor monitor)
					throws CoreException {

				monitor.beginTask("Project update " + project, 2);

				try {

					// 1. disable
					// Plugin.instance().manager().handleDelete(project);
					monitor.worked(1);

					Thread.sleep(500);

					// 2. enable
					// Plugin.instance().manager().handleCreate(project);
					monitor.worked(1);

				} catch (final Throwable e) {

					Plugin.log.error("Project update failure.", e);

				} finally {
					monitor.done();
				}

			}
		});

	}
}
