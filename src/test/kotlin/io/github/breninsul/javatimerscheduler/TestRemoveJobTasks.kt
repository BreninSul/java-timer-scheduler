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

class TestRemoveJobTasks {
    @Test
    fun testVirtualWaitJobIsPerformed() {
        testInternal(SchedulerType.VIRTUAL_WAIT, runDelay = Duration.ofSeconds(10))
    }

    @Test
    fun testVirtualNoWaitJobIsPerformed() {
        testInternal(SchedulerType.VIRTUAL_NO_WAIT, runDelay = Duration.ofSeconds(10))
    }

    @Test
    fun testCommonJobIsPerformedIfThrows() {
        testInternal(SchedulerType.THREAD_WAIT, runDelay = Duration.ofSeconds(10))
    }

    private fun testInternal(
        type: SchedulerType,
        jobDelay: Duration = Duration.ofMillis(10),
        runDelay: Duration = Duration.ofSeconds(2),
    ) {
        val counter = AtomicLong(0)
        val id1 =
            TaskSchedulerRegistry.registerTypeTask(type, "test", jobDelay.multipliedBy(2)) {
                runJob(counter)
            }
        val id2 =
            TaskSchedulerRegistry.registerTypeTask(type, "test", jobDelay.multipliedBy(2)) {
                runJob(counter)
            }
        Thread.sleep(runDelay)
        TaskSchedulerRegistry.remove(id1)
        TaskSchedulerRegistry.remove(id2)
        val counterValue1 = counter.get()
        Thread.sleep(runDelay)
        val counterValue2 = counter.get()
        Assertions.assertTrue(counterValue2 >= counterValue1, "Counter value have to be $counterValue2>=$counterValue1")
        Assertions.assertTrue(counterValue2 <= counterValue1 * 1.5, "Counter value have to be $counterValue2<=${counterValue1 * 1.5}")
    }

    private fun runJob(counter: AtomicLong) {
        Thread.sleep(Duration.ofSeconds(1))
        counter.incrementAndGet()
    }
}
