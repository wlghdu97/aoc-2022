fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf {
            val (opponent, ally) = it.split(" ")

            fun map(sign: String): Int {
                return when (sign) {
                    "A", "X" -> 1
                    "B", "Y" -> 2
                    "C", "Z" -> 3
                    else -> throw IllegalArgumentException()
                }
            }

            fun selectedShape(): Int {
                return map(ally)
            }

            fun roundOutcome(): Int {
                val o = map(opponent)
                val a = map(ally)
                return when {
                    (o == a) -> 3
                    (o == 1 && a == 2) || (o == 2 && a == 3) || (o == 3 && a == 1) -> 6
                    else -> 0
                }
            }

            selectedShape() + roundOutcome()
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val (opponent, ally) = it.split(" ")

            fun selectedShape(): Int {
                return when (opponent) {
                    "A" -> {
                        when (ally) {
                            "X" -> 3
                            "Y" -> 1
                            "Z" -> 2
                            else -> throw IllegalArgumentException()
                        }
                    }
                    "B" -> {
                        when (ally) {
                            "X" -> 1
                            "Y" -> 2
                            "Z" -> 3
                            else -> throw IllegalArgumentException()
                        }
                    }
                    "C" -> {
                        when (ally) {
                            "X" -> 2
                            "Y" -> 3
                            "Z" -> 1
                            else -> throw IllegalArgumentException()
                        }
                    }
                    else -> throw IllegalArgumentException()
                }
            }

            fun roundOutcome(): Int {
                return when (ally) {
                    "X" -> 0
                    "Y" -> 3
                    "Z" -> 6
                    else -> throw IllegalArgumentException()
                }
            }

            selectedShape() + roundOutcome()
        }
    }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
