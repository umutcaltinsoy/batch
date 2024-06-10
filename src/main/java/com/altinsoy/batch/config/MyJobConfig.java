package com.altinsoy.batch.config;


import com.altinsoy.batch.dto.UserDto;
import com.altinsoy.batch.entity.User;
import com.altinsoy.batch.tasklets.FileDeletionTasklet;
import com.altinsoy.batch.tasklets.FilePreparationTasklet;
import com.altinsoy.batch.tasklets.UserDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class MyJobConfig {

    @Bean
    public Job billingJob(JobRepository jobRepository, Step step1, Step step2, Step step3, Step step4) {
        return new JobBuilder("BillingJob", jobRepository)
                .start(step1)
//                .start(step2)
                .next(step2)
                .next(step3)
                .next(step4)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("filePreparation", jobRepository)
                .tasklet(new FilePreparationTasklet(), transactionManager)
                .build();
    }

//    @Bean
//    public JdbcBatchItemWriter<UserDto> billingDataTableWriter(DataSource dataSource) {
//        String sql = "INSERT INTO USERS (name, surname) VALUES (:name, :surname)";
//        System.out.println("sql : " + sql);
//        return new JdbcBatchItemWriterBuilder<UserDto>()
//                .dataSource(dataSource)
//                .sql(sql)
//                .beanMapped()
//                .build();
//    }
//
//    @Bean
//    public Step step2(
//            JobRepository jobRepository, PlatformTransactionManager transactionManager,
//            ItemReader<UserDto> billingDataFileReader, ItemWriter<UserDto> billingDataTableWriter) {
//        return new StepBuilder("fileIngestion", jobRepository)
//                .<UserDto, UserDto>chunk(50, transactionManager)
//                .reader(billingDataFileReader)
//                .writer(billingDataTableWriter)
//                .build();
//    }
//
//    @Bean
//    public FlatFileItemReader<UserDto> billingDataFileReader() {
//        log.info("Initializing FlatFileItemReader for billing data");
//
//        FileSystemResource resource = new FileSystemResource("staging/users.csv");
//        log.info("Reading file from path: {}", resource.getPath());
//
//        FlatFileItemReader<UserDto> reader = new FlatFileItemReader<>();
//        reader.setResource(resource);
//        reader.setLinesToSkip(1); // Skip the header line
//        reader.setLineMapper(new DefaultLineMapper<UserDto>() {{
//            setLineTokenizer(new DelimitedLineTokenizer() {{
//                setNames("name", "surname");
//            }});
//            setFieldSetMapper(new BeanWrapperFieldSetMapper<UserDto>() {{
//                setTargetType(UserDto.class);
//            }});
//        }});
//
//        log.info("FlatFileItemReader initialized successfully");
//        return reader;
//    }

    // Using for insert all the data from excel to DB.
    @Bean
    public JdbcBatchItemWriter<User> billingDataTableWriter(DataSource dataSource) {
        String sql = "INSERT INTO USERS (name, surname) VALUES (:name, :surname)";
        System.out.println("sql : " + sql);
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    // Using for copy .csv file from resource to staging.
    @Bean
    public Step step2(
            JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ItemReader<User> billingDataFileReader, ItemWriter<User> billingDataTableWriter) {
        return new StepBuilder("fileIngestion", jobRepository)
                .<User, User>chunk(500, transactionManager)
                .reader(billingDataFileReader)
                .writer(billingDataTableWriter)
//                .taskExecutor(taskExecutor())
                .build();
    }


    // Using for copy .csv file from resource to staging.
    @Bean
    public FlatFileItemReader<User> billingDataFileReader() {
        log.info("Initializing FlatFileItemReader for billing data");

        FileSystemResource resource = new FileSystemResource("staging/users.csv");
        log.info("Reading file from path: {}", resource.getPath());

        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(resource);
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<User>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("name", "surname");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                setTargetType(User.class);
            }});
        }});

        log.info("FlatFileItemReader initialized successfully");
        return reader;
    }

    @Bean
    public JdbcCursorItemReader<User> userDataTableReader(DataSource dataSource) {
        String sql = "select * from USERS";
        return new JdbcCursorItemReaderBuilder<User>()
                .name("userDataTableReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(new DataClassRowMapper<>(User.class))
                .build();
    }

    @Bean
    public UserDataProcessor billingDataProcessor() {
        return new UserDataProcessor();
    }

    @Bean
    public FlatFileItemWriter<UserDto> userDataFileWriter() {
        return new FlatFileItemWriterBuilder<UserDto>()
                .resource(new FileSystemResource("staging/users_process.csv"))
                .name("userDataFileWriter")
                .delimited()
                .names("user.id", "user.name", "user.surname")
                .headerCallback(writer -> writer.write("id,name,surname"))
                .build();
    }

    // To process data from DB with given condition
    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      ItemReader<User> userDataTableReader,
                      ItemProcessor<User, UserDto> userDataProcessor,
                      ItemWriter<UserDto> userDataFileWriter) {
        return new StepBuilder("reportGeneration", jobRepository)
                .<User, UserDto>chunk(100, transactionManager)
                .reader(userDataTableReader)
                .processor(userDataProcessor)
                .writer(userDataFileWriter)
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("fileRemoving", jobRepository)
                .tasklet(new FileDeletionTasklet(), transactionManager)
                .build();
    }
}
