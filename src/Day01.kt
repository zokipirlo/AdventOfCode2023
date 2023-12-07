fun main() {
    val regexDigits = "(\\d)".toRegex()
    val regexAll = "(\\d)|(?=((one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)))".toRegex()
    val numMap = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
    )
    fun convertStringDigitToNum(stringDigit: String): Int? = stringDigit.toIntOrNull() ?: numMap[stringDigit]

    fun getCalibrationNumber(regex: Regex, line: String): Int {
        val values = regex.findAll(line)
            .flatMap { it.groupValues }
            .filterNot { it.isEmpty() }
        //println(values)
        val digits = values.mapNotNull { convertStringDigitToNum(it) }
        val num = "${digits.first()}${digits.last()}"
        //println("$line = ${digits.first()} ${digits.last()} = $num")
        return num.toInt()
    }


    fun part1(input: List<String>): Int {
        return input.sumOf { getCalibrationNumber(regexDigits, it) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { getCalibrationNumber(regexAll, it) }
    }

    // test if implementation meets criteria from the description, like:
    // val testInput = readInput("Day01_test")
    // check(part1(testInput) == 1)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
