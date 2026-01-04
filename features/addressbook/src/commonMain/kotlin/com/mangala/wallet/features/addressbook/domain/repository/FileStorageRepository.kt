package com.mangala.wallet.features.addressbook.domain.repository

/**
 * Repository để xử lý lưu trữ và truy xuất file
 * Interface này sẽ có các implementation riêng cho từng nền tảng (Android, iOS) 
 */
interface FileStorageRepository {
    /**
     * Lưu một hình ảnh vào bộ nhớ và trả về đường dẫn có thể truy cập
     * @param imageBytes Mảng byte của hình ảnh
     * @param fileName Tên file để lưu
     * @return Đường dẫn đến file đã lưu
     */
    suspend fun saveImage(imageBytes: ByteArray, fileName: String): String
    
    /**
     * Đọc một hình ảnh từ bộ nhớ
     * @param path Đường dẫn đến file
     * @return Mảng byte của hình ảnh hoặc null nếu không tìm thấy
     */
    suspend fun loadImage(path: String): ByteArray?
    
    /**
     * Xóa một file từ bộ nhớ
     * @param path Đường dẫn đến file
     * @return true nếu xóa thành công, false nếu không
     */
    suspend fun deleteFile(path: String): Boolean
}
