/**
 * 
 */
package com.nowgroup.kenan.batch;

import java.io.File;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

/**
 * @author dtorresf
 *
 */
public class DoneFileRenameTasklet implements Tasklet {
	private Resource[] resource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.
	 * springframework.batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		for (int i = 0; i < resource.length; i++) {
			File f = resource[i].getFile();
			File dest = new File(f.getCanonicalPath() + ".done");
			f.renameTo(dest);
		}
		return RepeatStatus.FINISHED;
	}

	public Resource[] getResource() {
		return resource;
	}

	public void setResource(Resource[] resource) {
		this.resource = resource;
	}

}
