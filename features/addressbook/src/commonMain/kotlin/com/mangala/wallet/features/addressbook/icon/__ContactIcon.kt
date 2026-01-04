package com.mangala.wallet.features.addressbook.icon

import androidx.compose.ui.graphics.vector.ImageVector
import com.mangala.wallet.features.addressbook.icon.contacticon.BoubleQuestionMark
import com.mangala.wallet.features.addressbook.icon.contacticon.Circle
import com.mangala.wallet.features.addressbook.icon.contacticon.ClearText
import com.mangala.wallet.features.addressbook.icon.contacticon.ContactFavoriteEmptyIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.DeleteButton
import com.mangala.wallet.features.addressbook.icon.contacticon.DocumentCopy
import com.mangala.wallet.features.addressbook.icon.contacticon.EditButton
import com.mangala.wallet.features.addressbook.icon.contacticon.Email
import com.mangala.wallet.features.addressbook.icon.contacticon.HistoryButton
import com.mangala.wallet.features.addressbook.icon.contacticon.ImpotantDate
import com.mangala.wallet.features.addressbook.icon.contacticon.Location
import com.mangala.wallet.features.addressbook.icon.contacticon.NoContact
import com.mangala.wallet.features.addressbook.icon.contacticon.NoGroup
import com.mangala.wallet.features.addressbook.icon.contacticon.NoTransactionIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.OpenExternalApp
import com.mangala.wallet.features.addressbook.icon.contacticon.OutlineStar
import com.mangala.wallet.features.addressbook.icon.contacticon.Phone
import com.mangala.wallet.features.addressbook.icon.contacticon.Qrcode
import com.mangala.wallet.features.addressbook.icon.contacticon.ShareButton
import com.mangala.wallet.features.addressbook.icon.contacticon.Social
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.features.addressbook.icon.contacticon.TagUser
import com.mangala.wallet.features.addressbook.icon.contacticon.UnfavoriteStar
import kotlin.collections.List as ____KtList

public object ContactIcon

private var __AllIcons: ____KtList<ImageVector>? = null

public val ContactIcon.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Circle, ContactFavoriteEmptyIcon, DeleteButton, DocumentCopy, EditButton,
        HistoryButton, NoContact, NoGroup, NoTransactionIcon, Qrcode,
        ShareButton, Star, Email, ImpotantDate, Location, Phone, Social, TagUser, BoubleQuestionMark, OpenExternalApp, OutlineStar, ClearText, UnfavoriteStar)
    return __AllIcons!!
  }
