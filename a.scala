// Assume the SparkContext has already been constructed
val sc: SparkContext
// Load the edges as a graph
val graph = GraphLoader.edgeListFile(sc, "graphx/data/collaboration.txt")
// Run PageRank
val ranks = graph.pageRank(0.0001).vertices
// Join the ranks with the usernames
val persons = sc.textFile("graphx/data/persons.txt").map { line =>
  val fields = line.split(",")
  (fields(0).toLong, fields(1))
}
val ranksByPersonname = users.join(ranks).map {
  case (id, (personName, rank)) => (personName, rank)
}
// Print the result
println(ranksByUsername.collect().mkString("\n"))
