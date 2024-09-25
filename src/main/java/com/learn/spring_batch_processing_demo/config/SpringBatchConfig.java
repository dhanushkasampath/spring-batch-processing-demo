package com.learn.spring_batch_processing_demo.config;

import com.learn.spring_batch_processing_demo.entity.Customer;
import com.learn.spring_batch_processing_demo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing  // this tells the spring boot, for this application we want to enable the batch processing
@AllArgsConstructor
public class SpringBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CustomerRepository customerRepository;

    // create the ItemReader bean
    @Bean
    public FlatFileItemReader<Customer> reader(){
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv")); // specify where is my csv file is located to read
        itemReader.setName("csvReader"); // give a name to our reader
        itemReader.setLinesToSkip(1); // ask itemReader not to read the first row, since it contains colum information.
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    // this specifies how to read the csv file and how to map the data in it to java objects
    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        // this lineTokenizer will read the csv file with given delimiter and header details
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        // as the next step we need to map above information to particular objects.
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        //now set both lineTokenizer and fieldSetMapper to line mapper
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    // create the ItemProcessor bean
    // by default beans are singleton. if needed we can change that using @Scope("prototype"))
    @Bean
    public CustomerProcessor processor(){
        return new CustomerProcessor();
    }

    // create the ItemWriter bean
    @Bean
    public RepositoryItemWriter<Customer> writer(){
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    /**
     * we want to process the data in a chunk.
     * chunk size is 10 means process 10 records at a time
     *
     * Here we are providing reader/writer and processor to step
     * @return
     */
    @Bean
    public Step step1(){ // a job can have multiple steps. So here we make this as step1
        return new StepBuilder("my-csv-step", jobRepository)
            .<Customer, Customer>chunk(10, transactionManager)  // Define chunk size and transaction manager
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    public Job runJob(){
        return new JobBuilder("exampleJob", jobRepository)
            .start(step1())  // Set the first step of the job
//             .next(step2()) // if there are some more steps, all of them should pass into this job
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10); // Since I have 1000 records, I want to have 10 threads executes parallel.
        return asyncTaskExecutor;
    }
}
