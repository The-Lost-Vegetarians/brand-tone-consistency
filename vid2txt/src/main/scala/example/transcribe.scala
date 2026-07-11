package example

import scala.sys.process._
import java.io.File
import scala.util.Try

object transcribe {

  def runCmd(cmd: Seq[String]): Int = {
    println(s"Running: ${cmd.mkString(" ")}")
    Process(cmd).!
  }

  def readFile(path: String): String =
    Try(scala.io.Source.fromFile(path).mkString).getOrElse("")

  def main(args: Array[String]): Unit = {
    val videoFile      = "content/nikeAd.mp4"
    val whisperExe     = "whisper-bin-x64/Release/whisper-cli.exe"
    val modelPath      = "whisper-bin-x64/Release/ggml-large-v2.bin"
    val tempDir        = new File("Desktop/bdata/temp")

    // Create the temp directory if it doesn't exist
    if (!tempDir.exists()) tempDir.mkdirs()

    val audioFile      = new File(tempDir, "temp_audio.wav").getAbsolutePath
    val transcriptFile = new File(tempDir, "transcript_output").getAbsolutePath

    println(s"Video Input: $videoFile")
    println("---")

    val ffmpegCmd = Seq("ffmpeg-8.0-essentials_build/bin/ffmpeg.exe", "-y", "-i", videoFile, "-ar", "16000", "-ac", "1", "-f", "wav", audioFile)
    if (runCmd(ffmpegCmd) != 0) {
      System.err.println("FFmpeg execution failed.")
      sys.exit(1)
    }

    val whisperCmd = Seq(whisperExe, "-m", modelPath, "-f", audioFile, "-otxt", "-of", transcriptFile)
    if (runCmd(whisperCmd) != 0) {
      System.err.println("Whisper (whisper-cli.exe) failed. See STDERR above for details.")
      sys.exit(1)
    }
    
    val transcript = readFile(transcriptFile + ".txt")
    println("\n=== TRANSCRIPT ===\n")
    println(transcript)

    // Clean up
    new File(audioFile).delete()
    new File(transcriptFile + ".txt").delete()
    println("\nCleanup complete.")
  }
}



    

    