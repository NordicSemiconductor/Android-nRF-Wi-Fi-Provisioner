package no.nordicsemi.wifi.provisioner.library.exception

data class ResponseErrorException(val code: ResponseError) : Exception("Received error response.")

enum class ResponseError {
    INVALID_ARGUMENT, INVALID_PROTO, INTERNAL_ERROR
}