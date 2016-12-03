package tsinghua
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import java.io._
import it.unipd.dei.graphx.diameter.DiameterApproximation

/** TODO: cache and distribute
*/

object Dblp{
  def main(args: Array[String]) {

      val master = "spark://Macintosh-2.local:7077"
      val folder = "/Users/SunJc/Downloads/spark-2.0.2-bin-hadoop2.7"

      val sc = new SparkContext(master, "analysis",
      System.getenv("SPARK_HOME"), Seq(System.getenv("SPARK_TEST_JAR")))

      sc.addJar(folder+"/simple-project_2.11-1.0.jar")

      // begin script
      val whereami = System.getProperty("user.dir")

      val edgeFile = "data/graphx/mergedEdges.txt"
      val nodeFile = "data/graphx/mergedNodes.txt"
      //val edgeFile = "data/graphx/followers.txt"
      //val nodeFile = "data/graphx/users.txt"

      // Load the edges as a graph
      val graph = GraphLoader.edgeListFile(sc, edgeFile)

      // compute diameter
      val graphWithCC = graph.mapEdges(x=>1.0).connectedComponents()
      val ccID = graphWithCC.vertices.map{case (_,x)=>x}.distinct
      val CCs = ccID.collect.map{ case id => graphWithCC.subgraph( (_=>true) , (_,x)=> x==id ) } // lost
      val diameters = CCs.map( DiameterApproximation.run(_))

      // print diameters
      val file1 = new File(whereami + "/data/graphx/dblpDiameters.txt")
      val bw1 = new BufferedWriter(new FileWriter(file1))
      bw1.write("diameter,one node of the connected component\n")
      bw1.write(diameters.zip(CCs.map(_.vertices.min)).mkString("\n"))
      bw1.close()

      // Run PageRank
      val ranks = graph.pageRank(0.0001).vertices
      // Join the ranks with the usernames
      val persons = sc.textFile(nodeFile).map { line => val fields = line.split(",") ;(fields(0).toLong, fields(1)) }
      val ranksByPersonname = persons.join(ranks).map { case (id, (personName, rank)) => (personName, rank) }

      // Print the result
      val file2 = new File(whereami + "/data/graphx/dblpRanks.txt")
      val bw2 = new BufferedWriter(new FileWriter(file2))
      bw2.write(ranksByPersonname.collect().mkString("\n"))
      bw2.close()
    }
  }
