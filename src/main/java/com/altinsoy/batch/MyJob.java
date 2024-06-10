//package com.altinsoy.batch;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Component
//@RequiredArgsConstructor
//public class MyJob implements Job {
//
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//
//
//    @Override
//    public String getName() {
//        return "MyJob";
//    }
//
//    @Override
//    public void execute(JobExecution execution) {
//        JobParameters jobParameters = execution.getJobParameters();
//        String inputFile = jobParameters.getString("input.file");
//        execution.setStatus(BatchStatus.COMPLETED);
//        execution.setExitStatus(ExitStatus.COMPLETED);
//        this.jobRepository.update(execution);
//    }
//
//
//    public Job job() {
//        return new JobBuilder("BillingJob", jobRepository)
//                .start(step1(jobRepository,transactionManager))
//                .build();
//    }
//
//    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new StepBuilder("filePreparation", jobRepository)
//                .tasklet(new FilePreparationTasklet(), transactionManager)
//                .build();
//    }
//}
