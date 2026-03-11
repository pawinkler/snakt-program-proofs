package chapter02_making_it_formal.examples

import org.jetbrains.kotlin.formver.plugin.NeverVerify
import org.jetbrains.kotlin.formver.plugin.verify

// Dafny:
//   method Swap(X: int, Y: int) {
//     var x, y := X, Y;
//     var tmp := x;
//     x := y;
//     y := tmp;
//     assert x == Y && y == X;
//   }
fun swap(X: Int, Y: Int) {
    var x = X
    var y = Y
    val tmp = x
    x = y
    y = tmp
    verify(x == Y && y == X)
}

// Dafny:
//   method SwapArithmetic(X: int, Y: int) {
//     var x, y := X, Y;
//     x := y - x;
//     y := y - x;
//     x := y + x;
//     assert x == Y && y == X;
//   }
fun swapArithmetic(X: Int, Y: Int) {
    var x = X
    var y = Y
    x = y - x
    y = y - x
    x = y + x
    verify(x == Y && y == X)
}

// Dafny:
//   method SwapBitvectors(X: bv8, Y: bv8) {
//     var x, y := X, Y;
//     x := x ^ y;
//     y := x ^ y;
//     x := x ^ y;
//     assert x == Y && y == X;
//   }
//
// NOTE: Dafny's bv8 is an 8-bit bitvector type with wrapping arithmetic. Kotlin has
// no native bitvector type; Int is used here as the closest equivalent. Bitwise
// operators (xor) are not in SnaKt's supported operator set, so verification is
// skipped. The assert is left as a comment for documentation purposes.
@NeverVerify
fun swapBitvectors(X: Int, Y: Int) {
    var x = X
    var y = Y
    x = x xor y
    y = x xor y
    x = x xor y
    // verify(x == Y && y == X)  // not verifiable: xor not supported by SnaKt/Viper
}

// Dafny:
//   method SwapSimultaneous(X: int, Y: int) {
//     var x, y := X, Y;
//     x, y := y, x;
//     assert x == Y && y == X;
//   }
//
// NOTE: Kotlin has no simultaneous assignment syntax. A temporary variable is used,
// which is semantically equivalent.
fun swapSimultaneous(X: Int, Y: Int) {
    var x = X
    var y = Y
    val newX = y
    val newY = x
    x = newX
    y = newY
    verify(x == Y && y == X)
}