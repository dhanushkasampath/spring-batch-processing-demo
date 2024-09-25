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

