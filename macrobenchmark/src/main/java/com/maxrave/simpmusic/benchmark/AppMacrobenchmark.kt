package com.maxrave.simpmusic.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkRule
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppMacrobenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun coldStartup() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.COLD,
            compilationMode = CompilationMode.DEFAULT,
        ) {
            pressHome()
            startActivityAndWait()
        }
    }

    @Test
    fun homeScrolling() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            setupBlock = { startActivityAndWait() },
        ) {
            repeat(5) {
                device.swipe(
                    device.displayWidth / 2,
                    (device.displayHeight * 0.78f).toInt(),
                    device.displayWidth / 2,
                    (device.displayHeight * 0.30f).toInt(),
                    18,
                )
            }
        }
    }

    @Test
    fun searchNavigationAndInput() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            setupBlock = { startActivityAndWait() },
        ) {
            val searchTab =
                device.wait(Until.findObject(By.text("Search")), 5_000)
                    ?: device.wait(Until.findObject(By.descContains("Search")), 5_000)
            assertNotNull("Search tab was not found", searchTab)
            searchTab!!.click()

            val searchField = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 5_000)
            assertNotNull("Search input was not found", searchField)
            searchField!!.click()
            searchField.setText("daft punk")
            device.pressBack()
        }
    }

    @Test
    fun playerTransition() {
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            setupBlock = { startActivityAndWait() },
        ) {
            val miniPlayer =
                device.wait(Until.findObject(By.descContains("Now playing")), 5_000)
                    ?: device.wait(Until.findObject(By.textContains("Now playing")), 2_000)
            assertNotNull(
                "A mini-player must be visible (start playback once before running this benchmark)",
                miniPlayer,
            )
            miniPlayer!!.click()
            device.waitForIdle()
            device.pressBack()
        }
    }

    private companion object {
        const val TARGET_PACKAGE = "com.pluto.music"
    }
}
