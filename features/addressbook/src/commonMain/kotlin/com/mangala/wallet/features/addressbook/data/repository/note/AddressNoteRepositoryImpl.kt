package com.mangala.wallet.features.addressbook.data.repository.note

import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteHistoryLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.note.AddressNoteLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteHistoryEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class AddressNoteRepositoryImpl(
    private val addressNoteLocalDataSource: AddressNoteLocalDataSource,
    private val addressNoteHistoryLocalDataSource: AddressNoteHistoryLocalDataSource
) : AddressNoteRepository {

    override suspend fun getAddressNotesByWalletAddressId(walletAddressId: String): Result<List<AddressNoteEntity>> {
        return try {
            val notes = addressNoteLocalDataSource.getAddressNotesByWalletAddressId(walletAddressId)
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAddressNoteById(noteId: String): Result<AddressNoteEntity> {
        return try {
            val noteEntity = addressNoteLocalDataSource.getAddressNoteById(noteId)
            if (noteEntity != null) {
                Result.success(noteEntity)
            } else {
                Result.failure(NoSuchElementException("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addAddressNote(
        walletAddressId: String,
        content: String,
        formatOptions: String?,
        aiSuggestionSource: String?,
        reminderDate: Instant?
    ): Result<AddressNoteEntity> {
        return try {
            val newNote = addressNoteLocalDataSource.insertAddressNote(
                walletAddressId = walletAddressId,
                content = content,
                formatOptions = formatOptions,
                aiSuggestionSource = aiSuggestionSource,
                reminderDate = reminderDate
            )
            Result.success(newNote)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAddressNote(
        noteId: String,
        content: String,
        formatOptions: String?,
        aiSuggestionSource: String?,
        reminderDate: Instant?
    ): Result<AddressNoteEntity> {
        return try {
            // Get the current note for history
            val currentNote = addressNoteLocalDataSource.getAddressNoteById(noteId)
                ?: return Result.failure(NoSuchElementException("Note not found"))

            // Add to history
            addressNoteHistoryLocalDataSource.insertAddressNoteHistory(
                noteId = noteId,
                walletAddressId = currentNote.walletAddressId,
                oldContent = currentNote.content,
                changedBy = null // Could be set from a user ID or device ID
            )

            // Update note
            val updatedNote = addressNoteLocalDataSource.updateAddressNote(
                noteId = noteId,
                content = content,
                formatOptions = formatOptions,
                aiSuggestionSource = aiSuggestionSource,
                reminderDate = reminderDate
            )

            if (updatedNote != null) {
                Result.success(updatedNote)
            } else {
                Result.failure(Exception("Failed to update note"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAddressNote(noteId: String): Result<Boolean> {
        return try {
            val result = addressNoteLocalDataSource.softDeleteAddressNote(noteId)
            if (result) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete note"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchAddressNotes(walletAddressId: String, searchQuery: String): Result<List<AddressNoteEntity>> {
        return try {
            val notes = addressNoteLocalDataSource.searchAddressNotes(walletAddressId, searchQuery).first()
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAddressNoteHistory(noteId: String): Result<List<AddressNoteHistoryEntity>> {
        return try {
            val history = addressNoteHistoryLocalDataSource.getAddressNoteHistoryByNoteId(noteId).first()
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}