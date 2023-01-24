package ex3

import arrow.core.memoize

fun main() {
    /**
     * Memoization - technique of caching intermediate values to avoid computations.
     */

    heavyCalculation("42")
    heavyCalculation("42")

    val memoizedHeavyCalculation = ::heavyCalculation.memoize() // works for function with up to five arguments, in parallel execution might be called twice
    memoizedHeavyCalculation("32")
    memoizedHeavyCalculation("32")

    // region:Fibonacci

    println("10th number of fibonacci sequence = " + fibonacci(10))
    println("Call count = $callCount")
    println("10th number of fibonacci sequence = " + fibonacci(15))
    println("Call count = $callCount")

    callCount = 0

    println("10th number of fibonacci sequence = " + fibonacciMemo(10))
    println("Call count = $callCount")
    println("10th number of fibonacci sequence = " + fibonacciMemo(15))
    println("Call count = $callCount") // endregion
}

fun heavyCalculation(value: String): Int {
    println("Called for value $value")
    return value.toInt()
}

// region:Fibonacci
var callCount = 0

fun fibonacci(n: Int): Int {
    callCount++
    return when {
        n < 0 -> 0
        n == 1 -> 1
        else -> fibonacci(n - 1) + fibonacci(n - 2)
    }
}

val memoizedFibonacci = ::fibonacciMemo.memoize()

fun fibonacciMemo(n: Int): Int {
    callCount++
    return when {
        n < 0 -> 0
        n == 1 -> 1
        else -> memoizedFibonacci(n - 1) + memoizedFibonacci(n - 2)
    }
}
// endregion
