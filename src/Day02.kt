fun main() {
    data class GameSet(val red: Int, val green: Int, val blue: Int)
    data class Game(val id: Int, val gameSets: List<GameSet>) {
        val maxRed = gameSets.maxOf { it.red }
        val maxGreen = gameSets.maxOf { it.green }
        val maxBlue = gameSets.maxOf { it.blue }
        val power = maxRed * maxGreen * maxBlue
    }

    fun parseSet(set: String): GameSet {
        val colorMap = mutableMapOf<String, Int>()
        set.split(",").forEach {
            val (num, color) = it.trim().split(" ")
            colorMap[color.trim()] = num.trim().toInt()
        }
        return GameSet(
                red = colorMap["red"] ?: 0,
                green = colorMap["green"] ?: 0,
                blue = colorMap["blue"] ?: 0,
        )
    }

    fun parseGame(input: String): Game {
        val (id, sets) = input.split(":")
        return Game(
                id = id.removePrefix("Game").trim().toInt(),
                gameSets = sets.split(";").map { parseSet(it.trim()) }
        )
    }

    fun part1(input: List<String>): Int {
        val games = input.map { parseGame(it) }
        val candidates = games.filter { it.maxRed <= 12 && it.maxGreen <= 13 && it.maxBlue <= 14 }
        return candidates.sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        val games = input.map { parseGame(it) }
        return games.sumOf { it.power }
    }

    // test if implementation meets criteria from the description, like:
    // val testInput = readInput("Day02_test")
    // check(part1(testInput) == 1)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
