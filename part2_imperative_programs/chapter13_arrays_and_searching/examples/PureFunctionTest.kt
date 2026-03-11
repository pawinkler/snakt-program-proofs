// ALWAYS_VALIDATE
package chapter13_arrays_and_searching.examples

import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.implies

// Probe file: @Pure one-liners for every common Kotlin infix / operator / extension
// function that is side-effect-free.  Where multiple call syntaxes exist for the
// same operation, all are listed.
//
// NOTE on terminology:
//   - `infix fun`   — called as `a foo b`  (requires `infix` keyword)
//   - `operator fun`— called as `a + b`    (requires `operator` keyword; NOT `a plus b`)
//   - extension fun — called as `a.foo(b)` (no special keyword)
// Many infix funs can also be called as `a.foo(b)`; operator funs as `a.plus(b)`.
//
// Sections:
//   1. Boolean infix   — and / or / xor   (infix fun)
//   2. Int bitwise     — and / or / xor / shl / shr / ushr  (infix fun)
//   3. Int arithmetic  — plus / minus / times / div / rem / unaryMinus  (operator fun)
//   4. Comparison      — compareTo / coerceAtLeast / coerceAtMost / coerceIn / maxOf / minOf
//   5. Range-producing — rangeTo / until / downTo / step     (infix fun; @NeverVerify)
//   6. Pair-producing  — to                                  (infix fun; @NeverVerify)
//   7. Long bitwise    — and / or / xor / shl / shr / ushr  (infix fun; @NeverVerify)
//   8. SnaKt infix     — implies                             (infix fun)

// ── 1. Boolean infix ──────────────────────────────────────────────────────────

// Boolean.and — non-short-circuit logical AND (infix)
@Pure fun boolAndInfix(a: Boolean, b: Boolean): Boolean = a and b
@Pure fun boolAndMethod(a: Boolean, b: Boolean): Boolean = a.and(b)
// && is the short-circuit variant (different evaluation semantics, same result on pure args)
@Pure fun boolAndShortCircuit(a: Boolean, b: Boolean): Boolean = a && b

// Boolean.or — non-short-circuit logical OR (infix)
@Pure fun boolOrInfix(a: Boolean, b: Boolean): Boolean = a or b
@Pure fun boolOrMethod(a: Boolean, b: Boolean): Boolean = a.or(b)
@Pure fun boolOrShortCircuit(a: Boolean, b: Boolean): Boolean = a || b

// Boolean.xor — exclusive OR (infix)
@Pure fun boolXorInfix(a: Boolean, b: Boolean): Boolean = a xor b
@Pure fun boolXorMethod(a: Boolean, b: Boolean): Boolean = a.xor(b)

// Boolean.not — logical negation (operator)
@Pure fun boolNotOperator(a: Boolean): Boolean = !a
@Pure fun boolNotMethod(a: Boolean): Boolean = a.not()

// ── 2. Int bitwise ────────────────────────────────────────────────────────────

// Int.and — bitwise AND (infix)
@Pure fun intAndInfix(a: Int, b: Int): Int = a and b
@Pure fun intAndMethod(a: Int, b: Int): Int = a.and(b)

// Int.or — bitwise OR (infix)
@Pure fun intOrInfix(a: Int, b: Int): Int = a or b
@Pure fun intOrMethod(a: Int, b: Int): Int = a.or(b)

// Int.xor — bitwise XOR (infix)
@Pure fun intXorInfix(a: Int, b: Int): Int = a xor b
@Pure fun intXorMethod(a: Int, b: Int): Int = a.xor(b)

// Int.shl — shift left (infix)
@Pure fun intShlInfix(a: Int, bits: Int): Int = a shl bits
@Pure fun intShlMethod(a: Int, bits: Int): Int = a.shl(bits)

// Int.shr — arithmetic shift right, sign-extending (infix)
@Pure fun intShrInfix(a: Int, bits: Int): Int = a shr bits
@Pure fun intShrMethod(a: Int, bits: Int): Int = a.shr(bits)

// Int.ushr — logical shift right, zero-extending (infix)
@Pure fun intUshrInfix(a: Int, bits: Int): Int = a ushr bits
@Pure fun intUshrMethod(a: Int, bits: Int): Int = a.ushr(bits)

// Int.inv — bitwise complement (operator, unary)
@Pure fun intInvOperator(a: Int): Int = a.inv()

// ── 3. Int arithmetic ─────────────────────────────────────────────────────────

// Int.plus — addition (operator)
@Pure fun intPlusOperator(a: Int, b: Int): Int = a + b
@Pure fun intPlusMethod(a: Int, b: Int): Int = a.plus(b)

// Int.minus — subtraction (operator)
@Pure fun intMinusOperator(a: Int, b: Int): Int = a - b
@Pure fun intMinusMethod(a: Int, b: Int): Int = a.minus(b)

// Int.times — multiplication (operator)
@Pure fun intTimesOperator(a: Int, b: Int): Int = a * b
@Pure fun intTimesMethod(a: Int, b: Int): Int = a.times(b)

// Int.div — integer division, truncates toward zero (operator)
@Pure fun intDivOperator(a: Int, b: Int): Int = a / b
@Pure fun intDivMethod(a: Int, b: Int): Int = a.div(b)

// Int.rem — remainder, sign follows dividend (operator)
@Pure fun intRemOperator(a: Int, b: Int): Int = a % b
@Pure fun intRemMethod(a: Int, b: Int): Int = a.rem(b)

// Int.unaryMinus — negation (operator)
@Pure fun intUnaryMinusOperator(a: Int): Int = -a
@Pure fun intUnaryMinusMethod(a: Int): Int = a.unaryMinus()

// Int.unaryPlus — identity (operator)
@Pure fun intUnaryPlusOperator(a: Int): Int = +a
@Pure fun intUnaryPlusMethod(a: Int): Int = a.unaryPlus()

// Int.inc / dec — increment / decrement (operator; used via ++ / --)
@Pure fun intIncMethod(a: Int): Int = a.inc()
@Pure fun intDecMethod(a: Int): Int = a.dec()

// ── 4. Comparison and clamping ────────────────────────────────────────────────

// Int.compareTo — three-way comparison; desugars <, >, <=, >= (operator)
@Pure fun intLt(a: Int, b: Int): Boolean = a < b
@Pure fun intLtMethod(a: Int, b: Int): Int = a.compareTo(b)   // returns neg/0/pos
@Pure fun intLe(a: Int, b: Int): Boolean = a <= b
@Pure fun intGt(a: Int, b: Int): Boolean = a > b
@Pure fun intGe(a: Int, b: Int): Boolean = a >= b
@Pure fun intEq(a: Int, b: Int): Boolean = a == b
@Pure fun intNe(a: Int, b: Int): Boolean = a != b

// Clamping — extension funs (not infix, not operator)
@Pure fun intCoerceAtLeast(a: Int, min: Int): Int = a.coerceAtLeast(min)
@Pure fun intCoerceAtMost(a: Int, max: Int): Int = a.coerceAtMost(max)
@Pure fun intCoerceIn(a: Int, lo: Int, hi: Int): Int = a.coerceIn(lo, hi)

// Top-level stdlib funs
@Pure fun intMaxOf(a: Int, b: Int): Int = maxOf(a, b)
@Pure fun intMinOf(a: Int, b: Int): Int = minOf(a, b)
@Pure fun intAbsOf(a: Int): Int = kotlin.math.abs(a)

// ── 5. Range-producing infix ──────────────────────────────────────────────────
// IntRange / IntProgression have no Viper model; prevents errors.

// Int.rangeTo — closed range [a, b] (operator `..`)
@Pure fun intRangeToOperator(a: Int, b: Int): IntRange = a..b
@Pure fun intRangeToMethod(a: Int, b: Int): IntRange = a.rangeTo(b)

// Int.until — half-open range [a, b) (infix)
@Pure fun intUntilInfix(a: Int, b: Int): IntRange = a until b
@Pure fun intUntilMethod(a: Int, b: Int): IntRange = a.until(b)

// Int.downTo — closed descending range (infix)
@Pure fun intDownToInfix(a: Int, b: Int): IntProgression = a downTo b

// IntProgression.step — stride (infix)
@Pure fun intStepInfix(a: Int, b: Int, n: Int): IntProgression = (a..b) step n
@Pure fun intStepMethod(a: Int, b: Int, n: Int): IntProgression = (a..b).step(n)

// ── 6. Pair-producing infix ───────────────────────────────────────────────────

// to — construct a Pair (infix); Pair has no Viper model → @NeverVerify
@Pure fun pairToInfix(a: String, b: Int): Pair<String, Int> = a to b
@Pure fun pairToMethod(a: String, b: Int): Pair<String, Int> = a.to(b)

// ── 7. Long bitwise infix ─────────────────────────────────────────────────────
// Same infix funs as Int but on Long.  @NeverVerify: SnaKt/Long support unconfirmed.

@Pure fun longAndInfix(a: Long, b: Long): Long = a and b
@Pure fun longAndMethod(a: Long, b: Long): Long = a.and(b)
@Pure fun longOrInfix(a: Long, b: Long): Long = a or b
@Pure fun longOrMethod(a: Long, b: Long): Long = a.or(b)
@Pure fun longXorInfix(a: Long, b: Long): Long = a xor b
@Pure fun longXorMethod(a: Long, b: Long): Long = a.xor(b)
@Pure fun longShlInfix(a: Long, bits: Int): Long = a shl bits
@Pure fun longShlMethod(a: Long, bits: Int): Long = a.shl(bits)
@Pure fun longShrInfix(a: Long, bits: Int): Long = a shr bits
@Pure fun longShrMethod(a: Long, bits: Int): Long = a.shr(bits)
@Pure fun longUshrInfix(a: Long, bits: Int): Long = a ushr bits
@Pure fun longUshrMethod(a: Long, bits: Int): Long = a.ushr(bits)

// ── 8. SnaKt infix: implies ───────────────────────────────────────────────────

// Boolean.implies — logical implication, i.e. !a || b (SnaKt infix fun)
@Pure fun boolImpliesInfix(a: Boolean, b: Boolean): Boolean = a implies b
@Pure fun boolImpliesMethod(a: Boolean, b: Boolean): Boolean = a.implies(b)

// ── 9. Complex expressions ─────────────────────────────────────────────────────
// Each function combines several pure operations. Only @Pure functions and
// basic operators are called; no side effects.

// Level 1: two operations — negate then add
@Pure fun negateAndAdd(a: Int, b: Int): Int = -a + b

// Level 1: two operations — shift then mask
@Pure fun shiftAndMask(a: Int, bits: Int, mask: Int): Int = (a + bits) and mask

// Level 2: if-expression choosing between two arithmetic results
@Pure fun absVal(a: Int): Int = if (a >= 0) a else -a

// Level 2: chained comparisons combined with boolean operators
@Pure fun isBetween(x: Int, lo: Int, hi: Int): Boolean = x >= lo && x <= hi

// Level 2: arithmetic with multiple operators, precedence matters
@Pure fun dotProduct2D(ax: Int, ay: Int, bx: Int, by: Int): Int = ax * bx + ay * by

// Level 2: bitwise expression combining three operations
//@Pure fun setBit(value: Int, bit: Int): Int = value or (1 shl bit)
//@Pure fun clearBit(value: Int, bit: Int): Int = value and (1 shl bit).inv()
//@Pure fun toggleBit(value: Int, bit: Int): Int = value xor (1 shl bit)
//@Pure fun testBit(value: Int, bit: Int): Boolean = (value ushr bit) and 1 != 0

// Level 3: when-expression over an Int
@Pure fun sign(a: Int): Int = when {
    a > 0  ->  1
    a < 0  -> -1
    else   ->  0
}

// Level 3: local vals feeding into a final expression
//@Pure fun clampedSum(a: Int, b: Int, lo: Int, hi: Int): Int {
//    val sum = a + b
//    return sum.coerceIn(lo, hi)
//}

// Level 3: boolean logic combining implication and xor
@Pure fun iff(a: Boolean, b: Boolean): Boolean = (a implies b) && (b implies a)
@Pure fun nand(a: Boolean, b: Boolean): Boolean = !(a && b)
@Pure fun nor(a: Boolean, b: Boolean): Boolean  = !(a || b)

// Level 4: multiple local vals, mixed arithmetic and bitwise
//@Pure fun average(a: Int, b: Int): Int {
//    val sum  = a + b
//    val half = sum shr 1          // divide by 2 via arithmetic shift
//    val odd  = sum and 1          // remainder bit
//    return half + odd             // round up if sum was odd
//}

// Level 4: recursive-style (single call depth) pure function
//@Pure fun max3(a: Int, b: Int, c: Int): Int = maxOf(maxOf(a, b), c)
//@Pure fun min3(a: Int, b: Int, c: Int): Int = minOf(minOf(a, b), c)

// Level 5: nested if + arithmetic + bitwise + boolean
//@Pure fun parityIsEven(a: Int): Boolean {
//    var x = a
//    x = x xor (x ushr 16)
//    x = x xor (x ushr 8)
//    x = x xor (x ushr 4)
//    x = x xor (x ushr 2)
//    x = x xor (x ushr 1)
//    return (x and 1) == 0        // true iff number of set bits is even
//}
