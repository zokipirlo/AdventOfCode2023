fun main() {
    data class SensorData(val data: String) {
        fun calculateDiff(numbers: List<Int>): List<Int> {
            return numbers.mapIndexedNotNull { index, number ->
                when (index) {
                    0 -> null
                    else -> number - numbers[index - 1]
                }
            }
        }

        val numbers = data.split(" ").map { it.toInt() }
        val diffs: MutableList<List<Int>> = mutableListOf(numbers)

        init {
            var lastNumbers = numbers
            while (!lastNumbers.allZeros()) {
                lastNumbers = calculateDiff(lastNumbers)
                diffs.add(lastNumbers)
            }
        }

        fun predictNext(): Int {
            val prediction = diffs.reversed().fold(0) { acc, diffs ->
//                println("$acc ${diffs.last()}")
                acc + diffs.last()
            }
//            println("prediction for $diffs = $prediction")
            return prediction
        }

        fun predictPrev(): Int {
            val prediction = diffs.reversed().fold(0) { acc, diffs ->
//                println("$acc ${diffs.last()}")
                diffs.first() - acc
            }
//            println("prediction for $diffs = $prediction")
            return prediction
        }
    }


    fun part1(input: List<String>): Int {
        val predictions = input.map { SensorData(it).predictNext() }
        return predictions.sum()
    }

    fun part2(input: List<String>): Int {
        val predictions = input.map { SensorData(it).predictPrev() }
        return predictions.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
