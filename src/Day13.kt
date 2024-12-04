private data class Pattern(
    val horizontal: MutableList<String> = mutableListOf()
) {
    private val data by lazy { horizontal.map { it.toCharArray() }.toTypedArray() }
    private lateinit var hashH: List<Int>
    private val hashV = mutableListOf<Int>()

    private fun generateHashes() {
        hashH = horizontal.map { string ->
            string.replace('#', '1').replace('.', '0').toInt(2)
        }
//        println(hashH)

        val width = horizontal.first().count()
        for (x in 0..<width) {
            hashV.add(
                buildString {
                    for (y in 0..<data.size) {
                        append(
                            when (data[y][x]) {
                                '#' -> '1'
                                else -> '0'
                            }
                        )
                    }
                }.toInt(2)
            )
        }
//        println(hashV)
    }

    private fun checkPatternData(pattern: List<Int>, index: Int, size: Int): Int {
        val part1 = pattern.subList(index - size, index)
        val part2 = pattern.subList(index, index + size)
        val reverse = part2.reversed()
        val isEqual = part1 == reverse
//        println("equal=$isEqual for part1: $part1, part2: $part2 -> $reverse")
        return when (isEqual) {
            true -> index
            false -> -1
        }
    }

    private fun checkSubPattern(pattern: List<Int>, index: Int): Int {
        val size = minOf(index, pattern.size - index)
        return checkPatternData(pattern, index, size)
    }

    private fun findAllEquals(pattern: List<Int>): List<Int> {
        return pattern.mapIndexedNotNull { index, value ->
            if (index != 0 && value == pattern[index - 1]) index else null
        }
    }

    private fun checkPattern(pattern: List<Int>): Int {
        val indexes = findAllEquals(pattern)
//        println("$pattern -> $indexes")
        return indexes.maxOfOrNull { index ->
            checkSubPattern(pattern, index)
        } ?: -1
    }

    fun calculateResult(): Int {
        generateHashes()
        val hResult = checkPattern(hashH)
        if (hResult != -1) {
//            println("Horizontal match $hResult")
            return hResult * 100
        }
        val vResult = checkPattern(hashV)
        if (vResult != -1) {
//            println("Vertical match $vResult")
            return vResult
        }
        println("No match ($hashH $hashV)")
        return 0
    }
}

fun main() {
    fun parsePatterns(input: List<String>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        input.foldIndexed(Pattern()) { index, pattern, line ->
            if (index == 0) {
                patterns.add(pattern)
            }
            if (line.isEmpty()) {
                Pattern().also { patterns.add(it) }
            } else {
                pattern.apply { horizontal.add(line) }
            }
        }
        return patterns
    }


    fun part1(input: List<String>): Int {
        val patterns = parsePatterns(input)
        return patterns.sumOf { pattern -> pattern.calculateResult() }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)

    val input = readInput("Day13")
    check(part1(input) == 33735)
    part1(input).println()
//    part2(input).println()
}
