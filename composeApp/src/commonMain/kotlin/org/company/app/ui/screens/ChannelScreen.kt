package org.company.app.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.github.aakira.napier.Napier
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.company.app.UserRegion
import org.company.app.data.repository.Repository
import org.company.app.domain.model.channel.Channel
import org.company.app.domain.model.channel.Item
import org.company.app.domain.model.search.Search
import org.company.app.domain.model.videos.Youtube
import org.company.app.domain.usecases.ResultState
import org.company.app.presentation.MainViewModel
import org.company.app.theme.LocalThemeIsDark
import org.company.app.ui.components.ChannelCommunity
import org.company.app.ui.components.ChannelHome
import org.company.app.ui.components.ChannelLiveStream
import org.company.app.ui.components.ChannelPlaylists
import org.company.app.ui.components.ChannelVideos
import org.company.app.ui.components.ErrorBox
import org.company.app.ui.components.FeaturedChannel
import org.company.app.ui.components.SearchVideoItemCard
import org.company.app.ui.components.SearchVideosList
import org.company.app.ui.components.ShimmerEffectChannel
import org.company.app.ui.components.VideoItemCard
import org.company.app.ui.components.VideosList

class ChannelScreen(
    private val channel: Item,
) : Screen {
    @Composable
    override fun Content() {
        val isDark by LocalThemeIsDark.current
        val repository = remember { Repository() }
        val viewModel = remember { MainViewModel(repository) }
        var playlists by remember { mutableStateOf<Youtube?>(null) }
        var multipleVideo by remember { mutableStateOf<Youtube?>(null) }
        var channelSections by remember { mutableStateOf<Youtube?>(null) }
        var channelLiveStream by remember { mutableStateOf<Search?>(null) }
        var channelAllVideos by remember { mutableStateOf<Youtube?>(null) }
        var channelCommunities by remember { mutableStateOf<Youtube?>(null) }
        var ownChannelVideo by remember { mutableStateOf<Search?>(null) }
        var featuresChannels by remember { mutableStateOf<Channel?>(null) }
        var isSearchEnabled by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var data by remember { mutableStateOf<Search?>(null) }
        var errorData by remember { mutableStateOf("") }
        var query by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            viewModel.getPlaylists(channel.id)
            viewModel.getChannelSections(channel.id)
            viewModel.getChannelLiveStreams(channel.id)
            viewModel.getChannelVideos(channel.contentDetails.relatedPlaylists.uploads)
            viewModel.getChannelCommunity(channel.id)
            viewModel.getOwnChannelVideos(channel.id)

            // Assuming channelSections?.items is a list of objects with channel IDs
            val channelIds = channelSections?.items?.map { it.id.toString() }

            if (!channelIds.isNullOrEmpty()) {
                viewModel.getChannelDetails(channelIds.toString())
            }
            channelAllVideos?.items?.forEach { channelVideos ->
                // viewModel.getMultipleVideo(channelVideos.contentDetails)
            }

        }
        //Simple Playlist
        val state by viewModel.playlists.collectAsState()

        //Channel Section Data Like Home Screens Videos,Playlists ID,Featured Channels
        val channelState by viewModel.channelSections.collectAsState()

        //Channel LiveStreams
        val liveStreams by viewModel.channelLiveStream.collectAsState()

        //Channel All Videos
        val allVideos by viewModel.channelVideos.collectAsState()

        //Channel Community
        val channelCommunity by viewModel.channelCommunity.collectAsState()

        //OWN Channel Videos
        val ownChannelVideos by viewModel.ownChannelVideos.collectAsState()

        //Featured Channels
        val channelDetails by viewModel.channelDetails.collectAsState()

        //Multiple Videos
        val multipleVideos by viewModel.multipleVideos.collectAsState()

        when (state) {
            is ResultState.LOADING -> {
                ShimmerEffectChannel()
            }

            is ResultState.SUCCESS -> {
                val response = (state as ResultState.SUCCESS).response
                playlists = response
            }

            is ResultState.ERROR -> {
                val error = (state as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }
        when (multipleVideos) {
            is ResultState.LOADING -> {
                //ShimmerEffectChannel()
            }

            is ResultState.SUCCESS -> {
                val response = (multipleVideos as ResultState.SUCCESS).response
                multipleVideo = response
            }

            is ResultState.ERROR -> {
                val error = (multipleVideos as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }
        //Channel Sections
        when (channelState) {
            is ResultState.LOADING -> {
                // LoadingBox()
            }

            is ResultState.SUCCESS -> {
                val response = (channelState as ResultState.SUCCESS).response
                channelSections = response
            }

            is ResultState.ERROR -> {
                val error = (channelState as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }
        //Channel LiveStreams
        when (liveStreams) {
            is ResultState.LOADING -> {
                //LoadingBox()
            }

            is ResultState.SUCCESS -> {
                val response = (liveStreams as ResultState.SUCCESS).response
                channelLiveStream = response
            }

            is ResultState.ERROR -> {
                val error = (liveStreams as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }

        //Channel All Videos
        when (allVideos) {
            is ResultState.LOADING -> {
                //LoadingBox()
            }

            is ResultState.SUCCESS -> {
                val response = (allVideos as ResultState.SUCCESS).response
                channelAllVideos = response
                Napier.d("$channelAllVideos", tag = "ChannelHome")
            }

            is ResultState.ERROR -> {
                val error = (allVideos as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }

        //Channel Community
        when (channelCommunity) {
            is ResultState.LOADING -> {
                //LoadingBox()
            }

            is ResultState.SUCCESS -> {
                val response = (channelCommunity as ResultState.SUCCESS).response
                channelCommunities = response
            }

            is ResultState.ERROR -> {
                val error = (channelCommunity as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }

        //Own Channel Videos
        when (ownChannelVideos) {
            is ResultState.LOADING -> {
                //LoadingBox()
            }

            is ResultState.SUCCESS -> {
                val response = (ownChannelVideos as ResultState.SUCCESS).response
                ownChannelVideo = response
            }

            is ResultState.ERROR -> {
                val error = (ownChannelVideos as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }
        //Channel Details
        when (channelDetails) {
            is ResultState.LOADING -> {
                // LoadingBox()
            }

            is ResultState.SUCCESS -> {
                val response = (channelDetails as ResultState.SUCCESS).response
                featuresChannels = response
            }

            is ResultState.ERROR -> {
                val error = (channelDetails as ResultState.ERROR).error
                ErrorBox(error = error)
            }
        }
        if (isSearchEnabled) {
           val searchState = viewModel.channelSearch.collectAsState().value
            when (searchState) {
                is ResultState.LOADING -> {
                    //isLoading = true
                }

                is ResultState.SUCCESS -> {
                    isLoading = false
                    val response = (searchState as ResultState.SUCCESS).response
                    data = response
                }

                is ResultState.ERROR -> {
                    isLoading = false
                    error = true
                    val error = (searchState as ResultState.ERROR).error
                    errorData = error
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(top = 49.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        isSearchEnabled = false
                        query = ""
                    }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    }

                    TextField(
                        value = query,
                        onValueChange = {
                            query = it
                        },
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Handle search or done action
                                viewModel.getChannelSearch(channel.id,query)
                                println("Channel Search ${channel.id}")
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        enabled = true,
                        placeholder = {
                            Text("Search YouTube....")
                        }, trailingIcon = {
                            IconButton(onClick = {
                                viewModel.getSearch(query, UserRegion())
                            }) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            }
                        }
                    )
                    IconButton(onClick = {
                    }) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp, color = Color.LightGray
                )
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (error) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        SelectionContainer {
                            Text(errorData)
                        }
                    }
                } else {
                    data?.let {youtube ->
                        LazyVerticalGrid(columns = GridCells.Adaptive(300.dp)) {
                            youtube.items?.let { items ->
                                items(items) { videos ->
                                    SearchVideoItemCard(videos)
                                }
                            }
                        }
                    }
                }

            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val navigator = LocalNavigator.current
                // Custom Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back Icon
                    IconButton(onClick = {
                        navigator?.pop()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Arrow Back",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Title
                    Text(
                        text = channel.snippet.title,
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        color = if (isDark) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Search Icon
                    IconButton(onClick = { isSearchEnabled = !isSearchEnabled }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Arrow",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }

                    // More Vert Icon
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Vert",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }
                }

                // Channel Poster Image
                val poster: Resource<Painter> =
                    asyncPainterResource(channel.brandingSettings.image?.bannerExternalUrl.toString())
                KamelImage(
                    resource = poster,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                        .height(130.dp)
                        .padding(start = 20.dp, end = 20.dp)
                        .clip(shape = RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop,
                    onLoading = {
                        CircularProgressIndicator(it)
                    },
                    onFailure = {
                        Text(text = "Failed to Load Image")
                    },
                    animationSpec = tween(),
                )

                // Channel Details
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Channel Image
                    val image: Resource<Painter> =
                        asyncPainterResource(data = channel.snippet.thumbnails.default?.url.toString())
                    KamelImage(
                        resource = image,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Channel Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = channel.snippet.title.toString(),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = if (isDark) Color.White else Color.Black
                        )
                        val isVerified = channel.status.isLinked
                        if (isVerified) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 4.dp),
                                tint = if (isDark) Color.White else Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel Subscribers and Videos
                    Text(
                        text = "${channel.snippet.customUrl} • ${formatSubscribers(channel.statistics.subscriberCount)} Subscribers • ${
                            formatLikes(
                                channel.statistics.videoCount
                            )
                        } videos",
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                        color = if (isDark) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel Details Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Channel Description
                        channel.snippet.localized.description.let {
                            Text(
                                text = it,
                                color = if (isDark) Color.White else Color.Black,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Arrow Icon
                        IconButton(onClick = {
                            navigator?.push(ChannelDetail(channel = channel))
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = if (isDark) Color.White else Color.Black
                            )
                        }
                    }
                }


                // Channel Links
                Text(
                    text = "facebook.com/grandThumb?ref=book...",
                    color = if (isDark) Color.White else Color.Black
                )

                // Subscribe Button
                TextButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color.Red else Color.Black
                    )
                ) {
                    Text(
                        text = "Subscribe", color = Color.White
                    )
                }
                var selectedTabIndex by remember { mutableStateOf(0) }


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 8.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                                    .height(2.dp).background(MaterialTheme.colorScheme.primary)
                            )
                        },
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        contentColor = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = {
                                selectedTabIndex = 0
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            Text(text = "Home")
                        }
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = {
                                selectedTabIndex = 1
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            Text(text = "Videos")
                        }
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = {
                                selectedTabIndex = 2
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            Text(text = "LiveStreams")
                        }
                        Tab(
                            selected = selectedTabIndex == 3,
                            onClick = {
                                selectedTabIndex = 3
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            Text(text = "Playlists")
                        }
                        Tab(
                            selected = selectedTabIndex == 4,
                            onClick = {
                                selectedTabIndex = 4
                            },
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            Text(text = "Community")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    //Data of Row Tabs
                    Column(
                        modifier = Modifier
                            .height(900.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        when (selectedTabIndex) {
                            0 -> {
                                /*   multipleVideo?.let {
                                   ChannelHome(it, modifier = Modifier.fillMaxWidth(), title = "Home")
                               }*/
                                playlists.let { youtube ->
                                    youtube?.let { it1 ->
                                        ChannelHome(
                                            youtube = it1,
                                            modifier = Modifier.weight(1f),
                                            title = "Home"
                                        )
                                    }
                                }
                                ownChannelVideo?.let { ChannelVideos(it) }
                                featuresChannels?.let { channel ->
                                    FeaturedChannel(
                                        channel,
                                        featuredText = "Sub To All Channels for Cookie"
                                    )
                                }
                            }


                            1 -> {
                                ownChannelVideo?.let { ChannelVideos(it) }
                            }

                            2 -> {
                                channelLiveStream?.let { ChannelLiveStream(it) }
                            }

                            3 -> {
                                playlists?.let { youtube: Youtube? ->
                                    ChannelPlaylists(youtube!!)
                                }
                            }

                            4 -> {
                                channelCommunities?.let { youtube ->
                                    ChannelCommunity(
                                        youtube,
                                        channel.brandingSettings.image?.bannerExternalUrl.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}