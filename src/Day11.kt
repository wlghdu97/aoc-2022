import java.util.*
import kotlin.math.floor

private class Monkey constructor(
    val holdingItems: Queue<Long>,
    val operation: (old: Long) -> Long,
    val divisibleBy: Int,
    val ifTrue: Int,
    val ifFalse: Int
)

private class KeepAwayGame constructor(
    private val monkeys: Map<Int, Monkey>,
    private val inspectModifier: (Long) -> Long = {
        floor(it / 3f).toLong() // rounded down to the nearest integer (?)
    }
) {

    init {
        require(monkeys.size > 1)
    }

    fun doRound(count: Int): Long {
        val inspectCounts = IntArray(monkeys.size) { 0 }

        repeat(count) {
            monkeys.forEach { (index, monkey) ->
                while (monkey.holdingItems.isNotEmpty()) {
                    val currentItem = monkey.holdingItems.poll()
                    if (currentItem != null) {
                        val inspected = inspectModifier.invoke(monkey.operation.invoke(currentItem))
                        inspectCounts[index] += 1
                        val throwTo = if (inspected % monkey.divisibleBy == 0L) {
                            monkey.ifTrue
                        } else {
                            monkey.ifFalse
                        }
                        monkeys[throwTo]?.holdingItems?.add(inspected)
                    }
                }
            }
        }

        return with(inspectCounts.sorted().reversed()) {
            this[0].toLong() * this[1].toLong()
        }
    }
}

private val regexIndex = Regex("Monkey (\\d+):")
private val regexOperation = Regex("new = old (\\*|\\+) (\\d+|old)")
private val regexTestExpression = Regex("  Test: divisible by (\\d+)")
private val regexThrowToExpression = Regex("    If (true|false): throw to monkey (\\d+)")

private fun parseMonkeys(input: List<String>): Map<Int, Monkey> {
    val monkeys = TreeMap<Int, Monkey>()
    val iterator = input.iterator()

    fun String.parseAsOperation(): (old: Long) -> Long {
        val destructured = regexOperation.matchEntire(this)?.destructured
        if (destructured != null) {
            val (operator, operand) = destructured
            return { old ->
                val operandValue = when {
                    (operand == "old") -> old
                    (operand.toIntOrNull() != null) -> operand.toLong()
                    else -> throw IllegalArgumentException()
                }
                when (operator) {
                    "*" -> old * operandValue
                    "+" -> old + operandValue
                    else -> throw IllegalArgumentException()
                }
            }
        } else {
            throw IllegalArgumentException()
        }
    }

    fun String.parseAsTestExpression(): Int {
        return regexTestExpression.matchEntire(this)?.groupValues?.get(1)?.toIntOrNull()
            ?: throw IllegalArgumentException()
    }

    fun String.parseAsThrowToExpression(): Int {
        val destructured = regexThrowToExpression.matchEntire(this)?.destructured
        if (destructured != null) {
            val (_, destIndex) = destructured
            return destIndex.toInt()
        } else {
            throw IllegalArgumentException()
        }
    }

    while (iterator.hasNext()) {
        val line = iterator.next()
        if (line.isNotBlank()) {
            val index = regexIndex.matchEntire(line)?.groupValues?.get(1)?.toIntOrNull()
                ?: throw IllegalArgumentException()
            val startingItems = iterator.next()
                .substringAfter("  Starting items: ")
                .split(", ")
                .map { it.toLong() }
            val operation = iterator.next()
                .substringAfter("  Operation: ")
                .parseAsOperation()
            val divisibleBy = iterator.next()
                .parseAsTestExpression()
            val ifTrue = iterator.next()
                .parseAsThrowToExpression()
            val ifFalse = iterator.next()
                .parseAsThrowToExpression()

            monkeys[index] = Monkey(
                holdingItems = LinkedList(startingItems),
                operation = operation,
                divisibleBy = divisibleBy,
                ifTrue = ifTrue,
                ifFalse = ifFalse
            )
        }
    }

    return monkeys
}

fun main() {
    fun part1(input: List<String>): Long {
        return KeepAwayGame(parseMonkeys(input)).doRound(20)
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)
        val maxDivisible = monkeys.values.fold(1) { acc, monkey -> acc * monkey.divisibleBy }
        return KeepAwayGame(monkeys) {
            it % maxDivisible
        }.doRound(10000)
    }

    val testInput = readInput("Day11-Test01")
    val input = readInput("Day11")
    println(part1(testInput))
    println(part1(input))
    println(part2(testInput))
    println(part2(input))
}
