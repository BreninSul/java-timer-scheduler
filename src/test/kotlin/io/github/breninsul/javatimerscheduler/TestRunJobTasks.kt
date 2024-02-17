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

package io.github.breninsul.javatimerscheduler

import io.github.breninsul.javatimerscheduler.registry.SchedulerType
import io.github.breninsul.javatimerscheduler.registry.TaskSchedulerRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

class TestRunJobTasks {
    @Test
    fun testVirtualJobIsPerformed() {
        testInternal(SchedulerType.VIRTUAL_NO_WAIT)
    }

    @Test
    fun testVirtualJobIsPerformedIfThrows() {
        testInternal(SchedulerType.VIRTUAL_NO_WAIT, tryException = true)
    }
    @Test
    fun testVirtuaWaitlJobIsPerformed() {
        testInternal(SchedulerType.VIRTUAL_WAIT, moreEqThen = 14, lessEqThen = 22)
    }

    @Test
    fun testVirtuaWaitlJobIsPerformedIfThrows() {
        testInternal(SchedulerType.VIRTUAL_WAIT, moreEqThen = 14, lessEqThen = 22, tryException = true)
    }

    @Test
    fun testCommonJobIsPerformed() {
        testInternal(SchedulerType.THREAD_WAIT, moreEqThen = 14, lessEqThen = 22)
    }

    @Test
    fun testCommonJobIsPerformedIfThrows() {
        testInternal(SchedulerType.THREAD_WAIT, moreEqThen = 14, lessEqThen = 22, tryException = true)
    }

    private fun testInternal(
        type: SchedulerType,
        jobDelay: Duration = Duration.ofMillis(10),
        runDelay: Duration = Duration.ofSeconds(10),
        sleep:Duration = Duration.ofSeconds(1),
        moreEqThen: Int = 700,
        lessEqThen: Int = 1100,
        tryException: Boolean = false,
    ) {
        val counter = AtomicLong(0)
        TaskSchedulerRegistry.registerTypeTask(type, "test", jobDelay.multipliedBy(2)) {
            if (tryException)runJobWithRandomExceptions(sleep,counter) else runJob(sleep,counter)
        }
        TaskSchedulerRegistry.registerTypeTask(type, "test", jobDelay.multipliedBy(2)) {
            if (tryException)runJobWithRandomExceptions(sleep,counter) else runJob(sleep,counter)
        }
        Thread.sleep(runDelay)
        val counterValue = counter.get()
        Assertions.assertTrue(counterValue >= moreEqThen, "Counter value have to be $counterValue>=$moreEqThen")
        Assertions.assertTrue(counterValue <= lessEqThen, "Counter value have to be $counterValue<=$lessEqThen")
        println(counterValue)
    }

    private fun runJobWithRandomExceptions(sleep:Duration,counter: AtomicLong) {
        runJob(sleep,counter)
        if (Random.nextInt(10) == 5) {
            throw RuntimeException("Test exception")
        }
    }

    private fun runJob(sleep:Duration,counter: AtomicLong) {
        Thread.sleep(sleep)
        counter.incrementAndGet()
    }
}
