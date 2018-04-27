package com.carrotgarden.eclipse.space.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * wrapper for long running operations, provides a way to turn the operation
 * into different types of "runnables" that exist within eclipse
 */
public abstract class EclipseRunnable implements IRunnableWithProgress {

	protected String jobName = "Any Job " + generateId();
	private int jobCtr = 0;

	private synchronized int generateId() {
		return jobCtr++;
	}

	public EclipseRunnable(final String jobName) {
		this.jobName = jobName;
	}

	public abstract void doit(IProgressMonitor mon) throws Throwable;

	public Job asJob() {
		return new Job(jobName) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					doit(monitor);
					return Status.OK_STATUS;
				} catch (final Throwable e) {
					return ExceptionUtil.status(e);
				}
			}

			@Override
			public Job yieldRule(final IProgressMonitor monitor) {
				return null;
			}
		};
	}

	public WorkspaceJob asWorkspaceJob() {
		return new WorkspaceJob(jobName) {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				try {
					doit(monitor);
					return Status.OK_STATUS;
				} catch (final Throwable e) {
					return ExceptionUtil.status(e);
				}
			}
		};
	}

	/**
	 * 
	 */
	@Override
	public final void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			doit(monitor);
		} catch (final InterruptedException e) {
			throw e;
		} catch (final OperationCanceledException e) {
			throw new InterruptedException("Canceled by user");
		} catch (final InvocationTargetException e) {
			throw e;
		} catch (final Throwable e) {
			throw new InvocationTargetException(e);
		}
	}

	@Override
	public String toString() {
		return jobName;
	}

}
