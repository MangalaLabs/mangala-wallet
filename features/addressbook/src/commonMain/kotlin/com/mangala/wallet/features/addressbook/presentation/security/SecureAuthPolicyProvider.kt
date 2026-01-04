package com.mangala.wallet.features.addressbook.presentation.security

/**
 * Định danh các hành động cần bảo vệ
 */
enum class SecureActionId {
    AddContact,
    EditContact,
    DeleteContact,
    ExportContacts,
    AddTag,
    DeleteGroup,
    ViewContactList,
    ExportEncryptedFile,
    ViewHighSecurityContact, // View high security contact details
    // Privacy Mode - Address Reveal Actions
    RevealAddress,           // Standard address reveal (HIDDEN mode)
    RevealSensitiveAddress,  // Sensitive contact/address reveal  
    RevealSecretAddress      // SECRET mode reveal (highest security)
}

/**
 * Loại xác thực cần thiết cho hành động
 */
sealed class SecureActionType {
    object None : SecureActionType()
    object RequirePin : SecureActionType()
    object RequireBiometryOrPin : SecureActionType()
    object Require2FA : SecureActionType()
}

/**
 * Interface cung cấp policy xác thực cho từng hành động
 */
interface SecureAuthPolicyProvider {
    fun getPolicyFor(action: SecureActionId): SecureActionType
}

/**
 * Triển khai mặc định của SecureAuthPolicyProvider
 */
class DefaultSecureAuthPolicyProvider : SecureAuthPolicyProvider {
    override fun getPolicyFor(action: SecureActionId): SecureActionType {
        return when (action) {
            SecureActionId.AddContact -> SecureActionType.None  // No auth needed for better UX
            
            SecureActionId.EditContact,
            SecureActionId.DeleteContact -> SecureActionType.RequireBiometryOrPin

            SecureActionId.AddTag -> SecureActionType.RequirePin

            SecureActionId.ExportContacts,
            SecureActionId.ExportEncryptedFile,
            SecureActionId.DeleteGroup -> SecureActionType.Require2FA

            SecureActionId.ViewContactList -> SecureActionType.None
            
            SecureActionId.ViewHighSecurityContact -> SecureActionType.RequireBiometryOrPin
            
            // Privacy Mode - Address Reveal Policies
            SecureActionId.RevealAddress -> SecureActionType.RequirePin
            SecureActionId.RevealSensitiveAddress -> SecureActionType.RequireBiometryOrPin
            SecureActionId.RevealSecretAddress -> SecureActionType.Require2FA
        }
    }
}

/**
 * Triển khai có thể cấu hình của SecureAuthPolicyProvider
 * Cho phép cấu hình quy tắc xác thực từ remote config hoặc local settings
 */
class ConfigurableSecureAuthPolicyProvider(
    private val defaultProvider: SecureAuthPolicyProvider = DefaultSecureAuthPolicyProvider(),
//    private val configProvider: SecurityConfigProvider
) : SecureAuthPolicyProvider {

    override fun getPolicyFor(action: SecureActionId): SecureActionType {
//        // Kiểm tra xem có cấu hình đặc biệt cho hành động này không
//        val configuredPolicy = configProvider.getSecurityPolicyForAction(action.name)
//
//        return if (configuredPolicy != null) {
//            // Ưu tiên cấu hình từ config provider
//            mapStringToSecureActionType(configuredPolicy)
//        } else {
//            // Nếu không có, sử dụng policy mặc định
            return defaultProvider.getPolicyFor(action)
//        }
    }

    /**
     * Map string từ config thành SecureActionType
     */
    private fun mapStringToSecureActionType(policy: String): SecureActionType {
        return when (policy.lowercase()) {
            "none" -> SecureActionType.None
            "pin" -> SecureActionType.RequirePin
            "biometryorpin" -> SecureActionType.RequireBiometryOrPin
            "2fa" -> SecureActionType.Require2FA
            else -> SecureActionType.None // Fallback to none if invalid
        }
    }
}

/**
 * Interface cung cấp cấu hình bảo mật
 * Có thể triển khai với remote config, local settings, hoặc role-based
 */
interface SecurityConfigProvider {
    fun getSecurityPolicyForAction(actionId: String): String?
}

/**
 * Triển khai SecurityConfigProvider dựa trên role
 */
class RoleBasedSecurityConfigProvider(
    private val userRoleProvider: UserRoleProvider
) : SecurityConfigProvider {

    override fun getSecurityPolicyForAction(actionId: String): String? {
        val role = userRoleProvider.getCurrentUserRole()

        // Policy dựa trên role
        return when (role) {
            "admin" -> getAdminPolicy(actionId)
            "manager" -> getManagerPolicy(actionId)
            "user" -> getUserPolicy(actionId)
            else -> null
        }
    }

    private fun getAdminPolicy(actionId: String): String? {
        // Admin có thể được nới lỏng một số ràng buộc
        return when (actionId) {
            "DeleteContact" -> "pin" // Admin chỉ cần PIN để xóa contact
            "ExportContacts" -> "biometryorpin" // Admin không cần 2FA để export
            else -> null
        }
    }

    private fun getManagerPolicy(actionId: String): String? {
        // Managers có thể có policy khác
        return null
    }

    private fun getUserPolicy(actionId: String): String? {
        // Regular users follow default policy
        return null
    }
}

/**
 * Interface cung cấp thông tin về role của người dùng hiện tại
 */
interface UserRoleProvider {
    fun getCurrentUserRole(): String
}