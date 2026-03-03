# SnaKt — Formal Verification for Kotlin

SnaKt is a Kotlin compiler plugin that translates Kotlin code into [Viper](https://www.pm.inf.ethz.ch/research/viper.html), a verification intermediate language, and formally verifies it. You annotate your Kotlin functions with specifications (preconditions, postconditions, loop invariants), and the plugin checks them at compile time using an SMT solver.

## Table of Contents

1. [Core Annotations Reference](#core-annotations-reference)
2. [Controlling Verification](#controlling-verification)
3. [Pure Functions](#pure-functions)
4. [Runtime Assertions with `verify()`](#runtime-assertions-with-verify)
5. [Preconditions](#preconditions)
6. [Postconditions](#postconditions)
7. [Loop Invariants](#loop-invariants)
8. [Universal Quantification with `forAll`](#universal-quantification-with-forall)
9. [Logical Connectives in Specifications](#logical-connectives-in-specifications)
10. [Ownership: `@Unique` and `@Borrowed`](#ownership-unique-and-borrowed)
11. [Kotlin Contracts Integration](#kotlin-contracts-integration)
12. [Supported Kotlin Features](#supported-kotlin-features)
13. [Standard Library Support](#standard-library-support)
14. [Working with Strings](#working-with-strings)
15. [Working with Lists](#working-with-lists)
16. [SMT Solver Triggers](#smt-solver-triggers)
17. [Advanced: Manual Permission Annotation](#advanced-manual-permission-annotation)

---

## Core Annotations Reference

All SnaKt annotations are imported from `org.jetbrains.kotlin.formver.plugin`.

| Annotation / Function   | Description |
|-------------------------|-------------|
| `@AlwaysVerify`         | Always verify this function, regardless of plugin configuration |
| `@NeverVerify`          | Never run the Viper verifier on this function (conversion still happens) |
| `@NeverConvert`         | Exclude this function entirely from Viper conversion |
| `@Pure`                 | Declare a function as side-effect-free (used in specifications) |
| `@Unique`               | Mark a value or field as having unique ownership |
| `@Borrowed`             | Mark a parameter as borrowed (not consumed) |
| `@Manual`               | Mark a property field for manual permission management |
| `preconditions { }`     | Declare assumptions that must hold on entry |
| `postconditions<T> { }` | Declare properties guaranteed on return |
| `loopInvariants { }`    | Declare properties maintained across loop iterations |
| `verify(expr, ...)`     | Assert that expressions hold at a specific point |
| `forAll<T> { }`         | Universal quantification over a type |
| `triggers(...)`         | Provide SMT solver trigger expressions inside `forAll` |
| `implies`               | Logical implication operator for use in specifications |

---

## Controlling Verification

By default, the plugin verifies functions according to project-level configuration. You can override this per function:

```kotlin
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.NeverVerify
import org.jetbrains.kotlin.formver.plugin.NeverConvert

// Always submit this function to the Viper verifier
@AlwaysVerify
fun alwaysChecked(x: Int): Int {
    return x + 1
}

// Convert to Viper but skip the verification step
@NeverVerify
fun convertButSkip(): Boolean {
    return false
}

// Do not convert or verify this function at all
@NeverConvert
fun helperFunction() {
    // side effects, I/O, etc.
}
```

`@NeverConvert` is typically used for helper functions that perform I/O or other side effects that are irrelevant to the verification of surrounding code.

---

## Pure Functions

The `@Pure` annotation marks a function as side-effect-free. Pure functions can be called inside specification blocks (`preconditions`, `postconditions`, `loopInvariants`, and `verify()`).

```kotlin
import org.jetbrains.kotlin.formver.plugin.Pure

@Pure
fun square(x: Int): Int = x * x

@Pure
fun isPositive(x: Int): Boolean = x > 0
```

**Error:** Calling a non-pure function inside a `@Pure` function is a compile-time error:

```kotlin
fun sideEffect(): Int = 1

// ERROR: INTERNAL_ERROR — testWronglyAnnotatedAsPure calls a non-pure function
@Pure
fun testWronglyAnnotatedAsPure(): Int {
    return sideEffect()
}
```

Arguments to `verify()` must also be pure expressions. Increment operators like `x++` inside `verify()` produce a `PURITY_VIOLATION` diagnostic:

```kotlin
@NeverVerify
fun test() {
    var x = 42
    verify(true, 2 <= x)           // OK
    verify(x++ < 43)               // ERROR: PURITY_VIOLATION
}
```

---

## Runtime Assertions with `verify()`

`verify()` asserts that one or more boolean expressions hold at the current program point. It can be used with or without `@AlwaysVerify` (the function still needs to be converted).

```kotlin
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun demonstrateVerify(x: Int) {
    verify(x + 1 > x)
    verify(
        0 <= x || x < 0,
        true,
    )
}
```

Multiple arguments to `verify()` are each checked independently.

---

## Preconditions

`preconditions { }` declares what must be true when a function is called. Each statement in the block is a boolean expression that is assumed to hold on entry.

```kotlin
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.preconditions
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun accessString(idx: Int) {
    preconditions {
        0 <= idx
        idx < 3
    }
    // The verifier now knows: 0 <= idx < 3
    verify(0 <= idx, idx < 3, idx != 100)
    verify("aaa"[idx] == 'a')
}
```

Callers must satisfy the preconditions. If `accessString(2)` is called, the verifier confirms the caller passes a valid index.

---

## Postconditions

`postconditions<ReturnType> { result -> ... }` specifies properties that the function guarantees on return. The lambda parameter names the return value; `it` is available as shorthand.

```kotlin
import org.jetbrains.kotlin.formver.plugin.*

// Guarantee the result is greater than the input
@AlwaysVerify
fun addFive(init: Int): Int {
    postconditions<Int> { result ->
        result > init
    }
    return init + 5
}

// Shorthand using `it`
@AlwaysVerify
fun returnGreater13(): Int {
    postconditions<Int> {
        it > 13
    }
    return 16
}

// Precondition and postcondition together
@AlwaysVerify
fun subtractTen(int: Int): Int {
    preconditions {
        int > 10
    }
    postconditions<Int> {
        it > 0
    }
    return int - 10
}
```

Postconditions are used as preconditions by callers. In this example the verifier accepts the call below because `returnGreater13()` guarantees `result > 13 > 10`:

```kotlin
@AlwaysVerify
fun chainedCall() = subtractTen(returnGreater13())
```

### Example: Proving a mathematical identity

```kotlin
@AlwaysVerify
fun recursiveSumOfIntegersUpToN(n: Int): Int {
    preconditions { n >= 0 }
    postconditions<Int> { res -> res == n * (n + 1) / 2 }

    if (n == 0) return 0
    else return n + recursiveSumOfIntegersUpToN(n - 1)
}
```

---

## Loop Invariants

`loopInvariants { }` appears at the start of a `while` loop body. Each statement is a boolean expression that must hold before the loop starts, and must be re-established at the end of every iteration.

```kotlin
import org.jetbrains.kotlin.formver.plugin.*

@AlwaysVerify
fun countToTen(n: Int) {
    var it = 0
    while (it < 10) {
        loopInvariants {
            it <= 10
        }
        it = it + 1
    }
    verify(it == 10)
}
```

### Nested loops

```kotlin
@AlwaysVerify
fun loopInsideLoop() {
    var i = 0
    while (i < 10) {
        loopInvariants {
            i <= 10
        }
        var j = i + 1
        while (j < 10) {
            loopInvariants {
                i < j
                j <= 10
            }
            j = j + 1
        }
        i = i + 1
    }
}
```

### Loops with `break`

```kotlin
@AlwaysVerify
fun withBreak() {
    var i = 0
    while (true) {
        loopInvariants {
            i <= 10
        }
        if (i >= 10) break
        i++
    }
    verify(i == 10)
}
```

### Complete example: iterative sum with proven correctness

```kotlin
@AlwaysVerify
fun sumOfIntegersUpToN(n: Int): Int {
    preconditions { n >= 0 }
    postconditions<Int> { res -> res == n * (n + 1) / 2 }

    var sum = 0
    var i = 0
    while (i < n) {
        loopInvariants {
            i <= n
            sum == i * (i + 1) / 2
        }
        sum += i + 1
        ++i
    }
    return sum
}
```

---

## Universal Quantification with `forAll`

`forAll<T> { ... }` expresses a property that holds for all values of type `T`. It is used inside `postconditions`, `loopInvariants`, and `preconditions`.

```kotlin
import org.jetbrains.kotlin.formver.plugin.*

// Every integer squared is non-negative
fun anyIntegerSquaredAtLeastZero(): Int {
    postconditions<Int> { res ->
        forAll<Int> {
            it * it >= 0
            it * it >= res
        }
    }
    return 0
}
```

### `forAll` with implication

```kotlin
// For all non-zero integers, the square is >= 1
fun anyIntegerSquaredIsAtLeastOneExceptZero(): Int {
    postconditions<Int> { res ->
        forAll<Int> {
            (it != 0) implies (it * it >= res)
        }
    }
    return 1
}
```

### `forAll` in loop invariants

```kotlin
// Find first position in string where character is >= c
@AlwaysVerify
fun String.firstAtLeast(c: Char): Int {
    postconditions<Int> { res ->
        0 <= res && res <= length
        forAll<Int> {
            (0 <= it && it < res) implies (get(it) < c)
        }
        (res != length) implies (get(res) >= c)
    }

    var i = 0
    while (i < length) {
        loopInvariants {
            0 <= i && i <= length
            forAll<Int> {
                (0 <= it && it < i) implies (get(it) < c)
            }
        }
        if (get(i) >= c) break
        ++i
    }
    return i
}
```

---

## Logical Connectives in Specifications

Inside specification blocks, use standard Kotlin operators plus the `implies` infix function:

| Expression         | Meaning                     |
|--------------------|---------------------------  |
| `a && b`           | Logical conjunction         |
| `a \|\| b`         | Logical disjunction         |
| `!a`               | Logical negation            |
| `a implies b`      | Logical implication (a → b) |

```kotlin
@AlwaysVerify
fun testImplies(arg: Boolean): Boolean {
    postconditions<Boolean> { res ->
        arg implies !res
        res implies !arg
        !arg implies res
        !res implies arg
    }
    return !arg
}

@AlwaysVerify
fun testAnd(arg1: Boolean, arg2: Boolean): Boolean {
    postconditions<Boolean> { res ->
        res implies (arg1 && arg2)
        !res implies (!arg1 || !arg2)
        (arg1 && arg2) implies res
    }
    return arg1 && arg2
}
```

---

## Ownership: `@Unique` and `@Borrowed`

SnaKt includes a lightweight ownership/uniqueness checker. A value annotated `@Unique` has a single owner and is **consumed** (moved) when passed to a function that expects a `@Unique` parameter. `@Borrowed` allows passing a unique value without consuming it.

### Basic annotations

```kotlin
import org.jetbrains.kotlin.formver.plugin.Unique
import org.jetbrains.kotlin.formver.plugin.Borrowed

class Box(@Unique val a: Any)

fun consumeBox(@Unique box: Box) { /* box is consumed here */ }
fun borrowBox(@Borrowed box: Box) { /* box is not consumed */ }
```

### Unique fields

A field annotated `@Unique` transfers ownership when accessed:

```kotlin
class A {
    @Unique val x = 1
}

class B {
    @Unique val y = A()
}

fun consumeA(@Unique a: A) {}
fun consumeB(@Unique b: B) {}

// After consuming a.data, `a` itself is partially moved and cannot be consumed
fun test(@Unique a: A) {
    consumeA(a.data)  // consumes the @Unique field
    consumeA(a)       // ERROR: UNIQUENESS_VIOLATION — a has been partially moved
}
```

### Borrowing

`@Borrowed` allows a function to use a unique value without consuming it, so the caller can use it again:

```kotlin
fun sharedBorrowingA(@Borrowed y: A) {}

fun borrowingB(@Borrowed @Unique z: B) {
    sharedBorrowingA(z.y)  // OK — borrowing z.y
}

fun valid_borrow(@Unique z: B) {
    borrowingB(z)   // borrow — z still owned by caller
    consumeB(z)     // OK — z was not consumed
}
```

Passing a `@Borrowed` value to a consuming (non-borrowed) parameter is an error:

```kotlin
fun borrowedToNonBorrowed(@Borrowed @Unique z: B) {
    consumeB(z)  // ERROR: UNIQUENESS_VIOLATION — cannot consume a borrowed value
}
```

### Running only the uniqueness checker

Add `// UNIQUE_CHECK_ONLY` at the top of a file to run only the uniqueness checker and skip Viper verification:

```kotlin
// UNIQUE_CHECK_ONLY

import org.jetbrains.kotlin.formver.plugin.Unique
import org.jetbrains.kotlin.formver.plugin.Borrowed
```

---

## Kotlin Contracts Integration

SnaKt understands Kotlin's standard `contract` DSL and encodes the contract effects into the Viper model.

### `returns()` and `returns(value)`

```kotlin
import kotlin.contracts.contract
import kotlin.contracts.ExperimentalContracts

@OptIn(ExperimentalContracts::class)
fun returnsTrue(): Boolean {
    contract {
        returns(true)
    }
    return true
}
```

### `returns(value) implies (condition)`

```kotlin
@OptIn(ExperimentalContracts::class)
fun isNonNegative(x: Int): Boolean {
    contract {
        returns(true) implies (x >= 0)
        returns(false) implies (x < 0)
    }
    return x >= 0
}
```

### `returns(null)` and `returnsNotNull()`

```kotlin
@OptIn(ExperimentalContracts::class)
fun returnsNullImpliesInput(x: Boolean?): Boolean? {
    contract {
        returns(null) implies (x == null)
        returnsNotNull() implies (x != null)
    }
    return x
}
```

### Type-narrowing contracts with `is`

```kotlin
@OptIn(ExperimentalContracts::class)
fun isString(x: Any?): Boolean {
    contract {
        returns(true) implies (x is String)
    }
    return x is String
}
```

The verifier uses the contract information to refine the types of values at call sites, mirroring how Kotlin's smart-cast mechanism works.

---

## Supported Kotlin Features

SnaKt handles a broad range of idiomatic Kotlin. The following constructs are supported in verified code.

### Control flow

```kotlin
// if / else
fun abs(x: Int): Int = if (x >= 0) x else -x

// when
fun classify(x: Int): Int = when {
    x < 0  -> -1
    x == 0 -> 0
    else   -> 1
}

// while with break / continue
fun firstNegative(arr: IntArray): Int {
    var i = 0
    while (i < arr.size) {
        if (arr[i] < 0) break
        i++
    }
    return i
}

// try / catch
fun safe(x: Int): Int {
    return try { x / 1 } catch (e: Exception) { 0 }
}
```

### Operators

| Category   | Supported operators |
|------------|---------------------|
| Arithmetic | `+`, `-`, `*`, `/`  |
| Comparison | `<`, `<=`, `>`, `>=`, `==`, `!=` |
| Boolean    | `&&`, `\|\|`, `!`   |
| Increment  | `++`, `--` (pre and post) |
| Type ops   | `is`, `!is`, `as`, `as?` |
| Null-safe  | `?.`, `?:` (Elvis)  |

### Classes and interfaces

```kotlin
open class Shape(val area: Int)

interface Drawable {
    val color: String
}

class Circle(area: Int, override val color: String) : Shape(area), Drawable
```

Supported OOP features include:
- Primary and secondary constructors
- Inheritance (single and multiple interface)
- Property getters and setters with backing fields
- Extension properties
- Member functions and overloading

### Lambdas and inline functions

Inline functions whose lambda parameters are expanded at call sites are fully supported. Non-local returns from inline lambdas are handled correctly.

```kotlin
inline fun <T> myRun(block: () -> T): T = block()

@AlwaysVerify
fun useMyRun(x: Int): Int {
    return myRun { x + 1 }
}
```

### Generics

Basic generic classes and functions are supported:

```kotlin
class Box<T>(val value: T)

fun <T> identity(x: T): T = x
```

### Nullability

Nullable types (`T?`), null checks, and smart-casts through null checks are fully modelled:

```kotlin
@AlwaysVerify
fun safeHead(list: List<Int>?): Int? {
    if (list != null && !list.isEmpty()) {
        return list[0]
    }
    return null
}
```

---

## Standard Library Support

When the `REPLACE_STDLIB_EXTENSIONS` mode is active, the following standard library functions are handled:

| Function  | Notes |
|-----------|-------|
| `check(condition)` | Modelled as a verified assertion |
| `run { }` | Inline expansion |
| `x.run { }` | Inline expansion with receiver |
| `x.let { }` | Inline expansion, `it` bound to receiver |
| `x.also { }` | Inline expansion, `it` bound to receiver |
| `with(x) { }` | Inline expansion with receiver |
| `x.apply { }` | Inline expansion with receiver |

```kotlin
fun useStdlib(x: Int) {
    check(x > 0)

    val result = x.let { it + 1 }
    verify(result == x + 1)

    x.also { verify(it == x) }

    with(x) { verify(this == x) }
}
```

### Scoped receiver disambiguation

When multiple `with`/`run` blocks are nested, use `this@label` to refer to the right receiver:

```kotlin
fun nestedReceivers(x: Int) {
    with(true) {
        false.run {
            verify(
                !this,        // false (from run)
                this@with,    // true (from with)
            )
        }
    }
}
```

---

## Working with Strings

SnaKt models `String` as a sequence of `Char` values and supports:

| Operation             | Example                        |
|-----------------------|--------------------------------|
| Length                | `s.length`                     |
| Character access      | `s[i]` (requires `0 <= i < s.length`) |
| Comparison            | `s[i] == 'a'`, `s[i] < c`     |
| Concatenation         | `"Hello" + " " + "World"`      |
| String literals       | `"abc"[0] == 'a'`              |

```kotlin
@AlwaysVerify
fun testStrings(s: String) {
    val len = s.length
    verify("str".length == 3)
    verify("Kotlin" + "." + "String" == "Kotlin.String")

    // Character-level reasoning
    val str = "aba"
    verify(str[0] == str[2])
    verify(str[1] == 'b')
}
```

Character arithmetic works for specifications:

```kotlin
@AlwaysVerify
fun charArithmetic(s: String) {
    if (s.length > 0) {
        val diff = s[0] - 'a'
        verify(diff * diff >= 0)
    }
}
```

---

## Working with Lists

SnaKt supports `List<T>` and `MutableList<T>` with the following operations:

| Operation           | Notes |
|---------------------|-------|
| `l.size`            | Returns the number of elements |
| `l.isEmpty()`       | True if `l.size == 0` |
| `l[i]`              | Requires `0 <= i < l.size` |
| `l.add(x)`          | Appends to a `MutableList` |
| `emptyList<T>()`    | Creates an empty list |

```kotlin
import org.jetbrains.kotlin.formver.plugin.AlwaysVerify

@AlwaysVerify
fun lastOrNull(l: List<Int>): Int? {
    val size = l.size
    return if (size != 0) l[size - 1] else null
}

@AlwaysVerify
fun addAndGet(l: MutableList<Int>) {
    l.add(1)
    val n = l[0]
}

@AlwaysVerify
fun nullableList(l: List<Int>?) {
    if (l != null && !l.isEmpty()) {
        val x = l[l.size - 1]
    }
}
```

---

## SMT Solver Triggers

For complex `forAll` expressions, the SMT solver may fail to instantiate the quantifier automatically. Use `triggers(expr, ...)` to hint which terms should trigger instantiation:

```kotlin
import org.jetbrains.kotlin.formver.plugin.*

// Single trigger
fun withSimpleTrigger(): Int {
    postconditions<Int> { res ->
        forAll<Int> {
            triggers(it * it)       // guide the SMT solver
            it * it >= 0
            it * it >= res
        }
    }
    return 0
}

// Multiple trigger expressions
fun withMultipleTriggers(): Int {
    postconditions<Int> { res ->
        forAll<Int> {
            triggers(it * it, it + 1)
            (it != 0) implies (it * it >= res)
        }
    }
    return 1
}
```

Triggers can also appear inside `loopInvariants`:

```kotlin
fun withTriggersInLoop(str: String): Int {
    var res = 0
    var i = 10
    while (i > 0) {
        loopInvariants {
            forAll<Int> {
                triggers(str[it])
                (0 <= it && it < str.length) implies ((str[it] - 'a') * (str[it] - 'a') >= res)
            }
        }
        i--
    }
    return res
}
```

---

## Advanced: Manual Permission Annotation

The `@Manual` annotation (applied as `@property:Manual`) opts a specific property out of the automatic Viper permission system. This is useful when interoperating with code that manages its own heap permissions.

```kotlin
import org.jetbrains.kotlin.formver.plugin.Manual

class ManualPermissionFields(
    val a: Int,
    @property:Manual var b: Int,   // b is managed manually
)

fun readFields(mpf: ManualPermissionFields) {
    val a = mpf.a   // automatic permission
    val b = mpf.b   // manual permission
}

fun writeField(mpf: ManualPermissionFields) {
    mpf.b = 123     // manual permission
}
```

Files using `@Manual` typically include `// NEVER_VALIDATE` at the top, since the manual heap model is not checked end-to-end.

---

## File-Level Directives

These comments at the top of a test or source file control plugin behaviour:

| Directive                   | Effect |
|-----------------------------|--------|
| `// UNIQUE_CHECK_ONLY`      | Run only the uniqueness checker; skip Viper |
| `// ALWAYS_VALIDATE`        | Force verification even if the project default is off |
| `// NEVER_VALIDATE`         | Skip Viper verification for the whole file |
| `// RENDER_PREDICATES`      | Include predicate definitions in Viper output |
| `// REPLACE_STDLIB_EXTENSIONS` | Use SnaKt's stdlib replacements (`run`, `let`, etc.) |
| `// WITH_STDLIB`            | Include stdlib types and functions in the Viper model |
