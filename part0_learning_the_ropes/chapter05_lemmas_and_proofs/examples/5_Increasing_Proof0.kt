package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.postconditions

// Dafny:
//   function More(x: int): int {
//     if x <= 0 then 1 else More(x - 2) + 3
//   }
//
// NOTE: Termination is not checked by SnaKt (same gap as ch. 3). The Dafny
// verifier infers `decreases if x <= 0 then 0 else x` automatically.
@Pure
fun more(x: Int): Int = if (x <= 0) 1 else more(x - 2) + 3

// Dafny:
//   lemma Increasing(x: int)
//     ensures x < More(x)
//   {
//     // proof is automatic by Dafny's automatic induction
//   }
//
// Dafny's automatic induction heuristic finds the inductive proof without a
// body. SnaKt has no such heuristic — the SMT solver alone is unlikely to
// prove a universally quantified inductive property without guidance.
//
// The explicit inductive proof structure is:
//   Base case  (x <= 0): More(x) = 1, and 1 > 0 >= x, so x < More(x). ✓
//   Inductive step (x > 0): call increasing(x - 2) to obtain x-2 < More(x-2)
//     as a verified postcondition; then More(x) = More(x-2) + 3 > (x-2) + 3 = x+1 > x. ✓
//
// The recursive call to `increasing` here is the induction hypothesis application —
// it is a ghost/proof-only call with no runtime effect.
//
// NOTE: `postconditions<Unit>` is used because the function returns nothing.
// The `it` parameter in the lambda is Unit and is not referenced. This usage
// is not explicitly shown in USAGE.md; if SnaKt rejects it, replace with
// `verify(x < more(x))` at the end of the body (loses the caller-visible spec).
@AlwaysVerify
fun increasing(x: Int) {
    postconditions<Unit> { x < more(x) }
    if (x > 0) {
        increasing(x - 2)
    }
}