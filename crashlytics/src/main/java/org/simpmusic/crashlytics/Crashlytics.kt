package org.simpmusic.crashlytics

import android.content.Context
import android.util.Log
import com.maxrave.logger.Logger
import com.maxrave.domain.data.player.PlayerError
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid

// Sent crash to Sentry
fun reportCrash(throwable: Throwable) {
    Logger.e("Crashlytics", "Uncaught exception: ${throwable.message}\n${throwable.stackTraceToString()}")
    Sentry.captureException(throwable)
}

/**
 * Installed after CustomActivityOnCrash so the complete fatal stack trace reaches the on-disk
 * app/error logs before the previous handler restarts or terminates the process.
 */
fun installCrashLogging() {
    val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        runCatching {
            Logger.e("Crashlytics", "FATAL on ${thread.name}: ${throwable.message}\n${throwable.stackTraceToString()}")
            Sentry.captureException(throwable)
        }
        previousHandler?.uncaughtException(thread, throwable)
            ?: run {
                android.os.Process.killProcess(android.os.Process.myPid())
                kotlin.system.exitProcess(10)
            }
    }
}

private const val APP_OPEN = "app.open"

fun configCrashlytics(
    applicationContext: Context,
    dsn: String,
) {
    SentryAndroid.init(applicationContext) { options ->
        Log.d("Sentry", "dsn: $dsn")
        options.dsn = dsn
        options.isSendDefaultPii = true
        options.setTracesSampler { context -> if (context.transactionContext.name == APP_OPEN) 1.0 else 0.0 }
    }
    Sentry.startTransaction(APP_OPEN, APP_OPEN).finish()
}

fun pushPlayerError(error: PlayerError) {
    Sentry.withScope { scope ->
        Sentry.captureMessage("Player Error: ${error.message}, code: ${error.errorCode}, code name: ${error.errorCodeName}")
    }
}