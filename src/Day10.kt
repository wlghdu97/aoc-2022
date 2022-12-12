import java.util.*

private class CPU constructor(private val instructions: List<Instruction>) {

    fun run(onCycle: (cycle: Int, x: Int) -> Unit) {
        val instQueue: Queue<Instruction> = LinkedList()
        instructions.forEach {
            instQueue.add(it)
        }

        var x = 1
        var cycle = 1
        var currentInst: Instruction? = instQueue.poll()
        while (currentInst != null) {
            val inst = currentInst
            repeat(inst.cycleToFinish) {
                onCycle(cycle, x)
                if (it == inst.cycleToFinish - 1) {
                    x = inst.onFinishInstruction(x)
                }
                cycle += 1
            }
            currentInst = instQueue.poll()
        }
    }

    sealed class Instruction {
        abstract val cycleToFinish: Int
        abstract fun onFinishInstruction(register: Int): Int

        object Noop : Instruction() {
            override val cycleToFinish: Int = 1

            override fun onFinishInstruction(register: Int): Int {
                return register // do nothing
            }
        }

        class Addx(val argument: Int) : Instruction() {
            override val cycleToFinish: Int = 2

            override fun onFinishInstruction(register: Int): Int {
                return register + argument
            }
        }
    }
}

private fun parseInstructions(input: List<String>): List<CPU.Instruction> {
    return input.map {
        val parts = it.split(" ")
        when (parts[0]) {
            "addx" -> {
                CPU.Instruction.Addx(parts[1].toInt())
            }

            "noop" -> {
                CPU.Instruction.Noop
            }

            else -> {
                throw IllegalArgumentException()
            }
        }
    }
}

private fun CPU.sumOfSpecificCycleStrengths(vararg cycles: Int): Int {
    var sum = 0
    run { cycle, x ->
        if (cycles.contains(cycle)) {
            sum += (cycle * x)
        }
    }
    return sum
}

private fun CPU.printPixels(width: Int) {
    val monitor = StringBuilder()
    run { cycle, x ->
        val range = (x - 1)..(x + 1)
        val inRange = ((cycle - 1) % width) in range
        if (inRange) {
            monitor.append("#")
        } else {
            monitor.append(".")
        }
    }
    println(monitor.chunked(width).joinToString("\n"))
}

fun main() {
    fun test1(input: List<String>): Int {
        return CPU(parseInstructions(input)).sumOfSpecificCycleStrengths(20, 60, 100, 140, 180, 220)
    }

    fun part1(input: List<String>): Int {
        return CPU(parseInstructions(input)).sumOfSpecificCycleStrengths(20, 60, 100, 140, 180, 220)
    }

    fun test2(input: List<String>) {
        CPU(parseInstructions(input)).printPixels(40)
    }

    fun part2(input: List<String>) {
        CPU(parseInstructions(input)).printPixels(40)
    }

    val testInput = readInput("Day10-Test01")
    val input = readInput("Day10")
    println(test1(testInput))
    println(part1(input))
    test2(testInput)
    part2(input)
}
