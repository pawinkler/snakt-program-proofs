package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

// `more` and `increasing` are defined (with proof) in 5_Increasing_Proof0.kt.
// The Dafny file declares `Increasing` with `// proof omitted here, but see
// separate files` — that corresponds to the Proof0–Proof9 files.

// Dafny:
//   method ExampleLemmaUse(a: int) {
//     var b := More(a);
//     Increasing(a);         // brings `a < More(a)` into scope
//     Increasing(b);         // brings `b < More(b)` into scope
//     var c := More(b);
//     assert 2 <= c - a;
//   }
//
// In Dafny, a lemma call makes its postcondition available as a fact at that
// program point. In SnaKt, calling a function with `postconditions` does the
// same — the verifier axiomatises the postcondition at the call site.
//
// Proof sketch: increasing(a) gives a < b; increasing(b) gives b < c.
// Since a,b,c are integers: a+1 <= b and b+1 <= c, so a+2 <= c, i.e. 2 <= c-a.
@AlwaysVerify
fun exampleLemmaUse(a: Int) {
    val b = more(a)
    increasing(a)               // postcondition: a < more(a), i.e. a < b
    increasing(b)               // postcondition: b < more(b), i.e. b < c
    val c = more(b)
    verify(2 <= c - a)
}