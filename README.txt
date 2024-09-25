There are some instances where we need to process millions of records faster(in a fraction of time)
in such cases we can use spring-batch

WHAT IS SPRING BATCH?
=====================
Spring batch is one of the core module of spring framework and using this spring batch you can create
robust batch processing system.

WHAT IS BATCH PROCESSING?
========================
Batch processing is a technique which processes data in a large group instead of a single element of data
where you can process a high volume of data with minimal human interaction.

WHEN TO USE
===========

1. When we want to transform huge number of data we must use this spring batch concept.
Eg-1:
pls refer img-1.png
Lets say we want to build a billing analysis system.
we need to insert data in csv file(source) to a database(destination).
If we do not use batch processing technique, we need to insert each an every row separately to database table.
it is a painful job.
For this kind of scenario we can use batch processing and it will save time and more efficient than typical way.

Eg-2:
pls refer img-1.png
Lets say every day we need to generate a csv(destination) report by fetching data from database(source).
This also can be done using batch processing technique.

In this repo we will demonstrate the 1st example
Here our requirement is to process a csv file and add it to database using spring batch

SPRING BATCH ARCHITECTURE
=========================

The first key component of Spring Batch architecture is "Job Launcher". it is an interface which is used to launch spring batch jobs
This also can be considered as the entry point to initiate any spring batch.
it has a method called "run()", who will trigger the "Job" object.

Once "Job Launcher" calls the run method, immediately it will create another component called "Job"
Job is the work to be executed. it can be a simple or complex task.

As soon as "Job Launcher" launches a "Job", it calls another component called "Job Repository". Job Repository helps to maintain
the state of the job. State management is an important aspect when processing large amount of data.

"Job" component calls another component called "Step" and it calls another 3 components as
1. ItemReader (read the data from source; CSV file)
2. ItemProcessor(process the read data)
3. ItemWriter (write the data to destination; Database)

A job can have multiple steps as in img-2.png.

This is the complete architecture of spring-batch

CODE IMPLEMENTATION
===================

1. create packages as config/controller/entity/repository

2. create a new class as Customer to persist data in CSV file

3. Create a new interface as CustomerRepository which extends JpaRepository to perform db related operations

4. Add data source related properties to application.yml file

5. copy the customers.csv file to resource directory

6. Configure the ItemReader/ItemProcessor/ItemWriter using a new class as SpringBatchConfig in config package
   6.1 To create the "Step", there is "StepBuilderFactory"
   6.2 To create the "Job", there is "JobBuilderFactory"
   6.3 In the ItemWriter we need to persist the data in database. For that we need the repository to be injected also
   6.4 Create the ---ItemReader-- bean inside the configuration class
   6.5 Create the ---ItemWriter-- bean inside the configuration class

7. Create a new class as "CustomerProcessor" in config package as the ---ItemProcessor---

8. Create an object of "Step" and pass the above created ItemReader, ItemProcessor, ItemWriter to it. (refer img-2.png)

9. Then pass that created "Step" object to "Job" Object

10. Then we need to pass the created job object into "Job Launcher"

11. Create a RestController as JobController in controller package and write a method in it to trigger the jobs

12. Call the created rest endpoint using postman.

curl --location 'http://localhost:8081/jobs/importCustomers'

Then we can see that it took nearly 6 seconds to insert all 1000 rows to database. It added data sequentially(img-3.png).
By default spring-batch is synchronous. Not Asynchronous.
But this is not what we expected. we, too, can insert data sequentially by following default data insertion.

So we need to ask spring-batch to execute data insertion concurrently.

13. Next we need to define a custom taskExecutor in SpringBatchConfig class. Then add that to step bean.

14. Clear the data in the table and re-run the application.

15. When the app starts we can see that there are some tables automatically added by spring boot(refer img-3.png)
to maintain the state and other information related to batch processing.

16. After introducing taskExecutor property to Step object, we can see that the time has reduced to 3s from 6s, when adding 1000 records to database.
Also, we can notice that there is no order of insertion data to database table. that means 10 threads have simultaneously added data to it

We don't have control over the threads. We don't know which thread will process which record.

If we want to get that control too, we can use Spring-Batch-Partitioning. In there we can specify thread-1 should process 1st 10 records,... as like that


EXAMPLE SCENARIO
================

Let's say we want to process only records where country is "United States".
In that case we can customize it at ItemProcessor(refer img-4.png).
After adding that if condition, restart the app and trigger the request. Then we can see that only records with country "United States"
have been persisted in the database.

As like this we can use Spring-Batch to get better performance in our application.


Special Note
============

In my local setup tables that spring-batch creates automatically did not get created.
So I had to execute the sql script in the README directory to create them manually
!!!!!!!!!!!!!!!!!!END!!!!!!!!!!!!!!!!!!


