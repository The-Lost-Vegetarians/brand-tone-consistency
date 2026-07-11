package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.input.PortableDataStream
import scala.sys.process._
import java.io.File
import scala.util.Try
import java.io.ByteArrayInputStream
import java.nio.file.{Files, StandardCopyOption}
import org.apache.hadoop.fs.Path
import java.util.UUID

case class TranscriptionResult(brand: String, sourceFile: String, transcript: String, error: Option[String])

object DistributedTranscriber {

  /**
   * This is the new worker logic. Instead of a path, it now receives a
   * stream of data directly from Spark.
   *
   * @param hdfsPath The full HDFS path of the video file.
   * @param dataStream A Spark object that lets us read the file's content as a stream.
   * @return A TranscriptionResult case class.
   */
  def transcribeVideoFromStream(hdfsPath: String, dataStream: PortableDataStream): TranscriptionResult = {
    // Worker node configuration (same as before)
    val ffmpegExe  = "/usr/bin/ffmpeg"
    val whisperExe = "/opt/whisper.cpp/build/bin/main"
    val modelPath  = "/opt/whisper.cpp/models/ggml-base.en.bin"

    val tempDir = new File(s"/tmp/spark_job_${UUID.randomUUID().toString}")
    tempDir.mkdirs()

    val pathObject = new Path(hdfsPath)
    val localVideoFile = new File(tempDir, pathObject.getName)
    val audioFile      = new File(tempDir, "temp_audio.wav")
    val transcriptFile = new File(tempDir, "transcript_output")

    val brandName = pathObject.getParent.getName
    val sourceFileName = pathObject.getName

    try {
      // --- THE NEW, MORE ROBUST METHOD ---
      // Step 1: Stream the file content from Spark's PortableDataStream
      // directly to a local temporary file on the worker.
      println(s"Streaming $hdfsPath to local file ${localVideoFile.getAbsolutePath}")
      val inputStream = dataStream.open()
      Files.copy(inputStream, localVideoFile.toPath, StandardCopyOption.REPLACE_EXISTING)
      inputStream.close()
      // --- END OF NEW METHOD ---

      // Step 2 & 3 (FFmpeg and Whisper) are the same as before
      val ffmpegCmd = Seq(ffmpegExe, "-y", "-i", localVideoFile.getAbsolutePath, "-ar", "16000", "-ac", "1", "-c:a", "pcm_s16le", audioFile.getAbsolutePath)
      if (runCmd(ffmpegCmd) != 0) throw new Exception("FFmpeg command failed.")

      val whisperCmd = Seq(whisperExe, "-m", modelPath, "-f", audioFile.getAbsolutePath, "-otxt", "-of", transcriptFile.getAbsolutePath)
      if (runCmd(whisperCmd) != 0) throw new Exception("Whisper.cpp command failed.")

      val transcript = Try(scala.io.Source.fromFile(transcriptFile.getAbsolutePath + ".txt").mkString).getOrElse("Transcript file not found.")
      TranscriptionResult(brandName, sourceFileName, transcript.trim, None)

    } catch {
      case e: Exception =>
        TranscriptionResult(brandName, sourceFileName, "", Some(e.getMessage))
    } finally {
      deleteRecursively(tempDir)
    }
  }

  def runCmd(cmd: Seq[String]): Int = {
    val processLogger = ProcessLogger(_ => (), _ => ())
    (Process(cmd) #< new ByteArrayInputStream(Array.emptyByteArray)).!(processLogger)
  }

  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory) file.listFiles.foreach(deleteRecursively)
    if (file.exists) file.delete()
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Distributed Video Transcriber")
      .getOrCreate()

    // Use a glob pattern to find all video files. The '/*/*' will match
    // '/brand_name/video.mp4'.
    val hdfsFilesPath = "hdfs://namenode:9000/data/raw/videos/*/*.mp4"
    
    println(s"Searching for video files in: $hdfsFilesPath")

    // Use binaryFiles to create an RDD of (filePath, fileStream)
    // This is the key change that makes the process more robust.
    val filesRDD = spark.sparkContext.binaryFiles(hdfsFilesPath)
    
    if (filesRDD.isEmpty()) {
      println("No video files found. Exiting.")
      spark.stop()
      return
    }

    println(s"Found ${filesRDD.count()} video files to process.")

    // Map over the RDD, applying our new transcription function
    val resultsRDD = filesRDD.map { case (path, stream) => transcribeVideoFromStream(path, stream) }
    
    val allResults = resultsRDD.collect()

    println("\n" + "="*30 + " FINAL RESULTS " + "="*30)
    allResults.foreach { result =>
      println(s"Brand: ${result.brand}\tFile: ${result.sourceFile}")
      if (result.error.isEmpty) {
        println(s"Transcript: ${result.transcript}")
      } else {
        println(s"--> ERROR: ${result.error.get}")
      }
      println("-" * 80)
    }
    println("="*80)

    spark.stop()
  }
}