package com.aithanasakis.channelslist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.aithanasakis.channelslist.data.models.Channel
import com.aithanasakis.channelslist.ui.ChannelsListTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


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
        viewModel.onDpadClicked(keyCode)
        return super.onKeyUp(keyCode, event)
    }

    private fun onChannelClick(channel: Channel) {
        val viewIntent = Intent("android.intent.action.VIEW", Uri.parse(channel.url))
        startActivity(viewIntent)
    }

}

@Composable
private fun ChannelRow(channel: Channel, onChannelClick: (Channel) -> Unit, focused: Boolean) {
    val color =
        if (focused) colorResource(id = R.color.purple_200) else colorResource(id = R.color.teal_700)
    Row(
        modifier = Modifier.clickable(onClick = { onChannelClick(channel) }).fillMaxWidth()
            .padding(8.dp).background(color = color)
    ) {

        val imageModifier = Modifier.preferredSize(46.dp).clip(shape = CircleShape)
        val image = vectorResource(id = R.drawable.header)

        Image(
            imageVector = image,
            contentDescription = "",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)) {
            Text(
                text = channel.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
            Text(text = channel.subtitle, style = MaterialTheme.typography.body2)
        }
    }
}

@Composable
private fun ChannelList(
    mainViewModel: MainViewModel
) {
    val channelsState = mainViewModel.channelsFlow.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lazyRowState = rememberLazyListState()
    if (channelsState.value.selectedIndex > 0) {
        coroutineScope.launch {
            lazyRowState.snapToItemIndex(channelsState.value.selectedIndex)
        }
    }
    LazyRow(state = lazyRowState) {
        itemsIndexed(items = channelsState.value.channels, itemContent = { index, channel ->
            ChannelRow(channel = channel, onChannelClick = {
                mainViewModel.onChannelClick(it)
            }, focused = index == channelsState.value.selectedIndex)
            Divider()
        })
    }
}

@Composable
fun ChannelScreen(
    mainViewModel: MainViewModel
) {
    ChannelList(mainViewModel)
}

@Composable
fun WebViewChannel(channel: Channel, modifier: Modifier) {
    AndroidView({ context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            loadUrl(channel.url)
        }
    }, modifier = modifier)
}