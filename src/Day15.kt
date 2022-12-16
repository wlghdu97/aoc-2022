import kotlin.collections.List
import kotlin.collections.any
import kotlin.collections.firstOrNull
import kotlin.collections.map
import kotlin.collections.maxOf
import kotlin.collections.minOf
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private class Map constructor(val sensors: List<Sensor>) {
    val minX = sensors.minOf { min(it.sensorPos.x - it.sensorRadius, it.beaconPos.x) }
    val maxX = sensors.maxOf { max(it.sensorPos.x + it.sensorRadius, it.beaconPos.x) }

    fun inBeaconRange(position: Position): Boolean {
        if (sensors.any { (position == it.sensorPos) || (position == it.beaconPos) }) {
            return false
        }
        return sensors.any { it.intersects(position) }
    }

    fun firstSensorInPosition(position: Position): Sensor? {
        return sensors.firstOrNull { it.intersects(position) }
    }

    class Sensor(val sensorPos: Position, val beaconPos: Position) {
        val sensorRadius = sensorPos.taxicabDistance(beaconPos)

        fun intersects(position: Position): Boolean {
            val distance = sensorPos.taxicabDistance(position)
            return (distance <= sensorRadius)
        }

        private fun Position.taxicabDistance(other: Position) =
            abs(this.x - other.x) + abs(this.y - other.y)
    }

    data class Position(val x: Int, val y: Int)
}

private val sensorRegex = Regex("Sensor at x=([-\\d]+), y=([-\\d]+): closest beacon is at x=([-\\d]+), y=([-\\d]+)")

private fun parseMap(input: List<String>): Map {
    val sensors = input.map {
        val (sensorX, sensorY, beaconX, beaconY) = sensorRegex.matchEntire(it)?.destructured
            ?: throw IllegalArgumentException()
        Map.Sensor(Map.Position(sensorX.toInt(), sensorY.toInt()), Map.Position(beaconX.toInt(), beaconY.toInt()))
    }
    return Map(sensors)
}

fun main() {
    fun part1(input: List<String>, y: Int): Int {
        val map = parseMap(input)
        var intersects = 0
        for (x in map.minX..map.maxX) {
            if (map.inBeaconRange(Map.Position(x, y))) {
                intersects += 1
            }
        }
        return intersects
    }

    fun part2(input: List<String>, squareWidth: Int): Long {
        val map = parseMap(input)
        var beaconCandidate: Position? = null

        for (x in 0..squareWidth) {
            var y = 0
            while (y in 0..squareWidth) {
                val sensor = map.firstSensorInPosition(Map.Position(x, y))
                if (sensor != null) {
                    // jump down to bottom of its sensor range
                    val localX = abs(sensor.sensorPos.x - x)
                    y = sensor.sensorPos.y + sensor.sensorRadius - localX + 1
                    continue
                } else {
                    beaconCandidate = Position(x, y)
                    break
                }
            }
        }
        return beaconCandidate?.let {
            (it.x * 4000000L) + it.y
        } ?: throw NullPointerException()
    }

    val testInput = readInput("Day15-Test01")
    val input = readInput("Day15")
    println(part1(testInput, 10))
    println(part1(input, 2000000))
    println(part2(testInput, 20))
    println(part2(input, 4000000))
}
