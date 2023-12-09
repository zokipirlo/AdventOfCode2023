fun main() {
    data class RaceData(val time: Long, val record: Long) {
        fun calculateOptions(): Int {
            return (1..time).asSequence()
                .map { press ->
                    val travel = time - press
                    press * travel
                }
                .filter { it > record }
                .count()
        }
    }

    data class Race(val raceData: List<RaceData>)

    fun part1(input: List<String>): Int {
        val times = input[0].removePrefix("Time:").trim().splitMultipleSpaces().map { it.toLong() }
        val records = input[1].removePrefix("Distance:").trim().splitMultipleSpaces().map { it.toLong() }
        val race = Race(
            raceData = times.mapIndexed { index, time ->
                RaceData(time, records[index])
            }
        )
        return race.raceData.map { it.calculateOptions() }.multiply()
    }

    fun part2(input: List<String>): Int {
        val num = input[0].removePrefix("Time:").trim().replace(" ", "")
        println(num)
        val time = input[0].removePrefix("Time:").trim().replace(" ", "").toLong()
        val record = input[1].removePrefix("Distance:").trim().replace(" ", "").toLong()
        return RaceData(time, record).calculateOptions()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
