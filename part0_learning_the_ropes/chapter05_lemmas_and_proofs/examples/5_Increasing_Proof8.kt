package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.implies
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   lemma {:induction false} Increasing(x: int)
//     ensures x < More(x)
//   {
//     if x <= 0 {
//       calc { x; <= 0; < 1; == More(x); }           -- same as Proof6/Proof7
//     } else {
//       calc {
//         x < More(x);
//       <==  // arithmetic
//         x + 1 < More(x);
//       ==   // def. More, since 0 < x
//         x + 1 < More(x - 2) + 3;
//       ==   // subtract 3 from each side
//         x - 2 < More(x - 2);
//       <==  { Increasing(x - 2); }
//         true;
//       }
//     }
//   }
//
// NOTE: `calc` has no SnaKt equivalent (see Proof6).
//
// This else branch is Proof7's chain written backwards: `<==` ("is implied by")
// instead of `==>`. The chain reads from the conclusion back to `true`, which
// in Dafny composes by transitivity to `true <== ... <== x < More(x)`, i.e.,
// `x < More(x)`.
//
// Dafny `A; <== B;` verifies `B ==> A`. In SnaKt: `verify(B implies A)`.
// The SMT solver doesn't distinguish direction — the verify() calls are hints
// regardless of which way the original chain ran, so the translation looks
// almost identical to Proof7 with the `implies` arguments swapped.
@AlwaysVerify
fun increasingProof8(x: Int) {
    postconditions<Unit> { x < more(x) }
    if (x <= 0) {
        // calc chain: x  <=  0  <  1  ==  more(x)
        verify(x <= 0)          //  x  <=  0   (branch condition)
        verify(0 < 1)           //  0  <   1   (arithmetic)
        verify(1 == more(x))    //  1  ==  more(x)   (def. more for x <= 0)
    } else {
        // calc chain (backwards): x < more(x)  <==  x+1 < more(x)
        //                          ==  x+1 < more(x-2)+3  ==  x-2 < more(x-2)  <==  true
        increasingProof8(x - 2)                                                    // { Increasing(x-2) }
        verify(true implies (x - 2 < more(x - 2)))                                //  x-2 < more(x-2)  <==  true
        verify((x + 1 < more(x - 2) + 3) == (x - 2 < more(x - 2)))              //  ==  subtract 3 from each side
        verify(more(x) == more(x - 2) + 3)                                         // def. more hint for next step
        verify((x + 1 < more(x)) == (x + 1 < more(x - 2) + 3))                   //  ==  def. more, since 0 < x
        verify((x + 1 < more(x)) implies (x < more(x)))                            //  x < more(x)  <==  x+1 < more(x)
    }
}