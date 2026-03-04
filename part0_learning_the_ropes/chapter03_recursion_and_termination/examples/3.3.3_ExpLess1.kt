package chapter03_recursion_and_termination.examples

import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.preconditions

// NOTE: Dafny's `nat` is a non-negative integer. Kotlin has no `nat` type;
// `Int` is used throughout with explicit `0 <= n` preconditions where needed.
//
// NOTE: `decreases` clauses (termination metrics) cannot be translated —
// SnaKt does not check termination. The mutual recursion in ExpLess1/ExpLess2
// is accepted by the verifier via circular axiomatisation (same gap as
// 3.0_EndlessProblems), even though Dafny proves it terminates via the
// lexicographic measure (n, 1) > (n, 0) > (n-1, 1).

// Dafny:
//   function ExpLess1S(n: nat): nat {
//     if n == 0 then 0 else 2 * ExpLess1S(n - 1) + 1
//   }
@Pure
fun expLess1S(n: Int): Int {
    preconditions { 0 <= n }
    return if (n == 0) 0 else 2 * expLess1S(n - 1) + 1
}

// Dafny:
//   function ExpLess1(n: nat): nat
//     decreases n, 1
//   {
//     if n == 0 then 0 else ExpLess2(n) + 1
//   }
@Pure
fun expLess1(n: Int): Int {
    preconditions { 0 <= n }
    return if (n == 0) 0 else expLess2(n) + 1
}

// Dafny:
//   function ExpLess2(n: nat): nat
//     requires 1 <= n
//     decreases n, 0
//   {
//     2 * ExpLess1(n - 1)
//   }
@Pure
fun expLess2(n: Int): Int {
    preconditions { 1 <= n }
    return 2 * expLess1(n - 1)
}