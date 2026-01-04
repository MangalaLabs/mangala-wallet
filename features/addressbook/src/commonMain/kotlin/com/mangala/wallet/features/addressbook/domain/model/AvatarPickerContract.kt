package com.mangala.wallet.features.addressbook.domain.model


interface AvatarPickerContract {
    fun openImagePicker()
    fun isImagePickerSupported(): Boolean
}