package com.diffplug.gradle.imagegrinder;

import java.io.Serializable;
import java.util.Random;

import org.gradle.api.Action;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.Incremental;

public abstract class ImageGrinderTaskApi {
	@Incremental
	@PathSensitive(PathSensitivity.RELATIVE)
	@InputDirectory
	public abstract DirectoryProperty getSrcDir();

	@OutputDirectory
	public abstract DirectoryProperty getDstDir();

	Action<Img<?>> grinder;

	public void grinder(Action<Img<?>> grinder) {
		this.grinder = grinder;
	}

	// up-to-date checking on the uncheckable `Action<Img<?>> grinder`
	@Input
	Serializable bumpThisNumberWhenTheGrinderChanges = new NeverUpToDateBetweenRuns();

	static class NeverUpToDateBetweenRuns extends LazyForwardingEquality<Integer> {
		private static final long serialVersionUID = 1L;
		private static final Random RANDOM = new Random();

		@Override
		protected Integer calculateState() throws Exception {
			return RANDOM.nextInt();
		}
	}

	public Serializable getBumpThisNumberWhenTheGrinderChanges() {
		return bumpThisNumberWhenTheGrinderChanges;
	}

	public void setBumpThisNumberWhenTheGrinderChanges(Serializable bumpThisNumberWhenTheGrinderChanges) {
		this.bumpThisNumberWhenTheGrinderChanges = bumpThisNumberWhenTheGrinderChanges;
	}
}
