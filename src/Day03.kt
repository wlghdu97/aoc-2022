fun main() {
    fun priority(compartment: Char): Int {
        return when (compartment) {
            in ('a'..'z') -> compartment.code - 96
            in ('A'..'Z') -> compartment.code - 38
            else -> throw IllegalArgumentException()
        }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val middle = it.length / 2
            val first = it.substring(0, middle).toCharArray()
            val second = it.substring(middle).toCharArray().toSet()
            priority(first.intersect(second).first())
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { rucksacks ->
            val badge = rucksacks.map { it.toCharArray().toSet() }.reduce { acc, chars ->
                acc.intersect(chars)
            }.first()
            priority(badge)
        }
    }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
