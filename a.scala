//import org.apache.spark.SparkContext
//import org.apache.spark._
import org.apache.spark.graphx._
// To make some of the examples work we will also need RDD
//import org.apache.spark.rdd.RDD
// Assume the SparkContext has already been constructed

// Load the edges as a graph
val graph = GraphLoader.edgeListFile(sc, "data/graphx/followers.txt")
// Run PageRank
val ranks = graph.pageRank(0.0001).vertices
// Join the ranks with the usernames
val persons = sc.textFile("data/graphx/users.txt").map { line => val fields = line.split(",") ;(fields(0).toLong, fields(1)) }
val ranksByPersonname = persons.join(ranks).map { case (id, (personName, rank)) => (personName, rank) }
// Print the result
println(ranksByPersonname.collect().mkString("\n"))
