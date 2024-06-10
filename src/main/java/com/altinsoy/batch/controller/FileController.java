package com.altinsoy.batch.controller;

import com.altinsoy.batch.service.CSVWriterService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final CSVWriterService csvWriterService;
    private final JobLauncher jobLauncher;
    private final Job myBillingJob;


    @PostMapping("/save-myinput")
    public void myBillingJob(@RequestParam("input.file") String inputFile) {
        long startTime = System.currentTimeMillis();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input.file", inputFile)
//                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(myBillingJob, jobParameters);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Metodun çalışma süresi: " + duration + " milisaniye");
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }

}
