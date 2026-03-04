package chapter04_inductive_datatypes.examples

import org.jetbrains.kotlin.formver.plugin.NeverConvert
import org.jetbrains.kotlin.formver.plugin.Pure

// Dafny:
//   datatype Op = Add | Mul
enum class Op { Add, Mul }

// Dafny:
//   datatype List<T> = Nil | Cons(head: T, tail: List<T>)
//
// Kotlin's stdlib List<T> is not the same thing; this is Dafny's inductive
// linked-list type. Named `DList` to avoid clashing with Kotlin's List.
// `out T` (covariance) lets the singleton `DNil` serve as `DList<Expr>`
// without needing a type parameter at every call site.
sealed class DList<out T>
object DNil : DList<Nothing>()
data class DCons<out T>(val head: T, val tail: DList<T>) : DList<T>()

// Dafny:
//   datatype Expr = Const(nat) | Var(string) | Node(op: Op, args: List<Expr>)
//
// Variants renamed ExprConst / ExprVar / ExprNode to avoid clashing with
// `Node` already defined in this package by 4.2_Destructors.kt.
// Dafny's `nat` field → `Int` (non-negativity not enforced by the type).
sealed class Expr
data class ExprConst(val value: Int) : Expr()
data class ExprVar(val name: String) : Expr()
data class ExprNode(val op: Op, val args: DList<Expr>) : Expr()

// Dafny:
//   function AST_Example(): Expr {
//     // 10 * (x + 7 * y)
//     Node(Mul, Cons(Const(10), Cons(Node(Add, ...), Nil)))
//   }
@Pure
fun astExample(): Expr =
    ExprNode(Op.Mul,
        DCons(ExprConst(10),
        DCons(ExprNode(Op.Add,
            DCons(ExprVar("x"),
            DCons(ExprNode(Op.Mul,
                DCons(ExprConst(7),
                DCons(ExprVar("y"),
                DNil))),
            DNil))),
        DNil)))

// Dafny:
//   function Eval(e: Expr, env: map<string, nat>): nat { ... }
//   function EvalList(args: List<Expr>, op: Op, env: map<string, nat>): nat { ... }
//
// NOTE: Dafny's `map<string, nat>` → Kotlin `Map<String, Int>`. SnaKt/Viper has
// no built-in map model (not listed in USAGE.md). Attempting to convert these
// functions to Viper would produce INTERNAL_ERROR. Both are marked @NeverConvert
// so they are completely excluded from Viper processing.
// `@Pure` is intentionally omitted: a @NeverConvert function cannot be used in
// Viper specs, so the @Pure contract cannot be honoured by the verifier.
//
// `if s in env.Keys then env[s] else 0` → `env.getOrDefault(name, 0)`
@NeverConvert
fun eval(e: Expr, env: Map<String, Int>): Int = when (e) {
    is ExprConst -> e.value
    is ExprVar -> env.getOrDefault(e.name, 0)
    is ExprNode -> evalList(e.args, e.op, env)
}

@NeverConvert
fun evalList(args: DList<Expr>, op: Op, env: Map<String, Int>): Int = when (args) {
    is DNil -> when (op) {
        Op.Add -> 0
        Op.Mul -> 1
    }
    is DCons -> {
        val v0 = eval(args.head, env)
        val v1 = evalList(args.tail, op, env)
        when (op) {
            Op.Add -> v0 + v1
            Op.Mul -> v0 * v1
        }
    }
}