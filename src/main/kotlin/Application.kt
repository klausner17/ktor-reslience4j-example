package com.klausner.examples.ktor.resilience4j

import com.fasterxml.jackson.databind.DeserializationFeature
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.kotlin.circuitbreaker.CircuitBreakerConfig
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.vavr.control.Try
import java.time.Duration

fun main() {
    val port = System.getenv("SERVER_PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            jackson {
                this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }

        routing {
            get("/greeting") {
                val supplier = circuitBreaker.decorateSupplier { getMessage() }

                val message = Try.ofSupplier(supplier)
                        .recover { getCircuitOpenMessage() }.get()

                call.respond(mapOf("greeting" to message))
            }
            get("/switch") {
                produceError = produceError.not()
                call.respond(mapOf("isProducingError" to produceError))
            }
        }
    }.start(wait = true)
}

var produceError: Boolean = false

val circuitBreakerConfig = CircuitBreakerConfig {
    failureRateThreshold(100f)
    minimumNumberOfCalls(10)
    slidingWindowSize(10)
    waitDurationInOpenState(Duration.ofSeconds(10))
}

val circuitBreakerRegistry: CircuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig)

val circuitBreaker: CircuitBreaker = circuitBreakerRegistry.circuitBreaker("greetingCircuit")

fun getMessage() = if (!produceError) "hello world" else throw Exception()

fun getCircuitOpenMessage() = "h3110 w0r1d"