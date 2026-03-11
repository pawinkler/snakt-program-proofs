package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   method Triple(x: int) returns (r: int)
//     ensures r == 3 * x
//   {
//     var y := 2 * x;
//     r := x + y;
//   }
fun triple4(x: Int): Int {
    postconditions<Int> { it == 3 * x }
    val y = 2 * x
    return x + y
}

// Dafny:
//   method Caller() {
//     var t := Triple(18);
//     assert t < 100;
//   }
fun caller() {
    val t = triple4(18)
    verify(t < 100)  // provable: postcondition gives t == 54
}

// Dafny:
//   method Index(n: int) returns (i: int)
//     requires 1 <= n
//     ensures 0 <= i < n
//   {
//     i := n / 2;
//   }
fun index(n: Int): Int {
    preconditions { 1 <= n }
    postconditions<Int> { 0 <= it && it < n }
    return n / 2
}

// Dafny:
//   method Index(n: int) returns (i: int)
//     requires 1 <= n
//     ensures 0 <= i < n
//   {
//     i := 0;
//   }
fun index2(n: Int): Int {
    preconditions { 1 <= n }
    postconditions<Int> { 0 <= it && it < n }
    return 0
}

// Dafny:
//   method Min(x: int, y: int) returns (m: int)
//     ensures m <= x && m <= y
//     ensures m == x || m == y
fun min4(x: Int, y: Int): Int {
    postconditions<Int> {
        it <= x && it <= y
        it == x || it == y
    }
    return if (x <= y) x else y
}