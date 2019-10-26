package me.ddevil.mirai

interface Toggleable {
    val enabled: Boolean
    suspend fun initialize()
    suspend fun terminate()
}


abstract class AbstractToggleable : Toggleable {
    override val enabled: Boolean
        get() = isEnabled

    private var isEnabled: Boolean = false
    final override suspend fun initialize() {
        if (isEnabled) {
            return
        }
        isEnabled = true
        onInitialize()
    }

    final override suspend fun terminate() {
        if (!isEnabled) {
            return
        }
        isEnabled = false
        onTerminate()
    }

    protected abstract suspend fun onInitialize()
    protected abstract suspend fun onTerminate()
}
