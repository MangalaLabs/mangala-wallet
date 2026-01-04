package com.mangala.wallet.model.provider.coingecko


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoingeckoPriceHistoryResponse(
    @SerialName("community_data")
    val communityData: CommunityData? = CommunityData(),
    @SerialName("developer_data")
    val developerData: DeveloperData? = DeveloperData(),
    @SerialName("id")
    val id: String? = "",
    @SerialName("image")
    val image: Image? = Image(),
    @SerialName("market_data")
    val marketData: MarketData? = MarketData(),
    @SerialName("name")
    val name: String? = "",
    @SerialName("public_interest_stats")
    val publicInterestStats: PublicInterestStats? = PublicInterestStats(),
    @SerialName("symbol")
    val symbol: String? = ""
) {
    @Serializable
    data class CommunityData(
        @SerialName("facebook_likes")
        val facebookLikes: String? = null,
        @SerialName("reddit_accounts_active_48h")
        val redditAccountsActive48h: String? = null,
        @SerialName("reddit_average_comments_48h")
        val redditAverageComments48h: Double? = 0.0,
        @SerialName("reddit_average_posts_48h")
        val redditAveragePosts48h: Double? = 0.0,
        @SerialName("reddit_subscribers")
        val redditSubscribers: String? = null,
        @SerialName("twitter_followers")
        val twitterFollowers: String? = null
    )

    @Serializable
    data class DeveloperData(
        @SerialName("closed_issues")
        val closedIssues: Int? = 0,
        @SerialName("code_additions_deletions_4_weeks")
        val codeAdditionsDeletions4Weeks: CodeAdditionsDeletions4Weeks? = CodeAdditionsDeletions4Weeks(),
        @SerialName("commit_count_4_weeks")
        val commitCount4Weeks: Int? = 0,
        @SerialName("forks")
        val forks: Int? = 0,
        @SerialName("pull_request_contributors")
        val pullRequestContributors: Int? = 0,
        @SerialName("pull_requests_merged")
        val pullRequestsMerged: Int? = 0,
        @SerialName("stars")
        val stars: Int? = 0,
        @SerialName("subscribers")
        val subscribers: Int? = 0,
        @SerialName("total_issues")
        val totalIssues: Int? = 0
    ) {
        @Serializable
        data class CodeAdditionsDeletions4Weeks(
            @SerialName("additions")
            val additions: Int? = 0,
            @SerialName("deletions")
            val deletions: Int? = 0
        )
    }

    @Serializable
    data class Image(
        @SerialName("small")
        val small: String? = "",
        @SerialName("thumb")
        val thumb: String? = ""
    )

    @Serializable
    data class MarketData(
        @SerialName("current_price")
        val currentPrice: CurrentPrice? = CurrentPrice(),
        @SerialName("market_cap")
        val marketCap: MarketCap? = MarketCap(),
        @SerialName("total_volume")
        val totalVolume: TotalVolume? = TotalVolume()
    ) {
        @Serializable
        data class CurrentPrice(
            @SerialName("aed")
            val aed: Double? = 0.0,
            @SerialName("ars")
            val ars: Double? = 0.0,
            @SerialName("aud")
            val aud: Double? = 0.0,
            @SerialName("bch")
            val bch: Double? = 0.0,
            @SerialName("bdt")
            val bdt: Double? = 0.0,
            @SerialName("bhd")
            val bhd: Double? = 0.0,
            @SerialName("bits")
            val bits: Double? = 0.0,
            @SerialName("bmd")
            val bmd: Double? = 0.0,
            @SerialName("bnb")
            val bnb: Double? = 0.0,
            @SerialName("brl")
            val brl: Double? = 0.0,
            @SerialName("btc")
            val btc: Double? = 0.0,
            @SerialName("cad")
            val cad: Double? = 0.0,
            @SerialName("chf")
            val chf: Double? = 0.0,
            @SerialName("clp")
            val clp: Double? = 0.0,
            @SerialName("cny")
            val cny: Double? = 0.0,
            @SerialName("czk")
            val czk: Double? = 0.0,
            @SerialName("dkk")
            val dkk: Double? = 0.0,
            @SerialName("dot")
            val dot: Double? = 0.0,
            @SerialName("eos")
            val eos: Double? = 0.0,
            @SerialName("eth")
            val eth: Double? = 0.0,
            @SerialName("eur")
            val eur: Double? = 0.0,
            @SerialName("gbp")
            val gbp: Double? = 0.0,
            @SerialName("hkd")
            val hkd: Double? = 0.0,
            @SerialName("huf")
            val huf: Double? = 0.0,
            @SerialName("idr")
            val idr: Double? = 0.0,
            @SerialName("ils")
            val ils: Double? = 0.0,
            @SerialName("inr")
            val inr: Double? = 0.0,
            @SerialName("jpy")
            val jpy: Double? = 0.0,
            @SerialName("krw")
            val krw: Double? = 0.0,
            @SerialName("kwd")
            val kwd: Double? = 0.0,
            @SerialName("link")
            val link: Double? = 0.0,
            @SerialName("lkr")
            val lkr: Double? = 0.0,
            @SerialName("ltc")
            val ltc: Double? = 0.0,
            @SerialName("mmk")
            val mmk: Double? = 0.0,
            @SerialName("mxn")
            val mxn: Double? = 0.0,
            @SerialName("myr")
            val myr: Double? = 0.0,
            @SerialName("ngn")
            val ngn: Double? = 0.0,
            @SerialName("nok")
            val nok: Double? = 0.0,
            @SerialName("nzd")
            val nzd: Double? = 0.0,
            @SerialName("php")
            val php: Double? = 0.0,
            @SerialName("pkr")
            val pkr: Double? = 0.0,
            @SerialName("pln")
            val pln: Double? = 0.0,
            @SerialName("rub")
            val rub: Double? = 0.0,
            @SerialName("sar")
            val sar: Double? = 0.0,
            @SerialName("sats")
            val sats: Double? = 0.0,
            @SerialName("sek")
            val sek: Double? = 0.0,
            @SerialName("sgd")
            val sgd: Double? = 0.0,
            @SerialName("thb")
            val thb: Double? = 0.0,
            @SerialName("try")
            val tryX: Double? = 0.0,
            @SerialName("twd")
            val twd: Double? = 0.0,
            @SerialName("uah")
            val uah: Double? = 0.0,
            @SerialName("usd")
            val usd: Double? = 0.0,
            @SerialName("vef")
            val vef: Double? = 0.0,
            @SerialName("vnd")
            val vnd: Double? = 0.0,
            @SerialName("xag")
            val xag: Double? = 0.0,
            @SerialName("xau")
            val xau: Double? = 0.0,
            @SerialName("xdr")
            val xdr: Double? = 0.0,
            @SerialName("xlm")
            val xlm: Double? = 0.0,
            @SerialName("xrp")
            val xrp: Double? = 0.0,
            @SerialName("yfi")
            val yfi: Double? = 0.0,
            @SerialName("zar")
            val zar: Double? = 0.0
        )

        @Serializable
        data class MarketCap(
            @SerialName("aed")
            val aed: Double? = 0.0,
            @SerialName("ars")
            val ars: Double? = 0.0,
            @SerialName("aud")
            val aud: Double? = 0.0,
            @SerialName("bch")
            val bch: Double? = 0.0,
            @SerialName("bdt")
            val bdt: Double? = 0.0,
            @SerialName("bhd")
            val bhd: Double? = 0.0,
            @SerialName("bits")
            val bits: Double? = 0.0,
            @SerialName("bmd")
            val bmd: Double? = 0.0,
            @SerialName("bnb")
            val bnb: Double? = 0.0,
            @SerialName("brl")
            val brl: Double? = 0.0,
            @SerialName("btc")
            val btc: Double? = 0.0,
            @SerialName("cad")
            val cad: Double? = 0.0,
            @SerialName("chf")
            val chf: Double? = 0.0,
            @SerialName("clp")
            val clp: Double? = 0.0,
            @SerialName("cny")
            val cny: Double? = 0.0,
            @SerialName("czk")
            val czk: Double? = 0.0,
            @SerialName("dkk")
            val dkk: Double? = 0.0,
            @SerialName("dot")
            val dot: Double? = 0.0,
            @SerialName("eos")
            val eos: Double? = 0.0,
            @SerialName("eth")
            val eth: Double? = 0.0,
            @SerialName("eur")
            val eur: Double? = 0.0,
            @SerialName("gbp")
            val gbp: Double? = 0.0,
            @SerialName("hkd")
            val hkd: Double? = 0.0,
            @SerialName("huf")
            val huf: Double? = 0.0,
            @SerialName("idr")
            val idr: Double? = 0.0,
            @SerialName("ils")
            val ils: Double? = 0.0,
            @SerialName("inr")
            val inr: Double? = 0.0,
            @SerialName("jpy")
            val jpy: Double? = 0.0,
            @SerialName("krw")
            val krw: Double? = 0.0,
            @SerialName("kwd")
            val kwd: Double? = 0.0,
            @SerialName("link")
            val link: Double? = 0.0,
            @SerialName("lkr")
            val lkr: Double? = 0.0,
            @SerialName("ltc")
            val ltc: Double? = 0.0,
            @SerialName("mmk")
            val mmk: Double? = 0.0,
            @SerialName("mxn")
            val mxn: Double? = 0.0,
            @SerialName("myr")
            val myr: Double? = 0.0,
            @SerialName("ngn")
            val ngn: Double? = 0.0,
            @SerialName("nok")
            val nok: Double? = 0.0,
            @SerialName("nzd")
            val nzd: Double? = 0.0,
            @SerialName("php")
            val php: Double? = 0.0,
            @SerialName("pkr")
            val pkr: Double? = 0.0,
            @SerialName("pln")
            val pln: Double? = 0.0,
            @SerialName("rub")
            val rub: Double? = 0.0,
            @SerialName("sar")
            val sar: Double? = 0.0,
            @SerialName("sats")
            val sats: Double? = 0.0,
            @SerialName("sek")
            val sek: Double? = 0.0,
            @SerialName("sgd")
            val sgd: Double? = 0.0,
            @SerialName("thb")
            val thb: Double? = 0.0,
            @SerialName("try")
            val tryX: Double? = 0.0,
            @SerialName("twd")
            val twd: Double? = 0.0,
            @SerialName("uah")
            val uah: Double? = 0.0,
            @SerialName("usd")
            val usd: Double? = 0.0,
            @SerialName("vef")
            val vef: Double? = 0.0,
            @SerialName("vnd")
            val vnd: Double? = 0.0,
            @SerialName("xag")
            val xag: Double? = 0.0,
            @SerialName("xau")
            val xau: Double? = 0.0,
            @SerialName("xdr")
            val xdr: Double? = 0.0,
            @SerialName("xlm")
            val xlm: Double? = 0.0,
            @SerialName("xrp")
            val xrp: Double? = 0.0,
            @SerialName("yfi")
            val yfi: Double? = 0.0,
            @SerialName("zar")
            val zar: Double? = 0.0
        )

        @Serializable
        data class TotalVolume(
            @SerialName("aed")
            val aed: Double? = 0.0,
            @SerialName("ars")
            val ars: Double? = 0.0,
            @SerialName("aud")
            val aud: Double? = 0.0,
            @SerialName("bch")
            val bch: Double? = 0.0,
            @SerialName("bdt")
            val bdt: Double? = 0.0,
            @SerialName("bhd")
            val bhd: Double? = 0.0,
            @SerialName("bits")
            val bits: Double? = 0.0,
            @SerialName("bmd")
            val bmd: Double? = 0.0,
            @SerialName("bnb")
            val bnb: Double? = 0.0,
            @SerialName("brl")
            val brl: Double? = 0.0,
            @SerialName("btc")
            val btc: Double? = 0.0,
            @SerialName("cad")
            val cad: Double? = 0.0,
            @SerialName("chf")
            val chf: Double? = 0.0,
            @SerialName("clp")
            val clp: Double? = 0.0,
            @SerialName("cny")
            val cny: Double? = 0.0,
            @SerialName("czk")
            val czk: Double? = 0.0,
            @SerialName("dkk")
            val dkk: Double? = 0.0,
            @SerialName("dot")
            val dot: Double? = 0.0,
            @SerialName("eos")
            val eos: Double? = 0.0,
            @SerialName("eth")
            val eth: Double? = 0.0,
            @SerialName("eur")
            val eur: Double? = 0.0,
            @SerialName("gbp")
            val gbp: Double? = 0.0,
            @SerialName("hkd")
            val hkd: Double? = 0.0,
            @SerialName("huf")
            val huf: Double? = 0.0,
            @SerialName("idr")
            val idr: Double? = 0.0,
            @SerialName("ils")
            val ils: Double? = 0.0,
            @SerialName("inr")
            val inr: Double? = 0.0,
            @SerialName("jpy")
            val jpy: Double? = 0.0,
            @SerialName("krw")
            val krw: Double? = 0.0,
            @SerialName("kwd")
            val kwd: Double? = 0.0,
            @SerialName("link")
            val link: Double? = 0.0,
            @SerialName("lkr")
            val lkr: Double? = 0.0,
            @SerialName("ltc")
            val ltc: Double? = 0.0,
            @SerialName("mmk")
            val mmk: Double? = 0.0,
            @SerialName("mxn")
            val mxn: Double? = 0.0,
            @SerialName("myr")
            val myr: Double? = 0.0,
            @SerialName("ngn")
            val ngn: Double? = 0.0,
            @SerialName("nok")
            val nok: Double? = 0.0,
            @SerialName("nzd")
            val nzd: Double? = 0.0,
            @SerialName("php")
            val php: Double? = 0.0,
            @SerialName("pkr")
            val pkr: Double? = 0.0,
            @SerialName("pln")
            val pln: Double? = 0.0,
            @SerialName("rub")
            val rub: Double? = 0.0,
            @SerialName("sar")
            val sar: Double? = 0.0,
            @SerialName("sats")
            val sats: Double? = 0.0,
            @SerialName("sek")
            val sek: Double? = 0.0,
            @SerialName("sgd")
            val sgd: Double? = 0.0,
            @SerialName("thb")
            val thb: Double? = 0.0,
            @SerialName("try")
            val tryX: Double? = 0.0,
            @SerialName("twd")
            val twd: Double? = 0.0,
            @SerialName("uah")
            val uah: Double? = 0.0,
            @SerialName("usd")
            val usd: Double? = 0.0,
            @SerialName("vef")
            val vef: Double? = 0.0,
            @SerialName("vnd")
            val vnd: Double? = 0.0,
            @SerialName("xag")
            val xag: Double? = 0.0,
            @SerialName("xau")
            val xau: Double? = 0.0,
            @SerialName("xdr")
            val xdr: Double? = 0.0,
            @SerialName("xlm")
            val xlm: Double? = 0.0,
            @SerialName("xrp")
            val xrp: Double? = 0.0,
            @SerialName("yfi")
            val yfi: Double? = 0.0,
            @SerialName("zar")
            val zar: Double? = 0.0
        )
    }

    @Serializable
    data class PublicInterestStats(
        @SerialName("alexa_rank")
        val alexaRank: String? = null,
        @SerialName("bing_matches")
        val bingMatches: String? = null
    )
}