package tsinghua
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._

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
      val DEBUG = 0

      val whereami = System.getProperty("user.dir")


      if (DEBUG != 0){
            val resDir = whereami + "/data/graphx/sample"
            deblpAnalysis(resDir)

      }else{
            val d = new File(whereami+"/data/graphx/evolving")
            val dir = d.listFiles.filter(_.isDirectory).toList
            val dirs = sc.parallelize(dir)
            dirs.foreach(d => deblpAnalysis(d))
      }

      
    }

    def deblpAnalysis(resultDir: string): Unit ={

      //
      val edgeFile = resultDir + "/mergedEdges.txt"
      val nodeFile = resultDir + "/mergedNodes.txt"

      // Load the edges as a graph
      val graph = GraphLoader.edgeListFile(sc, edgeFile)

      // compute diameter
      val graphWithCC = graph.mapEdges(x=>1.0).connectedComponents()
      val ccID = graphWithCC.vertices.map{case (_,x)=>x}.distinct
      val CCs = ccID.collect.map{ case id => graphWithCC.subgraph( (_=>true) , (_,x)=> x==id ) } // lost
      val diameters = CCs.map( DiameterApproximation.run(_))

      // print diameters
      var file = new File(resultDir + "/dblpDiameters.txt")
      var bw = new BufferedWriter(new FileWriter(file))
      bw.write("diameter,nodesOfTheConnectedComponent\n")
      bw write diameters.zip(CCs.map(x=> (x.vertices.map{case (_1,_2) =>_1 })).toList).map{case(x,y) => "" + x + ","+y.collect.mkString(",")}.toList.mkString("\n")
      bw.close()

      // write degrees(undirected graph, inDegree as degree)
      val degrees = graph.outDegrees
      file = new File(resultDir + "/dblpDegrees.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write "vertex,degree\n"
      bw write degrees.collect().map{case (v,d) => f"$v%s,$d%s"}.mkString("\n")
      bw.close()

      // compute cluster coefficient
      val neighbors = graph.collectNeighborIds(EdgeDirection.In)
      val nMap = neighbors.collectAsMap
      val clusterCoeff = neighbors.map{case (v,n) => v -> n.map( n1 => n.filter( n2 => n2 != n1).map( n2 => nMap.get(n1).getOrElse(Array()).filter( n3 =>n2 ==  n3 ) )).flatten.flatten.length}.
            join(graph.outDegrees).map{case (v,(l,1)) => v -> 0.0 ; case (v,(l,n)) => v -> (l + 0.0) / (n*(n-1))}

      // print cluster coefficient
      file = new File(resDir + "/dblpClusterCoeffient.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write "vertex,clusterCoeffient\n"
      bw write clusterCoeff.collect().map{case (v,c) => f"$v%s,$c%f"}.mkString("\n")
      bw.close()

      // print average cluster coefficient
      file = new File(resDir + "/dblpAvgClusterCoeffient.txt")
      bw = new BufferedWriter(new FileWriter(file))
      val (totalC,n) = clusterCoeff.collect().map{case (v,c) => c}.foldLeft((0.0,0))( (s,n) => (s._1 +n , s._2 + 1 ) ) 
      bw write (totalC/n).toString
      bw.close()

      // compute avg shortest path length
      val (spls,num) = CCs.map(x=> ShortestPaths.run(x, x.vertices.map{case (_1,_2) => _1 }.collect())).toList.
            map(_.vertices.collect.toList.map{case (src,y) => y.toList.map{case (target,dist) => dist} }).
            flatten.flatten.foldLeft((0.0,0))( (s,n) => (s._1 +n , s._2 + 1 ) ) 
      val avgSPL = spls/(num-graph.vertices.count)

      // print average shortest path length, excluding infinity
      file = new File(resDir + "/dblpAvgSpl.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write avgSPL.toString
      bw.close()


      // compute asscociative coeffcient
      val degreesMap = graph.outDegrees.collectAsMap
      val edges = graph.edges.map{case Edge(s,t,w) => s->t}
      val edgeDegrees = edges map {case (s,t) => degreesMap.get(s).getOrElse(-100000000) -> degreesMap.get(t).getOrElse(-100000000)}
      val (a,b,c) = edgeDegrees.collect.toList.foldLeft( (0.0,0.0,0.0) ) { case ((h1,h2,h3),(j,k)) => ( (h1 + j + k) , (h2 + j * j + k * k) , (h3+ (j+0.0) * k)) }
      val M = edges.collect.length
      val M_1 = 1.0 / M
      // asscio coeffcient = ( M^-1 * c - (M^-1 * 0.5 * a)^2 ) / ( M^-1 * 0.5 * b - (M^-1 * 0.5 * a)^2 )
      val D = (M_1 * 0.5 * a) * (M_1 * 0.5 * a)
      val ac = (M_1 * c - D) / ( M_1 * 0.5 * b - D)

      // print asscociative coeffcient
      file = new File(resDir + "/dblpAsscociativeCoefficien.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write ac.toString
      bw.close()

      // Run PageRank
      val ranks = graph.pageRank(0.0001).vertices
      // Join the ranks with the usernames
      val persons = sc.textFile(nodeFile).map { line => val fields = line.split(",") ;(fields(0).toLong, fields(1)) }
      val ranksByPersonname = persons.join(ranks).map { case (id, (personName, rank)) => f"$personName%s,$rank%s" }

      // Print the result
      file = new File(resDir + "/dblpRanks.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write "vertex,pageRank\n"
      bw.write(ranksByPersonname.collect().mkString("\n"))
      bw.close()
    }
  }
