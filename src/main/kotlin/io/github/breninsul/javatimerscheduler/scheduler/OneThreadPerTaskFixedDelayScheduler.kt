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

package io.github.breninsul.javatimerscheduler.scheduler

import io.github.breninsul.javatimerscheduler.timer.RunnableTimerTask
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * [OneThreadPerTaskFixedDelayScheduler] Responsibilities:
 * Schedules a task to run at a fixed rate delay.
 *
 * @property name Name of the task.
 * @property fixedRateDelay Fixed delay rate at which task is scheduled to run.
 * @property firstDelay Initial delay before task is run for the first time.
 * @property loggerClass Class used for logging.
 * @property loggingLevel Level at which logging is done.
 * @property runnable Task that needs to be run.
 *
 * @constructor constructs and initializes the scheduler.
 */
open class OneThreadPerTaskFixedDelayScheduler(
    val name: String,
    val fixedRateDelay: Duration,
    val firstDelay: Duration,
    protected val loggerClass: KClass<*>,
    protected val loggingLevel: Level = Level.FINEST,
    protected val runnable: Runnable,
) : Timer(name) {
    /**
     * Atomic counter for tasks handled by this scheduler.
     */
    protected open val counter get() = AtomicLong(1)

    /**
     * Initialize scheduler on instantiation.
     */
    init {
        init()
    }

    /**
     * Wrapper function to initiate the scheduler.
     */
    private fun init() {
        initTimer()
    }

    /**
     * Function to initialize the Timer with a [RunnableTimerTask] and schedule it.
     *
     * @return Initialized timer.
     */
    protected open fun initTimer(): Timer {
        val task = RunnableTimerTask(name, counter, loggerClass, loggingLevel, runnable)
        scheduleAtFixedRate(task, firstDelay.toMillis(), fixedRateDelay.toMillis())
        return this
    }
}
