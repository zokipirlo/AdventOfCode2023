import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.absoluteValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun String.splitMultipleChar(escaped: String) = this.split("$escaped+".toRegex())
fun String.splitMultipleSpaces() = this.splitMultipleChar("\\s")
fun String.splitMultipleDots() = this.splitMultipleChar("\\.")
public fun Iterable<Int>.multiply(): Int {
    var sum: Int = 1
    for (element in this) {
        sum *= element
    }
    return sum
}

// https://github.com/ivzb/playground/blob/cf17763f9e4faa4f044b7c5bbc41425dfa8b8c6a/src/main/kotlin/utils/Math.kt
/**
 * Euclid's algorithm for finding the greatest common divisor of a and b.
 */
fun gcd(a: Long, b: Long): Long = if (b == 0L) a.absoluteValue else gcd(b, a % b)
fun gcd(f: Long, vararg n: Long): Long = n.fold(f, ::gcd)
fun Iterable<Long>.gcd(): Long = reduce(::gcd)

fun gcd(a: BigInteger, b: BigInteger): BigInteger = if (b == BigInteger.ZERO) a.abs() else gcd(b, a % b)


/**
 * Find the least common multiple of a and b using the gcd of a and b.
 */
fun lcm(a: Long, b: Long): Long = (a * b) / gcd(a, b)
fun lcm(a: BigInteger, b: BigInteger): BigInteger = (a * b) / gcd(a, b)

fun List<Int>.lcm(): Int = this.map { it.toLong() }.lcm().toInt()
fun IntRange.lcm(): Int = this.toList().lcm()

fun List<Long>.lcm(): Long = this.reduce { acc, n -> lcm(acc, n) }
fun LongRange.lcm(): Long = this.toList().lcm()

infix fun Long.isDivisibleBy(divider: Int): Boolean = this % divider == 0L

infix fun Int.isDivisibleBy(divider: Int): Boolean = this % divider == 0

fun Iterable<Long>.product(): Long = reduce(Long::times)

fun Iterable<Int>.allZeros(): Boolean = all { it == 0 }
