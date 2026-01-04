package com.mangala.wallet.websocket.chat.lifecycle

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppLifecycleManagerImpl(
    private val application: Application
) : AppLifecycleManager, DefaultLifecycleObserver {
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _appState = MutableStateFlow(AppState.FOREGROUND)
    private val observers = mutableSetOf<AppLifecycleObserver>()
    
    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    
    override fun observeAppState(): Flow<AppState> = _appState.asStateFlow()
    
    override fun getCurrentAppState(): AppState = _appState.value
    
    override fun getBackgroundTimeRemaining(): Double {
        // Android doesn't have a direct equivalent to iOS background time
        // Return a large value to indicate no specific limit
        return Double.MAX_VALUE
    }
    
    override suspend fun requestBackgroundTime(task: suspend () -> Unit): Boolean {
        // Android doesn't have the same background time concept as iOS
        // Just execute the task
        return try {
            withContext(Dispatchers.IO) {
                task()
            }
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
    
    // DefaultLifecycleObserver methods
    override fun onStart(owner: LifecycleOwner) {
        updateAppState(AppState.FOREGROUND)
        notifyEvent(AppLifecycleEvent.OnStart)
    }
    
    override fun onStop(owner: LifecycleOwner) {
        updateAppState(AppState.BACKGROUND)
        notifyEvent(AppLifecycleEvent.OnStop)
    }
    
    override fun onResume(owner: LifecycleOwner) {
        updateAppState(AppState.FOREGROUND)
        notifyEvent(AppLifecycleEvent.OnResume)
    }
    
    override fun onPause(owner: LifecycleOwner) {
        notifyEvent(AppLifecycleEvent.OnPause)
    }
    
    private fun updateAppState(state: AppState) {
        _appState.value = state
        coroutineScope.launch {
            observers.forEach { it.onAppStateChanged(state) }
        }
    }
    
    private fun notifyEvent(event: AppLifecycleEvent) {
        coroutineScope.launch {
            observers.forEach { it.onLifecycleEvent(event) }
        }
    }
    
    fun onTrimMemory(level: Int) {
        notifyEvent(AppLifecycleEvent.OnTrimMemory(level))
    }
    
    fun onLowMemory() {
        notifyEvent(AppLifecycleEvent.OnLowMemory)
    }
}