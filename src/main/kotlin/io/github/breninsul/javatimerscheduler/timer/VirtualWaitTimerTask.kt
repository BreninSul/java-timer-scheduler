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
 * Class that represents a new virtual any run timer task.
 *
 * @param name The name of the task.
 * @param counter An AtomicLong used for incrementing a count.
 * @param loggerClass A class reference used for retrieving a logger.
 * @param loggingLevel The level at which the logger should log.
 * @param runnable The actual task to be run.
 */
open class VirtualWaitTimerTask(
    name: String,
    counter: AtomicLong = AtomicLong(0),
    loggerClass: KClass<*> = VirtualWaitTimerTask::class,
    loggingLevel: Level = Level.FINEST,
    runnable: Runnable,
) : VirtualTimerTask(name, counter, loggerClass, loggingLevel, runnable) {
    protected open val taskSemaphore = Semaphore(1)

    override fun runInternal(currentThread: Thread) {
        taskSemaphore.sync {
            addThread(currentThread)
            runnable.run()
            removeThread(currentThread)
        }
    }
}
