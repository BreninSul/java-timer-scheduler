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

import io.github.breninsul.javatimerscheduler.sync
import io.github.breninsul.javatimerscheduler.timer.VirtualRunnableTimerTask
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * Scheduling registry for tasks to be run on a single timer, using virtual threads
 */
open class SingletonTimerVirtualThreadsTaskSchedulerRegistry : MapTaskSchedulerRegistry<TimerTask>() {
    /**
     * Common timer used for all tasks
     */
    protected open val commonTimer = Timer("virtual-thread-timer")

    override fun cancelInternal(task: TimerTask) {
        task.cancel()
    }

    override fun registerTask(
        name: String,
        fixedRateDelay: Duration,
        firstDelay: Duration,
        loggerClass: KClass<*>,
        loggingLevel: Level,
        runnable: Runnable,
    ): Long {
        return semaphore.sync {
            val id = Random.nextLong()
            val task = VirtualRunnableTimerTask(name, AtomicLong(1), loggerClass, loggingLevel, runnable)
            commonTimer.scheduleAtFixedRate(task, firstDelay.toMillis(), fixedRateDelay.toMillis())
            tasksMap[id] = task
            return@sync id
        }
    }
}
