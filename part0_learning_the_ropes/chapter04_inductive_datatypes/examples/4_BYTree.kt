package chapter04_inductive_datatypes.examples

import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.preconditions

// BYTree, BlueLeaf, YellowLeaf, Node, and blueCount are already defined in
// 4.2_Destructors.kt (same package) and are reused here without redeclaration.

// Dafny:
//   function Example(): BYTree {
//     Node(BlueLeaf, Node(YellowLeaf, BlueLeaf))
//   }
@Pure
fun byTreeExample(): BYTree = Node(BlueLeaf, Node(YellowLeaf, BlueLeaf))

// Dafny:
//   function BlueCount(t: BYTree): nat { match t ... }
//
// Already translated in 4.2_Destructors.kt as `blueCount`. That version uses
// `when` (matching the `match` form here) so no re-translation is needed.

// Dafny:
//   function LeftDepth(t: BYTree): nat {
//     match t
//     case BlueLeaf => 0
//     case YellowLeaf => 0
//     case Node(left, _) => 1 + LeftDepth(left)
//   }
@Pure
fun leftDepth(t: BYTree): Int = when (t) {
    is BlueLeaf -> 0
    is YellowLeaf -> 0
    is Node -> 1 + leftDepth(t.left)
}

// Dafny:
//   predicate IsNode(t: BYTree) {
//     match t
//     case BlueLeaf => false
//     case YellowLeaf => false
//     case Node(_, _) => true
//   }
//
// The match is equivalent to a single type-check; `t is Node` is the idiomatic
// Kotlin form. Expanded `when` would be equally valid but unnecessarily verbose.
@Pure
fun isNode(t: BYTree): Boolean = t is Node

// Dafny:
//   function GetLeft(t: BYTree): BYTree
//     requires t.Node?
//   { match t case Node(left, _) => left }
//
// Dafny's discriminator precondition `t.Node?` → `t is Node`.
// The body only has one reachable branch given the precondition; an explicit
// cast `t as Node` is used since preconditions don't produce Kotlin smart casts.
@Pure
fun getLeft(t: BYTree): BYTree {
    preconditions { t is Node }
    return (t as Node).left
}