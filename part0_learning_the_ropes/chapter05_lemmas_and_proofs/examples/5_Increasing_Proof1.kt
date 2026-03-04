package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.postconditions

// Dafny:
//   function More(x: int): int { ... }   -- same as Proof0; `more` reused from there.
//
//   lemma {:induction false} Increasing(x: int)
//     ensures x < More(x)
//   {
//     if x <= 0 {
//     } else {
//       Increasing(x-2);
//     }
//   }
//
// The Dafny attribute `{:induction false}` disables automatic induction for
// this lemma, forcing the programmer to supply the induction step explicitly
// (the `Increasing(x-2)` call). It has no SnaKt equivalent — SnaKt never
// performs automatic induction, so the attribute is always implicitly in
// effect. It cannot be expressed or translated.
//
// As a result, Proof0 and Proof1 produce IDENTICAL SnaKt code. The only
// difference between them is in Dafny: Proof0 relies on the automatic
// heuristic; Proof1 spells out what that heuristic found. In SnaKt the
// explicit induction step was already necessary in Proof0.
//
// The function is renamed `increasingManual` to avoid clashing with
// `increasing` already defined in 5_Increasing_Proof0.kt.
@AlwaysVerify
fun increasingManual(x: Int) {
    postconditions<Unit> { x < more(x) }
    if (x > 0) {
        increasingManual(x - 2)
    }
}