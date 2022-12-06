import java.util.*

fun main() {
    fun CharArray.findFirstDistinctMarkerIndex(digits: Int): Int {
        val acc = LinkedList<Char>()
        for ((index, signal) in this.withIndex()) {
            acc.add(signal)
            if (acc.size < digits) {
                continue
            }
            if (acc.size == digits + 1) {
                acc.removeFirst()
            }
            if (acc.toSet().size == digits) {
                return index + 1
            }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        return input[0].toCharArray().findFirstDistinctMarkerIndex(4)
    }

    fun part2(input: List<String>): Int {
        return input[0].toCharArray().findFirstDistinctMarkerIndex(14)
    }

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
