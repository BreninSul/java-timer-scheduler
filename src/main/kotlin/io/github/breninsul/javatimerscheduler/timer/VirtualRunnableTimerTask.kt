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

package io.github.breninsul.javatimerscheduler.timer

import io.github.breninsul.javatimerscheduler.sync
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * A class that represents a virtual runnable timer task.
 * This class manages and schedules a set of threads for execution.
 *
 * @property name The name of the virtual runnable timer task.
 * @property counter An atomic counter used across tasks.
 * @property loggerClass The class used for logging.
 * @property loggingLevel The level at which to log messages.
 * @property runnable The runnable task scheduled for execution.
 */
open class VirtualRunnableTimerTask(
    name: String,
    counter: AtomicLong = AtomicLong(0),
    loggerClass: KClass<*> = VirtualRunnableTimerTask::class,
    loggingLevel: Level = Level.FINEST,
    runnable: Runnable,
) : RunnableTimerTask(name, counter, loggerClass, loggingLevel, runnable) {
    /**
     * A list of threads under management by this instance.
     */
    protected open val threadsList: MutableList<Thread> = mutableListOf()

    /**
     * A semaphore to ensure thread-safe access to the list of threads.
     */
    protected open val semaphore = Semaphore(1)

    /**
     * Cancels the timer task, interrupting all managed threads.
     *
     * @return a boolean indicating if cancellation was successful.
     */
    override fun cancel(): Boolean {
        semaphore.sync {
            threadsList.forEach {
                try {
                    it.interrupt()
                } catch (t: Throwable) {
                    logger.log(Level.FINEST, "Error during virtual thread cancellation", t)
                }
            }
        }
        return super.cancel()
    }

    /**
     * Starts a new thread and manages it using this instance.
     */
    override fun run() {
        Thread.ofVirtual().name(name).start {
            val currentThread = Thread.currentThread()
            semaphore.sync {
                threadsList.add(currentThread)
            }
            super.run()
            semaphore.sync {
                threadsList.remove(currentThread)
            }
        }
    }
}
