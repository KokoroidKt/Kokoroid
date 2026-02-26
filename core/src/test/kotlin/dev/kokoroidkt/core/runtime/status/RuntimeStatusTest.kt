/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.status

import dev.kokoroidkt.core.exceptions.status.NotAllowedInternalStatusChange
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RuntimeStatusTest {
    /**
     * Test if the method allows a valid status transition from `Initializing` to `Starting` with step `LoadingDrivers`
     */
    @Test
    fun testIfMethodAllowsValidTransitionFromInitializingToStartingWithLoadingDrivers() {
        val runtimeStatus = RuntimeStatus()
        val newStatus = InternalStatus.Starting(InternalStatus.Starting.StartingStep.LoadingDrivers())
        runtimeStatus.status = newStatus
        assertEquals(newStatus, runtimeStatus.status)
    }

    /**
     * Test if the method allows a valid status transition from `Starting` with step `LoadingDrivers` to `Starting` with step `LoadingAdapters`
     */
    @Test
    fun testIfMethodAllowsValidTransitionFromStartingWithLoadingDriversToStartingWithLoadingAdapters() {
        val runtimeStatus = RuntimeStatus()
        runtimeStatus.status = InternalStatus.Starting(InternalStatus.Starting.StartingStep.LoadingDrivers())
        val newStatus = InternalStatus.Starting(InternalStatus.Starting.StartingStep.LoadingAdapters())
        runtimeStatus.status = newStatus
        assertEquals(newStatus, runtimeStatus.status)
    }

    /**
     * Test if the method throws an exception for an invalid transition from `Initializing` to `Running`
     */
    @Test
    fun testIfMethodThrowsExceptionForInvalidTransitionFromInitializingToRunning() {
        val runtimeStatus = RuntimeStatus()
        val newStatus = InternalStatus.Running()
        val exception =
            assertThrows<NotAllowedInternalStatusChange> {
                runtimeStatus.status = newStatus
            }
        assertEquals(
            "Not allowed status change from dev.kokoroidkt.core.runtime.status.InternalStatus.Initializing to dev.kokoroidkt.core.runtime.status.InternalStatus.Running",
            exception.message,
        )
    }

    /**
     * Test if the method allows a valid status transition from `Starting` with step `StartingDrivers` to `WaitForRunning`
     */
    @Test
    fun testIfMethodAllowsValidTransition() {
        val runtimeStatus = RuntimeStatus()
        try {
            listOf(
                InternalStatus.Starting(InternalStatus.Starting.StartingStep.LoadingDrivers()),
                InternalStatus.Starting(InternalStatus.Starting.StartingStep.LoadingAdapters()),
                InternalStatus.Starting(InternalStatus.Starting.StartingStep.LoadingPlugins()),
                InternalStatus.Starting(InternalStatus.Starting.StartingStep.StartingAdapters()),
                InternalStatus.Starting(InternalStatus.Starting.StartingStep.StartingDrivers()),
                InternalStatus.AfterStarting(),
                InternalStatus.Running(),
                InternalStatus.BeforeStopping(),
                InternalStatus.Stopping(InternalStatus.Stopping.StoppingStep.StoppingDrivers()),
                InternalStatus.Stopping(InternalStatus.Stopping.StoppingStep.StoppingAdapters()),
                InternalStatus.Stopping(InternalStatus.Stopping.StoppingStep.UnloadingPlugins()),
                InternalStatus.Stopping(InternalStatus.Stopping.StoppingStep.UnloadingAdapters()),
                InternalStatus.Stopping(InternalStatus.Stopping.StoppingStep.UnloadingDrivers()),
                InternalStatus.Stopped(),
            ).forEach { runtimeStatus.status = it }
            assert(true)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Test if the method throws an exception for an invalid transition from `Stopped` to `Initializing`
     */
    @Test
    fun testIfMethodThrowsExceptionForInvalidTransitionFromStoppedToInitializing() {
        val runtimeStatus = RuntimeStatus()
        runtimeStatus.status = InternalStatus.Stopped()
        val newStatus = InternalStatus.Initializing()
        val exception =
            assertThrows<NotAllowedInternalStatusChange> {
                runtimeStatus.status = newStatus
            }
        assertEquals(
            "Not allowed status change from dev.kokoroidkt.core.runtime.status.InternalStatus.Stopped to dev.kokoroidkt.core.runtime.status.InternalStatus.Initializing",
            exception.message,
        )
    }
}
