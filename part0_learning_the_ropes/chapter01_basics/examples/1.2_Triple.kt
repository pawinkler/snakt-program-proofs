package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.verify

// NOTE: Viper (the intermediate backend verifier used by SnaKt) stops it's verification at the first violated
//       assertion. Thus, only one warning is generated for this file.
fun triple2(x: Int): Int {
    val y = 2 * x
    val r = x + y
    verify(r == 10 * x)   // should fail
    verify(r < 5)         // should fail
    verify(false)         // should fail
    return r
}