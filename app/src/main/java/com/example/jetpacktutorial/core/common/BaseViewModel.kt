package com.example.jetpacktutorial.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {

    // One-shot UI events (snackbar, navigation, etc.)
    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    protected suspend fun sendEvent(event: Event) {
        _events.send(event)
    }
}
