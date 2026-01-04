package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.approver

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuthAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.remote.network.CustomResponseException
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.isNotNullOrBlank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultisigProposalApproverScreenModel(
    actionData: List<MultisigAction>,
    approver: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>,
    private val getAccountInfoUseCase: GetAccountInfoUseCase
) : BaseScreenModel() {

    private val _uiState =
        MutableStateFlow(
            MultisigProposalApproverUiModel(
                actions = actionData,
                approvers = approver
            )
        )
    val uiState = _uiState.asStateFlow()

    private var updateActorNameJob: Job? = null
    private var updatePermissionNameJob: Job? = null
    private val cachedPermissions =
        mutableMapOf<MultisigActionAuthorization, MutableMap<String, List<String>>>()
    private val cachedActors = mutableMapOf<MultisigActionAuthorization, List<String>>()
    private val cachedWeight = mutableMapOf<Pair<List<String>, List<String>>, List<Long>>()
    private val ongoingRequests = mutableSetOf<String>()

    init {
        filterAllAuthorization()
    }

    private fun filterAllAuthorization() {
        screenModelScope.launch {
            _uiState.update { currentState ->
                val existingApprovers =
                    currentState.approvers.mapNotNull { (authorization, associatedList) ->
                        val (threshold, accounts) = fetchThresholdAndAccounts(authorization)

                        val relevantActions = currentState.actions
                            .filter { it.authorizations.contains(authorization) }
                            .map { it.actionName }

                        if (relevantActions.isEmpty()) return@mapNotNull null

                        val actionNameString = relevantActions.joinToString(", ")
                        val listItem = associatedList.map { item ->
                            val weight =
                                if (item.authorizationName == authorization.authorizationName && item.permissionName == authorization.permissionName) {
                                    authorization.threshold ?: 0
                                } else {
                                    accounts.find {
                                        it.permission.actor == item.authorizationName &&
                                                it.permission.permission == item.permissionName
                                    }?.weight?.toInt() ?: 0
                                }

                            AuthorItem(item, weight)
                        }

                        Approver(
                            key = Author(
                                authorization,
                                authorIndex = -1,
                                actionName = actionNameString
                            ),
                            listItem = listItem.distinct(),
                            threshold = threshold,
                            numberAccounts = if (listItem.none {
                                    it.authorization.authorizationName == authorization.authorizationName &&
                                            it.authorization.permissionName == authorization.permissionName
                                }) accounts.size + 1 else accounts.size
                        )
                    }.toMutableList()

                val newAuthorizationsGrouped = currentState.actions
                    .flatMap { action -> action.authorizations.map { it to action.actionName } }
                    .groupBy { (authorization, _) -> authorization.authorizationName to authorization.permissionName }

                newAuthorizationsGrouped.forEach { (authKey, authorizations) ->
                    val (authorizationName, permissionName) = authKey

                    if (existingApprovers.none {
                            it.key.authorization.authorizationName == authorizationName &&
                                    it.key.authorization.permissionName == permissionName
                        }) {

                        val combinedActionNames = authorizations.joinToString(", ") { it.second }
                        val authorization = authorizations.first().first

                        val (threshold, accounts) = fetchThresholdAndAccounts(authorization)

                        existingApprovers.add(
                            Approver(
                                key = Author(
                                    authorization,
                                    authorIndex = -1,
                                    actionName = combinedActionNames
                                ),
                                listItem = emptyList(),
                                threshold = threshold,
                                numberAccounts = if (
                                    accounts.none {
                                        it.permission.permission == authorization.permissionName &&
                                                it.permission.actor == authorization.authorizationName
                                    }) {
                                    accounts.size + 1
                                } else {
                                    accounts.size
                                }

                            )
                        )
                    }
                }
                currentState.copy(listApprover = existingApprovers)
            }
        }
    }

    private suspend fun fetchThresholdAndAccounts(authorization: MultisigActionAuthorization): Pair<Int, List<AntelopeRequiredAuthAccount>> {
        return try {
            val accountInfo =
                getAccountInfoUseCase.withResult(authorization.authorizationName).getOrNull()
            val permissionInfo = accountInfo?.permissions?.find {
                it.permissionType.permissionName == authorization.permissionName
            }

            val threshold = permissionInfo?.requiredAuth?.threshold ?: 0
            val accounts = permissionInfo?.requiredAuth?.accounts ?: emptyList()

            val actors = accounts.map { it.permission.actor } + authorization.authorizationName
            val permissions =
                accounts.map { it.permission.permission } + authorization.permissionName
            val weights = accounts.map { it.weight } + (authorization.threshold?.toLong() ?: 0)

            cachedWeight[actors to permissions] = weights

            threshold to accounts
        } catch (e: Exception) {
            println("Error fetching account info for ${authorization.authorizationName}: ${e.message}")
            0 to emptyList()
        }
    }

    fun onAuthorizationNameChange(
        approverOrder: Int,
        index: Int,
        value: String,
        authorItem: AuthorItem,
        author: MultisigActionAuthorization
    ) {
        updateActorNameJob?.cancel()
        updateActorNameJob = screenModelScope.launch {
            val newName = value.trim()
            _uiState.update { currentState ->
                currentState.copy(
                    listApprover = currentState.listApprover.mapIndexed { i, approver ->
                        if (i != approverOrder) return@mapIndexed approver

                        approver.copy(
                            listItem = approver.listItem.mapIndexed { j, item ->
                                if (j == index && item.authorization == authorItem.authorization)
                                    item.copy(
                                        authorization = item.authorization.copy(
                                            authorizationName = newName
                                        ),
                                        firstInputLoading = true
                                    )
                                else item
                            }
                        )
                    }
                )
            }

            delay(APPROVER_SUGGESTION_DEBOUNCE)
            if (!isActive) return@launch

            val cachedActorList = cachedActors[author] ?: emptyList()
            val cachedPermissionList =
                cachedPermissions[author]?.get(author.authorizationName)
                    ?.plus(author.permissionName) ?: emptyList()

            val approverConfirm: Pair<List<String>, List<String>> = when {
                cachedPermissionList.size > 1 -> {
                    cachedActorList to cachedPermissionList
                }

                cachedPermissionList.size == 1 -> {
                    listOf(author.authorizationName) to listOf(author.permissionName)
                }

                !ongoingRequests.contains(author.authorizationName) -> {
                    ongoingRequests.add(author.authorizationName)

                    withContext(Dispatchers.IO) {
                        try {
                            val accounts =
                                getAccountInfoUseCase.withResult(author.authorizationName)
                                    .getOrNull()
                                    ?.permissions
                                    ?.find { it.permissionType.permissionName == author.permissionName }
                                    ?.requiredAuth
                                    ?.accounts
                                    ?: emptyList()

                            val actors =
                                accounts.map { it.permission.actor } + author.authorizationName
                            val permissions =
                                accounts.map { it.permission.permission } + author.permissionName
                            val weight = accounts.map { it.weight } + (author.threshold?.toLong()
                                ?: 0)

                            cachedWeight[actors to permissions] = weight
                            cachedPermissions.getOrPut(author) { mutableMapOf() }[author.authorizationName] =
                                permissions
                            cachedActors[author] = actors
                            actors to permissions
                        } catch (e: CustomResponseException) {
                            println("API Error: ${e.message}")
                            emptyList<String>() to emptyList()
                        } catch (e: Exception) {
                            println("Network/Unknown Error: ${e.message}")
                            emptyList<String>() to emptyList()
                        } finally {
                            ongoingRequests.remove(author.authorizationName)
                        }
                    }
                }

                else -> {
                    println("API request already in progress for ${author.authorizationName}")
                    return@launch
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    listApprover = currentState.listApprover.mapIndexed { i, approver ->
                        if (i != approverOrder) return@mapIndexed approver

                        val filteredActors = approverConfirm.first.filter {
                            it.contains(newName, ignoreCase = true)
                        }

                        val duplicates = approver.listItem
                            .groupBy { it.authorization.authorizationName to it.authorization.permissionName }
                            .filter { it.value.size > 1 }

                        approver.copy(
                            listItem = approver.listItem.mapIndexed { j, item ->
                                if (j == index) {

                                    val permissionsForAuthor =
                                        cachedPermissions[author]?.get(author.authorizationName)
                                            ?: emptyList()
                                    val filteredPermissions = if (newName.isBlank()) {
                                        permissionsForAuthor
                                    } else {
                                        permissionsForAuthor.ifEmpty { item.listPermission }
                                    }

                                    val matchingIndex =
                                        approverConfirm.first.indexOfFirst { actor ->
                                            actor == item.authorization.authorizationName
                                        }.takeIf {
                                            it >= 0 && approverConfirm.second.getOrNull(it) == item.authorization.permissionName
                                        }

                                    val error = when {
                                        duplicates.keys.any { it.first == newName && it.second == item.authorization.permissionName } -> "Duplicate item"
                                        filteredActors.isEmpty() -> "Invalid actor"
                                        else -> null
                                    }

                                    var weight = if (!error.isNullOrBlank()) {
                                        0
                                    } else {
                                        matchingIndex?.let {
                                            cachedWeight[approverConfirm]?.getOrNull(
                                                it
                                            )
                                        } ?: 0
                                    }

                                    if (item.firstInputError != null && matchingIndex != null && matchingIndex >= 0) {
                                        weight = 1
                                    }

                                    val permissionError = when {
                                        item.secondInputError.isNotNullOrBlank() ->
                                            if (item.secondInputError == "Invalid permission" &&
                                                item.listPermission.none { it == item.authorization.permissionName }
                                            ) item.secondInputError else item.secondInputError

                                        else -> item.secondInputError
                                    }

                                    if (newName.isBlank()) {
                                        item.copy(
                                            firstInputLoading = false,
                                            listActor = emptyList(),
                                            listPermission = emptyList(),
                                            weight = 0,
                                            firstInputError = null,
                                            secondInputError = null
                                        )
                                    } else {
                                        item.copy(
                                            firstInputLoading = false,
                                            listActor = filteredActors,
                                            listPermission = filteredPermissions,
                                            weight = weight.toInt(),
                                            firstInputError = error,
                                            secondInputError = permissionError
                                        )
                                    }
                                } else {
                                    if (duplicates.isNotEmpty() &&
                                        duplicates.keys.any { it.first == item.authorization.authorizationName && it.second == item.authorization.permissionName }
                                    ) {
                                        item.copy(firstInputError = "Duplicate item")
                                    } else {

                                        val permissionList =
                                            cachedPermissions[author]?.get(item.authorization.authorizationName)
                                        val matchingIndex =
                                            permissionList?.indexOf(item.authorization.permissionName)
                                                ?.takeIf { it >= 0 }
                                        var weight = matchingIndex?.let {
                                            cachedWeight[cachedActors[author] to permissionList]?.getOrNull(
                                                it
                                            )
                                        } ?: item.weight

                                        if (item.firstInputError != null && matchingIndex != null && matchingIndex >= 0) {
                                            weight = 1
                                        }
                                        item.copy(
                                            weight = weight.toInt(),
                                            firstInputError = when (item.firstInputError) {
                                                "Duplicate item" -> null
                                                else -> item.firstInputError
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    }

    fun onPermissionNameChange(
        approverOrder: Int,
        index: Int,
        value: String,
        authorItem: AuthorItem,
        author: MultisigActionAuthorization
    ) {
        updatePermissionNameJob?.cancel()
        updatePermissionNameJob = screenModelScope.launch {
            val newPermission = value.trim()
            _uiState.update { currentState ->
                currentState.copy(
                    listApprover = currentState.listApprover.mapIndexed { i, approver ->
                        if (i != approverOrder) return@mapIndexed approver

                        approver.copy(
                            listItem = approver.listItem.mapIndexed { j, item ->
                                if (j == index && item.authorization == authorItem.authorization)
                                    item.copy(
                                        authorization = item.authorization.copy(permissionName = newPermission),
                                        secondInputLoading = true
                                    )
                                else
                                    item
                            }
                        )
                    }
                )
            }

            delay(APPROVER_SUGGESTION_DEBOUNCE)
            if (!isActive) return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    listApprover = currentState.listApprover.mapIndexed { i, approver ->
                        if (i != approverOrder) return@mapIndexed approver

                        val authorizationCount = approver.listItem
                            .groupingBy { it.authorization.authorizationName to it.authorization.permissionName }
                            .eachCount()

                        approver.copy(
                            listItem = approver.listItem.mapIndexed { j, item ->
                                val key =
                                    item.authorization.authorizationName to item.authorization.permissionName
                                val isDuplicate = (authorizationCount[key] ?: 0) > 1
                                val permissionsForAuthor =
                                    cachedPermissions[author]?.get(author.authorizationName)
                                        ?: emptyList()

                                when {
                                    j == index -> {
                                        val filteredPermissions = if (newPermission.isBlank()) {
                                            permissionsForAuthor
                                        } else {
                                            item.listPermission.ifEmpty { permissionsForAuthor.ifEmpty { item.listPermission } }
                                                .filter {
                                                    it.contains(
                                                        newPermission,
                                                        ignoreCase = true
                                                    )
                                                }
                                        }

                                        val matchingIndex = item.listActor.indexOfFirst { actor ->
                                            newPermission in item.listPermission && actor == item.authorization.authorizationName
                                        }

                                        val weight =
                                            cachedWeight[cachedActors[author] to permissionsForAuthor]
                                                ?.getOrNull(matchingIndex) ?: 0

                                        item.copy(
                                            secondInputLoading = false,
                                            listPermission = filteredPermissions
                                                .takeUnless { item.listActor.isEmpty() }
                                                ?: emptyList(),
                                            weight = weight.takeUnless { newPermission.isBlank() }
                                                ?.toInt() ?: 0,
                                            firstInputError =
                                            if (newPermission.isBlank()) {
                                                if (item.firstInputError == "Invalid actor") {
                                                    item.firstInputError
                                                } else {
                                                    null
                                                }
                                            } else
                                                when {
                                                    isDuplicate -> "Duplicate item"
                                                    else -> item.firstInputError
                                                },
                                            secondInputError = when {
                                                newPermission.isBlank() -> null
                                                filteredPermissions.isEmpty() -> "Invalid permission"
                                                else -> null
                                            }
                                        )
                                    }

                                    isDuplicate -> item.copy(firstInputError = "Duplicate item")

                                    else -> {
                                        val permissionList =
                                            cachedPermissions[author]?.get(item.authorization.authorizationName)
                                        val matchingIndex =
                                            permissionList?.indexOf(item.authorization.permissionName)

                                        val weight = matchingIndex?.let {
                                            cachedWeight[cachedActors[author] to permissionList]?.getOrNull(
                                                it
                                            )
                                        } ?: item.weight

                                        item.copy(
                                            weight = if (item.firstInputError != null && matchingIndex != null && matchingIndex >= 0) 1 else weight.toInt(),
                                            firstInputError =
                                            when (item.firstInputError) {
                                                "Duplicate item" -> null
                                                else -> item.firstInputError
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    }

    fun onUpdateBothInputs(
        first: String,
        second: String,
        approverOrder: Int,
        index: Int,
        authorItem: AuthorItem,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                listApprover = currentState.listApprover.mapIndexed { i, approver ->
                    if (i != approverOrder) return@mapIndexed approver

                    approver.copy(
                        listItem = approver.listItem.mapIndexed { j, item ->
                            if (j == index && item.authorization == authorItem.authorization)
                                item.copy(
                                    authorization = item.authorization.copy(
                                        authorizationName = first,
                                        permissionName = second
                                    ),
                                    listActor = emptyList(),
                                    listPermission = emptyList(),
                                    weight = 0,
                                    firstInputError = null,
                                    secondInputError = null
                                )
                            else item.copy(
                                firstInputError = if (item.firstInputError == "Duplicate item") null else item.firstInputError
                            )
                        }
                    )
                }
            )
        }
    }

    fun onAddAuthorization(approverOrder: Int) {
        _uiState.update { currentState ->
            val updatedApprovers = currentState.listApprover.mapIndexed { index, approver ->
                if (index == approverOrder) {
                    val newAuthorization = MultisigActionAuthorization(
                        authorizationName = "",
                        permissionName = ""
                    )
                    approver.copy(
                        listItem = approver.listItem + AuthorItem(
                            authorization = newAuthorization,
                            weight = 0
                        )
                    )

                } else {
                    approver
                }
            }
            currentState.copy(
                listApprover = updatedApprovers
            )
        }
    }

    fun onRemoveAuthorization(
        authorIndex: Int, approverIndex: Int, author: MultisigActionAuthorization
    ) {
        _uiState.update { currentState ->
            val updatedApprovers = currentState.listApprover.mapIndexed { index, approver ->
                if (index == authorIndex) {
                    val newListItem = approver.listItem.toMutableList()
                    if (approverIndex in newListItem.indices) {
                        newListItem.removeAt(approverIndex)
                    } else {
                        println("Warning: Attempted to remove an index ($approverIndex) out of bounds for list of size ${newListItem.size}")
                    }

                    approver.copy(listItem = newListItem.mapIndexed { i, item ->
                        val authorizationCount = newListItem
                            .groupingBy { it.authorization.authorizationName to it.authorization.permissionName }
                            .eachCount()

                        val key =
                            item.authorization.authorizationName to item.authorization.permissionName
                        val isDuplicate = (authorizationCount[key] ?: 0) > 1

                        val permissionList =
                            cachedPermissions[author]?.get(item.authorization.authorizationName)
                        val matchingIndex =
                            permissionList?.indexOf(item.authorization.permissionName)

                        val weight = matchingIndex?.let {
                            cachedWeight[cachedActors[author] to permissionList]?.getOrNull(it)
                        } ?: item.weight

                        item.copy(
                            weight = if (item.firstInputError != null && (matchingIndex
                                    ?: -1) >= 0
                            ) 1 else weight.toInt(),
                            firstInputError = when {
                                item.firstInputError == "Invalid actor" -> item.firstInputError
                                isDuplicate -> item.firstInputError
                                else -> null
                            },
                            secondInputError = item.secondInputError
                        )
                    })
                } else approver
            }
            currentState.copy(listApprover = updatedApprovers)
        }
    }

    companion object {
        private const val APPROVER_SUGGESTION_DEBOUNCE = 300L
    }
}
