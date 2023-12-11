import java.awt.Polygon

data class Area(
    val pipeConnections: List<List<PipeConnection>>
) {
    var visitOrderIndex = 0
    var loopCompleted = false

    private fun visitNeighbors(posX: Int, posY: Int) {
//        println("visitNeighbors posX=$posX, posY=$posY")
        var x = posX
        var y = posY

        while (!loopCompleted) {
            val item = pipeConnections[y][x]
            item.updateValue(this)

            when {
                canMoveLeft(item) -> x--
                canMoveRight(item) -> x++
                canMoveUp(item) -> y--
                canMoveDown(item) -> y++
            }
        }
    }

    fun checkArea() {
        val oneDimension = pipeConnections.flatten()
        val start = oneDimension.find { it is PipeConnection.S } ?: throw Exception("Missing start")
        visitNeighbors(start.posX, start.posY)
    }

    //    private fun isLoopLineTop(yPos: Int, xPos: Int): Boolean {
//        if (yPos == 0) {
//            return false
//        }
//        return (yPos downTo 0).find { pipeConnections[it][xPos].count != null } != null
//    }
//
//    private fun firstNeighborTop(yPos: Int, xPos: Int): Int? {
//        if (yPos == 0) {
//            return null
//        }
//        return (yPos downTo 0).map { pipeConnections[it][xPos].count }.first { it == null || it > 0 }
//    }
//
//    private fun isLoopLineBottom(yPos: Int, xPos: Int): Boolean {
//        val length = pipeConnections.size
//        if (yPos >= length - 1) {
//            return false
//        }
//        return (yPos until length).find { pipeConnections[it][xPos].count != null } != null
//    }
//
//    private fun firstNeighborBottom(yPos: Int, xPos: Int): Int? {
//        val length = pipeConnections.size
//        if (yPos >= length - 1) {
//            return null
//        }
//        return (yPos until length).map { pipeConnections[it][xPos].count }.first { it == null || it > 0 }
//    }
//
//    private fun isLoopLineLeft(yPos: Int, xPos: Int): Boolean {
//        if (xPos == 0) {
//            return false
//        }
//        return (xPos downTo 0).find { pipeConnections[yPos][it].count != null } != null
//    }
//
//    private fun firstNeighborLeft(yPos: Int, xPos: Int): Int? {
//        if (xPos == 0) {
//            return null
//        }
//        return (xPos downTo 0).map { pipeConnections[yPos][it].count }.first { it == null || it > 0 }
//    }
//
//    private fun isLoopLineRight(yPos: Int, xPos: Int): Boolean {
//        val length = pipeConnections[yPos].size
//        if (xPos >= length - 1) {
//            return false
//        }
//        return (xPos until length).find { pipeConnections[yPos][it].count != null } != null
//    }
//
//    private fun firstNeighborRight(yPos: Int, xPos: Int): Int? {
//        val length = pipeConnections[yPos].size
//        if (xPos >= length - 1) {
//            return null
//        }
//        return (xPos until length).map { pipeConnections[yPos][it].count }.first { it == null || it > 0 }
//    }
//
//    private fun isTileInside(xPos: Int, yPos: Int): Boolean {
//        if (pipeConnections[yPos][xPos].count != null) {
//            return false
//        }
//        return isLoopLineTop(yPos, xPos) && isLoopLineBottom(yPos, xPos) &&
//                isLoopLineLeft(yPos, xPos) && isLoopLineRight(yPos, xPos)
//    }
//
//    private fun isTileOutside(xPos: Int, yPos: Int): Boolean {
//        if (pipeConnections[yPos][xPos].count != -1) {
//            return false
//        }
//        return firstNeighborTop(yPos, xPos) == null || firstNeighborBottom(yPos, xPos) == null ||
//                firstNeighborLeft(yPos, xPos) == null || firstNeighborRight(yPos, xPos) == null
//    }
//
    private fun constructPolygon(): Polygon {
        val polygon = Polygon()
        pipeConnections.flatten().filter { it.count != null }.sortedBy { it.count }.forEach {
            polygon.addPoint(it.posX, it.posY)
        }
        return polygon
    }

    fun mapAllInside() {
        val polygon = constructPolygon()
        pipeConnections.forEachIndexed { yPos, itemY ->
            itemY.forEachIndexed { xPos, itemX ->
                if (itemX.count == null && polygon.contains(xPos, yPos)) {
                    itemX.count = -1
                }
            }
        }
    }
//
//    fun mapAllOutside() {
//        pipeConnections.forEachIndexed { yPos, itemY ->
//            itemY.forEachIndexed { xPos, itemX ->
//                if (isTileOutside(xPos, yPos)) {
//                    itemX.count = -2
//                }
//            }
//        }
//    }

    fun countAllInside(): Int {
        return pipeConnections.flatten().count { it.count == -1 }
    }

    fun getMaxValue() = pipeConnections.flatten().maxOf { it.count ?: 0 }

    fun canMoveUp(item: PipeConnection): Boolean {
        val itemT = pipeConnections.getOrNull(item.posY - 1)?.get(item.posX)
        return item.canMoveUp(itemT)
    }

    fun canMoveDown(item: PipeConnection): Boolean {
        val itemB = pipeConnections.getOrNull(item.posY + 1)?.get(item.posX)
        return item.canMoveDown(itemB)
    }

    fun canMoveLeft(item: PipeConnection): Boolean {
        val itemL = pipeConnections[item.posY].getOrNull(item.posX - 1)
        return item.canMoveLeft(itemL)
    }

    fun canMoveRight(item: PipeConnection): Boolean {
        val itemR = pipeConnections[item.posY].getOrNull(item.posX + 1)
        return item.canMoveRight(itemR)
    }
}

sealed class PipeConnection(val posX: Int, val posY: Int) {
    var visited = false
    var count: Int? = null
    var visitOrder: Int? = null

    abstract fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection>

    open fun updateValue(area: Area) {
//        println("Visited posX=$posX posY=$posY visitOrder=${area.visitOrderIndex}")
        visited = true
        visitOrder = ++area.visitOrderIndex
        val neighbors = getNeighbors(area.pipeConnections).mapNotNull { it.count }
        if (neighbors.size == 2) {
            area.loopCompleted = true
        } else if (neighbors.isEmpty()) {
            return
        }

        count = neighbors.max() + 1
    }

    open fun canMoveUp(itemT: PipeConnection?): Boolean {
        return itemT != null && !itemT.visited && itemT !is G
    }

    open fun canMoveDown(itemB: PipeConnection?): Boolean {
        return itemB != null && !itemB.visited && itemB !is G
    }

    open fun canMoveLeft(itemL: PipeConnection?): Boolean {
        return itemL != null && !itemL.visited && itemL !is G
    }

    open fun canMoveRight(itemR: PipeConnection?): Boolean {
        return itemR != null && !itemR.visited && itemR !is G
    }

    override fun toString(): String {
//        return "PipeConnection(posX=$posX, posY=$posY, visited=$visited, count=$count)"
        return when (count) {
            null -> "[---]"
            else -> "[" + count.toString().padStart(3) + "]"
        }
//        return "[$visited $visitOrder $count)]"
    }

    class NS(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return listOfNotNull(
                pipeConnections.getOrNull(posY - 1)?.get(posX),
                pipeConnections.getOrNull(posY + 1)?.get(posX),
            )
        }

        override fun canMoveLeft(itemL: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveRight(itemR: PipeConnection?): Boolean {
            return false
        }
    }

    class EW(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return listOfNotNull(
                pipeConnections[posY].getOrNull(posX - 1),
                pipeConnections[posY].getOrNull(posX + 1),
            )
        }

        override fun canMoveUp(itemT: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveDown(itemD: PipeConnection?): Boolean {
            return false
        }
    }

    class NE(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return listOfNotNull(
                pipeConnections[posY].getOrNull(posX + 1),
                pipeConnections.getOrNull(posY - 1)?.get(posX),
            )
        }

        override fun canMoveDown(itemD: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveLeft(itemL: PipeConnection?): Boolean {
            return false
        }
    }

    class NW(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return listOfNotNull(
                pipeConnections[posY].getOrNull(posX - 1),
                pipeConnections.getOrNull(posY - 1)?.get(posX),
            )
        }

        override fun canMoveDown(itemD: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveRight(itemR: PipeConnection?): Boolean {
            return false
        }
    }

    class SW(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return listOfNotNull(
                pipeConnections[posY].getOrNull(posX - 1),
                pipeConnections.getOrNull(posY + 1)?.get(posX),
            )
        }

        override fun canMoveUp(itemT: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveRight(itemR: PipeConnection?): Boolean {
            return false
        }
    }

    class SE(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return listOfNotNull(
                pipeConnections[posY].getOrNull(posX + 1),
                pipeConnections.getOrNull(posY + 1)?.get(posX),
            )
        }

        override fun canMoveUp(itemT: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveLeft(itemL: PipeConnection?): Boolean {
            return false
        }
    }

    class G(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun updateValue(area: Area) {
            visitOrder = ++area.visitOrderIndex
            count = null
            visited = true
        }

        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return emptyList()
        }

        override fun canMoveUp(itemT: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveDown(itemD: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveLeft(itemL: PipeConnection?): Boolean {
            return false
        }

        override fun canMoveRight(itemR: PipeConnection?): Boolean {
            return false
        }
    }

    class S(posX: Int, posY: Int) : PipeConnection(posX, posY) {
        override fun updateValue(area: Area) {
            visitOrder = ++area.visitOrderIndex
            count = 0
            visited = true
        }

        override fun canMoveUp(itemT: PipeConnection?): Boolean {
            return itemT != null && (itemT is NS || itemT is SE || itemT is SW)
        }

        override fun canMoveDown(itemB: PipeConnection?): Boolean {
            return itemB != null && (itemB is NS || itemB is NE || itemB is NW)
        }

        override fun canMoveLeft(itemL: PipeConnection?): Boolean {
            return itemL != null && (itemL is EW || itemL is SE || itemL is NE)
        }

        override fun canMoveRight(itemR: PipeConnection?): Boolean {
            return itemR != null && (itemR is EW || itemR is SW || itemR is NW)
        }

        override fun getNeighbors(pipeConnections: List<List<PipeConnection>>): List<PipeConnection> {
            return emptyList()
        }
    }
}

fun main() {
    fun mapConnections(input: List<String>) = input.mapIndexed { posY, line ->
        line.toCharArray().mapIndexed { posX, c ->
            when (c) {
                '|' -> PipeConnection.NS(posX, posY)
                '-' -> PipeConnection.EW(posX, posY)
                'L' -> PipeConnection.NE(posX, posY)
                'J' -> PipeConnection.NW(posX, posY)
                '7' -> PipeConnection.SW(posX, posY)
                'F' -> PipeConnection.SE(posX, posY)
                '.' -> PipeConnection.G(posX, posY)
                'S' -> PipeConnection.S(posX, posY)
                else -> throw Exception("Unknown symbol")
            }
        }
    }

    fun part1(input: List<String>): Int {
        val area = Area(mapConnections(input))
        area.checkArea()
        val max = area.getMaxValue()
        return (max + 1) / 2
    }

    fun part2(input: List<String>): Int {
        val area = Area(mapConnections(input))
//        println("parsed")
//        area.pipeConnections.forEach {
//            it.println()
//        }
//        println("check area")
        area.checkArea()
//        area.pipeConnections.forEach {
//            it.println()
//        }

//        println("inside")
        area.mapAllInside()
//        area.pipeConnections.forEach {
//            it.println()
//        }
        val count = area.countAllInside()
//        count.println()
        return count
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day10_test1")
    val testInput2 = readInput("Day10_test2")
    val testInput3 = readInput("Day10_test3")
//    check(part1(testInput1) == 4)
//    check(part1(testInput2) == 8)

    check(part2(testInput1) == 4)
    check(part2(testInput2) == 8)
    check(part2(testInput3) == 10)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
