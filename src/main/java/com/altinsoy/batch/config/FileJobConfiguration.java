package com.altinsoy.batch.config;

import com.altinsoy.batch.job.FileJob;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileJobConfiguration {

    @Bean
    public Job job(JobRepository jobRepository) {
        return new FileJob(jobRepository);
    }
}
