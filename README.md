Running a simple Scala script for Spark
=======================================
This repository provides an example for wriring Scala scripts for Spark quickly. Scripts can then be run locally or  tested using the interactive spark console. This example is *not* about setting up or deploying Spark for production.

This repository contains an example Scala script and describes how to run a Spark query on a Docker image. It fetches zipped json data from an AWS S3 bucket, performs a simple group by on a Spark `DataFrame` and writes the results to the local file system.

1. Setup up a stand-alone Spark instance
----------------------------------------
Install the [docker-spark Docker image](https://github.com/sequenceiq/docker-spark) by sequenceiq and build the container. The Docker image uses [YARN](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/YARN.html) (Apache Hadoop NextGen MapReduce 2.0) as  Hadoop ResrouceManager.

```bash
# pull image
docker pull sequenceiq/spark:1.3.0

# build
docker build --rm -t sequenceiq/spark:1.3.0 .
```

2. Write Scala script
---------------------
Locally install Scala (follow [official instructions](http://www.scala-lang.org/download/))
and the [Scala build tool (sbt)](http://www.scala-sbt.org/) for building scala jars. 
Sbt will automatically pull the Spark libaries specified in the `simple.sbt` configuration file.

Replicate the file structure given in this repository and build the jar.

```bash
sbt package
```
The jar file will be generated as `target/scala-2.10/simple-project_2.10-1.0.jar`


3. Make jar file available in docker container
----------------------------------------------
To make the generated fat jar file available in the docker container,
lauch docker make a shared local directory available as with the volume
option. Launch a bash on docker as follows.

```bash
docker run -i -t -v ~/projects/spark:/var/spark:rw -h sandbox sequenceiq/spark:1.3.0 bash
```

This makes the local `~/projects/spark` directory available as `/var/spark` in the container.

4. Run spark
------------
To run the spark interactively in the Docker container, simply run the
following in the container.

```bash
spark-shell --master yarn-client --driver-memory 1g --executor-memory 1g --executor-cores 1
```

For the interactive shell you need to pass in AWS credentials as
envirionment variables by exporting them or by setting them in for the bash command:

```bash
AWS_ACCESSKEY_ID=your-access-id AWS_SECRET_ACCESS_KEY=your-secret-key spark-shell --master yarn-client --driver-memory 1g --executor-memory 1g --executor-cores 1
```

To submit the script as a Spark job, run the following in the mounted directory.

```bash
spark-submit --class "SimpleApp"  --master yarn-client --driver-memory 1g --executor-memory 1g --executor-cores 1 target/scala-2.10/simple-project_2.10-1.0.jar
```
