//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.postconditions
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// Dafny:
////   lemma {:induction false} Increasing(x: int)
////     ensures x < More(x)
////   {
////     if x <= 0 {
////       assert More(x) == 1;
////     } else {
////       assert More(x) == More(x - 2) + 3;
////       Increasing(x - 2);
////       assert More(x) == More(x - 2) + 3 && x - 2 < More(x - 2);
////       assert More(x) == More(x - 2) + 3 && x + 1 < More(x - 2) + 3;
////       assert x + 1 < More(x);
////     }
////   }
////
//// Contrast with Proof5: Proof4 bundles the definition of More into conjunctions
//// with each new fact, rather than asserting each fact individually. Both are
//// valid proof styles; Proof5 is cleaner. In SnaKt, `verify(a && b)` checks the
//// conjunction as a single obligation (both parts must hold simultaneously).
////
//// `more` and `{:induction false}` are discussed in Proof0/Proof1.
//@AlwaysVerify
//fun increasingProof4(x: Int) {
//    postconditions<Unit> { x < more(x) }
//    if (x <= 0) {
//        verify(more(x) == 1)                                                   // def. more for x <= 0
//    } else {
//        verify(more(x) == more(x - 2) + 3)                                    // def. more for 0 < x
//        increasingProof4(x - 2)                                                // induction hypothesis
//        verify(more(x) == more(x - 2) + 3 && x - 2 < more(x - 2))
//        verify(more(x) == more(x - 2) + 3 && x + 1 < more(x - 2) + 3)
//        verify(x + 1 < more(x))
//    }
//}