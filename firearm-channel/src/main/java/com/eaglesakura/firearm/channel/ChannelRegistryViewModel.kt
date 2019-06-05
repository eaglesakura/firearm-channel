package com.eaglesakura.firearm.channel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

/**
 * Channel registry for Activity(with lifecycle)
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class ChannelRegistryViewModel : ViewModel() {
    val registry: ChannelRegistry = ChannelRegistry()

    companion object {
        fun get(activity: FragmentActivity) =
            ViewModelProviders.of(activity).get(ChannelRegistryViewModel::class.java).registry

        fun get(fragment: Fragment) =
            ViewModelProviders.of(fragment).get(ChannelRegistryViewModel::class.java).registry
    }
}