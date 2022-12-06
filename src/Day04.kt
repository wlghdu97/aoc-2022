fun main() {
    fun String.toRange(): IntRange {
        val (a, b) = this.split("-")
        val range = a.toInt()..b.toInt()
        if (range.step < 0) {
            throw IllegalArgumentException()
        }
        return range
    }

    fun IntRange.includes(other: IntRange): Boolean {
        return when {
            (this.step != 1 || other.step != 1) -> {
                throw UnsupportedOperationException()
            }
            (this.first < other.first) -> {
                this.last >= other.last
            }
            (this.first > other.first) -> {
                this.last <= other.last
            }
            else -> {
                true
            }
        }
    }

    fun IntRange.overlap(other: IntRange): Boolean {
        return when {
            (this.step != 1 || other.step != 1) -> {
                throw UnsupportedOperationException()
            }
            (this.first < other.first) -> {
                this.last >= other.first
            }
            (this.first > other.first) -> {
                this.first <= other.last
            }
            else -> {
                true
            }
        }
    }

    fun part1(input: List<String>): Int {
        return input.count {
            val (first, second) = it.split(",")
            val firstRange = first.toRange()
            val secondRange = second.toRange()
            firstRange.includes(secondRange)
        }
    }

    fun part2(input: List<String>): Int {
        return input.count {
            val (first, second) = it.split(",")
            val firstRange = first.toRange()
            val secondRange = second.toRange()
            firstRange.overlap(secondRange)
        }
    }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
