//package chapter04_inductive_datatypes.examples
//
//import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
//import org.jetbrains.kotlin.formver.plugin.Pure
//import org.jetbrains.kotlin.formver.plugin.verify
//
//// Color is reused from 4.4_ColoredTree.kt (same package); not redeclared here.
//
//// Dafny:
////   datatype Tree<T> = Leaf(data: T) | Node(left: Tree<T>, right: Tree<T>)
////
//// NOTE: Variants renamed `TreeLeaf` / `TreeNode` to avoid clashes with
//// `ColoredLeaf`, `ColoredNode`, and `Node` already defined in this package.
//// NOTE: SnaKt support for generic sealed classes in Viper is not documented;
//// this translation is structurally correct Kotlin but may hit verifier limits.
//sealed class Tree<T>
//data class TreeLeaf<T>(val data: T) : Tree<T>()
//data class TreeNode<T>(val left: Tree<T>, val right: Tree<T>) : Tree<T>()
//
//// Dafny:
////   predicate AllBlue(t: Tree<Color>) {
////     match t
////     case Leaf(c) => c == Blue
////     case Node(left, right) => AllBlue(left) && AllBlue(right)
////   }
////
//// Dafny `match` → Kotlin `when`. Smart cast gives access to `t.data` / `t.left` etc.
//@Pure
//fun allBlue(t: Tree<Color>): Boolean = when (t) {
//    is TreeLeaf -> t.data == Color.Blue
//    is TreeNode -> allBlue(t.left) && allBlue(t.right)
//}
//
//// Dafny:
////   function Size<T>(t: Tree<T>): nat {
////     match t
////     case Leaf(_) => 1
////     case Node(left, right) => Size(left) + Size(right)
////   }
//@Pure
//fun <T> size(t: Tree<T>): Int = when (t) {
//    is TreeLeaf -> 1
//    is TreeNode -> size(t.left) + size(t.right)
//}
//
//// Dafny:
////   lemma Test() {
////     var a := Leaf(Color.Blue);
////     var b := Leaf(Color.Yellow);
////     var t := Tree.Node(a, b);
////     assert Size(t) == Size(t) == 2;
////   }
////
//// NOTE: `Size(t) == Size(t) == 2` in Dafny would be parsed as
//// `(Size(t) == Size(t)) == 2`, which is a bool-vs-int type error. This is
//// likely a stylistic redundancy in the book; translated as a single assertion.
//// `Tree.Node(a, b)` is Dafny's qualified constructor syntax; in Kotlin the
//// type is inferred from context.
//@AlwaysVerify
//fun testTree() {
//    val a = TreeLeaf(Color.Blue)
//    val b = TreeLeaf(Color.Yellow)
//    val t = TreeNode(a, b)
//    verify(size(t) == 2)
//}