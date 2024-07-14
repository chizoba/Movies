package com.remote.ghibli.dependencies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SuspendWrapper<T : Any>(
    private val context: CoroutineContext = Dispatchers.Main,
    private val block: suspend () -> T
) {
    fun subscribe(
        onSuccess: (item: T) -> Unit,
        onError: (error: Throwable) -> Unit
    ): Closeable {
        val job = Job()
        CoroutineScope(context + job).launch {
            try {
                onSuccess(block())
            } catch (error: Throwable) {
                onError(error)
            }
        }
        return Closeable { job.cancel() }
    }
}