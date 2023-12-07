import kotlin.math.pow

fun main() {
    data class Card(val id: Int, val winning: Set<Int>, val numbers: Set<Int>) {
        val winnings = winning.intersect(numbers).size
        val power = when {
            winnings == 0 -> 0.0
            else -> 2.0.pow(winnings - 1)
        }
        var awards = 0

        fun addAward() = awards++

        override fun toString(): String {
            return "Card(id=$id, winnings=$winnings, awards=$awards)"
        }
    }

    fun parseNumbers(numbers: String): Set<Int> {
        return numbers.chunked(3) { it.trim().toString().toInt() }.toSet()
    }

    fun parseCard(input: String): Card {
        val (id, numbers) = input.split(":")
        val (win, cand) = numbers.split("|")
        return Card(
                id = id.removePrefix("Card").trim().toInt(),
                winning = parseNumbers(win.trimEnd()),
                numbers = parseNumbers(cand)
        )
    }

    fun part1(input: List<String>): Int {
        val cards = input.map { parseCard(it) }
        return cards.sumOf { it.power.toInt() }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { parseCard(it) }

        cards.forEachIndexed { index, card ->
            if (index == cards.size - 1) {
                return@forEachIndexed
            }
            val nextCard = index + 1
            repeat(card.awards + 1) {
                repeat(card.winnings) {
                    cards.getOrNull(nextCard + it)?.addAward()
                }
            }
        }

        val allAwards = cards.sumOf { it.awards }
        return allAwards + cards.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
