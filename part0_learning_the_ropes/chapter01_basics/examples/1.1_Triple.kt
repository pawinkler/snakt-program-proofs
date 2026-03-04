package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.NeverConvert
import org.jetbrains.kotlin.formver.plugin.verify

@AlwaysVerify
fun triple1(x: Int): Int {
    val y = 2 * x
    val r = x + y
//    verify(r == 3 * x)
    verify(r == 3 * x + 1)  // should fail
    return r
}
