import kotlin.math.max

private sealed class Cave constructor(minX: Int, maxX: Int, maxY: Int) {
    val width = (max(maxX - minX, maxY) * 2 + 1)
    val height = maxY

    abstract val structures: Array<Array<Element>>

    private val centerX = width / 2
    private val sandGeyser = Position(centerX, 0)

    val sandCount: Int
        get() = structures.sumOf { y -> y.count { it == Element.SAND } }

    fun appendWall(from: Position, to: Position) {
        when {
            (from.x == to.x) -> {
                for (y in (from.y) toward (to.y)) {
                    structures[y][centerX - (500 - from.x)] = Element.WALL
                }
            }

            (from.y == to.y) -> {
                for (x in (from.x) toward (to.x)) {
                    structures[from.y][centerX - (500 - x)] = Element.WALL
                }
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    /**
     * A unit of sand always falls down one step if possible.
     * If the tile immediately below is blocked (by rock or sand),
     * the unit of sand attempts to instead move diagonally one step down and to the left.
     * If that tile is blocked, the unit of sand attempts to instead move diagonally one step down and to the right.
     * Sand keeps moving as long as it is able to do so, at each step trying to move down, then down-left, then down-right.
     * If all three possible destinations are blocked, the unit of sand comes to rest and no longer moves,
     * at which point the next unit of sand is created back at the source.
     */
    fun dropSandsUntilOverflow() {
        var sandX = sandGeyser.x
        var sandY = sandGeyser.y
        while (sandY < (height - 1)) {
            if (structures[sandGeyser.y][sandGeyser.x] != Element.AIR) {
                break
            }
            if (structures[sandY + 1][sandX] == Element.AIR) {
                sandY += 1
                continue
            } else {
                if (sandX - 1 < 0) {
                    break
                } else if (structures[sandY + 1][sandX - 1] == Element.AIR) {
                    sandX -= 1
                    sandY += 1
                    continue
                } else {
                    if (sandX + 1 >= width) {
                        break
                    } else if (structures[sandY + 1][sandX + 1] == Element.AIR) {
                        sandX += 1
                        sandY + 1
                        continue
                    } else {
                        structures[sandY][sandX] = Element.SAND
                        sandX = sandGeyser.x
                        sandY = sandGeyser.y
                    }
                }
            }
        }
    }

    fun structuresToString(): String {
        val sb = StringBuilder()
        sb.append("\n")
        structures.forEachIndexed { y, elements ->
            elements.forEachIndexed { x, element ->
                if (y == 0 && x == sandGeyser.x) {
                    sb.append("+")
                } else {
                    sb.append(
                        when (element) {
                            Element.AIR -> "."
                            Element.WALL -> "#"
                            Element.SAND -> "o"
                        }
                    )
                }
            }
            sb.append("\n")
        }
        sb.append("\n")
        return sb.toString()
    }

    data class Position(val x: Int, val y: Int)

    enum class Element {
        AIR, WALL, SAND;
    }
}

private class AbyssCave constructor(minX: Int, maxX: Int, maxY: Int) : Cave(minX, maxX, maxY) {
    override val structures = Array(height) { Array(width) { Element.AIR } }
}

private class FlooredCave constructor(
    minX: Int, maxX: Int, maxY: Int
) : Cave(minX, maxX, maxY + 2) {
    override val structures = Array(height) {
        if (it == (height - 1)) {
            Array(width) { Element.WALL }
        } else {
            Array(width) { Element.AIR }
        }
    }
}

private infix fun Int.toward(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}

private fun parseCaveStructures(input: List<String>, hasFloor: Boolean = false): Cave {
    var minX = Int.MAX_VALUE
    var maxX = 0
    var maxY = 0

    val walls = input.map { arr ->
        arr.split(" -> ").map { pos ->
            val (x, y) = pos.split(',').map { it.toInt() }
            Cave.Position(x, y)
        }
    }

    walls.forEach { positions ->
        positions.forEach { (x, y) ->
            if (x < minX) {
                minX = x
            }
            if (x > maxX) {
                maxX = x
            }
            if (maxY < y) {
                maxY = y
            }
        }
    }

    val cave = if (hasFloor) {
        FlooredCave(minX, maxX, maxY + 1)
    } else {
        AbyssCave(minX, maxX, maxY + 1)
    }
    walls.forEach { positions ->
        for (index in 1 until positions.size) {
            cave.appendWall(positions[index - 1], positions[index])
        }
    }

    return cave
}

fun main() {
    fun part1(input: List<String>): Int {
        val cave = parseCaveStructures(input)
        cave.dropSandsUntilOverflow()
        println(cave.structuresToString())
        return cave.sandCount
    }

    fun part2(input: List<String>): Int {
        val cave = parseCaveStructures(input, true)
        cave.dropSandsUntilOverflow()
        println(cave.structuresToString())
        return cave.sandCount
    }

    val testInput = readInput("Day14-Test01")
    val input = readInput("Day14")
    println(part1(testInput))
    println(part1(input))
    println(part2(testInput))
    println(part2(input))
}
