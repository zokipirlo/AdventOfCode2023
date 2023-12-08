import kotlinx.coroutines.*
import kotlin.time.measureTimedValue

fun main() {
    data class ConvertRule(
        val originalRange: LongRange,
        val destinationOffset: Long
    )

    data class RuleMap(
        val name: String,
        val maps: List<ConvertRule>
    ) {
        fun getDestination(origin: Long): Long {
            val range = maps.find { it.originalRange.contains(origin) }
            return when (range) {
                null -> origin
                else -> origin + range.destinationOffset
            }
        }
    }

    data class Almanac(
        val seeds: List<Long>,
        val rules: List<RuleMap>
    )

    fun parseMap(name: String, input: List<String>): RuleMap {
        val allRules = input.takeWhile { it.isNotEmpty() }
        val maps = allRules.map { line ->
            val (dest, orig, length) = line.split(" ").map { it.toLong() }
            ConvertRule(
                originalRange = LongRange(orig, orig + length - 1),
                destinationOffset = dest - orig
            )
        }
        return RuleMap(
            name = name,
            maps = maps
        )
    }

    fun parseSeeds(line: String): List<Long> {
        return line
            .removePrefix("seeds:")
            .trim()
            .split(" ")
            .map { it.toLong() }
    }

    fun parseAlmanac(input: List<String>): Almanac {
        val seeds = mutableListOf<Long>()
        val maps = mutableListOf<RuleMap>()
        input.forEachIndexed { index, line ->
            when {
                line.startsWith("seeds:") -> {
                    seeds.addAll(parseSeeds(line))
                }

                line.endsWith("map:") -> {
                    val name = line.substringBefore("map:").trim()
                    maps.add(parseMap(name, input.subList(index + 1, input.size)))
                }
            }
        }
        return Almanac(
            seeds = seeds,
            rules = maps
        )
    }

    fun getLastDestination(seed: Long, rules: List<RuleMap>): Long {
        val allResults = rules.runningFold(seed) { orig, rule -> rule.getDestination(orig) }
//        println("follow rules seed $seed: $allResults")
        return allResults.last()
    }

    fun part1(input: List<String>): Long {
        val almanac = parseAlmanac(input)
        println(almanac)
        return almanac.seeds.minOf { getLastDestination(it, almanac.rules) }
    }

    fun splitRange(range: LongRange): List<LongRange> {
        val half = (range.last - range.first) / 2
        val first = LongRange(
            range.first,
            range.first + half
        )
        return listOf(
            first,
            LongRange(
                first.last + 1,
                range.last
            )
        )
    }

    fun rangeSeeds(seeds: List<Long>): List<LongRange> {
        val ranges = seeds
            .chunked(2)
            .map { (start, len) ->
                val range = LongRange(start, start + len - 1)
                splitRange(range).map { splitRange(it) }.flatten()
//                range
            }
            .flatten()
        return ranges
    }

    fun part2(input: List<String>): Long {
        return runBlocking {
            val almanac = parseAlmanac(input)
            val time = measureTimedValue {
                withContext(Dispatchers.Default) {
                    val results = rangeSeeds(almanac.seeds).map { range ->
                        async {
                            val min = range.minOf { getLastDestination(it, almanac.rules) }
                            println("min for range: $range = $min")
                            min
                        }
                    }.awaitAll()
                    results.min()
                }
            }
            println("time ${time.duration}")
            time.value
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
//    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
//    part1(input).println()
    part2(input).println()
}
