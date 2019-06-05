package com.eaglesakura.firearm.channel

import androidx.annotation.CheckResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Channel holder.
 *
 * CAUTION!!
 * This object can't save to Bundle.
 * ChannelRegistry use to  an activity with short time or check to the runtime permission.
 * DON'T USE LONG TIME ACTIVITY.
 */
class ChannelRegistry {
    /**
     * Did register channels.
     *
     */
    private val channels = mutableMapOf<Any, Channel<*>>()

    private val lock = ReentrantLock()

    @Deprecated("this constructor will be deleted.", ReplaceWith("ChannelRegistry()"))
    constructor(owner: LifecycleOwner)

    constructor()

    /**
     * Returns channel num.
     */
    @Suppress("unused")
    val size: Int
        get() = lock.withLock {
            channels.size
        }

    internal fun unregister(key: Any): Boolean {
        return lock.withLock {
            channels.remove(key) != null
        }
    }

    /**
     * Get channel from key.
     * If channel not found then throws exception from function.
     */
    fun <T> get(key: Any): Channel<T> {
        return lock.withLock {
            @Suppress("UNCHECKED_CAST")
            channels[key]!! as Channel<T>
        }
    }

    /**
     * Get channel from key.
     * If channel not found then throws exception from function.
     */
    fun <T> find(key: Any): Channel<T>? {
        return lock.withLock {
            @Suppress("UNCHECKED_CAST")
            channels[key] as? Channel<T>?
        }
    }

    /**
     * Add channel to registry.
     * This function returns new channel for using, You should "close()" it.
     */
    @CheckResult
    fun <T> register(key: Any, channel: Channel<T>): Channel<T> {
        lock.withLock {
            val old = channels[key]
            if (old != null) {
                throw IllegalArgumentException("Registry contains key[$key]")
            }
            channels[key] = channel
            return channel
        }
    }

    companion object {
        /**
         * Get channel registry from Activity.
         */
        fun get(activity: FragmentActivity): ChannelRegistry =
            ChannelRegistryViewModel.get(activity)

        /**
         * Get channel registry from Fragment.
         * ChannelRegistry attach to Activity.
         * All Fragments and an Activity has same instance.
         */
        fun get(fragment: Fragment): ChannelRegistry = ChannelRegistryViewModel.get(fragment)
    }
}
