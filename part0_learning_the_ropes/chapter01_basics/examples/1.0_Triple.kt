package chapter01_basics.examples

import org.jetbrains.kotlin.formver.plugin.AlwaysVerify
import org.jetbrains.kotlin.formver.plugin.NeverConvert
import org.jetbrains.kotlin.formver.plugin.postconditions
import kotlin.contracts.ExperimentalContracts

@AlwaysVerify
fun triple(x: Int): Int {
    val y = 2 * x
    return x + y
}

@OptIn(ExperimentalContracts::class)
@AlwaysVerify
fun returnGreater13(): Int {
    postconditions<Int> {
        it > 13
    }
    return 16
}

fun test() = null

@NeverConvert
fun main(args: Array<String>) {
    println("Hello World!")
    returnGreater13()
}