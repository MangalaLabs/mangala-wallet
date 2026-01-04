package com.mangala.wallet.features.addressbook.domain.exceptions

/**
 * Base exception for Address Book operations
 */
sealed class AddressBookException(message: String) : Exception(message)

/**
 * Thrown when trying to create a contact/group with a name that already exists
 */
class DuplicateNameException(message: String) : AddressBookException(message)

/**
 * Thrown when validation fails
 */
class ValidationException(message: String) : AddressBookException(message)

/**
 * Thrown when trying to change immutable data
 */
class ImmutableDataException(message: String) : AddressBookException(message)