import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.graphx._
import org.apache.spark.graphx.lib._
import java.io._
import it.unipd.dei.graphx.diameter.DiameterApproximation

// Assume the SparkContext has already been constructed

val DEBUG = 1

var whereami = System.getProperty("user.dir")

var edgeFile = "data/graphx/mergedEdges.txt"
var nodeFile = "data/graphx/mergedNodes.txt"

val resultDir = "data/graphx"
edgeFile = resultDir + "/mergedEdges.txt"
nodeFile = resultDir + "/mergedNodes.txt"

if (DEBUG != 0){ edgeFile = "data/graphx/simple.txt";  nodeFile = "data/graphx/users.txt" }

// Load the edges as a graph
val graph = GraphLoader.edgeListFile(sc, edgeFile)

graph.cache()


// whose neighbor?
val who = 20

// how many hops? 1, 2, 3

val k = 3

// start getting neighborhood

val neighbors = graph.collectNeighborIds(EdgeDirection.In)

neighbors.cache()

val n1 = neighbors.lookup(who).flatten.toSet

var n = n1 + who

if(k > 1){ var n2 = n1.foldLeft(Set[Long]()){case (h,v)=> h ++ neighbors.lookup(v).flatten.toSet}; n2 = n2 -- n; n = n2 ++ n; if(k > 2){var n3 = n2.foldLeft(Set[Long]()){case (h,v)=> h ++ neighbors.lookup(v).flatten.toSet}; n3 = n3 -- n; n = n3 ++ n } }

val sg = graph.subgraph( (_=>true) , ( (id,x) => n.contains(id) ))  // no nesting rdd

// write to csv
var file = new File(resultDir + f"/neighborhoodEdges$who.csv")
var bw = new BufferedWriter(new FileWriter(file))
bw write "Source,Target,Type\n"
bw write sg.edges.map{case Edge(a,b,_) => f"$a%s,$b,Directed"}.collect.mkString("\n")
bw.close()

val persons = sc.textFile(nodeFile).map { line => val fields = line.split(",") ;(fields(0).toLong, fields(1)) }
file = new File(resultDir + f"/neighborhoodNodes$who.csv")
bw = new BufferedWriter(new FileWriter(file))
bw write "Id,Label\n"
bw write sg.vertices.join(persons).map { case (id, (_, name)) => f"$id%s,$name%s" }.collect.mkString("\n")
bw.close()
