package com.altinsoy.batch.controller;

import com.altinsoy.batch.entity.User;
import com.altinsoy.batch.service.CSVWriterService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static com.altinsoy.batch.service.UserGenerator.generateUsers;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchController {

    private final CSVWriterService csvWriterService;
    private final JobLauncher jobLauncher;
    private final Job billingJob;
    private final Job filePrep;
    private final Job myBillingJob;
    private final JobRepository jobRepository;

    @GetMapping("/export-to-csv")
    public ResponseEntity<Resource> exportToCsv() {
        List<User> users = getUsersFromSomewhere();
        System.out.println(users.size());
        try {
            byte[] csvData = csvWriterService.writeUsersToCSV(users);
            ByteArrayResource resource = new ByteArrayResource(csvData);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .contentLength(csvData.length)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> getUsersFromSomewhere() {

        return generateUsers(50000);
    }


    @PostMapping("/save-csv")
    public void importCsvToDBJob(@RequestParam("input.file") String inputFile) {
        long startTime = System.currentTimeMillis();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input.file", inputFile)
//                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            jobLauncher.run(billingJob, jobParameters);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Metodun çalışma süresi: " + duration + " milisaniye");
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {

            String resourceDirAbsolutePath = new File("src/main/resources/").getAbsolutePath();

            // Ensure the target directory exists
            Path targetDirPath = Paths.get(resourceDirAbsolutePath);
            if (Files.notExists(targetDirPath)) {
                Files.createDirectories(targetDirPath);
            }

            // Save the file to the target directory
            String filePath = resourceDirAbsolutePath + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("input.file", filePath)
                    .addDate("timestamp", new Date())  // To ensure the job runs every time
                    .toJobParameters();


            jobLauncher.run(billingJob, jobParameters);

            return "File uploaded successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "File upload failed";
        }
    }


    @PostMapping("/filePrep")
    public void filePrep(@RequestParam("input.file") String inputFile) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input.file", inputFile)
                .toJobParameters();
        System.out.println("CURRENT JOB NAME : " + filePrep.getName());
        try {
            jobLauncher.run(filePrep, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }

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
