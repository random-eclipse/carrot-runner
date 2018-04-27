package com.carrotgarden.eclipse.space.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;

import com.carrotgarden.eclipse.space.Nature;
import com.carrotgarden.eclipse.space.Plugin;
import com.carrotgarden.eclipse.space.util.EclipseRunnable;
import com.carrotgarden.eclipse.space.util.JobUtil;
import com.carrotgarden.eclipse.space.util.NatureUtil;

/**
 * @see plugin.xml
 */
public class NatureActivateAction extends BaseAction {

	public NatureActivateAction() {
	}

	@Override
	public void run(final IAction action) {

		final IProject project = getProject();

		JobUtil.schedule(new EclipseRunnable("Nature activate.") {
			@Override
			public void doit(final IProgressMonitor monitor)
					throws CoreException {

				monitor.beginTask("Nature activate: " + project, 2);

				try {

					// 1. nature
					NatureUtil.ensure(project, monitor, Nature.NATURE_ID,
							JavaCore.NATURE_ID);
					monitor.worked(1);

					Thread.sleep(500);

					// 2. config
					// Plugin.instance().manager().handleCreate(project);
					monitor.worked(1);

				} catch (final Throwable e) {

					Plugin.log.error("Nature activate failure.", e);

				} finally {
					monitor.done();
				}

			}
		});

	}
}
