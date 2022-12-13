import kotlin.math.max

sealed class Packet : Comparable<Packet> {
    data class Integer(val value: Int) : Packet()
    data class PacketList(val value: List<Packet>) : Packet()

    override fun compareTo(other: Packet): Int {
        when {
            (this is Integer && other is Integer) -> {
                return this.value.compareTo(other.value)
            }

            (this is PacketList && other is PacketList) -> {
                val max = max(this.value.size, other.value.size)
                for (index in 0 until max) {
                    val first = this.value.getOrNull(index)
                    val second = other.value.getOrNull(index)
                    when {
                        (first != null && second != null) -> {
                            val comp = first.compareTo(second)
                            if (comp != 0) {
                                return comp
                            }
                        }

                        (first == null) -> {
                            return -1
                        }

                        else -> {
                            return 1
                        }
                    }
                }
                return 0
            }

            (this is Integer) -> {
                return PacketList(listOf(this)).compareTo(other)
            }

            (other is Integer) -> {
                return this.compareTo(PacketList(listOf(other)))
            }

            else -> {
                throw IllegalStateException()
            }
        }
    }
}

private fun parsePackets(input: List<String>): List<Pair<Packet, Packet>> {
    val packets = arrayListOf<Pair<Packet, Packet>>()

    val iterator = input.iterator()
    while (iterator.hasNext()) {
        val line = iterator.next()
        if (line.isNotBlank()) {
            val first = line.toPacket()
            val second = iterator.next().toPacket()
            packets.add(first to second)
        }
    }

    return packets
}

private fun String.toPacket(): Packet {
    return when {
        (this.toIntOrNull() != null) -> {
            Packet.Integer(this.toInt())
        }

        (this.startsWith("[") && this.endsWith("]")) -> {
            val arrayInside = this.substring(1, this.length - 1)
            if (arrayInside.isBlank()) {
                Packet.PacketList(emptyList())
            } else {
                val packetList = arrayListOf<Packet>()
                var remaining = arrayInside
                while (remaining.isNotBlank()) {
                    val first = remaining.first()
                    when {
                        (first == ',') -> {
                            remaining = remaining.drop(1)
                        }

                        (first.isDigit()) -> {
                            val element = remaining.substringBefore(',')
                            packetList.add(Packet.Integer(element.toInt()))
                            remaining = remaining.drop(element.length)
                        }

                        (first == '[') -> {
                            val element = remaining.substringOuterBracket()
                            packetList.add(element.toPacket())
                            remaining = remaining.drop(element.length)
                        }

                        else -> {
                            throw IllegalArgumentException()
                        }
                    }
                }
                Packet.PacketList(packetList)
            }
        }

        else -> {
            throw IllegalArgumentException()
        }
    }
}

// assume it is well-formed
private fun String.substringOuterBracket(): String {
    require(this.startsWith('['))

    var leftBrackets = 0
    var rightBrackets = 0
    val iterator = this.iterator()
    var acc = ""
    while (iterator.hasNext()) {
        val char = iterator.nextChar().apply {
            acc += this
        }
        if (char == '[') {
            leftBrackets += 1
        } else if (char == ']') {
            rightBrackets += 1
            if (leftBrackets == rightBrackets) {
                return acc
            }
        }
    }

    return this
}

fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        parsePackets(input).forEachIndexed { index, (a, b) ->
            if (a < b) {
                sum += (index + 1)
            }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        val dividerPacketA = Packet.PacketList(listOf(Packet.PacketList(listOf(Packet.Integer(2)))))
        val dividerPacketB = Packet.PacketList(listOf(Packet.PacketList(listOf(Packet.Integer(6)))))
        val packets = ArrayList(parsePackets(input).flatMap { it.toList() }).apply {
            add(dividerPacketA)
            add(dividerPacketB)
        }
        val sorted = packets.sorted()
        return ((sorted.indexOf(dividerPacketA) + 1) * (sorted.indexOf(dividerPacketB) + 1))
    }

    val testInput = readInput("Day13-Test01")
    val input = readInput("Day13")
    println(part1(testInput))
    println(part1(input))
    println(part2(testInput))
    println(part2(input))
}
