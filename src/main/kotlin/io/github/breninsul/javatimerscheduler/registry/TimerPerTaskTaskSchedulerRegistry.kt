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

import io.github.breninsul.javatimerscheduler.scheduler.OneThreadPerTaskFixedDelayScheduler
import io.github.breninsul.javatimerscheduler.sync
import java.time.Duration
import java.util.*
import java.util.logging.Level
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * A class for a timer per task scheduler registry.
 */
open class TimerPerTaskTaskSchedulerRegistry : MapTaskSchedulerRegistry<Timer>() {
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
            val scheduler = OneThreadPerTaskFixedDelayScheduler(name, fixedRateDelay, firstDelay, loggerClass, loggingLevel, runnable)
            tasksMap[id] = scheduler
            return@sync id
        }
    }

    override fun cancelInternal(task: Timer) {
        task.cancel()
    }
}
