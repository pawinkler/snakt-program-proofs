package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun triple3(x: Int): Int {
    val r: Int
    if (x == 0) {
        r = 0
    } else {
        val y = 2 * x
        r = x + y
    }
    verify(r == 3 * x)
    return r
}

// Dafny uses a non-deterministic if: overlapping guards (0 <= x < 18) may take either branch.
// Kotlin's when is sequential, so the first matching case wins.
@AlwaysVerify
fun triple3b(x: Int): Int {
    val r: Int = when {
        x < 18 -> {
            val a = 2 * x
            val b = 4 * x
            (a + b) / 2
        }
        else -> {  // 0 <= x
            val y = 2 * x
            x + y
        }
    }
    verify(r == 3 * x)
    return r
}