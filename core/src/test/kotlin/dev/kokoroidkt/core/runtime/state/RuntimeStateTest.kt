/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.state

import dev.kokoroidkt.core.exceptions.state.NotAllowedInternalStateChange
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RuntimeStateTest {
    /**
     * Test if the method allows a valid status transition from `Initializing` to `Starting` with step `LoadingDrivers`
     */
    @Test
    fun testIfMethodAllowsValidTransitionFromInitializingToStartingWithLoadingDrivers() {
        val runtimeState = RuntimeState()
        val newStatus = InternalState.Starting(InternalState.Starting.StartingStep.LoadingDrivers())
        runtimeState.state = newStatus
        assertEquals(newStatus, runtimeState.state)
    }

    /**
     * Test if the method allows a valid status transition from `Starting` with step `LoadingDrivers` to `Starting` with step `LoadingAdapters`
     */
    @Test
    fun testIfMethodAllowsValidTransitionFromStartingWithLoadingDriversToStartingWithLoadingAdapters() {
        val runtimeState = RuntimeState()
        runtimeState.state = InternalState.Starting(InternalState.Starting.StartingStep.LoadingDrivers())
        val newStatus = InternalState.Starting(InternalState.Starting.StartingStep.LoadingAdapters())
        runtimeState.state = newStatus
        assertEquals(newStatus, runtimeState.state)
    }

    /**
     * Test if the method throws an exception for an invalid transition from `Initializing` to `Running`
     */
    @Test
    fun testIfMethodThrowsExceptionForInvalidTransitionFromInitializingToRunning() {
        val runtimeState = RuntimeState()
        val newStatus = InternalState.Running()
        val exception =
            assertThrows<NotAllowedInternalStateChange> {
                runtimeState.state = newStatus
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
        val runtimeState = RuntimeState()
        try {
            listOf(
                InternalState.Starting(InternalState.Starting.StartingStep.LoadingDrivers()),
                InternalState.Starting(InternalState.Starting.StartingStep.LoadingAdapters()),
                InternalState.Starting(InternalState.Starting.StartingStep.LoadingPlugins()),
                InternalState.Starting(InternalState.Starting.StartingStep.StartingAdapters()),
                InternalState.Starting(InternalState.Starting.StartingStep.StartingDrivers()),
                InternalState.AfterStarting(),
                InternalState.Running(),
                InternalState.BeforeStopping(),
                InternalState.Stopping(InternalState.Stopping.StoppingStep.StoppingDrivers()),
                InternalState.Stopping(InternalState.Stopping.StoppingStep.StoppingAdapters()),
                InternalState.Stopping(InternalState.Stopping.StoppingStep.UnloadingPlugins()),
                InternalState.Stopping(InternalState.Stopping.StoppingStep.UnloadingAdapters()),
                InternalState.Stopping(InternalState.Stopping.StoppingStep.UnloadingDrivers()),
                InternalState.Stopped(),
            ).forEach { runtimeState.state = it }
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
        val runtimeState = RuntimeState()
        runtimeState.state = InternalState.Stopped()
        val newStatus = InternalState.Initializing()
        val exception =
            assertThrows<NotAllowedInternalStateChange> {
                runtimeState.state = newStatus
            }
        assertEquals(
            "Not allowed status change from dev.kokoroidkt.core.runtime.status.InternalStatus.Stopped to dev.kokoroidkt.core.runtime.status.InternalStatus.Initializing",
            exception.message,
        )
    }
}
