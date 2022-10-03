package no.nordicsemi.wifi.provisioner.library.exception

data class ResponseErrorException(val code: ResponseError) : Exception(code.toString())

enum class ResponseError {
    INVALID_ARGUMENT, INVALID_PROTO, INTERNAL_ERROR;

    override fun toString(): String = when (this) {
        INVALID_ARGUMENT -> "Invalid argument"
        INVALID_PROTO -> "Invalid proto"
        INTERNAL_ERROR -> "Internal error"
    }
}