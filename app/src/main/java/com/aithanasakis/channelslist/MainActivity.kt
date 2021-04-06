package com.aithanasakis.channelslist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.aithanasakis.channelslist.data.models.Channel
import com.aithanasakis.channelslist.ui.ChannelsListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChannelsListTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ChannelScreen(viewModel)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEventsFlow.collect {
                (it as? MainUiEvents.ChannelClicked)?.run { onChannelClick(channel) }
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> viewModel.onOkButtonClicked()
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun onChannelClick(channel: Channel) {
        val viewIntent = Intent("android.intent.action.VIEW", Uri.parse(channel.url))
       // startActivity(viewIntent)
    }


}

@Composable
private fun ChannelRow(
    channel: Channel,
    onChannelClick: (Channel) -> Unit,
    modifier: Modifier,
    onChannelFocused: (Channel) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusedColor = colorResource(id = R.color.purple_200)
    val normalColor = colorResource(id = R.color.teal_700)

    var color by remember { mutableStateOf(normalColor) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .focusRequester(focusRequester)
            .clickable(onClick = {
                onChannelClick.invoke(channel)
                focusRequester.requestFocus()
            })
            .onFocusChanged {
                color = if (it.isFocused) {
                    onChannelFocused.invoke(channel)
                    focusedColor
                } else normalColor
            }
            .background(color = color)
    ) {

        val imageModifier = Modifier
            .focusModifier()
            .size(46.dp)
            .clip(shape = CircleShape)
        val image = ImageVector.vectorResource(id = R.drawable.header)

        Image(
            imageVector = image,
            contentDescription = "",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = channel.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
            Text(text = channel.subtitle, style = MaterialTheme.typography.body2)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun ChannelList(
    mainViewModel: MainViewModel
) {
    val channelsState = mainViewModel.channelsFlow.collectAsState()
    var listLoaded = false
    val coroutineScope = rememberCoroutineScope()
    val lazyRowState = rememberLazyListState()
    val focusReferences = channelsState.value.channels.map {
        FocusRequester()
    }
    LazyRow(state = lazyRowState) {
        itemsIndexed(items = channelsState.value.channels, itemContent = { index, channel ->
            ChannelRow(channel = channel, onChannelClick = {
                mainViewModel.onChannelClick(it)
            }, Modifier
                .focusOrder(focusReferences[index]) {
                    previous =
                        if (index - 1 >= 0) focusReferences[index - 1] else focusReferences[index]
                    left =
                        if (index - 1 >= 0) focusReferences[index - 1] else focusReferences[index]
                    right =
                        if (index + 1 < focusReferences.size) focusReferences[index + 1] else focusReferences[index]
                    next =
                        if (index + 1 < focusReferences.size) focusReferences[index + 1] else focusReferences[index]
                }, {
                mainViewModel.onChannelFocused(it)
                lazyRowState.checkToScrollList(
                    scope = coroutineScope,
                    itemIndex = index,
                    totalItemsCount = channelsState.value.channels.size
                )
            }
            )
            Divider()
            if (!listLoaded) {
                listLoaded = true
                DisposableEffect(null) {
                    focusReferences.first().requestFocus()
                    onDispose { }
                }
            }

        })

    }

}

private fun LazyListState.checkToScrollList(
    scope: CoroutineScope,
    itemIndex: Int,
    totalItemsCount: Int
) {
    val firstVisibleIndex = firstVisibleItemIndex
    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastIndex

    if (lastVisibleItemIndex == itemIndex && totalItemsCount > itemIndex + 1) {
        scope.launch {
            scrollToItem(itemIndex + 1)
        }
    }
    if (firstVisibleIndex == itemIndex && itemIndex != 0) {
        scope.launch {
            scrollToItem(itemIndex - 1)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun ChannelScreen(
    mainViewModel: MainViewModel
) {
    ChannelList(mainViewModel)
}
