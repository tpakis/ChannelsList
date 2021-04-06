package com.aithanasakis.channelslist

import androidx.lifecycle.ViewModel
import com.aithanasakis.channelslist.data.models.Channel
import com.aithanasakis.channelslist.data.models.staticChannelsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class MainUiEvents {
    class ChannelClicked(val channel: Channel) : MainUiEvents()
    object NoEvent : MainUiEvents()
}

data class ChannelsListState(val channels: List<Channel>)

class MainViewModel : ViewModel() {
    private var focusedChannel: Channel? = null
    private val _uiEventsFlow = MutableStateFlow<MainUiEvents>(MainUiEvents.NoEvent)
    val uiEventsFlow: StateFlow<MainUiEvents> = _uiEventsFlow

    private val _channelsFlow =
        MutableStateFlow(ChannelsListState(staticChannelsList))
    val channelsFlow: StateFlow<ChannelsListState> = _channelsFlow

    fun onChannelClick(channel: Channel) {
        _uiEventsFlow.value = MainUiEvents.ChannelClicked(channel)
    }

    fun onChannelFocused(channel: Channel) {
        focusedChannel = channel
    }

    fun onOkButtonClicked() {
        focusedChannel?.let {
            _uiEventsFlow.value = MainUiEvents.ChannelClicked(it)
        }
    }
}