# Csa profiles converter  

[![MPL-2.0 License](https://img.shields.io/badge/license-MPL_2.0-blue.svg)](https://www.mozilla.org/en-US/MPL/2.0/)

Helper microservice for swe-csa-runner that exports a csa calculation logs from a rabbit queue to an attachment file or to minio 

## Build application

Application is using Maven as base build framework. Application is simply built with following command.

```bash
mvn install
```

## Build docker image

For building Docker image of the application, start by building application.

```bash
mvn install
```

Then build docker image

```bash
docker build -t farao/csa-logs-exporter .
```
