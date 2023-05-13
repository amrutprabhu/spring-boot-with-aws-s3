# Spring Boot with AWS S3 

Here I explore how we can communicate with AWS S3 bucket using Spring Cloud AWS. 

For running the application locally we will use LocalStack. We will run LocalStack using docker compose. 

Also we look into writing integration test to make sure the whole setup works all fine.

### AWS commands to communicate with LocalStack 
Create S3 bucket in localstack
```shell
aws s3api create-bucket \
--bucket mybucket \
--endpoint-url http://localhost:4566 \
--create-bucket-configuration LocationConstraint=eu-central-1 \
--profile ltest 
```
Copy file to s3 bucket on localstack
```shell
aws s3 cp \
samplefile.txt \
s3://mybucket \
--endpoint-url http://localhost:4566 \
--profile ltest 
```

```shell
aws s3 ls s3://mybucket \
--endpoint-url http://localhost:4566 \
--profile ltest \
--region eu-central-1
```