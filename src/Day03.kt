fun main() {
    data class NumberRes(val number: Int, val line: Int, val range: IntRange)
    data class SymbolRes(val line: Int, val range: IntRange) {
        val id = "$line${range.last}".toInt()
    }

    val symbolRegex1 = "[\\D&&[^\\.]&&[^\\n]]".toRegex()
    val symbolRegex2 = "\\*".toRegex()

    fun parseNumbers(input: List<String>): List<NumberRes> {
        val regex = "\\d*".toRegex()
        return input.flatMapIndexed { lineIndex, line ->
            val results = regex.findAll(line)
            results.mapNotNull { result ->
                result.value.toIntOrNull()?.let {
                    NumberRes(it, lineIndex, result.range)
                }
            }.toList()
        }
    }

    fun parseSymbols(regex: Regex, input: List<String>): List<List<SymbolRes>> {
        return input.mapIndexed { lineIndex, line ->
            val results = regex.findAll(line)
            results.mapNotNull { result ->
                if (result.value.isNotEmpty()) {
                    SymbolRes(lineIndex, result.range)
                } else {
                    null
                }
            }.toList()
        }
    }

    fun symbolOnPosition(number: IntRange, symbols: List<SymbolRes>?): SymbolRes? {
        return symbols?.find { number.contains(it.range.first) || number.contains(it.range.last) }
    }

    fun getSymbolAdjacent(number: NumberRes, symbols: List<List<SymbolRes>>): SymbolRes? {
        val fixedRange = IntRange(number.range.first - 1, number.range.last + 1)
        intArrayOf(number.line - 1, number.line, number.line + 1).forEach { line ->
            val symbol = symbolOnPosition(fixedRange, symbols.getOrNull(line))
            if (symbol != null) {
                return symbol
            }
        }
        return null
    }

    fun isSymbolAdjacent(number: NumberRes, symbols: List<List<SymbolRes>>): Boolean {
        return getSymbolAdjacent(number, symbols) != null
    }


    fun part1(input: List<String>): Int {
        val symbols = parseSymbols(symbolRegex1, input)
        val numbers = parseNumbers(input)

        // println(numbers)
        // println(symbols)

        val filtered = numbers.filter { isSymbolAdjacent(it, symbols) }

        // println(filtered)

        return filtered.sumOf { it.number }
    }

    fun part2(input: List<String>): Int {
        val symbols = parseSymbols(symbolRegex2, input)
        val numbers = parseNumbers(input)

        val symbolAdjacent = numbers
            .map { it to getSymbolAdjacent(it, symbols)?.id }
            .asSequence()
            .filterNot {
                it.second == null
            }
            .groupBy(
                { it.second },
                { it.first.number }
            )
            .filter {
                it.value.size == 2
            }
            .mapValues { it.value[0] * it.value[1] }

        // println(symbolAdjacent)

        return symbolAdjacent.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")

    part1(input).println()
    part2(input).println()
}
