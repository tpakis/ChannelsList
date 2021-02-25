package com.aithanasakis.channelslist.data.models

data class Channel(val title: String, val subtitle: String, val photo: String, val id: Int, val url: String)

val staticChannelsList = listOf(
    Channel(
        "ANT1",
        "ANT1 live",
        "https://upload.wikimedia.org/wikipedia/en/3/3a/ANT1_Greek_channel_logo.svg",
        1,
        "https://www.antenna.gr/Live"
    ),
    Channel(
        "Star",
        "Star live",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Star_Channel_Greece_logo.svg/2880px-Star_Channel_Greece_logo.svg.png2",
        2,
        "https://www.star.gr/tv/live-stream/"
    ),
    Channel(
        "Skai",
        "Skai live",
        "https://upload.wikimedia.org/wikipedia/en/9/9c/Skai-newlogo-plain-blue.png",
        3,
        "https://www.skaitv.gr/live"
    ),
    Channel(
        "Alpha",
        "Alpha live",
        "https://upload.wikimedia.org/wikipedia/en/thumb/e/eb/Alpha_TV_logo.svg/1920px-Alpha_TV_logo.svg.png",
        4,
        "https://www.alphatv.gr/live/"
    ),
    Channel(
        "Ertflix",
        "Ertflix live",
        "https://upload.wikimedia.org/wikipedia/en/thumb/e/eb/Alpha_TV_logo.svg/1920px-Alpha_TV_logo.svg.png",
        5,
        "https://www.ertflix.gr/"
    ),
    Channel(
        "Melisses",
        "Melisses",
        "https://upload.wikimedia.org/wikipedia/en/3/3a/ANT1_Greek_channel_logo.svg",
        6,
        "https://www.antenna.gr/agriesmelisses"
    ),
    Channel(
        "Mourmoura",
        "Min Arxizeis tin mourmoura",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Star_Channel_Greece_logo.svg/2880px-Star_Channel_Greece_logo.svg.png2",
        7,
        "https://www.alphatv.gr/show/min-arxizeis-ti-moyrmoyra/epeisodia_min-arhizeis-ti-mourmoura/"
    ),
)