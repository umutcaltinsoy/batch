package com.altinsoy.batch.job;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobRepository;


public class FileJob implements Job {

    private JobRepository jobRepository;

    public FileJob(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public String getName() {
        return "FileJob";
    }

//    @Override
//    public void execute(JobExecution execution) {
//        System.out.println("Processing file...");
//        execution.setStatus(BatchStatus.COMPLETED);
//        execution.setExitStatus(ExitStatus.COMPLETED);
//
//        this.jobRepository.update(execution);
//    }

    @Override
    public void execute(JobExecution execution) {
        try {
            throw new Exception("Unable to process file...");
        } catch (Exception exception) {
            execution.addFailureException(exception);
            execution.setStatus(BatchStatus.COMPLETED);
            execution.setExitStatus(ExitStatus.FAILED.addExitDescription(exception.getMessage()));
        } finally {
            this.jobRepository.update(execution);
        }
    }
}
