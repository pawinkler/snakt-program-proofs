package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.postconditions

// Dafny:
//   lemma {:induction false} Increasing(x: int)
//     ensures x < More(x)
//   {
//     if 0 < x {
//       var y := x - 2;
//       Increasing(y);
//     }
//   }
//
// Identical proof to Proof1 except the argument `x - 2` is named `y` before
// the recursive call. In SnaKt (and mathematically) this makes no difference —
// `val y = x - 2` followed by `increasingWithVar(y)` is the same proof step
// as `increasingManual(x - 2)`. The SMT solver treats them identically.
//
// `{:induction false}` and `more` are discussed in Proof0 and Proof1.
// Renamed `increasingWithVar` to avoid clashing with earlier variants.
@AlwaysVerify
fun increasingWithVar(x: Int) {
    postconditions<Unit> { x < more(x) }
    if (0 < x) {
        val y = x - 2
        increasingWithVar(y)
    }
}
