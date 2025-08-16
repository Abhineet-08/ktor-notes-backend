package com.example.authentication

import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

// REMOVE these global variables
// private val hashKey = System.getenv("HASH_KEY").toByteArray()
// private val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

// The hash function should now accept the secret key as a parameter
fun hash(password: String, secretKey: String): String {
    val hmacKey = SecretKeySpec(secretKey.toByteArray(), "HmacSHA1")
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}