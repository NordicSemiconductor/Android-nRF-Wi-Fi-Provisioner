package no.nordicsemi.wifi.provisioner.library.internal

data class ResponseErrorException(val code: ResponseError) : Exception("Received error response.")

enum class ResponseError {
    INVALID_ARGUMENT, INVALID_PROTO, INTERNAL_ERROR
}