package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.preconditions

// Dafny:
//   method Min(x: int, y: int) returns (m: int)
//     ensures m <= x && m <= y
//     ensures m == x || m == y
@AlwaysVerify
fun min(x: Int, y: Int): Int {
    postconditions<Int> {
        it <= x && it <= y
        it == x || it == y
    }
    return if (x <= y) x else y
}

// Dafny:
//   function Average(a: int, b: int): int
//     requires 0 <= a && 0 <= b
//   {
//     (a + b) / 2
//   }
//
// Dafny `function` = pure, usable in specifications.
@Pure
@AlwaysVerify
fun average(a: Int, b: Int): Int {
    preconditions { 0 <= a && 0 <= b }
    return (a + b) / 2
}

// Dafny:
//   predicate IsEven(x: int) {
//     x % 2 == 0
//   }
//
// Dafny `predicate` = pure boolean function, usable in specifications.
@Pure
@AlwaysVerify
fun isEven(x: Int): Boolean = x % 2 == 0