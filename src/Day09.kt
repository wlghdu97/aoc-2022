import kotlin.math.abs
import kotlin.math.sign

enum class Direction {
    U, D, L, R;
}

class Instruction(val direction: Direction, val distance: Int)

data class Position(val x: Int, val y: Int)

fun List<Instruction>.calculateTailVisitedPositionCount(ropeLength: Int = 2): Int {
    assert(ropeLength > 1)

    val tailVisited = hashSetOf<Position>()

    val rope = Array(ropeLength) { Position(0, 0) }
    this.forEach { inst ->
        repeat(inst.distance) {
            for (headIndex in 0 until (ropeLength - 1)) {
                val oldHead = rope[headIndex]
                val head = if (headIndex == 0) {
                    when (inst.direction) {
                        Direction.U -> oldHead.copy(y = oldHead.y - 1)
                        Direction.D -> oldHead.copy(y = oldHead.y + 1)
                        Direction.L -> oldHead.copy(x = oldHead.x - 1)
                        Direction.R -> oldHead.copy(x = oldHead.x + 1)
                    }.apply {
                        rope[headIndex] = this
                    }
                } else {
                    oldHead
                }

                val tailIndex = headIndex + 1
                val tail = rope[tailIndex]
                var nextTailX = tail.x
                var nextTailY = tail.y

                if (abs(head.x - nextTailX) > 1) {
                    nextTailX += (head.x - nextTailX).sign
                    if (head.y != nextTailY) {
                        nextTailY += (head.y - nextTailY).sign
                    }
                }
                if (abs(head.y - nextTailY) > 1) {
                    nextTailY += (head.y - nextTailY).sign
                    if (head.x != nextTailX) {
                        nextTailX += (head.x - nextTailX).sign
                    }
                }

                if (tail.x != nextTailX || tail.y != nextTailY) {
                    if (tailIndex == (ropeLength - 1)) {
                        tailVisited.add(tail)
                    }
                    rope[tailIndex] = Position(nextTailX, nextTailY)
                }
            }
        }
    }
    tailVisited.add(rope[ropeLength - 1])

    return tailVisited.count()
}

fun makeTestInstructions(): List<Instruction> {
    return listOf(
        "R 4", "U 4", "L 3", "D 1",
        "R 4", "D 1", "L 5", "R 2"
    ).map { parseInstruction(it) }
}

fun makeTestInstructions2(): List<Instruction> {
    return listOf(
        "R 5", "U 8", "L 8", "D 3",
        "R 17", "D 10", "L 25", "U 20"
    ).map { parseInstruction(it) }
}

fun parseInstruction(raw: String): Instruction {
    val (dir, dist) = raw.split(" ")
    val direction = Direction.valueOf(dir)
    val distance = dist.toInt()
    return Instruction(direction, distance)
}

fun main() {
    fun test1(): Int {
        return makeTestInstructions().calculateTailVisitedPositionCount()
    }

    fun part1(input: List<String>): Int {
        return input.map { parseInstruction(it) }.calculateTailVisitedPositionCount()
    }

    fun test2(): Int {
        return makeTestInstructions().calculateTailVisitedPositionCount(10)
    }

    fun test3(): Int {
        return makeTestInstructions2().calculateTailVisitedPositionCount(10)
    }

    fun part2(input: List<String>): Int {
        return input.map { parseInstruction(it) }.calculateTailVisitedPositionCount(10)
    }

    val input = readInput("Day09")
    println(test1())
    println(part1(input))
    println(test2())
    println(test3())
    println(part2(input))
}
