package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   lemma {:induction false} Increasing(x: int)
//     ensures x < More(x)
//   {
//     if x <= 0 {
//       calc { x; <= { assert x <= 0; } 0; < 1; == More(x); }
//     } else {
//       calc { More(x); == More(x-2)+3; > { Increasing(x-2); } x-2+3; > x; }
//     }
//   }
//
// NOTE: Dafny's `calc` block is a proof-by-calculation construct with no SnaKt
// equivalent. A `calc` chain proves a relation (e.g. x < More(x)) by verifying
// each step independently and composing them by transitivity. In SnaKt, the
// same effect is achieved by sequential `verify()` calls — the SMT solver
// accumulates all facts in scope and applies transitivity implicitly.
//
// Each `verify()` below corresponds to one step in the original `calc` chain.
// The direction of the chain's relation is shown in the comments.
//
// `more` and `{:induction false}` are discussed in Proof0/Proof1.
@AlwaysVerify
fun increasingProof6(x: Int) {
    postconditions<Unit> { x < more(x) }
    if (x <= 0) {
        // calc chain: x  <=  0  <  1  ==  more(x)
        verify(x <= 0)          //  x  <=  0   (branch condition)
        verify(0 < 1)           //  0  <   1   (arithmetic)
        verify(1 == more(x))    //  1  ==  more(x)   (def. more for x <= 0)
        // SMT concludes: x < more(x) by transitivity
    } else {
        // calc chain: more(x)  ==  more(x-2)+3  >  x-2+3  >  x
        verify(more(x) == more(x - 2) + 3)      // more(x)     ==  more(x-2)+3  (def. more)
        increasingProof6(x - 2)                  // { Increasing(x-2) } gives x-2 < more(x-2)
        verify(more(x - 2) + 3 > x - 2 + 3)     // more(x-2)+3  >  x-2+3  (from induction hyp.)
        verify(x - 2 + 3 > x)                    // x-2+3        >  x      (arithmetic)
        // SMT concludes: more(x) > x, i.e. x < more(x), by transitivity
    }
}