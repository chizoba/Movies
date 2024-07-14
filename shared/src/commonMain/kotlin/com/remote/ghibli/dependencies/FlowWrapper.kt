package com.remote.ghibli.dependencies

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

class FlowWrapper<T>(private val flow: Flow<T>) {
    fun subscribe(
        context: CoroutineContext,
        onEach: (item: T) -> Unit,
        onCompletion: (cause: Throwable?) -> Unit,
    ): Closeable {
        val job = Job()
        var cause: Throwable? = null
        flow
            .onEach { onEach(it) }
            .catch { cause = it }
            .onCompletion { onCompletion(cause) }
            .launchIn(CoroutineScope(context + job))
        return Closeable { job.cancel() }
    }
}

fun <T> Flow<T>.wrap(
    onEach: (item: T) -> Unit,
    onCompletion: (cause: Throwable?) -> Unit,
    context: CoroutineContext = Dispatchers.Main,
) = FlowWrapper(this).subscribe(
    context = context,
    onEach = onEach,
    onCompletion = onCompletion,
)