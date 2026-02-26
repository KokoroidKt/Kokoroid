/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.status

import dev.kokoroidkt.core.constants.ExitStatus

sealed class InternalStatus {
    override fun toString(): String = this::class.qualifiedName.toString()

    abstract fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean

    class Initializing : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean =
            (
                next is Starting &&
                    next.step is Starting.StartingStep.LoadingDrivers
            ) || next is Stopped ||
                next is Initializing
    }

    class Starting(
        val step: StartingStep,
    ) : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = step.checkIsAllowChangeStatusTo(next)

        sealed class StartingStep {
            abstract fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean

            class LoadingDrivers : StartingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is Starting && next.step is LoadingAdapters
            }

            class LoadingAdapters : StartingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is Starting && next.step is LoadingPlugins
            }

            class LoadingPlugins : StartingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is Starting && next.step is StartingAdapters
            }

            class StartingAdapters : StartingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is Starting && next.step is StartingDrivers
            }

            class StartingDrivers : StartingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is AfterStarting
            }
        }

        override fun toString(): String = (this::class.qualifiedName + "(${step::class.qualifiedName})")
    }

    class AfterStarting : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is Running || next is BeforeStopping
    }

    class Running : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is BeforeStopping
    }

    class BeforeStopping : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean =
            next is Stopping && next.step is Stopping.StoppingStep.StoppingDrivers
    }

    class Stopping(
        val step: StoppingStep,
    ) : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = step.checkIsAllowChangeStatusTo(next)

        sealed class StoppingStep {
            abstract fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean

            class StoppingDrivers : StoppingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean =
                    next is Stopping && next.step is StoppingStep.StoppingAdapters
            }

            class StoppingAdapters : StoppingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean =
                    next is Stopping && next.step is StoppingStep.UnloadingPlugins
            }

            class UnloadingPlugins : StoppingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean =
                    next is Stopping && next.step is StoppingStep.UnloadingAdapters
            }

            class UnloadingAdapters : StoppingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean =
                    next is Stopping && next.step is StoppingStep.UnloadingDrivers
            }

            class UnloadingDrivers : StoppingStep() {
                override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = next is Stopped
            }
        }

        override fun toString(): String = (this::class.qualifiedName + "(${step::class.qualifiedName})")
    }

    class Stopped(
        val statusCode: Int = ExitStatus.SUCCESS_EXIT,
    ) : InternalStatus() {
        override fun checkIsAllowChangeStatusTo(next: InternalStatus): Boolean = false
    }
}
