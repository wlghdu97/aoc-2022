class Forest constructor(private val input: Array<IntArray>) {
    val size = input.size

    init {
        // check input is square
        assert(input.all { it.size == size })
    }

    operator fun get(x: Int, y: Int): Int {
        return input[x][y]
    }
}

fun Forest.visibleTreeFromOutsideCount(): Int {

    fun isVisible(targetX: Int, targetY: Int): Boolean {
        val height = this[targetX, targetY]

        val north = (0 until targetY).none { this[targetX, it] >= height }
        val south = (targetY + 1 until size).none { this[targetX, it] >= height }
        val west = (0 until targetX).none { this[it, targetY] >= height }
        val east = (targetX + 1 until size).none { this[it, targetY] >= height }

        return (north || south || west || east)
    }

    var innerVisible = 0
    for (x in 1 until size - 1) {
        for (y in 1 until size - 1) {
            if (isVisible(x, y)) {
                innerVisible += 1
            }
        }
    }

    return (size * 4 - 4) + innerVisible
}

fun Forest.highestScenicScore(): Int {

    fun scenicScoreFor(targetX: Int, targetY: Int): Int {
        val height = this[targetX, targetY]

        var north = 0
        for (y in (targetY - 1) downTo 0) {
            if (this[targetX, y] >= height) {
                north += 1
                break
            }
            north += 1
        }

        var south = 0
        for (y in (targetY + 1) until size) {
            if (this[targetX, y] >= height) {
                south += 1
                break
            }
            south += 1
        }

        var west = 0
        for (x in (targetX - 1) downTo 0) {
            if (this[x, targetY] >= height) {
                west += 1
                break
            }
            west += 1
        }

        var east = 0
        for (x in (targetX + 1) until size) {
            if (this[x, targetY] >= height) {
                east += 1
                break
            }
            east += 1
        }

        return (north * south * west * east)
    }

    var max = 0
    for (x in 1 until size - 1) {
        for (y in 1 until size - 1) {
            val score = scenicScoreFor(x, y)
            if (score > max) {
                max = score
            }
        }
    }

    return max
}

fun makeTestForest(): Forest {
    return Forest(
        arrayOf(
            intArrayOf(3, 0, 3, 7, 3),
            intArrayOf(2, 5, 5, 1, 2),
            intArrayOf(6, 5, 3, 3, 2),
            intArrayOf(3, 3, 5, 4, 9),
            intArrayOf(3, 5, 3, 9, 0)
        )
    )
}

fun parseForest(input: List<String>): Forest {
    val array = input.map {
        it.toCharArray().map { char -> char.digitToInt() }.toIntArray()
    }.toTypedArray()
    return Forest(array)
}

fun main() {
    fun test1(forest: Forest): Int {
        return forest.visibleTreeFromOutsideCount()
    }

    fun part1(input: List<String>): Int {
        return parseForest(input).visibleTreeFromOutsideCount()
    }

    fun test2(forest: Forest): Int {
        return forest.highestScenicScore()
    }

    fun part2(input: List<String>): Int {
        return parseForest(input).highestScenicScore()
    }

    val input = readInput("Day08")
    println(test1(makeTestForest()))
    println(part1(input))
    println(test2(makeTestForest()))
    println(part2(input))
}
