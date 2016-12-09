package tsinghua
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._

import java.io._
import it.unipd.dei.graphx.diameter.DiameterApproximation


object Dblp{
  def main(args: Array[String]) {

      val master = "spark://localhost:7077"
      val folder = "/data/guojianbo/spark-2.0.2-bin-hadoop2.7"
      //val master = "spark://Macintosh-2.local:7077"
      //val folder = "/Users/SunJc/Downloads/spark-2.0.2-bin-hadoop2.7"

      val sc = new SparkContext(master, "analysis",
      System.getenv("SPARK_HOME"), Seq(System.getenv("SPARK_TEST_JAR")))

      sc.addJar(folder+"/simple-project_2.11-1.0.jar")

      val DEBUG = 0

      val whereami = System.getProperty("user.dir")


      if (DEBUG != 0){
            val resDir = whereami + "/data/graphx/sample"
            deblpAnalysis(resDir,sc)

      }else{
            val d = new File(whereami+"/data/graphx/evolving")
            val dir = d.listFiles.filter(_.isDirectory).map(_.toString).toList
            val dirs = dir //sc.parallelize(dir)
            dirs.foreach(d => deblpAnalysis(d,sc))
      }


    }

    def deblpAnalysis(resultDir: String,sc: SparkContext): Unit ={

      val edgeFile = resultDir + "/mergedEdges.txt"
      val nodeFile = resultDir + "/mergedNodes.txt"

      //begin script

      // Load the edges as a graph
      val graph = GraphLoader.edgeListFile(sc, edgeFile)

      graph.cache()

      // write degrees(undirected graph, inDegree as degree)
      val degrees = graph.outDegrees
      var file = new File(resultDir + "/dblpDegrees.txt")
      var bw = new BufferedWriter(new FileWriter(file))
      bw write "vertex,degree\n"
      bw write degrees.collect().map{case (v,d) => f"$v%s,$d%s"}.mkString("\n")
      bw.close()

      // compute asscociative coeffcient
      val degreesMap = graph.outDegrees.collectAsMap
      val edges = graph.edges.map{case Edge(s,t,w) => s->t}
      val edgeDegrees = edges map {case (s,t) => degreesMap.get(s).getOrElse(-100000000) -> degreesMap.get(t).getOrElse(-100000000)}
      val (a,b,c,m) = edgeDegrees.aggregate( (0.0,0.0,0.0,0) ) ({case ((h1,h2,h3,h4),(j,k)) => ( (h1 + j + k) , (h2 + j * j + k * k) , (h3+ (j+0.0) * k) , h4 +1) },{ case ((h1,h2,h3,h4),(k1,k2,k3,k4)) => (h1+k1,h2+k2,h3+k3,h4+k4)})
      val M_1 = 1.0 / m
      // asscio coeffcient = ( M^-1 * c - (M^-1 * 0.5 * a)^2 ) / ( M^-1 * 0.5 * b - (M^-1 * 0.5 * a)^2 )
      val D = (M_1 * 0.5 * a) * (M_1 * 0.5 * a)
      val ac = (M_1 * c - D) / ( M_1 * 0.5 * b - D)

      // print asscociative coeffcient
      file = new File(resultDir + "/dblpAsscociativeCoefficien.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write ac.toString
      bw.close()

      // Run PageRank
      val ranks = graph.pageRank(0.0001).vertices
      // Join the ranks with the usernames
      val persons = sc.textFile(nodeFile).map { line => val fields = line.split(",") ;(fields(0).toLong, fields(1)) }
      val ranksByPersonname = persons.join(ranks).map { case (id, (personName, rank)) => f"$personName%s,$rank%s" }

      // Print pagerank
      file = new File(resultDir + "/dblpRanks.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write "vertex,pageRank\n"
      bw.write(ranksByPersonname.collect().mkString("\n"))
      bw.close()

      // compute cluster coefficient
      val neighbors = graph.collectNeighborIds(EdgeDirection.In)
      val nMap = neighbors.collectAsMap
      val clusterCoeff = neighbors.map{case (v,n) => v -> n.map( n1 => n.filter( n2 => n2 != n1).map( n2 => nMap.get(n1).getOrElse(Array()).filter( n3 =>n2 ==  n3 ) )).flatten.flatten.length}.join(graph.outDegrees).map{case (v,(l,1)) => v -> 0.0 ; case (v,(l,n)) => v -> (l + 0.0) / (n*(n-1))}

      // print cluster coefficient
      file = new File(resultDir + "/dblpClusterCoeffient.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write "vertex,clusterCoeffient\n"
      bw write clusterCoeff.collect().map{case (v,c) => f"$v%s,$c%f"}.mkString("\n")
      bw.close()

      // print average cluster coefficient
      file = new File(resultDir + "/dblpAvgClusterCoeffient.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write clusterCoeff.map{case (v,c) => c}.mean().toString
      bw.close()

      /*// compute diameter
      val graphWithCC = graph.mapEdges(x=>1.0).connectedComponents()
      graphWithCC.cache()
      val ccID = graphWithCC.vertices.map{case (_,x)=>x}.distinct
      val CCs = ccID.collect.toList.map{ case id => id -> graphWithCC.subgraph( (_=>true) , (_,x)=> x==id ) } // no nesting rdd
      graphWithCC.unpersist()
      val diameters = CCs.map{case (id,sg) => id -> DiameterApproximation.run(sg)}

      // print diameters
      file = new File(resultDir + "/dblpDiameters.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw.write("diameter,nodesOfTheConnectedComponent\n")
      bw write sc.parallelize(diameters).join( sc.parallelize( CCs.map{case(id,sg) => id -> sg.vertices.map(_._1).collect().mkString(",")})).map{ case( id, (d,vs) ) => "" + d + "," + vs }.collect.mkString("\n")
      bw.close()

      // compute avg shortest path length
      val avgSPL = sc.parallelize( CCs.map{case (id, x) => ShortestPaths.run(x, x.vertices.map{case (_1,_2) => _1 }.collect())}.map(_.vertices.collect.toList.map{case (src,y) => y.toList.map{case (target,dist) => dist}.filter(_>0) }). flatten.flatten).mean()

      // print average shortest path length, excluding infinity
      file = new File(resultDir + "/dblpAvgSpl.txt")
      bw = new BufferedWriter(new FileWriter(file))
      bw write avgSPL.toString
      bw.close()
      */
    }
  }
