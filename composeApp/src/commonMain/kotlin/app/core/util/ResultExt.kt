package app.core.util

inline fun <T, R> Result<T>.mapSuccess(transform: (T) -> R): Result<R> =
    fold(
        onSuccess = { Result.success(transform(it)) },
        onFailure = { Result.failure(it) },
    )
