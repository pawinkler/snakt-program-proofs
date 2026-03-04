package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   function Reduce(m: nat, x: int): int {
//     if m == 0 then x else Reduce(m / 2, x + 1) - m
//   }
//
// Termination: `m` strictly decreases (m/2 < m for m > 0 with integer division).
// Not checked by SnaKt (see ch. 3). `/` is integer (floor) division throughout.
@Pure
fun reduce(m: Int, x: Int): Int {
    preconditions { 0 <= m }
    return if (m == 0) x else reduce(m / 2, x + 1) - m
}

// Dafny:
//   lemma {:induction false} ReduceUpperBound(m: nat, x: int)
//     ensures Reduce(m, x) <= x
//   {
//     if m == 0 { } else {
//       calc {
//         Reduce(m, x);
//       ==  // def. Reduce
//         Reduce(m/2, x+1) - m;
//       <=  { ReduceUpperBound(m/2, x+1); assert Reduce(m/2, x+1) <= x+1; }
//         x + 1 - m;
//       <=  { assert 0 < m; }
//         x;
//       }
//     }
//   }
//
// NOTE: `calc` has no SnaKt equivalent (see 5_Increasing_Proof6.kt).
// Base case (m == 0): reduce(0, x) = x, so x <= x. Trivial; no hint needed.
@AlwaysVerify
fun reduceUpperBound(m: Int, x: Int) {
    preconditions { 0 <= m }
    postconditions<Unit> { reduce(m, x) <= x }
    if (m == 0) {
        // trivial: reduce(0, x) = x <= x
    } else {
        // calc chain: reduce(m,x)  ==  reduce(m/2,x+1)-m  <=  x+1-m  <=  x
        verify(reduce(m, x) == reduce(m / 2, x + 1) - m)   // def. reduce
        reduceUpperBound(m / 2, x + 1)                       // IH: reduce(m/2,x+1) <= x+1
        verify(reduce(m / 2, x + 1) <= x + 1)               // from IH postcondition
        verify(x + 1 - m <= x)                               // since 0 < m  (arithmetic)
    }
}

// Dafny:
//   lemma {:induction false} ReduceLowerBound(m: nat, x: int)
//     ensures x - 2 * m <= Reduce(m, x)
//   {
//     if m == 0 { } else {
//       calc {
//         Reduce(m, x);
//       ==  // def. Reduce
//         Reduce(m/2, x+1) - m;
//       >=  { ReduceLowerBound(m/2, x+1);
//             assert x+1-2*(m/2) <= Reduce(m/2,x+1); }
//         x + 1 - 2*(m/2) - m;
//       >=  { assert 2*(m/2) <= m; }
//         x + 1 - m - m;
//       >   // arithmetic: x+1-2m > x-2m
//         x - 2 * m;
//       }
//     }
//   }
//
// NOTE: The `>=` and `>` relations in calc translate to verify() calls
// asserting the same inequality in the expected direction.
//
// The key non-trivial hint is `2 * (m / 2) <= m` (integer division property:
// m = 2*(m/2) + m%2, so 2*(m/2) = m - m%2 <= m). This is a fact about
// integer division that Z3 has as an axiom for non-negative integers, but
// providing it explicitly as a verify() hint guides the SMT solver.
//
// Base case (m == 0): x - 0 <= x. Trivial; no hint needed.
@AlwaysVerify
fun reduceLowerBound(m: Int, x: Int) {
    preconditions { 0 <= m }
    postconditions<Unit> { x - 2 * m <= reduce(m, x) }
    if (m == 0) {
        // trivial: x - 0 <= x
    } else {
        // calc chain: reduce(m,x)  ==  reduce(m/2,x+1)-m  >=  x+1-2*(m/2)-m  >=  x+1-2m  >  x-2m
        verify(reduce(m, x) == reduce(m / 2, x + 1) - m)             // def. reduce
        reduceLowerBound(m / 2, x + 1)                                 // IH: x+1-2*(m/2) <= reduce(m/2,x+1)
        verify(x + 1 - 2 * (m / 2) <= reduce(m / 2, x + 1))         // from IH postcondition
        verify(2 * (m / 2) <= m)                                       // integer division: 2*(m/2) <= m
        verify(x + 1 - m - m <= x + 1 - 2 * (m / 2) - m)            // from 2*(m/2) <= m
        verify(x + 1 - m - m > x - 2 * m)                             // arithmetic: 1 > 0
    }
}