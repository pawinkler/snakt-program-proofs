package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   lemma {:induction false} Increasing(x: int)
//     ensures x < More(x)
//   { ... }
//
// A trimmed version of Proof3: each assertion states exactly one new fact,
// with the Dafny comment explaining where it comes from.
// The final `assert x < More(x)` is omitted — the verifier closes the gap
// from `x + 1 < More(x)` to the postcondition by arithmetic.
//
// `more` and `{:induction false}` are discussed in Proof0/Proof1.
@AlwaysVerify
fun increasingProof5(x: Int) {
    postconditions<Unit> { x < more(x) }
    if (x <= 0) {
        verify(more(x) == 1)                          // def. more for x <= 0
    } else {
        verify(more(x) == more(x - 2) + 3)            // def. more for 0 < x
        increasingProof5(x - 2)                        // induction hypothesis
        verify(x - 2 < more(x - 2))                   // what we get from the recursive call
        verify(x + 1 < more(x - 2) + 3)               // add 3 to each side
        verify(x + 1 < more(x))                        // previous line and def. more above
    }
}