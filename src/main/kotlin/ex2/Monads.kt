package ex2

import arrow.core.Either
import arrow.core.Either.Companion.catch
import arrow.core.continuations.either
import arrow.core.continuations.ensureNotNull
import ex2.ConfigFailure.InvalidPortFailure
import ex2.ConfigFailure.PortNotAvailableFailure
import ex2.ConfigFailure.PortOutOfRangeFailure
import ex2.ConfigFailure.SystemFailure

/**
 * What ex2.Monad types where exist in Java 8?
 *
 *
 * ```haskell
 * class ex2.Monad m where
 *   (>>=)  :: m a -> (  a -> m b) -> m b
 *   (>>)   :: m a ->  m b         -> m b
 *   return :: a                   -> m a
 * ```
 *
 * All told, a monad in X is just a monoid in the category of endofunctors of X,
 * with product × replaced by composition of endofunctors and unit set by the identity endofunctor.
 *
 */


/**
 * ex2.Monad is class that has three functions/operators:
 * 1. to wrap some value (return/pure)
 * 2. to invoke some function on monad ('bind' or '>>=')
 * 3. to sequence monadic computations without caring about the intermediate results ('then' or '>>')
 *
 * ex2.Monad laws:
 * 1. Left identity: pure(v).bind() === f.invoke(v)
 * 2. Right identity: m.bind(m::pure) === m
 * 3. Associativity: m.bind(f).bind(g) === m.bind((v) -> f.invoke(v).bind(g))
 */

interface Monad<V> {
    fun pure(v: V): Monad<V> // should be a "static" function
    fun <R> bind(f: (V) -> Monad<R>): Monad<R>
    fun get(): V
}

/**
 * Which methods from Java Optional class implement pure and bind?
 */

/**
 * Either is not a Monad, but it's a monadic structure that has similar behavior like Monads, such as the ability
 * to chain computations and handle errors
 *
 * Either
 * sealed class Either<out A, out B>
 *     data class Left<out A> constructor(val value: A) : Either<A, Nothing>() {
 *     data class Right<out B> constructor(val value: B) : Either<Nothing, B>() {
 *
 * Left - computation failed
 * Right - computation succeed
 */

/**
 * Let's re-map exceptions to Failures
 * - NullPointerException, SecurityException
 * - IllegalStateException
 * - NumberFormatException
 */

sealed interface ConfigFailure {
    object PortNotAvailableFailure : ConfigFailure
    data class InvalidPortFailure(val port: String) : ConfigFailure
    data class PortOutOfRangeFailure(val port: String) : ConfigFailure
    data class SystemFailure(val cause: Throwable) : ConfigFailure
}

data class Config(val port: Int)

fun main() {
    config()
        .tap(::println)
        .map {  }
        .tapLeft(::println)
        .mapLeft {  }
}

fun envOrThrow(name: String): String = System.getenv(name)

/**
 * bind() here unwraps Either<L,R>
 */

fun config(): Either<ConfigFailure, Config> =
    either.eager {
        val portOrNull: String = catch { envOrThrow("port") }
            .mapLeft { it: Throwable -> SystemFailure(it) }
            .map { it: String -> it }
            .bind()
        ensureNotNull(portOrNull) { PortNotAvailableFailure }
        val port = ensureNotNull(portOrNull.toIntOrNull()) { InvalidPortFailure(portOrNull) }
        ensure(port in 0..65536) { PortOutOfRangeFailure(portOrNull) }
        Config(port)
    }