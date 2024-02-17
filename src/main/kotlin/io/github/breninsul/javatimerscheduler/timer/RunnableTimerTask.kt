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

import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * Class that represents a runnable timer task.
 *
 * @property name The name of the task.
 * @property counter An AtomicLong used for incrementing a count.
 * @property loggerClass A class reference used for retrieving a logger.
 * @property loggingLevel The level at which the logger should log.
 * @property runnable The actual task to be run.
 */
open class RunnableTimerTask(
    protected val name: String,
    protected val counter: AtomicLong = AtomicLong(0),
    protected val loggerClass: KClass<*> = RunnableTimerTask::class,
    protected val loggingLevel: Level = Level.FINEST,
    protected val runnable: Runnable,
) : TimerTask() {
    /**
     * The logger for this class.
     */
    protected open val logger = Logger.getLogger(loggerClass.java.name)

    /**
     * Runs the task and logs the duration and any potential exceptions.
     */
    override fun run() {
        val time = System.currentTimeMillis()
        val errorText =
            try {
                runnable.run()
                ""
            } catch (t: Throwable) {
                "Exception executing Task $name. ${t.javaClass}:${t.message}"
            }
        logger.log(loggingLevel, "$name job №${counter.getAndIncrement()} took ${System.currentTimeMillis() - time}ms. $errorText")
    }
}
