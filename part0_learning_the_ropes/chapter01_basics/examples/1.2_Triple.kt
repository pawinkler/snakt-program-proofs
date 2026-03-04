package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun triple2(x: Int): Int {
    val y = 2 * x
    val r = x + y
    verify(r == 10 * x)   // would fail
    verify(r < 5)         // would fail
    verify(false)         // would fail
    return r
}