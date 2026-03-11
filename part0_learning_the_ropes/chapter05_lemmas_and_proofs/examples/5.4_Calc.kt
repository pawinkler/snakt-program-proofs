//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.preconditions
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// NOTE: Dafny's `calc` has no SnaKt equivalent (see 5_Increasing_Proof6.kt).
//// Each `calc` step becomes a `verify()` call. For pure arithmetic, the SMT
//// solver can often discharge the whole relation without hints; the stepping-
//// stone structure is included here to mirror the Dafny pedagogical intent.
////
//// NOTE: Dafny's `nat` → `Int` with `preconditions { 0 <= n }` where needed.
//
//// Dafny:
////   method CalcExample0(x: int) {
////     calc { 5*(x+3); == 5*x+5*3; == 5*x+15; }
////   }
//@AlwaysVerify
//fun calcExample0(x: Int) {
//    verify(5 * (x + 3) == 5 * x + 5 * 3)  // distribute * over +
//    verify(5 * x + 5 * 3 == 5 * x + 15)   // 5 * 3 == 15
//}
//
//// Dafny:
////   method CalcExample1(x: int, y: int) {
////     calc { (x+y)*(x-y); == x*x-x*y+y*x-y*y; == x*x-x*y+x*y-y*y; == x*x-y*y; }
////   }
////
//// Nonlinear arithmetic (polynomial identity). Z3 can verify the final relation
//// directly; the intermediate steps are included for documentary value.
//@AlwaysVerify
//fun calcExample1(x: Int, y: Int) {
//    verify((x + y) * (x - y) == x*x - x*y + y*x - y*y)  // distribute * over + and -
//    verify(x*x - x*y + y*x - y*y == x*x - x*y + x*y - y*y)  // commutativity: y*x == x*y
//    verify(x*x - x*y + x*y - y*y == x*x - y*y)               // -x*y and +x*y cancel
//}
//
//// Dafny:
////   method CalcExample2(x: int, n: nat) {
////     calc { 3*x+n+n; == 3*x+2*n; <= 3*x+3*n; == 3*(x+n); }
////   }
////
//// Mixed == and <= chain; the <= step requires 0 <= n.
//@AlwaysVerify
//fun calcExample2(x: Int, n: Int) {
//    preconditions { 0 <= n }
//    verify(3*x + n + n == 3*x + 2*n)   // n + n == 2*n
//    verify(3*x + 2*n <= 3*x + 3*n)     // 2*n <= 3*n since 0 <= n
//    verify(3*x + 3*n == 3 * (x + n))   // distribute * over +
//}
//
//// --- Shorter Dafny forms ---
////
//// Dafny allows `calc` without per-step relation hints (the verifier infers
//// the relation, defaulting to ==) and with a default operator set in the
//// `calc` header (e.g. `calc <= { ... }`). These syntactic variants translate
//// to identical SnaKt code — the verify() calls are the same regardless of
//// which Dafny shorthand is used.
//
//// Dafny:
////   method CalcExample0a(x: int, n: nat) {
////     calc { 5*(x+3); 5*x+5*3; 5*x+15; }  // == inferred between each step
////   }
//@AlwaysVerify
//fun calcExample0a(x: Int, n: Int) {  // n: nat parameter kept for signature parity
//    preconditions { 0 <= n }
//    verify(5 * (x + 3) == 5 * x + 5 * 3)
//    verify(5 * x + 5 * 3 == 5 * x + 15)
//}
//
//// Dafny:
////   method CalcExample2a(x: int, n: nat) {
////     calc { 3*x+n+n; 3*x+2*n; <= 3*x+3*n; 3*(x+n); }
////     // == inferred where no operator is given; <= stated explicitly
////   }
//@AlwaysVerify
//fun calcExample2a(x: Int, n: Int) {
//    preconditions { 0 <= n }
//    verify(3*x + n + n == 3*x + 2*n)
//    verify(3*x + 2*n <= 3*x + 3*n)
//    verify(3*x + 3*n == 3 * (x + n))
//}
//
//// Dafny:
////   method CalcExample2b(x: int, n: nat) {
////     calc <= {           // default operator is <=
////       3*x + n + n;
////     ==                  // override: this step is ==
////       3*x + 2*n;
////       3*x + 3*n;        // uses default <=
////     ==                  // override: this step is ==
////       3*(x + n);
////     }
////   }
////
//// NOTE: `calc <= { ... }` sets the default step relation to <=, overriding per
//// step with == where stated. SnaKt has no equivalent; the translation is the
//// same verify() sequence as CalcExample2/2a.
//@AlwaysVerify
//fun calcExample2b(x: Int, n: Int) {
//    preconditions { 0 <= n }
//    verify(3*x + n + n == 3*x + 2*n)   // == (explicit in Dafny)
//    verify(3*x + 2*n <= 3*x + 3*n)     // <= (default in Dafny)
//    verify(3*x + 3*n == 3 * (x + n))   // == (explicit in Dafny)
//}