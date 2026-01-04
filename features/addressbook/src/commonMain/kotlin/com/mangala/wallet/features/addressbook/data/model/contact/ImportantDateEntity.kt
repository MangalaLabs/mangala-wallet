package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateNow
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Entity cho những ngày quan trọng của một contact
 * Tương ứng với bảng 'important_dates' trong database
 */
data class ImportantDateEntity(
    val id: String, // UUID
    val contactId: String,
    val date: Instant, // Unix timestamp hoặc YYYYMMDD
    val description: String, // Ví dụ: "Anniversary", "First Met"
    val calendarType: CalendarType = CalendarType.SOLAR, // Solar hoặc Lunar calendar
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Chuyển đổi timestamp thành LocalDate để dễ hiển thị
     * @return LocalDate tương ứng với timestamp
     */
    fun getLocalDate(): LocalDate {
        return Instant.fromEpochMilliseconds(date.toEpochMilliseconds()).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    /**
     * Kiểm tra xem ngày quan trọng có đến trong thời gian tới không
     * @param daysAhead Số ngày phía trước để kiểm tra
     * @return true nếu ngày quan trọng nằm trong khoảng thời gian chỉ định
     */
    fun isUpcoming(daysAhead: Int): Boolean {
        val today = localDateNow()
        val dateAsLocalDate = getLocalDate()

        // Chỉ quan tâm đến ngày và tháng, không quan tâm năm
        val todayMonthDay = "${today.monthNumber}-${today.dayOfMonth}"
        val dateMonthDay = "${dateAsLocalDate.monthNumber}-${dateAsLocalDate.dayOfMonth}"

        // Kiểm tra nếu ngày này trong năm nay đã qua, thì tính cho năm sau
        if (dateMonthDay < todayMonthDay) {
            // Đã qua trong năm nay, kiểm tra cho năm sau
            return false
        }

        // Tính số ngày từ hôm nay đến ngày quan trọng
        val dayOfYear = today.dayOfYear
        val importantDayOfYear = dateAsLocalDate.dayOfYear
        val daysUntil = if (importantDayOfYear >= dayOfYear) {
            importantDayOfYear - dayOfYear
        } else {
            365 + importantDayOfYear - dayOfYear
        }

        return daysUntil <= daysAhead
    }

    companion object {
        // Các loại ngày quan trọng phổ biến
        const val TYPE_ANNIVERSARY = "Anniversary"
        const val TYPE_FIRST_MET = "First Met"
        const val TYPE_GRADUATION = "Graduation"
        const val TYPE_WORK_ANNIVERSARY = "Work Anniversary"

        /**
         * Lấy danh sách các loại ngày quan trọng phổ biến
         * @return Danh sách các loại
         */
        fun getCommonTypes(): List<String> {
            return listOf(
                TYPE_ANNIVERSARY,
                TYPE_FIRST_MET,
                TYPE_GRADUATION,
                TYPE_WORK_ANNIVERSARY
            )
        }

        /**
         * Tạo một đối tượng ImportantDateEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            contactId: String,
            date: Instant,
            description: String,
            calendarType: CalendarType = CalendarType.SOLAR
        ): ImportantDateEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return ImportantDateEntity(
                id = id,
                contactId = contactId,
                date = date,
                description = description,
                calendarType = calendarType,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}