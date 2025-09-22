import sbt._

object Dependencies {
  lazy val munit = "org.scalameta" %% "munit" % "0.7.29"
  val sparkCore = "org.apache.spark" %% "spark-core" % "3.3.0"
  val sparkSQL  = "org.apache.spark" %% "spark-sql"  % "3.3.0"
  val tess4j = "net.sourceforge.tess4j" % "tess4j" % "5.13.0"
}
