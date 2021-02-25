package com.aithanasakis.channelslist

import android.view.KeyEvent
import androidx.lifecycle.ViewModel
import com.aithanasakis.channelslist.data.models.Channel
import com.aithanasakis.channelslist.data.models.staticChannelsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class MainUiEvents {
    class ChannelClicked(val channel: Channel) : MainUiEvents()
    object NoEvent : MainUiEvents()
}

data class ChannelsListState(val selectedIndex: Int, val channels: List<Channel>)

class MainViewModel : ViewModel() {
    private var selectedItemIndex = 0
    private val _uiEventsFlow = MutableStateFlow<MainUiEvents>(MainUiEvents.NoEvent)
    val uiEventsFlow: StateFlow<MainUiEvents> = _uiEventsFlow

    private val _channelsFlow =
        MutableStateFlow(ChannelsListState(selectedItemIndex, staticChannelsList))
    val channelsFlow: StateFlow<ChannelsListState> = _channelsFlow


    fun onDpadClicked(keyCode: Int) {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> selectPreviousChannel()
            KeyEvent.KEYCODE_DPAD_RIGHT -> selectNextChannel()
            KeyEvent.KEYCODE_DPAD_CENTER -> onChannelClick(channelsFlow.value.channels[selectedItemIndex])
        }
    }

    fun onChannelClick(channel: Channel) {
        val indexOfChannelClicked = _channelsFlow.value.channels.indexOf(channel)
        _uiEventsFlow.value = MainUiEvents.ChannelClicked(channel)

        if (indexOfChannelClicked != selectedItemIndex) {
            selectedItemIndex = indexOfChannelClicked
            _channelsFlow.value = _channelsFlow.value.copy(selectedIndex = indexOfChannelClicked)
        }
    }

    private fun selectNextChannel() {
        if (selectedItemIndex < _channelsFlow.value.channels.lastIndex) {
            selectedItemIndex += 1
        } else {
            selectedItemIndex = 0
        }
        _channelsFlow.value = _channelsFlow.value.copy(selectedIndex = selectedItemIndex)
    }

    private fun selectPreviousChannel() {
        if (selectedItemIndex > 0) {
            selectedItemIndex -= 1
        } else {
            selectedItemIndex = _channelsFlow.value.channels.lastIndex
        }
        _channelsFlow.value = _channelsFlow.value.copy(selectedIndex = selectedItemIndex)
    }
}