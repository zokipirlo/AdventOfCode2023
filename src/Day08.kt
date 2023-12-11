import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

typealias NetworkType = MutableMap<String, Pair<String, String>>

fun main() {
    data class NavigationData(val element: String, val instructionIndex: Int, val count: Long)

    fun parseNetwork(input: List<String>): NetworkType {
        val map: NetworkType = mutableMapOf()
        input.forEach {
            map[it.substring(0, 3)] = Pair(it.substring(7, 10), it.substring(12, 15))
        }
        return map
    }

    fun navigate(
        instructions: CharArray,
        network: NetworkType,
    ): Long {
        var instructionIndex = 0
        var current = "AAA"
        var count = 1L
        while (true) {
            val selection = instructions[instructionIndex]
            instructionIndex++
            if (instructionIndex >= instructions.size) {
                instructionIndex = 0
            }

            current = when (selection) {
                'L' -> network[current]!!.first
                'R' -> network[current]!!.second
                else -> throw Exception("Missing element")
            }

            if (current == "ZZZ") {
                break
            }

            count++
        }
        return count
    }

    fun navigateParallel(
        instructions: CharArray,
        network: NetworkType,
        navigationData: NavigationData
    ): NavigationData {
        var instructionIndex = navigationData.instructionIndex
        var current = navigationData.element
        var count = navigationData.count
//        println("navigateParallel start $navigationData")
        while (true) {
            count++
            val selection = instructions[instructionIndex]
            instructionIndex++
            if (instructionIndex >= instructions.size) {
                instructionIndex = 0
            }

            current = when (selection) {
                'L' -> network[current]!!.first
                'R' -> network[current]!!.second
                else -> throw Exception("Missing element")
            }

//            println("navigateParallel iteration $current $instructionIndex $count")

            if (current[2] == 'Z') {
                break
            }
        }
        return NavigationData(current, instructionIndex, count)
    }

    fun fastForward(
        instructions: CharArray,
        network: NetworkType, navigationData: NavigationData, targetCount: Long
    ): NavigationData {
        var instructionIndex = navigationData.instructionIndex
        var current = navigationData.element
        var count = navigationData.count
        while (count < targetCount) {
            count++
            val selection = instructions[instructionIndex]
            instructionIndex++
            if (instructionIndex >= instructions.size) {
                instructionIndex = 0
            }

            current = when (selection) {
                'L' -> network[current]!!.first
                'R' -> network[current]!!.second
                else -> throw Exception("Missing element")
            }
        }
        return NavigationData(current, instructionIndex, count)
    }

    fun checkComplete(results: List<NavigationData>, count: Long): Boolean {
        return results.all { it.count == count }
    }

//    fun checkFfComplete(results: List<NavigationData>): Boolean {
//        return results.all { it.element[2] == 'Z' }
//    }

    fun navigateMultiple(instructions: CharArray, network: NetworkType): Long {
        var count = 0L
        runBlocking(Dispatchers.Default) {
            var startData = network.keys.filter { it.endsWith("A") }.map {
                NavigationData(it, 0, 0L)
            }
            while (true) {
                val results = startData.map { data ->
                    async {
                        val res = navigateParallel(instructions, network, data)
//                        println("Result for $data = $res")
                        res
                    }
                }.awaitAll()
                count = results.maxOf { it.count }
                when {
                    checkComplete(results, count) -> break
                    else -> {
                        count = results.map { it.count }.lcm()
                        break
//                        val diff = results.mapIndexed { index, item ->
//                            item.count - startData[index].count
//                        }
//                        println("Fast forward diff=$diff")
//                        startData = results
//                        val fastForwardResults = results.map {
//                            async {
//                                when (it.count) {
//                                    count -> it
//                                    else -> fastForward(instructions, network, it, count)
//                                }
//                            }
//                        }.awaitAll()
//                        val diff = fastForwardResults.mapIndexed { index, item ->
//                            item.count - startData[index].count
//                        }
//                        println("Fast forward diff=$diff")
//                        when {
//                            checkFfComplete(fastForwardResults) -> break
//                            else -> {
//                                startData = fastForwardResults
//                            }
//                        }
                    }
                }
            }
        }
        return count
    }

    fun part1(input: List<String>): Long {
        val instructions = input[0].toCharArray()
        val networkInput = input.subList(2, input.size)
        val network = parseNetwork(networkInput)
        val res = navigate(instructions, network)
        println(instructions)
        println(network)
        return res
    }

    fun part2(input: List<String>): Long {
        val instructions = input[0].toCharArray()
        val networkInput = input.subList(2, input.size)
        val network = parseNetwork(networkInput)
        val count = navigateMultiple(instructions, network)
        println(instructions)
        println(network)
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day08_test1")
    val testInput2 = readInput("Day08_test2")
    val testInput3 = readInput("Day08_test3")
    check(part1(testInput1) == 2L)
    check(part1(testInput2) == 6L)
    check(part2(testInput3) == 6L)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
