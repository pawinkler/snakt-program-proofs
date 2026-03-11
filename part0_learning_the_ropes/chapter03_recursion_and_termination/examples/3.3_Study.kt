//package chapter03_recursion_and_termination.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.NeverVerify
//import org.jetbrains.kotlin.formver.plugin.preconditions
//import org.jetbrains.kotlin.formver.plugin.postconditions
//
//// NOTE: Dafny's `nat` is a non-negative integer. `Int` is used throughout
//// with explicit preconditions for `0 <= n` where needed. `nat` return types
//// carry an implicit `0 <= result` which is captured in postconditions.
////
//// NOTE: `decreases` clauses are untranslatable — SnaKt does not check
//// termination. All termination arguments below are documented in comments only.
//
//// Dafny:
////   method RequiredStudyTime(c: nat) returns (hours: nat)
////     ensures hours <= 200
////
//// NOTE: This method has no body in Dafny — it is abstract/opaque, acting as
//// an axiom that some oracle exists satisfying the spec. Translated as
//// @NeverVerify with a stub body so callers can use the postcondition.
//@NeverVerify
//fun requiredStudyTime(c: Int): Int {
//    preconditions { 0 <= c }
//    postconditions<Int> { 0 <= it && it <= 200 }
//    return 0  // stub — body unverified; postcondition axiomatised for callers
//}
//
//// Dafny:
////   method Study(n: nat, h: nat)
////     decreases n, h
////   {
////     if h != 0 {
////       Study(n, h - 1);
////     } else if n == 0 {
////       // graduation!
////     } else {
////       var hours := RequiredStudyTime(n - 1);
////       Study(n - 1, hours);
////     }
////   }
////
//// Termination argument (untranslatable): lexicographic (n, h) decreases because
//// either h decreases, or h reaches 0 and n decreases (with h reset to hours <= 200).
//@AlwaysVerify
//fun study(n: Int, h: Int) {
//    preconditions { 0 <= n && 0 <= h }
//    if (h != 0) {
//        study(n, h - 1)
//    } else if (n == 0) {
//        // graduation!
//    } else {
//        val hours = requiredStudyTime(n - 1)
//        study(n - 1, hours)
//    }
//}
//
//// Dafny:
////   method StudyPlan(n: nat)
////     requires n <= 40
////     decreases 40 - n
////   {
////     if n == 40 {
////       // done
////     } else {
////       var hours := RequiredStudyTime(n);
////       Learn(n, hours);
////     }
////   }
////
//// Termination argument (untranslatable): `40 - n` decreases on each call to
//// StudyPlan since Learn eventually calls StudyPlan(n + 1).
//@AlwaysVerify
//fun studyPlan(n: Int) {
//    preconditions { 0 <= n && n <= 40 }
//    if (n == 40) {
//        // done
//    } else {
//        val hours = requiredStudyTime(n)
//        learn(n, hours)
//    }
//}
//
//// Dafny:
////   method Learn(n: nat, h: nat)
////     requires n < 40
////     decreases 40 - n, h
////   {
////     if h == 0 {
////       StudyPlan(n + 1);
////     } else {
////       Learn(n, h - 1);
////     }
////   }
////
//// Termination argument (untranslatable): lexicographic (40 - n, h) decreases
//// because either h decreases, or h reaches 0 and n increases (so 40 - n shrinks).
//@AlwaysVerify
//fun learn(n: Int, h: Int) {
//    preconditions { 0 <= n && n < 40 && 0 <= h }
//    if (h == 0) {
//        studyPlan(n + 1)
//    } else {
//        learn(n, h - 1)
//    }
//}