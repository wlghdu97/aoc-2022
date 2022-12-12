import java.util.*

private class Graph constructor(
    private val width: Int,
    private val height: Int,
    private val startPos: Position,
    private val endPos: Position,
    private val heightArray: Array<IntArray>
) : Iterable<Int> {
    private val edges = hashMapOf<Int, MutableSet<Int>>()

    init {
        require(heightArray.size == height && heightArray.all { it.size == width })
        calculateEdges()
    }

    fun calculateEdges() {
        heightArray.forEachIndexed { y, row ->
            row.forEachIndexed { x, h ->
                val source = nodeIndexFor(x, y)
                val edges = edges[source] ?: mutableSetOf()

                if ((y - 1) >= 0 && heightArray[y - 1][x] - 1 <= h) {
                    edges += nodeIndexFor(x, y - 1)
                }
                if ((y + 1) < height && heightArray[y + 1][x] - 1 <= h) {
                    edges += nodeIndexFor(x, y + 1)
                }
                if ((x - 1) >= 0 && heightArray[y][x - 1] - 1 <= h) {
                    edges += nodeIndexFor(x - 1, y)
                }
                if ((x + 1) < width && heightArray[y][x + 1] - 1 <= h) {
                    edges += nodeIndexFor(x + 1, y)
                }

                this.edges[source] = edges
            }
        }
    }

    fun dijkstra(start: Int, end: Int = nodeIndexFor(endPos.x, endPos.y)): Int {
        return dijkstra(Position(start % width, start / width), Position(end % width, end / width))
    }

    fun dijkstra(start: Position = startPos, end: Position = endPos): Int {
        val size = width * height
        val dist = IntArray(size) { INFINITY }
        val prev = IntArray(size) { UNDEFINED }
        val queue: Queue<Int> = LinkedList((0 until size).toList())
        dist[nodeIndexFor(start.x, start.y)] = 0

        while (queue.isNotEmpty()) {
            val u = queue.minBy { dist[it] }
            queue.remove(u)

            for (neighbor in edges[u] ?: emptySet()) {
                if (!queue.contains(neighbor)) {
                    continue
                }
                val alt = dist[u] + 1
                if (alt < dist[neighbor]) {
                    dist[neighbor] = alt
                    prev[neighbor] = u
                }
            }
        }

        return dist[nodeIndexFor(end.x, end.y)]
    }

    override fun iterator(): Iterator<Int> {
        return heightArray.flatMap { it.asIterable() }.iterator()
    }

    private fun nodeIndexFor(x: Int, y: Int): Int {
        return x + (y * width)
    }

    data class Position(val x: Int, val y: Int)

    companion object {
        private const val INFINITY = Int.MAX_VALUE
        private const val UNDEFINED = -1
    }
}

/**
 * a : 0, z :25
 */
private fun parseGraph(input: List<String>): Graph {
    require(input.isNotEmpty())

    val width = input.first().length
    val height = input.size
    val heightArray = Array(height) { IntArray(width) { -1 } }
    var startPos: Graph.Position? = null
    var endPos: Graph.Position? = null
    input.forEachIndexed { y, str ->
        str.toCharArray().forEachIndexed { x, c ->
            when (c) {
                'S' -> {
                    startPos = Graph.Position(x, y)
                    heightArray[y][x] = 0
                }

                'E' -> {
                    endPos = Graph.Position(x, y)
                    heightArray[y][x] = 25
                }

                in 'a'..'z' -> {
                    heightArray[y][x] = c.code - 97
                }
            }
        }
    }

    return Graph(width, height, startPos!!, endPos!!, heightArray)
}

fun main() {
    fun part1(input: List<String>): Int {
        return parseGraph(input).dijkstra()
    }

    fun part2(input: List<String>): Int {
        val graph = parseGraph(input)
        val startingPointCandidateIndices = graph.mapIndexedNotNull { index, height ->
            if (height == 0) {
                index
            } else {
                null
            }
        }
        return startingPointCandidateIndices.map { graph.dijkstra(it) }
            .filter { it > 0 }
            .min()
    }

    val testInput = readInput("Day12-Test01")
    val input = readInput("Day12")
    println(part1(testInput))
    println(part1(input))
    println(part2(testInput))
    println(part2(input))
}
