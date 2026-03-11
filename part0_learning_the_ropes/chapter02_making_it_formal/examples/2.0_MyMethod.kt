package chapter02_making_it_formal.examples

import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions

// Dafny:
//   method MyMethod(x: int) returns (y: int)
//     requires 10 <= x
//     ensures 25 <= y
//   {
//     var a, b;
//     a := x + 3;
//     if x < 20 {
//       b := 32 - x;
//     } else {
//       b := 16;
//     }
//     y := a + b;
//   }
fun myMethod(x: Int): Int {
    preconditions { 10 <= x }
    postconditions<Int> { 25 <= it }
    val a = x + 3
    val b = if (x < 20) 32 - x else 16
    return a + b
}