fun main() {
    fun part1(input: List<String>): Int {
        var max = 0
        var inc = 0
        input.forEach {
            if (it.isBlank()) {
                if (inc > max) {
                    max = inc
                }
                inc = 0
            } else {
                inc += it.toInt()
            }
        }
        return max
    }

    fun part2(input: List<String>): Int {
        var max = 0
        var secondMax = 0
        var thirdMax = 0

        var inc = 0
        input.forEach {
            if (it.isBlank()) {
                when {
                    (inc > max) -> {
                        thirdMax = secondMax
                        secondMax = max
                        max = inc
                    }
                    (inc > secondMax) -> {
                        thirdMax = secondMax
                        secondMax = inc
                    }
                    (inc > thirdMax) -> {
                        thirdMax = inc
                    }
                }
                inc = 0
            } else {
                inc += it.toInt()
            }
        }
        return (max + secondMax + thirdMax)
    }

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("Day01_test")
//    check(part1(testInput) == 1)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
