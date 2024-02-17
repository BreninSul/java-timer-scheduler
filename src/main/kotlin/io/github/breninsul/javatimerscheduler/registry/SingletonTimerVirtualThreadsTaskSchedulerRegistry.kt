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
import io.github.breninsul.javatimerscheduler.timer.VirtualNoWaitTimerTask
import io.github.breninsul.javatimerscheduler.timer.VirtualWaitTimerTask
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * SingletonTimerVirtualThreadsTaskSchedulerRegistry is a class that represents a registry for scheduling tasks using a singleton timer and virtual threads.
 * It extends the MapTaskSchedulerRegistry class and implements the SpecificTaskSchedulerRegistry interface.
 *
 * @property waitTillEndOfProcessBeforeNewRun A Boolean value that indicates whether the scheduler should wait for the current task execution to finish before starting a new task
 * execution.
 * @property commonTimer A Timer object that is shared among all tasks.
 *
 * @constructor Creates a SingletonTimerVirtualThreadsTaskSchedulerRegistry.
 * @param waitTillEndOfProcessBeforeNewRun A Boolean value that indicates whether the scheduler should wait for the current task execution to finish before starting a new task execution
 *.
 */
open class SingletonTimerVirtualThreadsTaskSchedulerRegistry(protected  val waitTillEndOfProcessBeforeNewRun:Boolean) : MapTaskSchedulerRegistry<TimerTask>() {
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
            val task = if(waitTillEndOfProcessBeforeNewRun) VirtualWaitTimerTask(name, AtomicLong(1), loggerClass, loggingLevel, runnable) else VirtualNoWaitTimerTask(name, AtomicLong(1), loggerClass, loggingLevel, runnable)
            commonTimer.scheduleAtFixedRate(task, firstDelay.toMillis(), fixedRateDelay.toMillis())
            tasksMap[id] = task
            return@sync id
        }
    }
}
