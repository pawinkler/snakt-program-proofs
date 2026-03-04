package chapter04_inductive_datatypes.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   datatype Color = Blue | Yellow | Green | Red
//
// Unit-constructor datatypes (no fields) map to Kotlin enum classes.
enum class Color { Blue, Yellow, Green, Red }

// Dafny:
//   datatype ColoredTree = Leaf(Color) | Node(ColoredTree, ColoredTree)
//
// NOTE: `Leaf` and `Node` are renamed `ColoredLeaf` / `ColoredNode` to avoid
// clashing with `BlueLeaf`, `YellowLeaf`, `Node` already defined in this package
// by 4.2_Destructors.kt. Dafny datatypes are module-scoped; Kotlin classes are
// package-scoped, so name collisions must be avoided manually.
// Dafny's unnamed positional fields are given descriptive names in Kotlin.
sealed class ColoredTree
data class ColoredLeaf(val color: Color) : ColoredTree()
data class ColoredNode(val left: ColoredTree, val right: ColoredTree) : ColoredTree()

// Dafny:
//   predicate IsSwedishFlagColor(c: Color) { c.Blue? || c.Yellow? }
//
// Dafny `predicate` = pure function returning bool.
// Discriminator `c.Blue?` → Kotlin equality check `c == Color.Blue`.
@Pure
fun isSwedishFlagColor(c: Color): Boolean = c == Color.Blue || c == Color.Yellow


// Dafny:
//   predicate IsLithuanianFlagColor(c: Color) { c != Blue }
@Pure
fun isLithuanianFlagColor(c: Color): Boolean = c != Color.Blue

// Dafny:
//   lemma Test(a: Color) { ... }
//
// Dafny `lemma` = ghost method used only for proofs, no compiled output.
// Translated as @AlwaysVerify with verify() assertions.
//
// NOTE: SnaKt's ability to reason about enum values is not documented in
// USAGE.md. The key assertion — that IsSwedishFlagColor(a) && IsLithuanianFlagColor(a)
// implies a == Yellow — follows from the exhaustive enum cases and simple
// boolean logic; an SMT solver should handle it if enums are modelled as a
// finite domain.
@AlwaysVerify
fun testColors(a: Color) {
    val y = Color.Yellow
    verify(isSwedishFlagColor(y) && isLithuanianFlagColor(y))

    val b = Color.Blue
    verify(isSwedishFlagColor(b) && !isLithuanianFlagColor(b))

    val r = Color.Red
    verify(!isSwedishFlagColor(r) && isLithuanianFlagColor(r))

    if (isSwedishFlagColor(a) && isLithuanianFlagColor(a)) {
        verify(a == Color.Yellow)
    }
}