// package example

// import org.apache.spark.sql.SparkSession

// object sparktest {
//   def main(args: Array[String]): Unit = {
//     // Initialize SparkSession
//     val spark = SparkSession.builder
//       .appName("TestSpark")
//       .master("spark://spark-master:7077")
//       .getOrCreate()

//     import spark.implicits._  // Needed for Dataset operations

//     // HDFS input path
//     val inputPath = "hdfs://namenode:9000/user/root/test.txt"
//     // HDFS output path
//     val outputPath = "hdfs://namenode:9000/user/root/test_modified"

//     // Read text file from HDFS as Dataset[String]
//     val text = spark.read.textFile(inputPath)

//     // Append a string to each line
//     val modified = text.map(line => line + " output through spark. \nThis is cool.")

//     // Print modified text to console
//     modified.collect().foreach(println)

//     // Write modified text back to HDFS (as text file)
//     modified.write.mode("overwrite").text(outputPath)

//     // Stop SparkSession
//     spark.stop()
//   }
// }
