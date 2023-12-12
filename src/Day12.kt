fun main() {
    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>, expansion: Int): Long {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
//    check(part2(testInput, 10) == 1030L)
//    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day12")
    part1(input).println()
//    part2(input, 1_000_000).println()
}
