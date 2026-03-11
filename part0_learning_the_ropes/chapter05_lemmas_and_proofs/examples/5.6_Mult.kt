//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.postconditions
//import org.jetbrains.kotlin.formver.plugin.preconditions
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// Dafny:
////   function Mult(x: nat, y: nat): nat {
////     if y == 0 then 0 else x + Mult(x, y - 1)
////   }
////
//// Computes x * y by repeated addition. Termination: `y` decreases. Not checked
//// by SnaKt. `/` in recursive calls of the lemma is integer division.
//@Pure
//fun mult(x: Int, y: Int): Int {
//    preconditions { 0 <= x && 0 <= y }
//    return if (y == 0) 0 else x + mult(x, y - 1)
//}
//
//// Dafny:
////   lemma {:induction false} MultCommutative(x: nat, y: nat)
////     ensures Mult(x, y) == Mult(y, x)
////   {
////     if x == y {
////     } else if x == 0 {
////       MultCommutative(x, y - 1);
////     } else if y < x {
////       MultCommutative(y, x);
////     } else {
////       calc { Mult(x,y); == x+Mult(x,y-1); == x+Mult(y-1,x);
////              == x+y-1+Mult(y-1,x-1); == x+y-1+Mult(x-1,y-1);
////              == y+Mult(x-1,y); == y+Mult(y,x-1); == Mult(y,x); }
////     }
////   }
////
//// NOTE: Termination is non-trivial (see ch.3 gap). Case 3 calls
//// MultCommutative(y,x) which enters case 4; case 4's recursive calls all
//// decrease x+y. Dafny is left to figure out the measure; SnaKt accepts it
//// via circular axiomatisation as usual.
////
//// NOTE: `calc` has no SnaKt equivalent (see 5_Increasing_Proof6.kt).
//// The calc chain is translated as sequential verify() stepping stones.
//@AlwaysVerify
//fun multCommutative(x: Int, y: Int) {
//    preconditions { 0 <= x && 0 <= y }
//    postconditions<Unit> { mult(x, y) == mult(y, x) }
//    if (x == y) {
//        // trivial: mult(x, x) == mult(x, x)
//
//    } else if (x == 0) {
//        // Dafny leaves only `MultCommutative(x, y-1)` with no further hints.
//        // In SnaKt we add def-unfolding hints since the SMT solver must see
//        // mult(0,y) = mult(0,y-1) and mult(y,0) = 0 = mult(y-1,0).
//        multCommutative(0, y - 1)                     // IH: mult(0,y-1) == mult(y-1,0)
//        verify(mult(0, y) == mult(0, y - 1))          // def. mult(0,y) = 0 + mult(0,y-1)
//        verify(mult(y - 1, 0) == 0)                   // def. mult(_,0) = 0
//        verify(mult(y, 0) == 0)                       // def. mult(_,0) = 0
//
//    } else if (y < x) {
//        // Symmetry: delegate to the case where first arg < second arg.
//        multCommutative(y, x)                          // IH: mult(y,x) == mult(x,y)
//
//    } else {
//        // Main case: 0 < x < y (x != y, x != 0, y >= x implies y > x).
//        //
//        // calc chain (6 == steps, 3 IH calls):
//        //   Mult(x,y)
//        //   == x + Mult(x, y-1)              [def. Mult]
//        //   == x + Mult(y-1, x)              [MultCommutative(x, y-1)]
//        //   == x + y-1 + Mult(y-1, x-1)     [def. Mult on Mult(y-1,x): (y-1)+Mult(y-1,x-1)]
//        //   == x + y-1 + Mult(x-1, y-1)     [MultCommutative(x-1, y-1)]
//        //   == y + Mult(x-1, y)              [def. Mult: Mult(x-1,y)=(x-1)+Mult(x-1,y-1)]
//        //   == y + Mult(y, x-1)              [MultCommutative(x-1, y)]
//        //   == Mult(y, x)                    [def. Mult]
//
//        verify(mult(x, y) == x + mult(x, y - 1))                         // def. mult(x,y)
//
//        multCommutative(x, y - 1)
//        verify(mult(x, y - 1) == mult(y - 1, x))                         // IH: Mult(x,y-1)==Mult(y-1,x)
//
//        verify(mult(y - 1, x) == (y - 1) + mult(y - 1, x - 1))          // def. mult(y-1,x), x>0
//
//        multCommutative(x - 1, y - 1)
//        verify(mult(y - 1, x - 1) == mult(x - 1, y - 1))                 // IH: Mult(y-1,x-1)==Mult(x-1,y-1)
//
//        verify(mult(x - 1, y) == (x - 1) + mult(x - 1, y - 1))          // def. mult(x-1,y), y>0
//
//        multCommutative(x - 1, y)
//        verify(mult(x - 1, y) == mult(y, x - 1))                         // IH: Mult(x-1,y)==Mult(y,x-1)
//
//        verify(mult(y, x) == y + mult(y, x - 1))                         // def. mult(y,x), x>0
//    }
//}