package com.carrotgarden.eclipse.space.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;

import com.carrotgarden.eclipse.space.Nature;
import com.carrotgarden.eclipse.space.Plugin;

/**
 */
public class NatureUtil {

	/**
	 * Make sure a project has a number of required natures. If the natures are not
	 * yet present, add them now.
	 * 
	 * @throws CoreException
	 */
	public static void ensure(final IProject project, final IProgressMonitor monitor, final String... nextNaturesArray)
			throws CoreException {

		final IProjectDescription description = project.getDescription();

		final String[] pastNaturesArray = description.getNatureIds();

		final Set<String> natureSet = new LinkedHashSet<String>();

		for (final String nature : nextNaturesArray) {
			natureSet.add(nature);
		}
		for (final String nature : pastNaturesArray) {
			natureSet.add(nature);
		}

		description.setNatureIds(natureSet.toArray(new String[natureSet.size()]));

		// Plugin.logInfo("natureSet = " + natureSet);

		if (natureSet.size() > pastNaturesArray.length) {
			// Some natures got added
			project.setDescription(description, monitor);
		} else {
			// No new natures added, but need to set it to force desired
			// ordering
			project.setDescription(description, IResource.AVOID_NATURE_CONFIG, monitor);
		}
	}

	/**
	 * Removes a nature from a project. This does nothing if the project doesn't
	 * have the nature.
	 */
	public static void remove(final IProject project, final String natureId, final IProgressMonitor monintor)
			throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final String[] oldNaturesArr = desc.getNatureIds();
		final Set<String> natures = new LinkedHashSet<String>();
		for (final String n : oldNaturesArr) {
			if (!n.equals(natureId)) {
				natures.add(n);
			}
		}
		if (natures.size() != oldNaturesArr.length) {
			// Something removed
			desc.setNatureIds(natures.toArray(new String[natures.size()]));
			project.setDescription(desc, monintor);
		}

	}

	/**
	 * verify project has a nature
	 */
	public static boolean hasNature(final IProject project, final String natureId) {
		try {
			if (project == null) {
				return false;
			}
			if (!project.isOpen()) {
				return false;
			}
			return project.hasNature(natureId);
		} catch (final Throwable e) {
			Plugin.log.error("Nature failure.", e);
			return false;
		}
	}

	/**
	 * verify project has java nature
	 */
	public static boolean hasJavaNature(final IProject project) {
		return hasNature(project, JavaCore.NATURE_ID);
	}

	/**
	 * verify project has runner plugin nature
	 */
	public static boolean hasPluginNature(final IProject project) {
		return hasNature(project, Nature.NATURE_ID);
	}

}
