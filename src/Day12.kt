import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    data class SpringRecord(
        val record: String,
        val condition: List<Int>
    ) {
        fun generateNew(record: String): List<String> {
            return listOf(
                record.replaceFirst('?', '.'),
                record.replaceFirst('?', '#')
            )
        }

        private fun generateAllCombinations(): List<String> {
            val combinations: MutableList<String> = mutableListOf(record)
            val questionMarks = record.count { it == '?' }
            if (questionMarks == 0) {
                return combinations
            }
            repeat(questionMarks) {
                repeat(combinations.size) {
                    combinations.addAll(generateNew(combinations.removeFirst()))
                }
            }
//            combinations.println()
            return combinations
        }

        fun generateValidCombinations(): List<String> {
            return generateAllCombinations().filter { isValid(it) }
        }

        fun countValidCombinations(): Int {
            return generateValidCombinations().count()
        }

        fun isValid(record: String = this.record): Boolean {
            val damagedList = record.splitMultipleDots()
//            damagedList.println()
            val damagedParts = damagedList.filter { it.isNotBlank() }.map { it.length }
//            damagedParts.println()
            return damagedParts == condition
        }
    }

    fun parseRecords(input: List<String>, transform: (record: String) -> String): List<SpringRecord> {
        return input.map {
            val transformed = transform(it)
            val (record, condition) = transformed.split(" ")
            SpringRecord(
                record = record,
                condition = condition.split(",").map { it.toInt() }
            )
        }
    }

    fun part1(input: List<String>): Int {
        val records = parseRecords(input) { it }
        return runBlocking(Dispatchers.Default) {
            val combinations = records.map { record ->
                async {
                    record.countValidCombinations()
                }
            }.awaitAll()
            combinations.sum()
        }
    }

    fun part2(input: List<String>): Long {
        val records = parseRecords(input) {
            val (record, condition) = it.split(" ")
            val builder = StringBuilder()
            repeat(5) {
                if (it > 0) {
                    builder.append('?')
                }
                builder.append(record)
            }
            builder.append(" ")
            repeat(5) {
                if (it > 0) {
                    builder.append(',')
                }
                builder.append(condition)
            }
            builder.toString()
        }
        println(records)
        return runBlocking(Dispatchers.Default) {
            val combinations = records.map { record ->
                async {
                    record.countValidCombinations().toLong()
                }
            }.awaitAll()
            combinations.sum()
        }
    }

    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("Day12_testC")
    val testInput = readInput("Day12_test")
//    check(part1(testInput) == 21)
    check(part2(testInput) == 506250L)

    val input = readInput("Day12")
//    part1(input).println()
//    part2(input, 1_000_000).println()
}
