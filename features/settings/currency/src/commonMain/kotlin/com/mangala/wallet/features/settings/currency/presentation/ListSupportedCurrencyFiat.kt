package com.mangala.wallet.features.settings.currency.presentation

import androidx.compose.runtime.Composable
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.UnitedStatesDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.UnitedArabEmiratesDirham
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArgentinePeso
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.AustralianDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BangladeshiTaka
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BahrainiDinar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BermudianDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BrazilianReal
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.CanadianDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SwissFranc
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ChileanPeso
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ChineseYuan
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.CzechKoruna
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.DanishKrone
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Euro
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BritishPound
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HongKongDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HungarianForint
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IndonesianRupiah
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IsraeliNewShekel
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IndianRupee
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.JapaneseYen
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SouthKoreanWon
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.KuwaitiDinar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SriLankanRupee
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MyanmarKyat
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MexicanPeso
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MalaysianRinggit
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.NigerianNaira
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.NorwegianKrone
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.NewZealandDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.PhilippinePeso
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.PakistaniRupee
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.PolishZloty
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.RussianRuble
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SaudiRiyal
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SwedishKrona
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SingaporeDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ThaiBaht
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TurkishLira
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.NewTaiwanDollar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.UkrainianHryvnia
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.VenezuelanBolívar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.VietnameseDong
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SouthAfricanRand
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Bitcoin

@Composable
fun listSupportedCurrencyFiat() = listOf(
    CurrencyScreenUiModel(Currency.USD, MR.strings.all_language_usd_name.desc().localized(), MangalaWalletPack.UnitedStatesDollar),
    CurrencyScreenUiModel(Currency.AED, MR.strings.all_language_aed_name.desc().localized(), MangalaWalletPack.UnitedArabEmiratesDirham),
    CurrencyScreenUiModel(Currency.ARS, MR.strings.all_language_ars_name.desc().localized(), MangalaWalletPack.ArgentinePeso),
    CurrencyScreenUiModel(Currency.AUD, MR.strings.all_language_aud_name.desc().localized(), MangalaWalletPack.AustralianDollar),
    CurrencyScreenUiModel(Currency.BDT, MR.strings.all_language_bdt_name.desc().localized(), MangalaWalletPack.BangladeshiTaka),
    CurrencyScreenUiModel(Currency.BHD, MR.strings.all_language_bhd_name.desc().localized(), MangalaWalletPack.BahrainiDinar),
    CurrencyScreenUiModel(Currency.BMD, MR.strings.all_language_bmd_name.desc().localized(), MangalaWalletPack.BermudianDollar),
    CurrencyScreenUiModel(Currency.BRL, MR.strings.all_language_brl_name.desc().localized(), MangalaWalletPack.BrazilianReal),
    CurrencyScreenUiModel(Currency.CAD, MR.strings.all_language_cad_name.desc().localized(), MangalaWalletPack.CanadianDollar),
    CurrencyScreenUiModel(Currency.CHF, MR.strings.all_language_chf_name.desc().localized(), MangalaWalletPack.SwissFranc),
    CurrencyScreenUiModel(Currency.CLP, MR.strings.all_language_clp_name.desc().localized(), MangalaWalletPack.ChileanPeso),
    CurrencyScreenUiModel(Currency.CNY, MR.strings.all_language_cny_name.desc().localized(), MangalaWalletPack.ChineseYuan),
    CurrencyScreenUiModel(Currency.CZK, MR.strings.all_language_czk_name.desc().localized(), MangalaWalletPack.CzechKoruna),
    CurrencyScreenUiModel(Currency.DKK, MR.strings.all_language_dkk_name.desc().localized(), MangalaWalletPack.DanishKrone),
    CurrencyScreenUiModel(Currency.EUR, MR.strings.all_language_eur_name.desc().localized(), MangalaWalletPack.Euro),
    CurrencyScreenUiModel(Currency.GBP, MR.strings.all_language_gbp_name.desc().localized(), MangalaWalletPack.BritishPound),
    CurrencyScreenUiModel(Currency.HKD, MR.strings.all_language_hkd_name.desc().localized(), MangalaWalletPack.HongKongDollar),
    CurrencyScreenUiModel(Currency.HUF, MR.strings.all_language_huf_name.desc().localized(), MangalaWalletPack.HungarianForint),
    CurrencyScreenUiModel(Currency.IDR, MR.strings.all_language_idr_name.desc().localized(), MangalaWalletPack.IndonesianRupiah),
    CurrencyScreenUiModel(Currency.ILS, MR.strings.all_language_ils_name.desc().localized(), MangalaWalletPack.IsraeliNewShekel),
    CurrencyScreenUiModel(Currency.INR, MR.strings.all_language_inr_name.desc().localized(), MangalaWalletPack.IndianRupee),
    CurrencyScreenUiModel(Currency.JPY, MR.strings.all_language_jpy_name.desc().localized(), MangalaWalletPack.JapaneseYen),
    CurrencyScreenUiModel(Currency.KRW, MR.strings.all_language_krw_name.desc().localized(), MangalaWalletPack.SouthKoreanWon),
    CurrencyScreenUiModel(Currency.KWD, MR.strings.all_language_kwd_name.desc().localized(), MangalaWalletPack.KuwaitiDinar),
    CurrencyScreenUiModel(Currency.LKR, MR.strings.all_language_lkr_name.desc().localized(), MangalaWalletPack.SriLankanRupee),
    CurrencyScreenUiModel(Currency.MMK, MR.strings.all_language_mmk_name.desc().localized(), MangalaWalletPack.MyanmarKyat),
    CurrencyScreenUiModel(Currency.MXN, MR.strings.all_language_mxn_name.desc().localized(), MangalaWalletPack.MexicanPeso),
    CurrencyScreenUiModel(Currency.MYR, MR.strings.all_language_myr_name.desc().localized(), MangalaWalletPack.MalaysianRinggit),
    CurrencyScreenUiModel(Currency.NGN, MR.strings.all_language_ngn_name.desc().localized(), MangalaWalletPack.NigerianNaira),
    CurrencyScreenUiModel(Currency.NOK, MR.strings.all_language_nok_name.desc().localized(), MangalaWalletPack.NorwegianKrone),
    CurrencyScreenUiModel(Currency.NZD, MR.strings.all_language_nzd_name.desc().localized(), MangalaWalletPack.NewZealandDollar),
    CurrencyScreenUiModel(Currency.PHP, MR.strings.all_language_php_name.desc().localized(), MangalaWalletPack.PhilippinePeso),
    CurrencyScreenUiModel(Currency.PKR, MR.strings.all_language_pkr_name.desc().localized(), MangalaWalletPack.PakistaniRupee),
    CurrencyScreenUiModel(Currency.PLN, MR.strings.all_language_pln_name.desc().localized(), MangalaWalletPack.PolishZloty),
    CurrencyScreenUiModel(Currency.RUB, MR.strings.all_language_rub_name.desc().localized(), MangalaWalletPack.RussianRuble),
    CurrencyScreenUiModel(Currency.SAR, MR.strings.all_language_sar_name.desc().localized(), MangalaWalletPack.SaudiRiyal),
    CurrencyScreenUiModel(Currency.SEK, MR.strings.all_language_sek_name.desc().localized(), MangalaWalletPack.SwedishKrona),
    CurrencyScreenUiModel(Currency.SGD, MR.strings.all_language_sgd_name.desc().localized(), MangalaWalletPack.SingaporeDollar),
    CurrencyScreenUiModel(Currency.THB, MR.strings.all_language_thb_name.desc().localized(), MangalaWalletPack.ThaiBaht),
    CurrencyScreenUiModel(Currency.TRY, MR.strings.all_language_try_name.desc().localized(), MangalaWalletPack.TurkishLira),
    CurrencyScreenUiModel(Currency.TWD, MR.strings.all_language_twd_name.desc().localized(), MangalaWalletPack.NewTaiwanDollar),
    CurrencyScreenUiModel(Currency.UAH, MR.strings.all_language_uah_name.desc().localized(), MangalaWalletPack.UkrainianHryvnia),
    CurrencyScreenUiModel(Currency.VEF, MR.strings.all_language_vef_name.desc().localized(), MangalaWalletPack.VenezuelanBolívar),
    CurrencyScreenUiModel(Currency.VND, MR.strings.all_language_vnd_name.desc().localized(), MangalaWalletPack.VietnameseDong),
    CurrencyScreenUiModel(Currency.ZAR, MR.strings.all_language_zar_name.desc().localized(), MangalaWalletPack.SouthAfricanRand),
    CurrencyScreenUiModel(Currency.BTC, MR.strings.all_language_btc_name.desc().localized(), MangalaWalletPack.Bitcoin)
)