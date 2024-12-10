private const val DAY = "Day14"

private enum class Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST;

    fun next() = when(this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }
}

private class Dish(input: List<String>) {
    val data = input.map { it.toCharArray() }.toTypedArray()
    private val maxY = data.lastIndex
    private val maxX = data[0].lastIndex
    private var direction = Direction.NORTH
    private val cache = mutableMapOf<String, Int>()

    fun findPositionNorth(x: Int, y: Int): Int {
        var findY = y
        while (findY > 0) {
            if (data[findY - 1][x] != '.') {
                break
            }
            findY--
        }
        return findY
    }

    fun findPositionSouth(x: Int, y: Int): Int {
        var findY = y
        while (findY < maxY) {
            if (data[findY + 1][x] != '.') {
                break
            }
            findY++
        }
        return findY
    }

    fun findPositionWest(x: Int, y: Int): Int {
        var findX = x
        while (findX > 0) {
            if (data[y][findX - 1] != '.') {
                break
            }
            findX--
        }
        return findX
    }

    fun findPositionEast(x: Int, y: Int): Int {
        var findX = x
        while (findX < maxX) {
            if (data[y][findX + 1] != '.') {
                break
            }
            findX++
        }
        return findX
    }

    fun moveItem(x: Int, y: Int, direction: Direction) {
        data[y][x] = '.'
        val yPos = when (direction) {
            Direction.NORTH -> findPositionNorth(x, y)
            Direction.WEST -> y
            Direction.SOUTH -> findPositionSouth(x, y)
            Direction.EAST -> y
        }
        val xPos = when (direction) {
            Direction.NORTH -> x
            Direction.WEST -> findPositionWest(x, y)
            Direction.SOUTH -> x
            Direction.EAST -> findPositionEast(x, y)
        }
        data[yPos][xPos] = 'O'
    }

    fun reshuffle() {
        when (direction) {
            Direction.NORTH,
            Direction.WEST -> {
                data.forEachIndexed { y, line ->
                    line.forEachIndexed { x, item ->
                        if (item == 'O') {
                            moveItem(x, y, direction)
                        }
                    }
                }
            }

            Direction.SOUTH,
            Direction.EAST -> {
                data.indices.reversed().forEach { y ->
                    data[y].indices.reversed().forEach { x ->
                        val item = data[y][x]
                        if (item == 'O') {
                            moveItem(x, y, direction)
                        }
                    }
                }
            }
        }
    }

    fun cycle() {
        repeat(4) {
            reshuffle()
            direction = direction.next()
        }
    }

    fun calculateLoad(): Int {
        var row = data.size
        var sum = 0
        data.forEachIndexed { y, line ->
            val count = line.count { ch -> ch == 'O' }
            sum += count * row
            row--
        }
        return sum
    }

    fun calculateHash(): String {
        return data.contentDeepToString()
    }

    fun repeatCycles(total:Int) {
        repeat(total) { currentCycle ->
            val newHash = calculateHash()
            val foundInCacheCycle = cache[newHash]
            when (foundInCacheCycle) {
                null -> cache[newHash] = currentCycle
                else -> {
                    // found loop
                    val cycleLength = currentCycle - foundInCacheCycle
                    val remaining = (total - currentCycle) % cycleLength
//                    println("found $cycleLength $currentCycle $foundInCacheCycle $remaining")
                    repeat(remaining) {
                        cycle()
                    }
                    return
                }
            }
            cycle()
        }
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        val dish = Dish(input)
        dish.reshuffle()
        return dish.calculateLoad()
    }


    fun part2(input: List<String>): Int {
        val dish = Dish(input)
        dish.repeatCycles(1000000000)
//        dish.repeatCycles(102)
        return dish.calculateLoad()
    }

    val testInput = readInput("${DAY}_test")
    check(part1(testInput) == 136)
    check(part2(testInput) == 64)

    val input = readInput(DAY)
    check(part1(input) == 109654)
    part1(input).println()
    part2(input).println()
}
