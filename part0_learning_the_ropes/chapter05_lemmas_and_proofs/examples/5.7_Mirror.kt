package chapter05_lemmas_and_proofs.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.Pure
import org.jetbrains.kotlin.formver.plugin.postconditions
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   datatype Tree<T> = Leaf(data: T) | Node(left: Tree<T>, right: Tree<T>)
//
// Redefined here (different package from 4.5_Tree.kt). Same naming conventions:
// TreeLeaf / TreeNode to avoid future clashes within this package.
//
// NOTE: SnaKt support for generic sealed classes and generic @Pure/@AlwaysVerify
// functions is uncertain (see 4.5_Tree.kt note). This file stacks that uncertainty
// with structural induction on the tree, which also requires sealed-class dispatch.
sealed class Tree5<T>
data class TreeLeaf5<T>(val data: T) : Tree5<T>()
data class TreeNode5<T>(val left: Tree5<T>, val right: Tree5<T>) : Tree5<T>()

// Dafny:
//   function Mirror<T>(t: Tree<T>): Tree<T> {
//     match t
//     case Leaf(_) => t
//     case Node(left, right) => Node(Mirror(right), Mirror(left))
//   }
@Pure
fun <T> mirror(t: Tree5<T>): Tree5<T> = when (t) {
    is TreeLeaf5 -> t
    is TreeNode5 -> TreeNode5(mirror(t.right), mirror(t.left))
}

// Dafny:
//   function Size<T>(t: Tree<T>): nat { ... }   -- same as 4.5_Tree.kt
@Pure
fun <T> size5(t: Tree5<T>): Int = when (t) {
    is TreeLeaf5 -> 1
    is TreeNode5 -> size5(t.left) + size5(t.right)
}

// Dafny:
//   lemma {:induction false} MirrorMirror<T>(t: Tree<T>)
//     ensures Mirror(Mirror(t)) == t
//   {
//     match t
//     case Leaf(_) => // trivial
//     case Node(left, right) =>
//       calc {
//         Mirror(Mirror(Node(left, right)));
//       ==  // def. Mirror (inner)
//         Mirror(Node(Mirror(right), Mirror(left)));
//       ==  // def. Mirror (outer)
//         Node(Mirror(Mirror(left)), Mirror(Mirror(right)));
//       ==  { MirrorMirror(left); MirrorMirror(right); }
//         Node(left, right);
//       }
//   }
//
// NOTE: `calc` has no SnaKt equivalent (see 5_Increasing_Proof6.kt).
// NOTE: `postconditions<Unit>` on a generic function is untested in SnaKt.
@AlwaysVerify
fun <T> mirrorMirror(t: Tree5<T>) {
    postconditions<Unit> { mirror(mirror(t)) == t }
    when (t) {
        is TreeLeaf5 -> {
            // trivial: mirror(mirror(leaf)) = mirror(leaf) = leaf
        }
        is TreeNode5 -> {
            // calc: mirror(mirror(t)) == mirror(Node(mir(r),mir(l)))
            //                         == Node(mir(mir(l)),mir(mir(r)))  [def. mirror outer]
            //                         == Node(l, r)                     [IH]
            val left = t.left
            val right = t.right
            verify(mirror(t) == TreeNode5(mirror(right), mirror(left)))        // def. mirror (inner)
            verify(mirror(mirror(t)) ==
                   TreeNode5(mirror(mirror(left)), mirror(mirror(right))))      // def. mirror (outer)
            mirrorMirror(left)                                                   // IH: mirror(mirror(left)) == left
            mirrorMirror(right)                                                  // IH: mirror(mirror(right)) == right
            verify(mirror(mirror(left)) == left)                                // from IH
            verify(mirror(mirror(right)) == right)                              // from IH
        }
    }
}

// Dafny:
//   lemma {:induction false} MirrorSize<T>(t: Tree<T>)
//     ensures Size(Mirror(t)) == Size(t)
//   {
//     match t
//     case Leaf(_) =>
//     case Node(left, right) =>
//       calc {
//         Size(Mirror(Node(left, right)));
//       ==  // def. Mirror
//         Size(Node(Mirror(right), Mirror(left)));
//       ==  // def. Size
//         Size(Mirror(right)) + Size(Mirror(left));
//       ==  { MirrorSize(right); MirrorSize(left); }
//         Size(right) + Size(left);
//       ==  // def. Size
//         Size(Node(left, right));
//       }
//   }
@AlwaysVerify
fun <T> mirrorSize(t: Tree5<T>) {
    postconditions<Unit> { size5(mirror(t)) == size5(t) }
    when (t) {
        is TreeLeaf5 -> {
            // trivial: size(mirror(leaf)) = size(leaf) = 1
        }
        is TreeNode5 -> {
            val left = t.left
            val right = t.right
            verify(mirror(t) == TreeNode5(mirror(right), mirror(left)))              // def. mirror
            verify(size5(mirror(t)) == size5(mirror(right)) + size5(mirror(left)))  // def. size on mirrored node
            mirrorSize(right)                                                          // IH: size(mirror(right)) == size(right)
            mirrorSize(left)                                                           // IH: size(mirror(left)) == size(left)
            verify(size5(mirror(right)) == size5(right))                             // from IH
            verify(size5(mirror(left)) == size5(left))                               // from IH
            verify(size5(t) == size5(left) + size5(right))                           // def. size on t
        }
    }
}
