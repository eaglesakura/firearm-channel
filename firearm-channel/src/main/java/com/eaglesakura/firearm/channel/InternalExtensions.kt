package com.eaglesakura.firearm.channel

import android.os.Looper
import androidx.annotation.UiThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.SelectClause1
import kotlinx.coroutines.selects.SelectClause2

/**
 * Channel close on exit.
 *
 * e.g.)
 * val channel: Channel<Int> = ...
 * channel.use {
 *      val value = receive()
 *      // do something.
 * }    // close on exit.
 *
 * @author @eaglesakura
 * @link https://github.com/eaglesakura/army-knife
 */
internal suspend fun <R, T> Channel<T>.use(block: suspend (channel: ReceiveChannel<T>) -> R): R {
    try {
        return block(this)
    } finally {
        close()
    }
}

/**
 * Call function from UI-Thread in Android Device.
 * If you call this function from the worker thread, then throw Error.
 */
@UiThread
internal fun assertUIThread() {
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
        throw Error("Thread[${Thread.currentThread()}] is not UI")
    }
}

/**
 * Subscribe lifecycle's event.
 */
@Suppress("unused")
internal fun Lifecycle.subscribe(receiver: (event: Lifecycle.Event) -> Unit) {
    this.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        fun onAny(@Suppress("UNUSED_PARAMETER") source: LifecycleOwner, event: Lifecycle.Event) {
            receiver(event)
        }
    })
}

/**
 * Delegate supported channel.
 *
 * If you want to the simple use case, then replace to "RendezvousChannel<T>".
 *
 * @author @eaglesakura
 * @link https://github.com/eaglesakura/army-knife
 */
abstract class DelegateChannel<T>(
    @Suppress("MemberVisibilityCanBePrivate")
    protected val origin: Channel<T>
) :
    Channel<T> {
    override val isClosedForReceive: Boolean
        get() = origin.isClosedForReceive

    override val isClosedForSend: Boolean
        get() = origin.isClosedForSend

    override val isEmpty: Boolean
        get() = origin.isEmpty

    override val isFull: Boolean
        get() = false

    override val onReceive: SelectClause1<T>
        get() = origin.onReceive

    override val onReceiveOrNull: SelectClause1<T?>
        get() = origin.onReceiveOrNull

    override val onSend: SelectClause2<T, SendChannel<T>>
        get() = origin.onSend

    override fun cancel() = origin.cancel()

    override fun cancel(cause: CancellationException?) = origin.cancel(cause)

    override fun cancel(cause: Throwable?): Boolean {
        cancel(cause as? CancellationException)
        return true
    }

    override fun close(cause: Throwable?): Boolean = origin.close(cause)

    override fun invokeOnClose(handler: (cause: Throwable?) -> Unit) = origin.invokeOnClose(handler)

    override fun iterator(): ChannelIterator<T> = origin.iterator()

    override fun offer(element: T): Boolean = origin.offer(element)

    override fun poll(): T? = origin.poll()

    override suspend fun receive(): T = origin.receive()

    override suspend fun receiveOrNull(): T? = origin.receiveOrNull()

    override suspend fun send(element: T) = origin.send(element)
}