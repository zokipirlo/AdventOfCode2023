private class PatternData(
    val data: String,
    val chars: CharArray = data.toCharArray()
) {
    fun diff(other: PatternData): Int {
        return chars.zip(other.chars).count { pair -> pair.first != pair.second }
    }

    private val hashNum by lazy {
        data.replace('#', '1').replace('.', '0').toInt(2)
    }

    override fun toString(): String {
        return hashNum.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PatternData

        return chars.contentEquals(other.chars)
    }

    override fun hashCode(): Int {
        return chars.contentHashCode()
    }
}

private data class Pattern(
    val horizontal: MutableList<String> = mutableListOf()
) {
    private val data by lazy { horizontal.map { it.toCharArray() }.toTypedArray() }
    private lateinit var hashH: List<PatternData>
    private val hashV = mutableListOf<PatternData>()

    private fun generateHashes() {
        hashH = horizontal.map { string ->
            PatternData(string)
        }
//        println(hashH)

        val width = horizontal.first().count()
        for (x in 0..<width) {
            hashV.add(
                PatternData(
                    buildString {
                        for (y in 0..<data.size) {
                            append(
                                when (data[y][x]) {
                                    '#' -> '1'
                                    else -> '0'
                                }
                            )
                        }
                    }
                )
            )
        }
//        println(hashV)
    }

    private fun checkPatternData(pattern: List<PatternData>, index: Int, size: Int, allowDiff: Int): Int {
        val part1 = pattern.subList(index - size, index)
        val part2 = pattern.subList(index, index + size)
        val reverse = part2.reversed()
        val diffs = part1.zip(reverse).sumOf { pair -> pair.first.diff(pair.second) }
//        println("diffs=$diffs for part1: $part1, part2: $part2 -> $reverse")
        return when (diffs == allowDiff) {
            true -> index
            false -> -1
        }
    }

    private fun checkSubPattern(pattern: List<PatternData>, index: Int, allowDiff: Int): Int {
        val size = minOf(index, pattern.size - index)
        val validIndex = checkPatternData(pattern, index, size, allowDiff)
//        println("valid index $validIndex")
        return validIndex
    }

    private fun findAllEquals(pattern: List<PatternData>, allowDiff: Int): List<Int> {
        return pattern.mapIndexedNotNull { index, value ->
            if (index != 0 && value.diff(pattern[index - 1]) <= allowDiff) index else null
        }
    }

    private fun checkPattern(pattern: List<PatternData>, allowDiff : Int): Int {
        val indexes = findAllEquals(pattern, allowDiff)
//        println("$pattern -> $indexes")
        //firstNotNullOfOrNull
        return indexes.maxOfOrNull { index ->
            checkSubPattern(pattern, index, allowDiff)
        } ?: -1
    }

    fun calculateResult(allowDiff : Int): Int {
        generateHashes()
        val hResult = checkPattern(hashH, allowDiff)
        if (hResult != -1) {
//            println("Horizontal match $hResult")
            return hResult * 100
        }
        val vResult = checkPattern(hashV, allowDiff)
        if (vResult != -1) {
//            println("Vertical match $vResult")
            return vResult
        }
//        println("No match ($hashH $hashV)")
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
        println("Part 1")
        val patterns = parsePatterns(input)
        return patterns.sumOf { pattern -> pattern.calculateResult(0) }
    }

    fun part2(input: List<String>): Int {
        println("Part 2")
        val patterns = parsePatterns(input)
        val sum = patterns.sumOf { pattern -> pattern.calculateResult(1) }
//        println("Part 2 sum $sum")
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
//    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    val part1Res = part1(input)
    check(part1Res == 33735)
    part1(input).println()
    part2(input).println()
}
