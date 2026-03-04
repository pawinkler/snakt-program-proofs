package chapter02_making_it_formal.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.postconditions

// Dafny:
//   method MyMethod(x: int) returns (y: int)
//     requires 10 <= x
//     ensures 25 <= y
//   {
//     var a := x + 3;
//     var b := 12;
//     y := a + b;
//   }
@AlwaysVerify
fun myMethod1(x: Int): Int {
    preconditions { 10 <= x }
    postconditions<Int> { 25 <= it }
    val a = x + 3
    val b = 12
    return a + b
}