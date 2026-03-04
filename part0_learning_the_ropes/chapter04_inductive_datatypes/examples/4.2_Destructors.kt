package chapter04_inductive_datatypes.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   datatype BYTree = BlueLeaf | YellowLeaf | Node(left: BYTree, right: BYTree)
//
// Dafny algebraic datatypes map to Kotlin sealed classes. Dafny's discriminator
// syntax `t.BlueLeaf?` maps to Kotlin's `is BlueLeaf` type check.
//
// NOTE: SnaKt's support for sealed class hierarchies in Viper verification is
// not documented in USAGE.md. The translation is structurally correct Kotlin,
// but whether the verifier can reason about sealed-class dispatch (e.g. prove
// BlueCount(BlueLeaf) == 1) may depend on unverified SnaKt capabilities.
// Mark with @AlwaysVerify / @Pure to exercise this and observe the result.

sealed class BYTree
object BlueLeaf : BYTree()
object YellowLeaf : BYTree()
data class Node(val left: BYTree, val right: BYTree) : BYTree()

// Dafny:
//   function BlueCount(t: BYTree): nat {
//     if t.BlueLeaf? then 1
//     else if t.YellowLeaf? then 0
//     else BlueCount(t.left) + BlueCount(t.right)
//   }
@Pure
fun blueCount(t: BYTree): Int = when (t) {
    is BlueLeaf -> 1
    is YellowLeaf -> 0
    is Node -> blueCount(t.left) + blueCount(t.right)
}

// Dafny:
//   method Test() {
//     assert BlueCount(BlueLeaf) == 1;
//     assert BlueCount(Node(YellowLeaf, BlueLeaf)) == 1;
//   }
@AlwaysVerify
fun test() {
    verify(blueCount(BlueLeaf) == 1)
    verify(blueCount(Node(YellowLeaf, BlueLeaf)) == 1)
}