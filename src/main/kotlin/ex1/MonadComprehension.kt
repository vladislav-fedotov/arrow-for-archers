package ex1

import arrow.core.continuations.ensureNotNull
import arrow.core.continuations.nullable
import arrow.core.continuations.result
import java.lang.IllegalStateException

/**
 * The purpose of monad comprehensions is to compose sequential chains of actions in a style that feels natural for
 * programmers of all backgrounds.
 */

fun main() {
    println("ex1.configOrNullJavaIdiomatic: ${ex1.configOrNullJavaIdiomatic()}")
    println("ex1.configOrNullKotlinIdiomatic: ${ex1.configOrNullKotlinIdiomatic()}")
    println("ex1.configOrNullArrowIdiomatic: ${ex1.configOrNullArrowIdiomatic()}")
    println("ex1.configOrArrowIdiomaticResult: ${ex1.configOrArrowIdiomaticResult()}")
}

data class Config(val port: Int)

fun envOrNull(name: String): String? =
    runCatching { System.getenv(name) }.getOrNull() // here we swallow all exceptions into null

fun configOrNullJavaIdiomatic(): Config? {
    val envOrNull = envOrNull("port")
    return if (envOrNull != null) {
        val portOrNull = envOrNull.toIntOrNull()
        if (portOrNull != null) Config(portOrNull) else null
    } else null
}

// Note: this syntax works only for nullable type and not for Result or Either
fun configOrNullKotlinIdiomatic(): Config? =
    envOrNull("port")?.toIntOrNull()?.let(::Config)

/**
 * bind() DSL function unwraps Int? to Int or Result<T> to T
 */

fun configOrNullArrowIdiomatic(): Config? =
    nullable.eager {
        val env: String = envOrNull("port").bind().also { println("ex1.ex2.envOrNull(\"port\").bind() - executed") }
        val port: Int = env.toIntOrNull().bind().also { println("env.toIntOrNull().bind() - executed") }
        Config(port)
    }

fun envOrResult(name: String): Result<String?> =
    runCatching { System.getenv(name) } // wrap nullable String to Result<String?>

fun configOrArrowIdiomaticResult(): Result<Config> =
    result.eager { // To unwrap Result<T> we're using corresponding object - result
        val envOrNull: String? = envOrResult("port").bind()//.also { println("envOrNullAndReturnResult(\"port\").bind() - executed") }
        ensureNotNull(envOrNull) { IllegalStateException("Required port value was null") } // Note: there is no way to wrap nullable with Result
//        if (envOrNull == null) throw IllegalStateException("Required port value was null")
        val port = runCatching { envOrNull.toInt() }.bind()//.also { println("envResult.toInt() - executed") } // Wrap possible exception to Result
        Config(port)
    }

// Possible un-wrappers:
// - nullable.eager {...}
// - result.eager {...}
// - either.eager {...}
// - option.eager {...}
// - ior.eager {...}
