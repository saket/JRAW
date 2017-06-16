package net.dean.jraw.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.JrawUtils

/**
 * This class forms a bridge from an [HttpAdapter] implementation to the HTTP library's response class
 */
data class HttpResponse(
    /** HTTP status code (200, 404, etc.) */
    val code: Int,
    /** A function that will read the body of a request. Used to lazy initialize the [body] property */
    private val readBody: () -> String,
    /** The response body's content type (eg. "application/json") */
    val contentType: String,
    /** HTTP request's method ("GET", "POST", etc.) */
    val requestMethod: String,
    /** The URL that the request was targeted at */
    val requestUrl: String
) {
    /** If the status code is 2XX */
    val successful: Boolean = code in 200..299
    /** Lazily initialized response body */
    val body: String by lazy(readBody)
    /** Lazily initialized response body as a Jackson JsonNode */
    val json: JsonNode by lazy { JrawUtils.parseJson(body) }

    /**
     * Uses Jackson to deserialize the body of this response to a given type
     *
     * ```kotlin
     * val foo = response.deserialize<Foo>()
     * // OR
     * val foo: Foo = response.deserialize()
     * ```
     */
    inline fun <reified  T : Any> deserialize() = JrawUtils.jackson.readValue<T>(body)
}
