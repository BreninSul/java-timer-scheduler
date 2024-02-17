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
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * Represents a virtual timer task.
 *
 * @property name The name of the task.
 * @property counter An AtomicLong used for incrementing a count.
 * @property loggerClass A class reference used for retrieving a logger.
 * @property loggingLevel The level at which the logger should log.
 * @property runnable The actual task to be run.
 */
abstract class VirtualTimerTask(
    name: String,
    counter: AtomicLong = AtomicLong(0),
    loggerClass: KClass<*> = VirtualTimerTask::class,
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
        try {
            var thread = removeAny()
            while (thread != null) {
                thread.interrupt()
                thread = removeAny()
            }
        } catch (t: Throwable) {
            logger.log(Level.FINEST, "Error during virtual thread cancellation", t)
        }
        return super.cancel()
    }

    /**
     * Starts a new thread and manages it using this instance.
     */
    override fun run() {
        Thread.ofVirtual().name(name).start {
            val currentThread = Thread.currentThread()
            runInternal(currentThread)
        }
    }

    abstract fun runInternal(currentThread: Thread)

    protected fun addThread(thread: Thread) {
        semaphore.sync {
            threadsList.add(thread)
        }
    }

    protected fun removeThread(thread: Thread) {
        semaphore.sync {
            threadsList.remove(thread)
        }
    }

    protected fun removeAny(): Thread? {
        return semaphore.sync {
            return@sync threadsList.removeFirstOrNull()
        }
    }
}
