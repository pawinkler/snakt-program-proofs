package chapter01_basics.exercises

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Kotlin has no multiple return values; use a data class to mirror Viper out-params.
data class IntPair(val a: Int, val b: Int)

// Viper:
//   method MaxSum(x: Int, y: Int) returns (s: Int, m: Int)
//     ensures s == x + y
//     ensures m >= x && m >= y
//     ensures m == x || m == y
//
// Returns IntPair(sum, max).
@AlwaysVerify
fun maxSum(x: Int, y: Int): IntPair {
    postconditions<IntPair> {
        it.a == x + y
        it.b >= x && it.b >= y
        it.b == x || it.b == y
    }
    val m = if (x > y) x else y
    return IntPair(x + y, m)
}

// Viper:
//   method MaxSumCall()
//   { var s, m := MaxSum(1928, 1); assert s == 1929; assert m == 1928 }
@AlwaysVerify
fun maxSumCall() {
    val r = maxSum(1928, 1)
    verify(r.a == 1929)
    verify(r.b == 1928)
}

// Viper:
//   method Reconstruct(s: Int, m: Int) returns (x: Int, y: Int)
//     requires s - m <= m
//     ensures s == x + y
//     ensures m >= x && m >= y
//     ensures m == x || m == y
//
// Returns IntPair(x, y) reconstructed from a known sum and max.
// Precondition ensures the smaller value (s - m) is non-negative and <= m.
@AlwaysVerify
fun reconstruct(s: Int, m: Int): IntPair {
    preconditions { s - m <= m }
    postconditions<IntPair> {
        it.a + it.b == s
        it.a <= m && it.b <= m
        it.a == m || it.b == m
    }
    return IntPair(m, s - m)
}

// Viper:
//   method TestMaxSum(x: Int, y: Int)
//   { var s, m := MaxSum(x, y); var xx, yy := Reconstruct(s, m);
//     assert (xx == x && yy == y) || (xx == y && yy == x) }
//
// The precondition of Reconstruct (s - m <= m) is dischargeable from MaxSum's
// postcondition: m == max(x,y) and m >= x && m >= y imply s - m == min(x,y) <= m.
@AlwaysVerify
fun testMaxSum(x: Int, y: Int) {
    val sm = maxSum(x, y)
    val r = reconstruct(sm.a, sm.b)
    verify((r.a == x && r.b == y) || (r.a == y && r.b == x))
}