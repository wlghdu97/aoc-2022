import java.util.*

// [W] [V]     [P]
// [B] [T]     [C] [B]     [G]
// [G] [S]     [V] [H] [N] [T]
// [Z] [B] [W] [J] [D] [M] [S]
// [R] [C] [N] [N] [F] [W] [C]     [W]
// [D] [F] [S] [M] [L] [T] [L] [Z] [Z]
// [C] [W] [B] [G] [S] [V] [F] [D] [N]
// [V] [G] [C] [Q] [T] [J] [P] [B] [M]
//  1   2   3   4   5   6   7   8   9

fun main() {
    fun makeStacks(): List<Stack<Char>> {
        return listOf(
            Stack<Char>().apply {
                push('V')
                push('C')
                push('D')
                push('R')
                push('Z')
                push('G')
                push('B')
                push('W')
            },
            Stack<Char>().apply {
                push('G')
                push('W')
                push('F')
                push('C')
                push('B')
                push('S')
                push('T')
                push('V')
            },
            Stack<Char>().apply {
                push('C')
                push('B')
                push('S')
                push('N')
                push('W')
            },
            Stack<Char>().apply {
                push('Q')
                push('G')
                push('M')
                push('N')
                push('J')
                push('V')
                push('C')
                push('P')
            },
            Stack<Char>().apply {
                push('T')
                push('S')
                push('L')
                push('F')
                push('D')
                push('H')
                push('B')
            },
            Stack<Char>().apply {
                push('J')
                push('V')
                push('T')
                push('W')
                push('M')
                push('N')
            },
            Stack<Char>().apply {
                push('P')
                push('F')
                push('L')
                push('C')
                push('S')
                push('T')
                push('G')
            },
            Stack<Char>().apply {
                push('B')
                push('D')
                push('Z')
            },
            Stack<Char>().apply {
                push('M')
                push('N')
                push('Z')
                push('W')
            }
        )
    }

    val regex = Regex("move (\\d+) from (\\d+) to (\\d+)")

    fun parse(command: String): Triple<Int, Int, Int> {
        val destructured = regex.matchEntire(command)?.destructured
        return if (destructured != null) {
            val (move, from, to) = destructured
            Triple(move.toInt(), from.toInt(), to.toInt())
        } else {
            throw IllegalArgumentException()
        }
    }

    fun part1(input: List<String>): String {
        val stacks = makeStacks()
        input.forEach {
            val (move, from, to) = parse(it)
            repeat(move) {
                val crate = stacks[from - 1].pop()
                stacks[to - 1].push(crate)
            }
        }
        return stacks.map { it.peek() }.toCharArray().concatToString()
    }

    fun part2(input: List<String>): String {
        val stacks = makeStacks()
        input.forEach {
            val (move, from, to) = parse(it)
            val crateStack = Stack<Char>().apply {
                repeat(move) {
                    push(stacks[from - 1].pop())
                }
            }
            while (crateStack.isNotEmpty()) {
                stacks[to - 1].push(crateStack.pop())
            }
        }
        return stacks.map { it.peek() }.toCharArray().concatToString()
    }

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

