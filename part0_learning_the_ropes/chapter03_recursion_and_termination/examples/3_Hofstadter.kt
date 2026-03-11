//package chapter03_recursion_and_termination.examples
//
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.preconditions
//
//// Hofstadter sequences. These are the canonical example of functions whose
//// termination is extremely difficult to prove formally. Even Dafny cannot
//// verify them with a simple `decreases` expression — auxiliary lemmas are
//// needed to establish bounds like `G(n) < n` for n >= 1 before the
//// termination argument can be made. The `// decreases ...?` comments in the
//// Dafny source reflect this open challenge.
////
//// SnaKt accepts all three via circular axiomatisation (no termination check),
//// which is the same gap as 3.0_EndlessProblems.
////
//// NOTE: Dafny's `nat` → `Int` with explicit `0 <= n` preconditions.
//
//// Dafny:
////   function G(n: nat): nat
////     // decreases ...?
////   { if n == 0 then 0 else n - G(G(n - 1)) }
////
//// Hofstadter G-sequence. Known closed form: G(n) = floor(n / phi) where
//// phi = (1 + sqrt(5)) / 2. Termination requires proving G(n) < n for n >= 1,
//// which itself requires induction on the structure of nested calls.
//@Pure
//fun hofstadterG(n: Int): Int {
//    preconditions { 0 <= n }
//    return if (n == 0) 0 else n - hofstadterG(hofstadterG(n - 1))
//}
//
//// Dafny:
////   function F(n: nat): nat
////     // decreases ...?
////   { if n == 0 then 1 else n - M(F(n - 1)) }
////
//// Hofstadter Female sequence. Mutually recursive with M.
//@Pure
//fun hofstadterF(n: Int): Int {
//    preconditions { 0 <= n }
//    return if (n == 0) 1 else n - hofstadterM(hofstadterF(n - 1))
//}
//
//// Dafny:
////   function M(n: nat): nat
////     // decreases ...?
////   { if n == 0 then 0 else n - F(M(n - 1)) }
////
//// Hofstadter Male sequence. Mutually recursive with F.
//@Pure
//fun hofstadterM(n: Int): Int {
//    preconditions { 0 <= n }
//    return if (n == 0) 0 else n - hofstadterF(hofstadterM(n - 1))
//}