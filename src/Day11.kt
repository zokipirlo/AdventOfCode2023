import java.awt.Point
import kotlin.math.abs

fun main() {
    data class GalaxyImage(
        val image: List<List<Int>>,
        val emptyRows: List<Int>,
        val emptyColumns: List<Int>,
        val expansion: Long
    ) {
        val galaxiees = image.mapIndexed { y, row ->
            row.mapIndexedNotNull { x, column ->
                if (column > 0) {
                    Point(x, y)
                } else {
                    null
                }
            }
        }.filterNot { it.isEmpty() }.flatten()


        fun getAllPairs(): List<Pair<Point, Point>> {
            val pairs = mutableListOf<Pair<Point, Point>>()
            for (i in galaxiees.indices) {
                for (j in i + 1 until galaxiees.size) {
                    pairs.add(Pair(galaxiees[i], galaxiees[j]))
                }
            }
            return pairs
        }

        private fun getYDistance(p1: Point, p2: Point): Long {
            return when (p1.y < p2.y) {
                true -> getYDistance(p1.y, p2.y)
                false -> getYDistance(p2.y, p1.y)
            }
        }

        private fun getYDistance(y1: Int, y2: Int): Long {
            val diff = y2 - y1
            val rows = (y1 until y2).filter { emptyRows.contains(it) }
            val rowsExpansion = rows.size * expansion
            println("y1 to y2 -> $y1 to $y2")
            println("rowsExpansion = $rowsExpansion, diff = $diff")
            return rowsExpansion + diff
        }

        private fun getXDistance(p1: Point, p2: Point): Long {
            return when (p1.x < p2.x) {
                true -> getXDistance(p1.x, p2.x)
                false -> getXDistance(p2.x, p1.x)
            }
        }

        private fun getXDistance(x1: Int, x2: Int): Long {
            val diff = x2 - x1
            val columns = (x1 until x2).filter { emptyColumns.contains(it) }
            val columnsExpansion = columns.size * expansion

            println("x1 to x2 -> $x1 to $x2")
            println("columnsExpansion = $columnsExpansion, diff = $diff")

            return columnsExpansion + diff
        }

        fun getDistance(pair: Pair<Point, Point>): Long {
            return getYDistance(pair.first, pair.second) + getXDistance(pair.first, pair.second)
        }
    }

    fun parseGalaxyImage(input: List<String>, addCount: Long): GalaxyImage {
        val emptyRows = mutableListOf<Int>()
        val emptyColumns = mutableListOf<Int>()

        var index = 1
        val galaxyImage = input.map { line ->
            line.map {
                if (it == '.') {
                    0
                } else {
                    index++
                }
            }.toMutableList()
        }.toMutableList()

//        galaxyImage.forEach { line ->
//            println(line.map { it.toString().padStart(2) })
//        }

        val rows = galaxyImage.size
        val columns = galaxyImage[0].size

//        println("rows = $rows, columns = $columns")
//        println("adding columns")
        (0 until columns).forEach { column ->
            val allZeros = (0 until rows).all { row -> galaxyImage[row][column] == 0 }
            if (allZeros) {
                emptyColumns.add(column)
            }
        }

//        println()
//        galaxyImage.forEach { line ->
//            println(line.map { it.toString().padStart(2) })
//        }

//        println("adding rows")
        (0 until rows).forEach { row ->
            val line = galaxyImage[row]
            if (line.allZeros()) {
                emptyRows.add(row)
            }
        }

//        println()
//        galaxyImage.forEach { line ->
//            println(line.map { it.toString().padStart(2) })
//        }

        println("empty rows")
        println(emptyRows)

        println("empty columns")
        println(emptyColumns)

        return GalaxyImage(galaxyImage, emptyRows, emptyColumns, addCount)
    }

    fun part1(input: List<String>): Long {
        val galaxy = parseGalaxyImage(input, 1L)
        val sum = galaxy.getAllPairs().sumOf {
            galaxy.getDistance(it)
        }
        println(sum)
        return sum
    }

    fun part2(input: List<String>, addCount: Long): Long {
        val galaxy = parseGalaxyImage(input, addCount)
        val sum = galaxy.getAllPairs().sumOf {
            galaxy.getDistance(it)
        }
        println(sum)
        return sum
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
//    check(part1(testInput) == 374L)
    check(part2(testInput, 10) == 1030L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input, 1_000_000).println()
}
