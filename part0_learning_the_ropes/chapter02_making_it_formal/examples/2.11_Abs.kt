package chapter02_making_it_formal.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.postconditions

// Dafny:
//   function Abs(x: int): int {
//     if x < 0 then -x else x
//   }
//
// Dafny `function` is pure/ghost and usable in specifications.
// Translated as @Pure so it can be called inside spec blocks.
@Pure
fun abs(x: Int): Int = if (x < 0) -x else x

// Dafny:
//   method AbsMethod(x: int) returns (y: int)
//     ensures 0 <= y && (x == y || x == -y)
//   {
//     y := Abs(x);
//   }
@AlwaysVerify
fun absMethod(x: Int): Int {
    postconditions<Int> { 0 <= it && (x == it || x == -it) }
    return abs(x)
}