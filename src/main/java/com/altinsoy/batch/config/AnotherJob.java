package com.altinsoy.batch.config;

import com.altinsoy.batch.tasklets.FilePreparationTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class AnotherJob {


    @Bean
    public Job filePrep(JobRepository jobRepository, Step filePreparationStep) {
        return new JobBuilder("FilePrep", jobRepository)
                .start(filePreparationStep)
                .build();
    }

    @Bean Step filePreparationStep(JobRepository jobRepository
    , PlatformTransactionManager transactionManager) {
        log.info("File prep starting");
        return new StepBuilder("filePrep", jobRepository)
                .tasklet(new FilePreparationTasklet(), transactionManager)
                .build();
    }
}
