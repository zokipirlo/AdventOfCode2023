fun main() {
    val cardStrength1 = charArrayOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
    val cardStrength2 = charArrayOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')

    data class Hand(val hand: String, val bid: Int, val part2: Boolean) : Comparable<Hand> {
        fun calculateStrength(hand: String): Int {
            val grouped = hand
                .groupBy { it }
            val sum = grouped.mapValues { (it.value.size) * (it.value.size) }
                .values
                .sum()
//            println("$hand : $grouped = $sum")
            return sum
        }

        val cardStrengthRule = when (part2) {
            true -> cardStrength2
            false -> cardStrength1
        }

        fun calculateCardStrength(hand: String) = hand
            .toCharArray()
            .map { (cardStrengthRule.indexOf(it) + 65).toChar() }
            .joinToString()

        val maxHand = when (part2 && hand.contains("J")) {
            true -> cardStrength2.map {
                hand.replace('J', it)
            }.maxWith(
                compareBy<String> { calculateStrength(it) }
                    .then(compareBy { calculateCardStrength(hand) })
            )

            false -> hand
        }

        val maxStrength = calculateStrength(maxHand)
        val cardStrength = calculateCardStrength(hand)

        override fun compareTo(other: Hand): Int {
            return compareValuesBy(this, other, { it.maxStrength }, { it.cardStrength })
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Hand

            if (hand != other.hand) return false
            if (bid != other.bid) return false

            return true
        }

        override fun hashCode(): Int {
            var result = hand.hashCode()
            result = 31 * result + bid
            return result
        }

        override fun toString(): String {
            return "Hand(hand='$hand', maxHand='$maxHand' bid=$bid)"
        }
    }

    fun parseHand(input: String, isPart2: Boolean): Hand {
        val (hand, bid) = input.split(" ")
        return Hand(hand, bid.toInt(), isPart2)
    }

    fun calculateResult(hands: List<Hand>): Long {
        val sorted = hands.sorted()
        return sorted.mapIndexed { index, hand -> ((index + 1) * hand.bid).toLong() }.sum()
    }

    fun part1(input: List<String>): Long {
        val hands = input.map { parseHand(it, false) }
        return calculateResult(hands)
    }

    fun part2(input: List<String>): Long {
        val hands = input.map { parseHand(it, true) }
        return calculateResult(hands)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
//    check(part1(testInput) == 6440L)
    check(part2(testInput) == 5905L)

    val input = readInput("Day07")
//    part1(input).println()
    part2(input).println()
}
