//package chapter03_recursion_and_termination.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.postconditions
//
//// This file demonstrates termination failures that Dafny catches.
////
//// KEY GAP: SnaKt/Viper does NOT perform termination checking. Dafny requires
//// all functions to terminate by default (proved via `decreases` clauses).
//// In Viper, recursive calls are axiomatised — the verifier assumes they satisfy
//// their postconditions and does not check that they actually terminate.
//// As a result, all functions below are ACCEPTED by SnaKt even though they are
//// non-terminating. This is a known limitation vs Dafny.
//
//// Dafny:
////   method BadDouble(x: int) returns (d: int)
////     ensures d == 2 * x
////   {
////     var y := BadDouble(x - 1); // error: failure to prove termination
////     d := y + 2;
////   }
////
//// SnaKt: verifies (incorrectly) — the recursive call is assumed to satisfy
//// `y == 2*(x-1)`, so `d = y + 2 == 2*x` follows. No termination check.
//@AlwaysVerify
//fun badDouble(x: Int): Int {
//    postconditions<Int> { it == 2 * x }
//    val y = badDouble(x - 1)
//    return y + 2
//}
//
//// Dafny:
////   method PartialId(x: int) returns (y: int)
////     ensures y == x
////   {
////     if x % 2 == 0 {
////       y := x;
////     } else {
////       y := PartialId(x); // error: failure to prove termination
////     }
////   }
////
//// SnaKt: verifies (incorrectly) — recursive call axiom gives `y == x` directly.
//@AlwaysVerify
//fun partialId(x: Int): Int {
//    postconditions<Int> { it == x }
//    return if (x % 2 == 0) x else partialId(x)
//}
//
//// Dafny:
////   method Squarish(x: int, guess: int) returns (y: int)
////     ensures x * x == y
////   {
////     if
////     case guess == x * x => y := guess;
////     case true => y := Squarish(x, guess - 1); // error: failure to prove termination
////     case true => y := Squarish(x, guess + 1); // error: failure to prove termination
////   }
////
//// NOTE: Dafny's non-deterministic `if/case` (with multiple `case true` branches)
//// has no Kotlin equivalent. Translated as a deterministic `when`, arbitrarily
//// choosing the `guess - 1` branch as the fallback. The verification gap (no
//// termination check) is the same regardless of which recursive branch is chosen.
////
//// SnaKt: verifies (incorrectly) — recursive call axiom gives `x * x == result`.
//@AlwaysVerify
//fun squarish(x: Int, guess: Int): Int {
//    postconditions<Int> { x * x == it }
//    return when {
//        guess == x * x -> guess
//        else -> squarish(x, guess - 1)
//    }
//}
//
//// Dafny:
////   method Impossible(x: int) returns (y: int)
////     ensures y % 2 == 0 && y == 10 * x - 3
////   {
////     y := Impossible(x); // error: failure to prove termination
////   }
////
//// The postcondition is mathematically impossible: `10*x - 3` is always odd,
//// never even. Dafny rejects it only due to the termination failure; if
//// termination were somehow assumed, it would be a vacuous proof (false premise
//// implies anything). SnaKt similarly "verifies" it via the circular axiom.
//@AlwaysVerify
//fun impossible(x: Int): Int {
//    postconditions<Int> { it % 2 == 0 && it == 10 * x - 3 }
//    return impossible(x)
//}
//
//// Dafny:
////   function Dubious(): int {
////     1 + Dubious() // error: failure to prove termination
////   }
////
//// SnaKt: verifies (incorrectly) — same circular-axiom issue for @Pure functions.
//@Pure
//fun dubious(): Int = 1 + dubious()