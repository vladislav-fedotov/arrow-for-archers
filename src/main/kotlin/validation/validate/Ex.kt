package validation.validate

import arrow.core.Invalid
import arrow.core.Validated
import arrow.core.continuations.either.eager
import arrow.core.invalid
import arrow.core.valid

suspend fun main() {
    val i = 3
    val errorIntValidate: Validated<MyError, Int> = eager {
        firstValidate(i).bind()
        println("After first validation 🤔")
        // short circuit computation if first validation fails
        secondValidate(i).bind()
        println("After second validation 😰")
        i
    }.toValidated()

    errorIntValidate
        .mapLeft(MyError::s)
        .fold(
            { println(it) },  // invalid
            { println("Success: $it") } // valid
        )
}

fun secondValidate(i: Int): Validated<MyError, Int> =
    if (i % 4 == 0) {
        i.valid()
    } else {
        println("Second validation failed 😰")
        Invalid(MyError("$i is i % 4 != 0"))
    }

fun firstValidate(i: Int): Validated<MyError, Int> =
    if (i % 2 == 0) {
        i.valid()
    } else {
        println("First validation failed 🤔")
        MyError("$i is i % 2 != 0").invalid()
    }

data class MyError(val s: String)
