package com.example.jetpacktutorial.core.common

// ── Resource wrapper ─────────────────────────────────────────────────────────

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T)         : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

// ── One-shot event base ───────────────────────────────────────────────────────

abstract class Event
