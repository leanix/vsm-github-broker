package net.leanix.vsm.githubbroker.shared.extensions

fun <T, U> Result<T>.flatMap(function: (T) -> Result<U>): Result<U> {
    return if (isSuccess) {
        function(getOrNull()!!)
    } else {
        Result.failure(this.exceptionOrNull()!!)
    }
}
