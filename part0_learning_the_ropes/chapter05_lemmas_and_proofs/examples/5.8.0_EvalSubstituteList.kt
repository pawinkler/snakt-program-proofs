//package chapter05_lemmas_and_proofs.examples
//
//import org.jetbrains.kotlin.formver.plugin.NeverConvert
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.preconditions
//
//// Types redefined from 4.6_Expr.kt (different package; same definitions).
//// See 4.6_Expr.kt for the naming rationale (DList, ExprConst, ExprVar, ExprNode).
//
//enum class Op5 { Add, Mul }
//
//sealed class DList5<out T>
//object DNil5 : DList5<Nothing>()
//data class DCons5<out T>(val head: T, val tail: DList5<T>) : DList5<T>()
//
//sealed class Expr5
//data class ExprConst5(val value: Int) : Expr5()
//data class ExprVar5(val name: String) : Expr5()
//data class ExprNode5(val op: Op5, val args: DList5<Expr5>) : Expr5()
//
//// Dafny:
////   function Eval(e: Expr, env: map<string, nat>): nat { ... }
////   function EvalList(args: List<Expr>, op: Op, env: map<string, nat>): nat { ... }
////
//// NOTE: `map<string, nat>` is not supported in SnaKt/Viper (see 4.6_Expr.kt).
//// Both functions are @NeverConvert. `env + (n to c)` is used as the Kotlin
//// approximation of Dafny's map-update `env[n := c]`.
//@NeverConvert
//fun eval5(e: Expr5, env: Map<String, Int>): Int = when (e) {
//    is ExprConst5 -> e.value
//    is ExprVar5   -> env.getOrDefault(e.name, 0)
//    is ExprNode5  -> evalList5(e.args, e.op, env)
//}
//
//@NeverConvert
//fun evalList5(args: DList5<Expr5>, op: Op5, env: Map<String, Int>): Int = when (args) {
//    is DNil5  -> when (op) { Op5.Add -> 0; Op5.Mul -> 1 }
//    is DCons5 -> {
//        val v0 = eval5(args.head, env)
//        val v1 = evalList5(args.tail, op, env)
//        when (op) { Op5.Add -> v0 + v1; Op5.Mul -> v0 * v1 }
//    }
//}
//
//// Dafny:
////   function Substitute(e: Expr, n: string, c: nat): Expr { ... }
////
//// Substitute does NOT use maps — fully translatable as @Pure.
//// `c: nat` → `Int` with precondition `0 <= c`.
//@Pure
//fun substitute(e: Expr5, n: String, c: Int): Expr5 {
//    preconditions { 0 <= c }
//    return when (e) {
//        is ExprConst5 -> e
//        is ExprVar5   -> if (e.name == n) ExprConst5(c) else e
//        is ExprNode5  -> ExprNode5(e.op, substituteList(e.args, n, c))
//    }
//}
//
//// Dafny:
////   function SubstituteList(es: List<Expr>, n: string, c: nat): List<Expr> { ... }
//@Pure
//fun substituteList(es: DList5<Expr5>, n: String, c: Int): DList5<Expr5> {
//    preconditions { 0 <= c }
//    return when (es) {
//        is DNil5  -> DNil5
//        is DCons5 -> DCons5(substitute(es.head, n, c), substituteList(es.tail, n, c))
//    }
//}
//
//// Dafny:
////   lemma EvalSubstitute(e: Expr, n: string, c: nat, env: map<string,nat>)
////     ensures Eval(Substitute(e, n, c), env) == Eval(e, env[n := c])
////
//// NOTE: The postcondition references `env[n := c]` (Dafny map update — a new
//// map identical to `env` except `n` maps to `c`). Kotlin approximation:
//// `env + (n to c)`. SnaKt has no map model, so this lemma is @NeverConvert.
////
//// The Dafny proof is by structural match on `e`; the Node case delegates to
//// EvalSubstituteList. Both are blocked by the map limitation.
//@NeverConvert
//fun evalSubstitute(e: Expr5, n: String, c: Int, env: Map<String, Int>) {
//    // postcondition (untranslatable):
//    //   eval5(substitute(e, n, c), env) == eval5(e, env + (n to c))
//}
//
//// Dafny:
////   lemma {:induction false} EvalSubstituteList(
////       args: List<Expr>, op: Op, n: string, c: nat, env: map<string,nat>)
////     ensures EvalList(SubstituteList(args,n,c), op, env)
////          == EvalList(args, op, env[n:=c])
////
//// NOTE: Postcondition and every calc step reference `env[n := c]` and
//// `EvalList`/`Eval` — all blocked by the map limitation. @NeverConvert.
////
//// The Dafny proof (for the Cons case) is a 6-step calc chain:
////   EvalList(SubstituteList(Cons(e,tail),n,c), op, env)
////   == EvalList(Cons(Substitute(e,n,c), SubstituteList(tail,n,c)), op, env)
////      [def. SubstituteList]
////   == match op { v0=Eval(Substitute(e,n,c),env), v1=EvalList(SubstituteList(tail,n,c),op,env) }
////      [def. EvalList]
////   == match op { v0=Eval(e,env[n:=c]), v1=EvalList(SubstituteList(tail,n,c),op,env) }
////      [EvalSubstitute(e,n,c,env)]
////   == match op { v0=Eval(e,env[n:=c]), v1=EvalList(tail,op,env[n:=c]) }
////      [EvalSubstituteList(tail,op,n,c,env) — IH]
////   == EvalList(Cons(e,tail), op, env[n:=c])
////      [def. EvalList]
////   == EvalList(args, op, env[n:=c])
////      [args == Cons(e,tail)]
////
//// The two IH calls (EvalSubstitute and EvalSubstituteList) are mutual recursion
//// between the two lemmas — another feature that would require mutual @Pure/@AlwaysVerify
//// support in SnaKt even if maps were available.
//@NeverConvert
//fun evalSubstituteList(args: DList5<Expr5>, op: Op5, n: String, c: Int, env: Map<String, Int>) {
//    // postcondition (untranslatable):
//    //   evalList5(substituteList(args, n, c), op, env) == evalList5(args, op, env + (n to c))
//}
