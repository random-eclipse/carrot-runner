package com.carrotgarden.eclipse.space.util;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.swt.widgets.Display;

/**
 */
public class JobUtil {

	/**
	 * Schedule this job with an appropriate default scheduling rule to avoid
	 * concurrency issues. This scheduling is safe, but it is very restrictive,
	 * so if it can be relaxed, and you care about this then don't use this
	 * method. Instead use the schedule method that allows specifying an
	 * explicit rule.
	 */
	public static Job schedule(final EclipseRunnable runable) {
		final Job job = runable.asJob();
		schedule(job, buildRule());
		return job;
	}

	/**
	 * Schedule a piece of work without attaching any scheduling rule to the
	 * Job.
	 * <p>
	 * Be careful when using this method of running tasks because it provides no
	 * guarantees whatsoever about what other things may be executing
	 * concurrently.
	 */
	public static void schedule(final ISchedulingRule rule,
			final EclipseRunnable runable) {
		schedule(runable.asJob(), rule);
	}

	/**
	 * Schedule this job as a "workspace job" (atomic operation, so only one
	 * resource delta generated for whole job).
	 */
	public static void workspaceJob(final EclipseRunnable runable) {
		final Job job = runable.asWorkspaceJob();
		schedule(job);
	}

	/**
	 * Schedule as a "user" job. (Normally this means that the job will popup a
	 * dialog to show progress. But the user can send the job into background if
	 * they so wish.
	 * <p>
	 * This is "normal" behavior, but this behavior is not guaranteed. It can be
	 * changed by user preferences, or because other circumstances (e.g. the
	 * dialog will not pop, in the context of another modal dialog because
	 * Eclipse has a kind of "design rule" not to pop dialogs on top of
	 * eachother if it can be avoided.)
	 * 
	 * @return
	 */
	public static Job userJob(final EclipseRunnable runable) {
		final Job job = runable.asJob();
		job.setUser(true);
		schedule(job);
		return job;
	}

	private static void schedule(final Job job, final ISchedulingRule rule) {
		job.setPriority(Job.BUILD);
		job.setRule(rule);
		job.schedule();
	}

	private static void schedule(final Job job) {
		schedule(job, buildRule());
	}

	/**
	 * Scheduling rule that conflicts only with itself and only contains itself.
	 * GradleRunnables that want to have a 'light' impact on blocking other jobs
	 * but still some guarantee that they won't trample over other
	 * GradleRunnable's should use this rule.
	 */
	public static final ISchedulingRule LIGHT_RULE = lightRule("LIGHT_RULE");

	/**
	 * Create a scheduling rule that conflicts only with itself and only
	 * contains itself. GradleRunnables that want to have a 'light' impact on
	 * blocking other jobs but still some guarantee that they won't trample over
	 * other things that require access to some internal shared resource that
	 * only they can access should use this rule to protect the resource.
	 */
	public static ISchedulingRule lightRule(final String name) {
		return new ISchedulingRule() {
			@Override
			public boolean contains(final ISchedulingRule rule) {
				return rule == this;
			}

			@Override
			public boolean isConflicting(final ISchedulingRule rule) {
				return rule == this || rule.contains(this);
			}

			@Override
			public String toString() {
				return name;
			};
		};
	}

	/**
	 * Value used to indicate we don't want to use a scheduling rule at all.
	 * GradleRunnable's scheduled with this 'rule' are completely unconstrained
	 * and can run concurrently with anything else.
	 */
	public static final ISchedulingRule NO_RULE = null;

	/**
	 * Join a job (wait for it to complete). If the job is null, the method
	 * returns immediately. (This assumes that things returning jobs to join,
	 * may, on occasion, return null if there's was nothing to do and so there
	 * is nothing to wair for).
	 */
	public static void join(final Job job) {
		if (job != null) {
			boolean done = false;
			do {
				try {
					job.join();
					done = true;
				} catch (final InterruptedException e) {
				}
			} while (!done);
		}
	}

	public static void checkCanceled(final IProgressMonitor monitor)
			throws OperationCanceledException {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	/**
	 * Sleep method that puts the current thread to sleep for some time and then
	 * returns. This method should be safe to call from UI or non-ui threads.
	 * <p>
	 * When called from UI threads it will ensure the UI event queue keeps
	 * getting processed while sleeping.
	 */
	public static void sleep() {
		final Display display = Display.getCurrent();
		if (display != null) {
			// We are running in UI thread
			display.readAndDispatch();
			display.sleep();
		} else {
			try {
				// We are not running in UI thread
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
			}
		}
	}

	/**
	 * What all Gradle related jobs should use instead of 'buildRule'. This is
	 * buildRule, but augmented so it also contains the LIGHT_RULE.
	 */
	public static ISchedulingRule buildRule() {
		return new MultiRule(new ISchedulingRule[] {
				ResourcesPlugin.getWorkspace().getRuleFactory().buildRule(),
				LIGHT_RULE });
	}

	// public static Joinable<Void> toJoinable(final Job job) {
	// return new Joinable<Void>() {
	// public Void join() throws Exception {
	// job.join();
	// return null;
	// }
	// };
	// }

	public static void withRule(final ISchedulingRule rule,
			IProgressMonitor mon, final int ticks, final EclipseRunnable runnable)
			throws Exception {
		mon = new SubProgressMonitor(mon, ticks);
		mon.beginTask(runnable.jobName, ticks);
		try {
			Job.getJobManager().beginRule(rule, mon);
			try {
				runnable.run(mon);
			} finally {
				Job.getJobManager().endRule(rule);
			}
		} finally {
			mon.done();
		}
	}

}
