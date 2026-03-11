//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.implies
//import org.jetbrains.kotlin.formver.plugin.postconditions
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// Dafny:
////   lemma {:induction false} Increasing(x: int)
////     ensures x < More(x)
////   {
////     if x <= 0 {
////       calc { x; <= 0; < 1; == More(x); }
////     } else {
////       calc {
////         true;
////       ==>  { Increasing(x - 2); }
////         x - 2 < More(x - 2);
////       ==   // add 3 to each side
////         x + 1 < More(x - 2) + 3;
////       ==   // def. More, since 0 < x
////         x + 1 < More(x);
////       ==>  // arithmetic
////         x < More(x);
////       }
////     }
////   }
////
//// NOTE: `calc` has no SnaKt equivalent (see Proof6). The base case is identical
//// to Proof6 and uses the same verify() stepping stones.
////
//// The else branch introduces a new calc style: `==` between boolean predicates
//// (logical equivalence) and `==>` (implication). These map to:
////   Dafny `==`  between booleans  →  verify(a == b)   (boolean equality in SnaKt)
////   Dafny `==>` (implication)     →  verify(a implies b)  (SnaKt `implies` infix)
////
//// The def. of `more(x)` must be made explicit as a hint before the `==` step
//// that substitutes it, since `verify()` calls are independent and the SMT solver
//// needs the fact in scope.
//@AlwaysVerify
//fun increasingProof7(x: Int) {
//    postconditions<Unit> { x < more(x) }
//    if (x <= 0) {
//        // calc chain: x  <=  0  <  1  ==  more(x)
//        verify(x <= 0)          //  x  <=  0   (branch condition)
//        verify(0 < 1)           //  0  <   1   (arithmetic)
//        verify(1 == more(x))    //  1  ==  more(x)   (def. more for x <= 0)
//    } else {
//        // calc chain: true  ==>  x-2 < more(x-2)  ==  x+1 < more(x-2)+3  ==  x+1 < more(x)  ==>  x < more(x)
//        increasingProof7(x - 2)                                                  // { Increasing(x-2) }
//        verify(x - 2 < more(x - 2))                                              // true  ==>  x-2 < more(x-2)
//        verify((x - 2 < more(x - 2)) == (x + 1 < more(x - 2) + 3))             //  ==  add 3 to each side
//        verify(more(x) == more(x - 2) + 3)                                       // def. more hint for next step
//        verify((x + 1 < more(x - 2) + 3) == (x + 1 < more(x)))                 //  ==  def. more, since 0 < x
//        verify((x + 1 < more(x)) implies (x < more(x)))                          //  ==>  arithmetic
//    }
//}