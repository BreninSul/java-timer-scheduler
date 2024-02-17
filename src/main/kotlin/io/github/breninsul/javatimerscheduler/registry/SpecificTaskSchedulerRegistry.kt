/*
 * MIT License
 *
 * Copyright (c) 2024 BreninSul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.breninsul.javatimerscheduler.registry

import java.time.Duration
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * This interface represents a registry for specific task scheduling.
 */
interface SpecificTaskSchedulerRegistry {
    /**
     * Registers a task with custom parameters.
     *
     * @param name The name of the task (default is "timer-task").
     * @param fixedRateDelay The fixed rate delay for the task to be repeated.
     * @param firstDelay The initial delay before the task starts, default is set to be same as fixedRateDelay.
     * @param loggerClass The class where the logger is located, default is the current class.
     * @param loggingLevel The level of logging for this task (default is Level.FINEST).
     * @param runnable The Runnable object which includes the task logic to be executed.
     * @return The task ID as a Long.
     */
    fun registerTask(
        name: String = "timer-task",
        fixedRateDelay: Duration,
        firstDelay: Duration = fixedRateDelay,
        loggerClass: KClass<*> = this::class,
        loggingLevel: Level = Level.FINEST,
        runnable: Runnable,
    ): Long

    /**
     * Removes a task from the registry.
     *
     * @param id The ID of the task to be removed.
     * @return Boolean value indicating whether the operation was successful.
     */
    fun remove(id: Long): Boolean

    /**
     * Clears all the tasks from the registry.
     */
    fun clear()
}
