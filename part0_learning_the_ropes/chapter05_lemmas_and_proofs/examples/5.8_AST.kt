package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.NeverConvert
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.preconditions

// Types (Op5, DList5, DNil5, DCons5, Expr5, ExprConst5, ExprVar5, ExprNode5) and functions
// (eval5, evalList5, substitute, substituteList) are defined in 5.8.0_EvalSubstituteList.kt
// (same package; reused here without redefinition).

// Dafny:
//   lemma EvalSubstitute(e: Expr, n: string, c: nat, env: map<string,nat>)
//     ensures Eval(Substitute(e, n, c), env) == Eval(e, env[n := c])
//   {
//     match e
//     case Const(_) =>
//     case Var(_) =>
//     case Node(op, args) => EvalSubstituteList(args, op, n, c, env);
//   }
//
// NOTE: Defined as @NeverConvert stub in 5.8.0_EvalSubstituteList.kt. The proof body
// (match on e; call evalSubstituteList for the Node case) is untranslatable because the
// postcondition references env[n := c] (map update not supported in SnaKt/Viper).

// Dafny:
//   lemma {:induction false} EvalSubstituteList(
//       args: List<Expr>, op: Op, n: string, c: nat, env: map<string,nat>)
//     ensures EvalList(SubstituteList(args,n,c), op, env) == EvalList(args, op, env[n:=c])
//   {
//     match args
//     case Nil =>
//     case Cons(e, tail) =>
//       EvalSubstitute(e, n, c, env);
//       EvalSubstituteList(tail, op, n, c, env);
//   }
//
// NOTE: Defined as @NeverConvert stub in 5.8.0_EvalSubstituteList.kt. The proof body
// (call evalSubstitute + recursive call) is untranslatable for the same reason.

// Dafny:
//   function Unit(op: Op): nat { match op case Add => 0 case Mul => 1 }
//
// NOTE: Renamed `unit5` — `Unit` is a built-in type name in Kotlin.
@Pure
fun unit5(op: Op5): Int = when (op) {
    Op5.Add -> 0
    Op5.Mul -> 1
}

// Dafny:
//   function Optimize(e: Expr): Expr {
//     if e.Node? then
//       var args := OptimizeAndFilter(e.args, Unit(e.op));
//       Shorten(e.op, args)
//     else
//       e
//   }
//
// NOTE: `e.Node?` (Dafny destructor test) → `e is ExprNode5` (Kotlin type check).
// Kotlin smart-casts `e` to `ExprNode5` inside the if branch.
// `Optimize` and `OptimizeAndFilter` are mutually recursive; SnaKt accepts this via
// circular axiomatisation (termination not checked).
@Pure
fun optimize(e: Expr5): Expr5 =
    if (e is ExprNode5) {
        val args = optimizeAndFilter(e.args, unit5(e.op))
        shorten(e.op, args)
    } else {
        e
    }

// Dafny:
//   function Shorten(op: Op, args: List<Expr>): Expr {
//     match args
//     case Nil        => Const(Unit(op))
//     case Cons(e, Nil) => e
//     case _          => Node(op, args)
//   }
//
// NOTE: `Cons(e, Nil)` (nested pattern) → `is DCons5` then `args.tail is DNil5`.
// After the `is DCons5` branch, Kotlin smart-casts `args` to `DCons5<Expr5>`.
@Pure
fun shorten(op: Op5, args: DList5<Expr5>): Expr5 = when (args) {
    is DNil5  -> ExprConst5(unit5(op))
    is DCons5 -> if (args.tail is DNil5) args.head else ExprNode5(op, args)
}

// Dafny:
//   function OptimizeAndFilter(es: List<Expr>, unit: nat): List<Expr>
//   {
//     match es
//     case Nil => Nil
//     case Cons(e, tail) =>
//       var e', tail' := Optimize(e), OptimizeAndFilter(tail, unit);
//       if e' == Const(unit) then tail' else Cons(e', tail')
//   }
//
// NOTE: `unit` renamed `unitVal` — lowercase `unit` shadows `Unit` visually; renamed for
// clarity. `e' == Const(unit)` → `ePrime == ExprConst5(unitVal)` (data class == checks
// structural equality, matching Dafny's value equality on datatypes).
@Pure
fun optimizeAndFilter(es: DList5<Expr5>, unitVal: Int): DList5<Expr5> {
    preconditions { 0 <= unitVal }
    return when (es) {
        is DNil5  -> DNil5
        is DCons5 -> {
            val ePrime    = optimize(es.head)
            val tailPrime = optimizeAndFilter(es.tail, unitVal)
            if (ePrime == ExprConst5(unitVal)) tailPrime else DCons5(ePrime, tailPrime)
        }
    }
}

// Dafny:
//   lemma OptimizeCorrect(e: Expr, env: map<string, nat>)
//     ensures Eval(Optimize(e), env) == Eval(e, env)
//   {
//     if e.Node? {
//       var args := OptimizeAndFilter(e.args, Unit(e.op));
//       calc {
//         Eval(Optimize(e), env);
//       ==  // def. Optimize
//         Eval(Shorten(e.op, args), env);
//       ==  { ShortenCorrect(e.op, args, env); }
//         Eval(Node(e.op, args), env);
//       ==  { OptimizeAndFilterCorrect(e.args, e.op, env); }
//         Eval(Node(e.op, e.args), env);
//       }
//     }
//   }
//
// NOTE: Postcondition references Eval (map-dependent, @NeverConvert). @NeverConvert.
// Non-Node cases: Optimize(e) == e trivially, so Eval(e, env) == Eval(e, env).
// Node case: 3-step calc chain using shortenCorrect and optimizeAndFilterCorrect.
@NeverConvert
fun optimizeCorrect(e: Expr5, env: Map<String, Int>) {
    // postcondition (untranslatable): eval5(optimize(e), env) == eval5(e, env)
    //
    // Proof (if e is ExprNode5):
    //   val args = optimizeAndFilter(e.args, unit5(e.op))
    //   calc:
    //     eval5(optimize(e), env)
    //     == eval5(shorten(e.op, args), env)           [def. optimize]
    //     == eval5(ExprNode5(e.op, args), env)         [shortenCorrect(e.op, args, env)]
    //     == eval5(ExprNode5(e.op, e.args), env)       [optimizeAndFilterCorrect(e.args, e.op, env)]
}

// Dafny:
//   lemma ShortenCorrect(op: Op, args: List<Expr>, env: map<string, nat>)
//     ensures Eval(Shorten(op, args), env) == Eval(Node(op, args), env)
//   {
//     match args
//     case Nil =>
//     case Cons(a, Nil) =>
//       calc {
//         Eval(Node(op, Cons(a, Nil)), env);
//       ==  // def. Eval
//         EvalList(Cons(a, Nil), op, env);
//       ==  // def. EvalList
//         var v0, v1 := Eval(a, env), EvalList(Nil, op, env);
//         match op case Add => v0 + v1 case Mul => v0 * v1;
//       ==  // def. EvalList on Nil
//         var v0, v1 := Eval(a, env), Unit(op);
//         match op case Add => v0 + v1 case Mul => v0 * v1;
//       ==  // substitute for v0, v1
//         match op case Add => Eval(a, env) + Unit(op) case Mul => Eval(a, env) * Unit(op);
//       ==  // def. Unit in each case (identity element: 0 for Add, 1 for Mul)
//         Eval(a, env);
//       }
//     case _ =>
//   }
//
// NOTE: Postcondition uses Eval (map-dependent). @NeverConvert.
// Nil: shorten returns Const(unit5(op)); both sides eval to unit5(op) — trivial.
// Cons(a, Nil): 5-step calc reducing Node(op, [a]) to Eval(a, env) via identity element.
// Other Cons: shorten returns Node(op, args) unchanged — trivial.
@NeverConvert
fun shortenCorrect(op: Op5, args: DList5<Expr5>, env: Map<String, Int>) {
    // postcondition (untranslatable):
    //   eval5(shorten(op, args), env) == eval5(ExprNode5(op, args), env)
    //
    // Proof by match on args:
    //   Nil case: trivial
    //   Cons(a, Nil) case: calc chain (see Dafny above) reducing to eval5(a, env)
    //     using unit5(op) as the identity element for op
    //   other Cons case: trivial (shorten(op, args) == ExprNode5(op, args))
}

// Dafny:
//   lemma OptimizeAndFilterCorrect(args: List<Expr>, op: Op, env: map<string, nat>)
//     ensures Eval(Node(op, OptimizeAndFilter(args, Unit(op))), env)
//          == Eval(Node(op, args), env)
//   {
//     match args
//     case Nil =>
//     case Cons(e, tail) =>
//       OptimizeCorrect(e, env);
//       OptimizeAndFilterCorrect(tail, op, env);
//   }
//
// NOTE: Postcondition uses Eval (map-dependent). @NeverConvert.
// Nil: optimizeAndFilter(Nil, _) == Nil — trivial.
// Cons case: call optimizeCorrect (head) + recursive call (tail) as induction hypotheses.
@NeverConvert
fun optimizeAndFilterCorrect(args: DList5<Expr5>, op: Op5, env: Map<String, Int>) {
    // postcondition (untranslatable):
    //   eval5(ExprNode5(op, optimizeAndFilter(args, unit5(op))), env)
    //     == eval5(ExprNode5(op, args), env)
    //
    // Proof by match on args:
    //   Nil case: trivial
    //   Cons(e, tail) case:
    //     optimizeCorrect(e, env)                     // IH: eval5(optimize(e), env) == eval5(e, env)
    //     optimizeAndFilterCorrect(tail, op, env)     // IH on tail
}