package com.mangala.wallet.features.addressbook.data.local.avatar

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import java.io.Serializable

/**
 * Triển khai của AvatarPickerContract cho Android
 * Quản lý việc chọn ảnh từ thư viện và xử lý quyền truy cập
 */
class AndroidAvatarPickerContract(
    private val context: Context,
    val onImageSelected: (String) -> Unit
) : AvatarPickerContract, Serializable {

    // Tránh lưu trữ launcher làm thành viên để tránh vấn đề serialization
    @Transient
    private var _imageLaunchCallback: ((String) -> Unit)? = null
    
    @Transient
    private var _permissionCallback: ((Boolean) -> Unit)? = null

    /**
     * Đăng ký callback để mở image picker
     */
    fun setImageLaunchCallback(callback: (String) -> Unit) {
        _imageLaunchCallback = callback
    }
    
    /**
     * Đăng ký callback để xử lý permission request
     */
    fun setPermissionCallback(callback: (Boolean) -> Unit) {
        _permissionCallback = callback
    }

    /**
     * Mở bộ chọn ảnh, kiểm tra quyền truy cập nếu cần
     */
    override fun openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ không cần quyền truy cập thư viện ảnh
            _imageLaunchCallback?.invoke("image/*")
        } else {
            // Kiểm tra quyền truy cập cho Android 9 và thấp hơn
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED

            if (hasPermission) {
                _imageLaunchCallback?.invoke("image/*")
            } else {
                _permissionCallback?.invoke(true)
            }
        }
    }

    /**
     * Kiểm tra xem nền tảng có hỗ trợ chọn ảnh không
     */
    override fun isImagePickerSupported(): Boolean {
        return true // Luôn hỗ trợ trên Android
    }
    
    companion object {
        // Add serialVersionUID to ensure serialization stability
        private const val serialVersionUID = 1L
    }
}