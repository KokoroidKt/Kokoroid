/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.state

import dev.kokoroidkt.core.constants.ExitStatus

sealed class InternalState {
    override fun toString(): String = this::class.qualifiedName.toString()

    abstract fun checkIsAllowChangeStateTo(next: InternalState): Boolean

    class Initializing : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean =
            (
                next is Starting &&
                    next.step is Starting.StartingStep.LoadingDrivers
            ) || next is Stopped ||
                next is Initializing
    }

    class Starting(
        val step: StartingStep,
    ) : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = step.checkIsAllowChangeStateTo(next)

        sealed class StartingStep {
            abstract fun checkIsAllowChangeStateTo(next: InternalState): Boolean

            class LoadingDrivers : StartingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is Starting && next.step is LoadingAdapters
            }

            class LoadingAdapters : StartingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is Starting && next.step is LoadingPlugins
            }

            class LoadingPlugins : StartingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is Starting && next.step is StartingAdapters
            }

            class StartingAdapters : StartingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is Starting && next.step is StartingDrivers
            }

            class StartingDrivers : StartingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is AfterStarting
            }
        }

        override fun toString(): String = (this::class.qualifiedName + "(${step::class.qualifiedName})")
    }

    class AfterStarting : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is Running || next is BeforeStopping
    }

    class Running : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is BeforeStopping
    }

    class BeforeStopping : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean =
            next is Stopping && next.step is Stopping.StoppingStep.StoppingDrivers
    }

    class Stopping(
        val step: StoppingStep,
    ) : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = step.checkIsAllowChangeStateTo(next)

        sealed class StoppingStep {
            abstract fun checkIsAllowChangeStateTo(next: InternalState): Boolean

            class StoppingDrivers : StoppingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean =
                    next is Stopping && next.step is StoppingStep.StoppingAdapters
            }

            class StoppingAdapters : StoppingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean =
                    next is Stopping && next.step is StoppingStep.UnloadingPlugins
            }

            class UnloadingPlugins : StoppingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean =
                    next is Stopping && next.step is StoppingStep.UnloadingAdapters
            }

            class UnloadingAdapters : StoppingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean =
                    next is Stopping && next.step is StoppingStep.UnloadingDrivers
            }

            class UnloadingDrivers : StoppingStep() {
                override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = next is Stopped
            }
        }

        override fun toString(): String = (this::class.qualifiedName + "(${step::class.qualifiedName})")
    }

    class Stopped(
        val statusCode: Int = ExitStatus.SUCCESS_EXIT,
    ) : InternalState() {
        override fun checkIsAllowChangeStateTo(next: InternalState): Boolean = false
    }
}
