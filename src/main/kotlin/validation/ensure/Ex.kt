package validation.ensure

import arrow.core.Either
import arrow.core.continuations.either

suspend fun main() {
    val i = 3
    val errorIntEither: Either<MyError, Int> = either {
        ensure(i % 2 == 0) { println("First validation failed 🤔"); MyError("$i is i % 2 != 0") }
        ensure(i % 4 == 0) { println("Second validation failed 😰"); MyError("$i is i % 4 != 0") }
        i
    }
    println(errorIntEither)
}

data class MyError(val s: String)
