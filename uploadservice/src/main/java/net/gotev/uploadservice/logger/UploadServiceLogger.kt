package net.gotev.uploadservice.logger

import java.lang.ref.WeakReference

object UploadServiceLogger {
    private var logLevel = LogLevel.Off
    private val defaultLogger = DefaultLoggerDelegate()
    private var loggerDelegate = WeakReference<Delegate>(defaultLogger)

    enum class LogLevel {
        Debug,
        Info,
        Error,
        Off
    }

    interface Delegate {
        fun error(tag: String, message: String, exception: Throwable?)
        fun debug(tag: String, message: String)
        fun info(tag: String, message: String)
    }

    @Synchronized
    @JvmStatic
    fun setDelegate(delegate: Delegate?) {
        loggerDelegate = WeakReference(delegate ?: defaultLogger)
    }

    @Synchronized
    @JvmStatic
    fun setLogLevel(level: LogLevel) {
        logLevel = level
    }

    @Synchronized
    @JvmStatic
    fun setDevelopmentMode(devModeOn: Boolean) {
        logLevel = if (devModeOn) LogLevel.Debug else LogLevel.Off
    }

    private fun loggerWithLevel(minLevel: LogLevel) =
        if (logLevel > minLevel || logLevel == LogLevel.Off) null else loggerDelegate.get()

    @JvmOverloads
    @JvmStatic
    fun error(tag: String, exception: Throwable? = null, message: () -> String) {
        loggerWithLevel(LogLevel.Error)?.error(tag, message(), exception)
    }

    @JvmStatic
    fun info(tag: String, message: () -> String) {
        loggerWithLevel(LogLevel.Info)?.info(tag, message())
    }

    @JvmStatic
    fun debug(tag: String, message: () -> String) {
        loggerWithLevel(LogLevel.Debug)?.debug(tag, message())
    }
}