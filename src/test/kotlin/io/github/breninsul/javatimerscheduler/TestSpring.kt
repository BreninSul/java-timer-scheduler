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

import io.github.breninsul.javatimerscheduler.autoconfigure.SpringDynamicScheduleRegistry
import io.github.breninsul.javatimerscheduler.autoconfigure.SpringDynamicSchedulerAutoconfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

@ExtendWith(SpringExtension::class)
@EnableScheduling
@Import(SpringDynamicSchedulerAutoconfiguration::class)
class TestSpring {
    @Test
    fun testTask() {
        val atomicLong = AtomicLong(0)
        SpringDynamicScheduleRegistry.registerCron("*/1 * * * * *") { atomicLong.incrementAndGet() }
        Thread.sleep(Duration.ofSeconds(10))
        val counter = atomicLong.get()
        Assertions.assertTrue(counter > 8, "Have to be more then ${8} current $counter")
        Assertions.assertTrue(counter < 12, "Have to be less then ${12} current $counter")
    }
}
