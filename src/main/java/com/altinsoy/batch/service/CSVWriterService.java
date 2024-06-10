package com.altinsoy.batch.service;

import com.altinsoy.batch.entity.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class CSVWriterService {
    private static final int THREAD_POOL_SIZE = 10;

    public byte[] writeUsersToCSV(List<User> users) throws IOException, InterruptedException {
        StringWriter stringWriter = new StringWriter();

        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader("name", "surname"))) {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            for (User user : users) {
                executor.submit(() -> {
                    try {
                        synchronized (csvPrinter) {
                            csvPrinter.printRecord(user.getName(), user.getSurname());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

//            for (User user : users) {
//
//                csvPrinter.printRecord(user.getId(), user.getName(), user.getSurname());
//            }


            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            saveCSVToFile(stringWriter.toString());
        }

        return stringWriter.toString().getBytes();
    }

    private void saveCSVToFile(String csvData) throws IOException {
        String filePath = "src/main/resources/users.csv";
        File file = new File(filePath);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(csvData);
        }
    }
}
