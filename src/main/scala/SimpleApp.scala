/* SimpleApp.scala */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql
import org.apache.spark.sql._

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "file:///usr/local/spark-1.3.0-bin-hadoop2.4/README.md" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    // handle AWS credentials
    // obviously, for real life code, don't hard code credentials
    val hadoopConf = sc.hadoopConfiguration
    hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    hadoopConf.set("fs.s3.awsAccessKeyId", "your-access-id")
    hadoopConf.set("fs.s3.awsSecretAccessKey", "your-secret-access-key")

    val path = "s3n://archive.babbel.com/events/2015/04/20/*"
    val dataFrame = sqlContext.jsonFile(path)

    var d1 = dataFrame.filter("name = 'lesson:started'").groupBy("content_uuid", "learn_language_alpha3", "title")
    var count = d1.count()
    count.toJSON.coalesce(1, true).saveAsTextFile("file:///data/results")
  }
}
