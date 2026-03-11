//package chapter03_recursion_and_termination.examples
//
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.preconditions
//
//// NOTE: Dafny's `nat` → `Int` with explicit `0 <= n` preconditions throughout.
//// NOTE: `decreases` clauses are untranslatable — SnaKt does not check termination.
////   Correct Dafny measures are documented in comments for each function.
//// NOTE: SeqSum uses `List<Int>` for Dafny's `seq<int>`, `s.size` for `|s|`.
//
//// --- Examples ---
//
//// Dafny:
////   function Fib(n: nat): nat
////     decreases n
////   { if n < 2 then n else Fib(n - 2) + Fib(n - 1) }
//// Termination: `n` — both n-2 and n-1 are less than n for n >= 2.
//@Pure
//fun fib(n: Int): Int {
//    preconditions { 0 <= n }
//    return if (n < 2) n else fib(n - 2) + fib(n - 1)
//}
//
//// Dafny:
////   function SeqSum(s: seq<int>, lo: int, hi: int): int
////     requires 0 <= lo <= hi <= |s|
////     decreases hi - lo
////   { if lo == hi then 0 else s[lo] + SeqSum(s, lo + 1, hi) }
//// Termination: `hi - lo` strictly decreases on each call.
//@Pure
//fun seqSum(s: List<Int>, lo: Int, hi: Int): Int {
//    preconditions { 0 <= lo && lo <= hi && hi <= s.size }
//    return if (lo == hi) 0 else s[lo] + seqSum(s, lo + 1, hi)
//}
//
//// Dafny:
////   function Ack(m: nat, n: nat): nat
////     // decreases ...?
////   { if m == 0 then n + 1
////     else if n == 0 then Ack(m - 1, 1)
////     else Ack(m - 1, Ack(m, n - 1)) }
//// Termination: lexicographic `m, n` — inner call Ack(m, n-1) has same m but
//// smaller n; outer call Ack(m-1, ...) has strictly smaller m.
//@Pure
//fun ack(m: Int, n: Int): Int {
//    preconditions { 0 <= m && 0 <= n }
//    return when {
//        m == 0 -> n + 1
//        n == 0 -> ack(m - 1, 1)
//        else -> ack(m - 1, ack(m, n - 1))
//    }
//}
//
//// --- Exercises: find the `decreases` clause for each function ---
//// SnaKt accepts all of these regardless (no termination check), but the answers
//// below give the correct Dafny `decreases` expression for each.
//
//// decreases: if x < 10 then 0 else x - 9
//// (x decreases by 1 each call; only recurses when x >= 10)
//@Pure
//fun f(x: Int): Int = if (x < 10) x else f(x - 1)
//
//// decreases: if x < 0 then 0 else x + 2
//// (x decreases by 2 per call; x+2 is nat-valued when x >= 0 and strictly decreasing)
//@Pure
//fun g(x: Int): Int = if (0 <= x) g(x - 2) else x
//
//// decreases: if x < -60 then 0 else x + 61
//// (x decreases by 1; x + 61 is nat-valued when x >= -60)
//@Pure
//fun h(x: Int): Int = if (x < -60) x else h(x - 1)
//
//// decreases: x + y
//// (each recursive call decreases either x or y; both are nat so x + y >= 0)
//@Pure
//fun i(x: Int, y: Int): Int {
//    preconditions { 0 <= x && 0 <= y }
//    return when {
//        x == 0 || y == 0 -> 12
//        x % 2 == y % 2 -> i(x - 1, y)
//        else -> i(x, y - 1)
//    }
//}
//
//// decreases: x, y
//// (lexicographic: either y decreases, or y reaches 0 and x decreases)
//@Pure
//fun j(x: Int, y: Int): Int {
//    preconditions { 0 <= x && 0 <= y }
//    return when {
//        x == 0 -> y
//        y == 0 -> j(x - 1, 3)
//        else -> j(x, y - 1)
//    }
//}
//
//// decreases: x, y + z
//// (when z > 0: z decreases so y + z decreases; when z = 0: x decreases, y+z resets to y+5)
//@Pure
//fun k(x: Int, y: Int, z: Int): Int {
//    preconditions { 0 <= x && 0 <= y && 0 <= z }
//    return when {
//        x < 10 || y < 5 -> x + y
//        z == 0 -> k(x - 1, y, 5)
//        else -> k(x, y - 1, z - 1)
//    }
//}
//
//// decreases: 100 - x
//// (x increases towards 100; 100 - x is nat-valued and strictly decreasing in the recursive branch)
//@Pure
//fun l(x: Int): Int = if (x < 100) l(x + 1) + 10 else x
//
//// decreases: if b then 0 else 1
//// (when b is false, one recursive call with b set to true; never recurses when b is true)
//@Pure
//fun m(x: Int, b: Boolean): Int = if (b) x else m(x + 25, true)
//
//// decreases: x, if b then 1 else 0
//// (when b=true: b flips to false, decreasing second component;
////  when b=false: x decreases by 1 — only recurses when x > 0)
//@Pure
//fun n(x: Int, y: Int, b: Boolean): Int = when {
//    x <= 0 || y <= 0 -> x + y
//    b -> n(x, y + 3, !b)
//    else -> n(x - 1, y, true)
//}