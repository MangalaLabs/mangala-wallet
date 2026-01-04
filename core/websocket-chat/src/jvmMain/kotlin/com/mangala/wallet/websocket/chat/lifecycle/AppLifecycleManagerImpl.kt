package com.mangala.wallet.websocket.chat.lifecycle

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppLifecycleManagerImpl : AppLifecycleManager {
    
    private val _appState = MutableStateFlow(AppState.FOREGROUND)
    private val observers = mutableSetOf<AppLifecycleObserver>()
    
    override fun observeAppState(): Flow<AppState> = _appState.asStateFlow()
    
    override fun getCurrentAppState(): AppState = _appState.value
    
    override fun getBackgroundTimeRemaining(): Double {
        // Desktop doesn't have background time limits
        return Double.MAX_VALUE
    }
    
    override suspend fun requestBackgroundTime(task: suspend () -> Unit): Boolean {
        // Desktop can run tasks without restrictions
        return try {
            task()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override fun registerLifecycleObserver(observer: AppLifecycleObserver) {
        observers.add(observer)
    }
    
    override fun unregisterLifecycleObserver(observer: AppLifecycleObserver) {
        observers.remove(observer)
    }
    
    fun updateAppState(state: AppState) {
        _appState.value = state
        observers.forEach { it.onAppStateChanged(state) }
    }
    
    fun notifyEvent(event: AppLifecycleEvent) {
        observers.forEach { it.onLifecycleEvent(event) }
    }
}