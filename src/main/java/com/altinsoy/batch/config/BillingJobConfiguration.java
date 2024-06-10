package com.altinsoy.batch.config;

import com.altinsoy.batch.dto.BillingData;
import com.altinsoy.batch.dto.ReportingData;
import com.altinsoy.batch.exception.PricingException;
import com.altinsoy.batch.service.PricingService;
import com.altinsoy.batch.tasklets.FilePreparationTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BillingJobConfiguration {

    @Bean
    public Step step12(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("filePreparation", jobRepository)
                .tasklet(new FilePreparationTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Job myBillingJob(JobRepository jobRepository, Step step12, Step step22) {
        return new JobBuilder("myBillingJob", jobRepository)
                .start(step12)
                .next(step22)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<BillingData> billingTableWriter(DataSource dataSource) {
        String sql = "insert into BILLING_DATA values (:dataYear, :dataMonth, :accountId, :phoneNumber, :dataUsage, :callDuration, :smsCount)";
        return new JdbcBatchItemWriterBuilder<BillingData>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    public Step step22(
            JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ItemReader<BillingData> billingFileReader, ItemWriter<BillingData> billingTableWriter,
            BillingDataSkipListener skipListener) {
        return new StepBuilder("fileInges", jobRepository)
                .<BillingData, BillingData>chunk(100, transactionManager)
                .reader(billingFileReader)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skipLimit(10)
                .listener(skipListener)
                .writer(billingTableWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<BillingData> billingFileReader() {
        FileSystemResource resource = new FileSystemResource("src/main/resources/billing.csv");


        FlatFileItemReader<BillingData> reader = new FlatFileItemReader<>();
        reader.setResource(resource);
        reader.setLineMapper(new DefaultLineMapper<BillingData>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("dataYear", "dataMonth", "accountId", "phoneNumber", "dataUsage", "callDuration", "smsCount");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<BillingData>() {{
                setTargetType(BillingData.class);
            }});
        }});

        return reader;
    }

    @Bean
    @StepScope
    public BillingDataSkipListener skipListener() {
        return new BillingDataSkipListener("billing-skip.csv");
    }

    @Bean
    public PricingService pricingService() {
        return  new PricingService();
    }
    @Bean
    public BillingDataProcessor billingProcessor() {
        return new BillingDataProcessor(pricingService());
    }

    @Bean
    public Step step31(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      ItemReader<BillingData> billingDataTableReader,
                      ItemProcessor<BillingData, ReportingData> billingDataProcessor,
                      ItemWriter<ReportingData> billingDataFileWriter) {
        return new StepBuilder("reportGeneration", jobRepository)
                .<BillingData, ReportingData>chunk(100, transactionManager)
                .reader(billingDataTableReader)
                .processor(billingDataProcessor)
                .writer(billingDataFileWriter)
                .faultTolerant()
                .retry(PricingException.class)
                .retryLimit(100)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ReportingData> billingDataFileWriter() {
        return new FlatFileItemWriterBuilder<ReportingData>()
                .resource(new FileSystemResource("/staging/billing-retry.csv"))
                .name("billingDataFileWriter")
                .delimited()
                .names("billingData.dataYear", "billingData.dataMonth", "billingData.accountId", "billingData.phoneNumber", "billingData.dataUsage", "billingData.callDuration", "billingData.smsCount", "billingTotal")
                .build();
    }
}
