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

package io.github.breninsul.javatimerscheduler.autoconfigure

import org.springframework.scheduling.Trigger
import org.springframework.scheduling.config.*
import java.time.Duration

/**
 * A registry for configuring scheduled tasks dynamically in Spring.
 */
object SpringDynamicScheduleRegistry {
    var registrar: ScheduledTaskRegistrar? = null

    /**
     * Set ScheduledTaskRegistrar for this registry
     * @param reg ScheduledTaskRegistrar
     */
    fun SpringDynamicScheduleRegistry.setRegistrar(reg: ScheduledTaskRegistrar) {
        registrar = reg
    }

    /**
     * Register a cron task
     * @param cron cron expression
     * @param task task to be scheduled
     */
    fun registerCron(
        cron: String,
        task: Runnable,
    ) {
        registerCron(CronTask(task, cron))
    }

    /**
     * Register a cron task
     * @param task CronTask to be scheduled
     */
    fun registerCron(task: CronTask) {
        registrar().scheduleCronTask(task)
    }

    /**
     * Register a task with fixed delay
     * @param delay delay duration
     * @param firstDelay initial delay duration(default is same as delay)
     * @param task task to be scheduled
     */
    fun registerFixedDelay(
        delay: Duration,
        firstDelay: Duration = delay,
        task: Runnable,
    ) {
        registerFixedDelay(FixedDelayTask(task, delay, firstDelay))
    }

    /**
     * Register a task with fixed delay
     * @param task FixedDelayTask to be scheduled
     */
    fun registerFixedDelay(task: FixedDelayTask) {
        registrar().scheduleFixedDelayTask(task)
    }

    /**
     * Register a task with fixed rate
     * @param delay rate duration
     * @param firstDelay initial delay duration(default is same as delay)
     * @param task task to be scheduled
     */
    fun registerFixedRate(
        delay: Duration,
        firstDelay: Duration = delay,
        task: Runnable,
    ) {
        registerFixedRate(FixedRateTask(task, delay, firstDelay))
    }

    /**
     * Register a task with fixed rate
     * @param task FixedRateTask to be scheduled
     */
    fun registerFixedRate(task: FixedRateTask) {
        registrar().scheduleFixedRateTask(task)
    }

    /**
     * Register a task with custom trigger
     * @param trigger trigger for execution
     * @param task task to be scheduled
     */
    fun registerTriggered(
        trigger: Trigger,
        task: Runnable,
    ) {
        registerTriggered(TriggerTask(task, trigger))
    }

    /**
     * Register a task with custom trigger
     * @param task TriggerTask to be scheduled
     */
    fun registerTriggered(task: TriggerTask) {
        registrar().scheduleTriggerTask(task)
    }

    private fun registrar(): ScheduledTaskRegistrar {
        if (registrar == null) {
            throw IllegalStateException("ScheduledTaskRegistrar is not set for SpringDynamicScheduleRegistry")
        }
        return registrar!!
    }
}
