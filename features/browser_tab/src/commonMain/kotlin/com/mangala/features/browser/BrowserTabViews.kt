package com.mangala.features.browser

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.features.browser.entities.DApp
import com.mangala.features.browser.entities.DAppCategory
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.ui.TextDescription2
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource

/*-----------------SEARCH BAR-----------*/
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: (msg: String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(CornerRadius.Small))
            .clickable {
                onClick("")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "",
            modifier = Modifier.padding(start = 16.dp).size(20.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.TINY))
        TextDescription2(
            "Search or type URL", modifier = Modifier
                .weight(1f)
                .padding( vertical = 12.dp),

        )
    }
}

/*-----------------DApp Item-----------*/

@Composable
fun ViewDAppItem(

) {
    val dapp = DApp(
        "",
        "Avalanche",
        "AVAX",
        "",
        DAppCategory.CROSS_CHAIN
    )
    DAppItem(dapp = dapp)
}
@Composable
fun DAppItem(
    dapp: DApp,
    modifier: Modifier = Modifier
) {
    val showGuide = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = MangalaWalletPack.Wallet,
                contentDescription = "",
                modifier = Modifier.padding(start = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f) // Takes up remaining horizontal space
            ) {
                Text(
                    text = dapp.title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                Text(
                    text = dapp.category.display,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                )
            }

            Button(
                onClick = { showGuide.value = true },
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "open",
                    style = MaterialTheme.typography.button,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DAppCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        backgroundColor = Color.White,
        onClick = {}
    ) {

        Column() {
            Spacer(Modifier.height(16.dp))
            Text(
                text = subtitle.uppercase(),
                style = MaterialTheme.typography.h6,
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DAppLimitEventCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        backgroundColor = Color.White,
        onClick = {}
    ) {
        Box {
            Icon(
                imageVector = MangalaWalletPack.Wallet,
                contentDescription = "",
                modifier = Modifier.padding(start = 16.dp)
            )
            Surface(
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Limited-time event".uppercase(),
                        style = MaterialTheme.typography.h6,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = description,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    content()
                }
            }
        }
    }
}

/*-----------------RECENT-----------*/
@Composable
fun RecentItem(
    drawable: ImageResource,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun RecentHorizontal(
    modifier: Modifier = Modifier
) {
    val etheriumLogo =listOf(
        {"com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24"})
    val dappLogoList = List(100) { listOf(etheriumLogo) }.flatten()//TODO: test

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
//        items(dappLogoList) { item ->
//            RecentItem( ImageSource,"")
        }
    }


@Composable
fun Section(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        content()
    }
}

/*-----------------BOOKMARKS-----------*/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarkItem(
    text: String,
    modifier: Modifier = Modifier,
    onBookmarkItem: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        backgroundColor = Color.White,
        onClick = {
            onBookmarkItem()
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
//            Image(
//                painter = painterResource(drawable),
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.size(50.dp)
//            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "",
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun BookmarksGrid(
    modifier: Modifier = Modifier,
    onBookmarkItem: () -> Unit
) {
    val etheriumLogo =listOf(
        {"com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24"})
    val dappLogoList = List(100) { listOf(etheriumLogo) }.flatten()//TODO: test
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(dappLogoList) { item ->
            BookmarkItem(
                text = "text",
                onBookmarkItem = {
                    onBookmarkItem()
                }
            )
        }
    }
}

@Composable
fun BookmarksSection(modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 0.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.h5
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 4.dp, end = 4.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                ) {

                    Icon(
                        imageVector = MangalaWalletPack.Wallet,
                        modifier = Modifier
                            .size(22.dp)
                            .padding(start = 6.dp),
                        contentDescription = ""
                    )
                    Text(
                        text = "",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            end = 6.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                    )

                }
            }
//            BookmarksGrid()
        }
    }
}

//@Preview(showBackground = true, heightDp = 1000)
@Composable
fun BrowserDappScreen(modifier: Modifier = Modifier, onBookmarkItem: () -> Unit) {
    Column(
        modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
//        SearchBar(Modifier.padding(horizontal = 16.dp),)
        Section(title = "recent") {
            RecentHorizontal()
        }
//        Spacer(modifier = Modifier.height(16.dp))
        BookmarksGrid(onBookmarkItem = {
            onBookmarkItem()
        })
//        BookmarksSection()
    }
}

@Composable
fun BrowserTodayScreen() {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        DAppCard(title = "Top dapps this week", subtitle = "Our Favorites") {
            Column(
                Modifier.padding(bottom = 32.dp, top = 16.dp)
            ) {
                val avalanche = DApp(
                    "",
                    "Avalanche",
                    "AVAX",
                    "",
                    DAppCategory.CROSS_CHAIN
                )
                DAppItem(dapp = avalanche)
                Spacer(Modifier.height(16.dp))
                DAppItem(dapp = avalanche)
                Spacer(Modifier.height(16.dp))
                DAppItem(dapp = avalanche)
            }
        }

        Spacer(Modifier.height(10.dp))
        DAppCard(title = "Popular Defi in the world", subtitle = "Let's Stake") {
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Column() {
                    val etheriumLogo =listOf(
                        {"com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24"})
                    val dappLogoList = List(100) { listOf(etheriumLogo) }.flatten()
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .height(160.dp)
                            .padding(start = 10.dp, end = 10.dp)
                    ) {
                        items(dappLogoList) { item ->
                            Icon(
                                imageVector = MangalaWalletPack.Wallet,
                                modifier = Modifier
                                    .size(22.dp)
                                    .padding(start = 6.dp),
                                contentDescription = ""
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        DAppLimitEventCard(
            title = "Get ready for back-to-school fun",
            description = "Pick up brand-new goodies and character customisation"
        ) {
            val avalanche = DApp(
                "",
                "Avalanche",
                "AVAX",
                "",
                DAppCategory.CROSS_CHAIN
            )
            DAppItem(dapp = avalanche)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun BrowserETHScreen() {
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        DAppCard(title = "Top apps this week", subtitle = "Our Favorites") {
            Column() {
                val etherium = DApp(
                    "",
                    "Eherium",
                    "ETH",
                    "",
                    DAppCategory.FINANCE
                )
                DAppItem(dapp = etherium)
                DAppItem(dapp = etherium)
                DAppItem(dapp = etherium)
            }
        }

        Spacer(Modifier.height(10.dp))
        DAppCard(title = "Popular Games in Vietnam", subtitle = "Let's Play") {
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Column() {
                    val etheriumLogo =listOf(
                        {"com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24"})
                    val dappLogoList = List(100) { listOf(etheriumLogo) }.flatten()
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .height(160.dp)
                            .padding(start = 10.dp, end = 10.dp)
                    ) {
                        items(dappLogoList) { item ->
                            Icon(
                                imageVector = MangalaWalletPack.Wallet,
                                modifier = Modifier
                                    .size(22.dp)
                                    .padding(start = 6.dp),
                                contentDescription = ""
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        DAppLimitEventCard(
            title = "Get ready for back-to-school fun",
            description = "Pick up brand-new goodies and character customisation"
        ) {
            val etherium = DApp(
                "",
                "Etherium",
                "ETH",
                "",
                DAppCategory.FINANCE
            )
            DAppItem(dapp = etherium)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

//@Composable
//fun DAppItem(
//    dapp: DApp,
//    modifier: Modifier = Modifier
//) {
//    val showGuide = remember {
//        mutableStateOf(false)
//    }
//    Column {
//        ConstraintLayout(
//            modifier = Modifier.fillMaxWidth(),
//        ) {
//            val (image, title, category, button) = createRefs()
//            Image(
//                painter = painterResource(dapp.drawable),
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(48.dp)
//                    .constrainAs(image) {
//                        start.linkTo(parent.start, 16.dp)
//                        top.linkTo(parent.top)
//                    }
//            )
//
//            Text(
//                text = dapp.title,
//                style = MaterialTheme.typography.h6,
//                modifier = Modifier.constrainAs(title) {
//                    start.linkTo(image.end, 16.dp)
//                    end.linkTo(button.start, 16.dp)
//                    top.linkTo(parent.top)
//                    width = Dimension.fillToConstraints
//                }
//            )
//
//            Text(
//                text = dapp.category.display,
//                style = MaterialTheme.typography.caption,
//                textAlign = TextAlign.Start,
//                fontSize = 14.sp,
//                modifier = Modifier
//                    .constrainAs(category) {
//                        start.linkTo(image.end, 16.dp)
//                        end.linkTo(button.start, 16.dp)
//                        top.linkTo(title.bottom, 2.dp)
//                        width = Dimension.fillToConstraints
//                    }
//            )
//            Button(
//                onClick = { showGuide.value = true },
//                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_200)),
//                modifier = Modifier
//                    .height(32.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .constrainAs(button) {
//                        end.linkTo(parent.end, 16.dp)
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                    }
//            ) {
//                Text(
//                    text = stringResource(id = com.schoolonair.wallet.component.resources.R.string.open).uppercase(),
//                    style = MaterialTheme.typography.button,
//                    color = Color.White,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//    }
////    if (showGuide.value) showAsBottomSheet(true) {
////        GuildConnectDApps()
////    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun DAppCard(
//    title: String,
//    subtitle: String,
//    content: @Composable () -> Unit
//) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = 0.dp,
//        backgroundColor = Color.White,
//        onClick = {
//            Timber.d("dapp BookmarkItem")
//        }
//    ) {
//
//        Column() {
//            Spacer(Modifier.height(16.dp))
//            Text(
//                text = subtitle.uppercase(),
//                style = MaterialTheme.typography.h6,
//                color = Color.Gray,
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(start = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = title,
//                style = MaterialTheme.typography.h5,
//                color = Color.Black,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(start = 16.dp)
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            content()
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun DAppLimitEventCard(
//    backgroundImage: Int,
//    title: String,
//    description: String,
//    content: @Composable () -> Unit
//) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = 0.dp,
//        backgroundColor = Color.White,
//        onClick = {
//            Timber.d("dapp BookmarkItem")
//        }
//    ) {
//        Box {
//            Image(
//                painter = painterResource(id = backgroundImage),
//                contentDescription = "",
//                contentScale = ContentScale.FillBounds,
//            )
//            Surface(color = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomStart)) {
//                Column {
//                    Spacer(Modifier.height(10.dp))
//                    Text(
//                        text = "Limited-time event".uppercase(),
//                        style = MaterialTheme.typography.h6,
//                        color = Color.Black,
//                        fontSize = 10.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(start = 16.dp)
//                    )
//
//                    Spacer(modifier = Modifier.height(5.dp))
//
//                    Text(
//                        text = title,
//                        style = MaterialTheme.typography.h6,
//                        color = Color.Black,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
//                    )
//
//                    Spacer(modifier = Modifier.height(5.dp))
//
//                    Text(
//                        text = description,
//                        style = MaterialTheme.typography.h6,
//                        color = Color.Black,
//                        fontSize = 10.sp,
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
//                    )
//                    Spacer(modifier = Modifier.height(20.dp))
//                    content()
//                }
//            }
//        }
//    }
//}
//
///*-----------------RECENT-----------*/
//@Composable
//fun RecentItem(
//    drawable: ImageResource,
//    text: StringResource,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//    ) {
//        Image(
//            painter = painterResource(drawable),
//            contentDescription = "",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(40.dp)
//                .clip(CircleShape)
//        )
//        Text(
//            text = stringResource(text),
//            style = MaterialTheme.typography.body1,
//            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp)
//        )
//    }
//}
//
//@Composable
//fun RecentHorizontal(
//    modifier: Modifier = Modifier
//) {
//    LazyRow(
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
//        modifier = modifier
//    ) {
//        items(recentData) { item ->
//            RecentItem(item.drawable, item.text)
//        }
//    }
//}
//
//@Composable
//fun Section(
//    title: StringResource,
//    modifier: Modifier = Modifier,
//    content: @Composable () -> Unit
//) {
//    Column(modifier) {
//        Text(
//            text = stringResource(title).uppercase(),
//            style = MaterialTheme.typography.h6,
//            modifier = Modifier
//                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
//        )
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(8.dp)
//        )
//        content()
//    }
//}
//
///*-----------------BOOKMARKS-----------*/
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun BookmarkItem(
//    @DrawableRes drawable: Int,
//    text: StringResource,
//    modifier: Modifier = Modifier,
//    onBookmarkItem: () -> Unit
//) {
//    Card(
//        shape = RoundedCornerShape(12.dp),
//        elevation = 0.dp,
//        backgroundColor = Color.White,
//        onClick = {
//            onBookmarkItem()
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            Image(
//                painter = painterResource(drawable),
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.size(50.dp)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = stringResource(text),
//                style = MaterialTheme.typography.h6
//            )
//            Text(
//                text = "Earn interest, deposit and borrow assets.",
//                style = MaterialTheme.typography.body2
//            )
//        }
//    }
//}
//
//@Composable
//fun BookmarksGrid(
//    modifier: Modifier = Modifier,
//    onBookmarkItem: () -> Unit
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(bookmarksData) { item ->
//            BookmarkItem(
//                drawable = item.drawable,
//                text = item.text,
//                onBookmarkItem = {
//                    onBookmarkItem()
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun BookmarksSection(modifier: Modifier = Modifier) {
//    Card(
//        modifier = Modifier
//            .padding(16.dp),
//        shape = RoundedCornerShape(12.dp),
//        elevation = 0.dp,
//        backgroundColor = Color.White
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(8.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = stringResource(com.schoolonair.wallet.component.resources.R.string.bookmarksSectionTitle),
//                    style = MaterialTheme.typography.h5
//                )
//                Row(
//                    horizontalArrangement = Arrangement.End,
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .padding(start = 4.dp, end = 4.dp)
//                        .background(Color.Gray, shape = RoundedCornerShape(8.dp))
//                ) {
//
//                    Icon(
//                        painter = painterResource(com.schoolonair.wallet.component.resources.R.drawable.ic_edit_20),
//                        modifier = Modifier
//                            .size(22.dp)
//                            .padding(start = 6.dp),
//                        contentDescription = ""
//                    )
//                    Text(
//                        text = stringResource(com.schoolonair.wallet.component.resources.R.string.edit),
//                        style = MaterialTheme.typography.body1,
//                        modifier = Modifier.padding(
//                            start = 4.dp,
//                            end = 6.dp,
//                            top = 4.dp,
//                            bottom = 4.dp
//                        )
//                    )
//
//                }
//            }
////            BookmarksGrid()
//        }
//    }
//}
//
////@Preview(showBackground = true, heightDp = 1000)
//@Composable
//fun BrowserDappScreen(modifier: Modifier = Modifier, onBookmarkItem: () -> Unit) {
//    Column(
//        modifier
//            .padding(vertical = 16.dp, horizontal = 16.dp)
//    ) {
////        SearchBar(Modifier.padding(horizontal = 16.dp))
////        Section(title = com.alphawallet.app.R.string.recent) {
////            RecentHorizontal()
////        }
////        Spacer(modifier = Modifier.height(16.dp))
//        BookmarksGrid(onBookmarkItem = {
//            onBookmarkItem()
//        })
////        BookmarksSection()
//    }
//}
//
//@Composable
//fun BrowserTodayScreen() {
//    val scrollState = rememberScrollState()
//
//    Column(
//        Modifier
//            .fillMaxSize()
//            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
//            .verticalScroll(scrollState)
//    ) {
//        Spacer(modifier = Modifier.height(10.dp))
//        DAppCard(title = "Top dapps this week", subtitle = "Our Favorites") {
//            Column(
//                Modifier.padding(bottom = 32.dp, top = 16.dp)
//            ) {
//                val avalanche = DApp(
//                    com.schoolonair.wallet.component.resources.R.drawable.logo_avalanche_24,
//                    "",
//                    "Avalanche",
//                    "AVAX",
//                    "",
//                    DAppCategory.CROSS_CHAIN
//                )
//                DAppItem(dapp = avalanche)
//                Spacer(Modifier.height(16.dp))
//                DAppItem(dapp = avalanche)
//                Spacer(Modifier.height(16.dp))
//                DAppItem(dapp = avalanche)
//            }
//        }
//
//        Spacer(Modifier.height(10.dp))
//        DAppCard(title = "Popular Defi in the world", subtitle = "Let's Stake") {
//            Row(
//                Modifier
//                    .fillMaxWidth()
//            ) {
//                Column() {
//                    val avalancheLogo =
//                        com.schoolonair.wallet.component.resources.R.drawable.logo_avalanche_24
//                    val dappLogoList = List(100) { listOf(avalancheLogo) }.flatten()
//                    LazyHorizontalGrid(
//                        rows = GridCells.Fixed(3),
//                        verticalArrangement = Arrangement.spacedBy(5.dp),
//                        horizontalArrangement = Arrangement.spacedBy(5.dp),
//                        modifier = Modifier
//                            .height(160.dp)
//                            .padding(start = 10.dp, end = 10.dp)
//                    ) {
//                        items(dappLogoList) { item ->
//                            Image(
//                                painter = painterResource(id = item),
//                                contentDescription = "",
//                                contentScale = ContentScale.Fit,
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .clickable {
//                                        /* Do smth */
//                                    }
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(15.dp))
//                }
//            }
//        }
//
//        Spacer(Modifier.height(10.dp))
//        DAppLimitEventCard(
//            backgroundImage = com.schoolonair.wallet.component.resources.R.drawable.onboarding_background_large,
//            title = "Get ready for back-to-school fun",
//            description = "Pick up brand-new goodies and character customisation"
//        ) {
//            val avalanche = DApp(
//                com.schoolonair.wallet.component.resources.R.drawable.logo_avalanche_24,
//                "",
//                "Avalanche",
//                "AVAX",
//                "",
//                DAppCategory.CROSS_CHAIN
//            )
//            DAppItem(dapp = avalanche)
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//    }
//}
//
//@Composable
//fun BrowserETHScreen() {
//    val scrollState = rememberScrollState()
//
//    Column(
//        Modifier
//            .fillMaxSize()
//            .verticalScroll(scrollState)
//    ) {
//        Spacer(modifier = Modifier.height(10.dp))
//        DAppCard(title = "Top apps this week", subtitle = "Our Favorites") {
//            Column() {
//                val etherium = DApp(
//                    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24,
//                    "",
//                    "Eherium",
//                    "ETH",
//                    "",
//                    DAppCategory.FINANCE
//                )
//                DAppItem(dapp = etherium)
//                DAppItem(dapp = etherium)
//                DAppItem(dapp = etherium)
//            }
//        }
//
//        Spacer(Modifier.height(10.dp))
//        DAppCard(title = "Popular Games in Vietnam", subtitle = "Let's Play") {
//            Row(
//                Modifier
//                    .fillMaxWidth()
//            ) {
//                Column() {
//                    val etheriumLogo =
//                        com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24
//                    val dappLogoList = List(100) { listOf(etheriumLogo) }.flatten()
//                    LazyHorizontalGrid(
//                        rows = GridCells.Fixed(3),
//                        verticalArrangement = Arrangement.spacedBy(5.dp),
//                        horizontalArrangement = Arrangement.spacedBy(5.dp),
//                        modifier = Modifier
//                            .height(160.dp)
//                            .padding(start = 10.dp, end = 10.dp)
//                    ) {
//                        items(dappLogoList) { item ->
//                            Image(
//                                painter = painterResource(id = item),
//                                contentDescription = "",
//                                contentScale = ContentScale.Fit,
//                                modifier = Modifier
//                                    .size(50.dp)
//                                    .clickable {
//                                        /* Do smth */
//                                    }
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(15.dp))
//                }
//            }
//        }
//
//        Spacer(Modifier.height(10.dp))
//        DAppLimitEventCard(
//            backgroundImage = com.schoolonair.wallet.component.resources.R.drawable.onboarding_background_large,
//            title = "Get ready for back-to-school fun",
//            description = "Pick up brand-new goodies and character customisation"
//        ) {
//            val etherium = DApp(
//                com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24,
//                "",
//                "Etherium",
//                "ETH",
//                "",
//                DAppCategory.FINANCE
//            )
//            DAppItem(dapp = etherium)
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//    }
//}
//
//private val bookmarksData = listOf(
//    com.schoolonair.wallet.component.resources.R.drawable.ic_opensea_20 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_avalanche_24 to com.schoolonair.wallet.component.resources.R.string.Aave,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//).map { DrawableStringPair(it.first, it.second) }
//
//private val recentData = listOf(
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.logo_ethereum_24 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//    com.schoolonair.wallet.component.resources.R.drawable.bep2 to com.schoolonair.wallet.component.resources.R.string.back,
//).map { DrawableStringPair(it.first, it.second) }


private data class DrawableStringPair(
    val drawable: ImageResource,
    val text: StringResource
)
