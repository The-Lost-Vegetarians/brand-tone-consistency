from pyspark.sql import SparkSession
spark = SparkSession.builder.appName("ToneAnalysis").getOrCreate()
print(spark)