//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.postconditions
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// Dafny:
////   lemma {:induction false} Increasing(x: int)
////     ensures x < More(x)
////   { ... }
////
//// This version makes every logical step an explicit assertion, showing exactly
//// how the proof proceeds. In Dafny, intermediate `assert` statements both check
//// that the fact holds and guide the verifier by providing stepping stones.
//// In SnaKt, `verify()` serves the same dual role.
////
//// `more` and `{:induction false}` are discussed in Proof0/Proof1.
//// Renamed `increasingVerbose` to avoid clashing with earlier variants.
//@AlwaysVerify
//fun increasingVerbose(x: Int) {
//    postconditions<Unit> { x < more(x) }
//    verify(true)
//    if (x <= 0) {
//        verify(x <= 0)
//        verify(x <= 0 && more(x) == 1)         // def. more for x <= 0
//        verify(x < more(x))
//    } else {
//        verify(0 < x)
//        verify(0 < x && more(x) == more(x - 2) + 3)  // def. more for 0 < x
//        increasingVerbose(x - 2)                      // induction hypothesis
//        verify(0 < x && more(x) == more(x - 2) + 3 && x - 2 < more(x - 2))
//        verify(more(x) == more(x - 2) + 3 && x + 1 < more(x - 2) + 3)
//        verify(x + 1 < more(x))
//        verify(x < more(x))
//    }
//    verify(x < more(x))
//}