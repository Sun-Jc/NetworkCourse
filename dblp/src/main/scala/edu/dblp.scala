package edu
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import java.io._
//import it.unipd.dei.graphx.diameter.DiameterApproximation._

// To make some of the examples work we will also need RDD
//import org.apache.spark.rdd.RDD
// Assume the SparkContext has already been constructed

object Dblp{
  def main(args: Array[String]) {


      //val master = "spark://gateway:7077"
      //val folder = "/home/sunjc16/spark-2.0.2-bin-hadoop2.7"
      val master = "spark://Macintosh-2.local:7077"
      val folder = "/Users/SunJc/Downloads/spark-2.0.2-bin-hadoop2.7"

      val sc = new SparkContext(master, "analysis",
      System.getenv("SPARK_HOME"), Seq(System.getenv("SPARK_TEST_JAR")))


      sc.addJar(folder+"/simple-project_2.11-1.0.jar")

      val edgeFile = "data/graphx/mergedEdges.txt"
      val nodeFile = "data/graphx/mergedNodes.txt"
      //val edgeFile = "data/graphx/followers.txt"
      //val nodeFile = "data/graphx/users.txt"

      // Load the edges as a graph
      val graph = GraphLoader.edgeListFile(sc, edgeFile)

      val result = DiameterApproximation.run(graph)

      // Run PageRank

      val ranks = graph.pageRank(0.0001).vertices
      // Join the ranks with the usernames
      val persons = sc.textFile(nodeFile).map { line => val fields = line.split(",") ;(fields(0).toLong, fields(1)) }
      val ranksByPersonname = persons.join(ranks).map { case (id, (personName, rank)) => (personName, rank) }

      // Print the result


      //ranksByPersonname.repartition(1).saveAsTextFile("resultY")
      //println(ranksByPersonname.collect().mkString("\n"))
      val whereami = System.getProperty("user.dir")
      val file = new File(whereami + "/data/graphx/dblpRanks.txt")
      val bw = new BufferedWriter(new FileWriter(file))

      bw.write(result+"\n")

      bw.write(ranksByPersonname.collect().mkString("\n"))
      //bw.write("xx")
      bw.close()
    }
  }
