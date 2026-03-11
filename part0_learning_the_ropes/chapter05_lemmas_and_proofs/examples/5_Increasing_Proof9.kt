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
////       calc { x; <= 0; < 1; == More(x); }           -- same as Proof6/7/8
////     } else {
////       calc {
////         x < More(x);
////       ==   // def. More, since 0 < x
////         x < More(x - 2) + 3;
////       ==   // subtract 3 from each side
////         x - 3 < More(x - 2);
////       <==  // arithmetic
////         x - 2 < More(x - 2);
////       <==  { Increasing(x - 2); }
////         true;
////       }
////     }
////   }
////
//// NOTE: `calc` has no SnaKt equivalent (see Proof6).
////
//// The else chain mixes `==` (boolean equivalence) and `<==` (is implied by),
//// reading from the conclusion back to `true`:
////
////   x < more(x)
////   ==   x < more(x-2)+3           (def. more)
////   ==   x-3 < more(x-2)           (subtract 3 from each side: a < b+3 ↔ a-3 < b)
////   <==  x-2 < more(x-2)           (arithmetic: x-2 < y implies x-3 < y)
////   <==  true                       (Increasing(x-2))
////
//// verify() calls follow the chain bottom-up (from `true` toward the conclusion)
//// since the induction hypothesis must be called first to make the fact available.
//@AlwaysVerify
//fun increasingProof9(x: Int) {
//    postconditions<Unit> { x < more(x) }
//    if (x <= 0) {
//        // calc chain: x  <=  0  <  1  ==  more(x)
//        verify(x <= 0)
//        verify(0 < 1)
//        verify(1 == more(x))
//    } else {
//        increasingProof9(x - 2)                                                    // { Increasing(x-2) }
//        verify(true implies (x - 2 < more(x - 2)))                                //  x-2 < more(x-2)  <==  true
//        verify((x - 2 < more(x - 2)) implies (x - 3 < more(x - 2)))              //  x-3 < more(x-2)  <==  x-2 < more(x-2)  (arithmetic)
//        verify((x < more(x - 2) + 3) == (x - 3 < more(x - 2)))                   //  ==  subtract 3 from each side
//        verify(more(x) == more(x - 2) + 3)                                         // def. more hint for next step
//        verify((x < more(x)) == (x < more(x - 2) + 3))                            //  ==  def. more, since 0 < x
//    }
//}