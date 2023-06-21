package validation.bind

import arrow.core.Either
import arrow.core.continuations.either.eager
import arrow.core.left
import arrow.core.right

suspend fun main() {
    val i = 3
    val errorIntEither: Either<MyError, Int> = eager {
        firstValidate(i).bind()
        println("After first validation 🤔")
        secondValidate(i).bind()
        println("After second validation 😰")
        i
    }
    errorIntEither
        .mapLeft(MyError::s)
        .fold(::println) { println("Success: $it") }
}

fun secondValidate(i: Int): Either<MyError, Int> =
    if (i % 4 == 0) {
        i.right()
    } else {
        println("Second validation failed 😰")
        MyError("$i is i % 4 != 0").left()
    }

fun firstValidate(i: Int): Either<MyError, Int> =
    if (i % 2 == 0) {
        i.right()
    } else {
        println("First validation failed 🤔")
        MyError("$i is i % 2 != 0").left()
    }

data class MyError(val s: String)
