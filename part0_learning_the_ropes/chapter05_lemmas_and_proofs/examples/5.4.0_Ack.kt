//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.postconditions
//import org.jetbrains.kotlin.formver.plugin.preconditions
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// Dafny:
////   function Ack(m: nat, n: nat): nat
////     decreases m, n
////   { if m == 0 then n + 1 else if n == 0 then Ack(m-1, 1) else Ack(m-1, Ack(m, n-1)) }
////
//// Same function as in 3_Examples.kt; redefined here since that is a different
//// package. Termination (decreases m, n) is not checked by SnaKt (see ch. 3).
//@Pure
//fun ack(m: Int, n: Int): Int {
//    preconditions { 0 <= m && 0 <= n }
//    return when {
//        m == 0 -> n + 1
//        n == 0 -> ack(m - 1, 1)
//        else   -> ack(m - 1, ack(m, n - 1))
//    }
//}
//
//// Dafny:
////   lemma Ack1(n: nat)
////     ensures Ack(1, n) == n + 2
////   {
////     if n == 0 {
////       // trivial
////     } else {
////       calc {
////         Ack(1, n);
////       ==  // def. Ack (m=1>0, n>0)
////         Ack(0, Ack(1, n - 1));
////       ==  // def. Ack(0, _) = _ + 1
////         Ack(1, n - 1) + 1;
////       ==  { Ack1(n - 1); }  // induction hypothesis: Ack(1,n-1) == (n-1)+2
////         (n - 1) + 2 + 1;
////       ==  // arithmetic
////         n + 2;
////       }
////     }
////   }
////
//// NOTE: `calc` has no SnaKt equivalent (see 5_Increasing_Proof6.kt).
//// The calc chain is translated as sequential verify() stepping stones.
//// Each step requires the SMT solver to unfold the definition of `ack` for
//// specific argument patterns — the same definitional-unfolding question that
//// arises for `more` in the Increasing proofs.
////
//// Base case (n == 0): Dafny says "trivial". In SnaKt, a hint is added since
//// the SMT solver must unfold `ack` twice (ack(1,0) → ack(0,1) → 2).
//@AlwaysVerify
//fun ack1(n: Int) {
//    preconditions { 0 <= n }
//    postconditions<Unit> { ack(1, n) == n + 2 }
//    if (n == 0) {
//        verify(ack(1, 0) == 0 + 2)                            // ack(1,0)→ack(0,1)→2; trivial in Dafny
//    } else {
//        // calc chain: Ack(1,n) == Ack(0,Ack(1,n-1)) == Ack(1,n-1)+1 == (n-1)+2+1 == n+2
//        verify(ack(1, n) == ack(0, ack(1, n - 1)))            // def. ack, m=1>0, n>0
//        verify(ack(0, ack(1, n - 1)) == ack(1, n - 1) + 1)   // def. ack(0, _) = _ + 1
//        ack1(n - 1)                                            // induction hypothesis
//        verify(ack(1, n - 1) == (n - 1) + 2)                  // from IH postcondition
//        verify(ack(1, n) == (n - 1) + 2 + 1)                  // combining the steps above
//        verify(ack(1, n) == n + 2)                             // arithmetic
//    }
//}