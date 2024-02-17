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
import java.time.Duration
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * TaskSchedulerRegistry is a class that acts as a registry for scheduling tasks.
 * It extends MapTaskSchedulerRegistry and provides implementations for registering and removing tasks.
 * It supports different types of schedulers, such as TimerPerTask and SingletonVirtualThreads.
 */
open class TaskSchedulerRegistry : MapTaskSchedulerRegistry<SchedulerType>() {
    /**
     * A TaskSchedulerRegistry specific to virtual threads tasks.
     */
    protected open val virtualThreadsTaskSchedulerRegistry = SingletonTimerVirtualThreadsTaskSchedulerRegistry()

    /**
     * A TaskSchedulerRegistry specific to timer per task tasks.
     */
    protected open val timerPerTaskTaskSchedulerRegistry = TimerPerTaskTaskSchedulerRegistry()

    /**
     * The default task type that the TaskSchedulerRegistry will use if no type is supplied on task registration.
     */
    protected open val defaultTaskType = SchedulerType.SINGLETON_VIRTUAL_THREADS

    /**
     * Register a new task with a specific SchedulerType.
     *
     * If no SchedulerType is provided, defaultTaskType is used.
     *
     * @param type The SchedulerType of the new task.
     * @param name The name of the new task.
     * @param fixedRateDelay The delay duration for fixed-rate execution.
     * @param firstDelay The initial delay before the task execution starts.
     * @param loggerClass The logging class.
     * @param loggingLevel The level of the log.
     * @param runnable The task that needs to be executed.
     * @return The id of the registered task.
     */
    fun registerTypeTask(
        type: SchedulerType = defaultTaskType,
        name: String = "timer-task",
        fixedRateDelay: Duration,
        firstDelay: Duration = fixedRateDelay,
        loggerClass: KClass<*> = this::class,
        loggingLevel: Level = Level.FINEST,
        runnable: Runnable,
    ): Long {
        return semaphore.sync {
            val id =
                when (type) {
                    SchedulerType.SINGLETON_VIRTUAL_THREADS -> virtualThreadsTaskSchedulerRegistry.registerTask(name, fixedRateDelay, firstDelay, loggerClass, loggingLevel, runnable)
                    SchedulerType.TIMER_PER_TASK -> timerPerTaskTaskSchedulerRegistry.registerTask(name, fixedRateDelay, firstDelay, loggerClass, loggingLevel, runnable)
                }
            tasksMap[id] = type
            return@sync id
        }
    }

    override fun registerTask(
        name: String,
        fixedRateDelay: Duration,
        firstDelay: Duration,
        loggerClass: KClass<*>,
        loggingLevel: Level,
        runnable: Runnable,
    ): Long {
        return registerTypeTask(defaultTaskType, name, fixedRateDelay, firstDelay, loggerClass, loggingLevel, runnable)
    }

    override fun remove(id: Long): Boolean {
        return semaphore.sync {
            return@sync clearInternal(id)
        }
    }

    override fun clearInternal(id: Long): Boolean {
        val type = tasksMap.remove(id)
        return when (type) {
            SchedulerType.SINGLETON_VIRTUAL_THREADS -> virtualThreadsTaskSchedulerRegistry.remove(id)
            SchedulerType.TIMER_PER_TASK -> timerPerTaskTaskSchedulerRegistry.remove(id)
            null -> false
        }
    }

    override fun cancelInternal(task: SchedulerType) {
    }

    override fun clear() {
        semaphore.sync {
            virtualThreadsTaskSchedulerRegistry.clear()
            timerPerTaskTaskSchedulerRegistry.clear()
            tasksMap.clear()
        }
    }

// ////////////////////////////////////////////////////////
// Static Members (Meant to be accessed by all instances) //
// ////////////////////////////////////////////////////////

    /**
     * A global instance of TaskSchedulerRegistry.
     *
     * This instance can be used globally for all callers who need a TaskSchedulerRegistry.
     */
    companion object : TaskSchedulerRegistry()
}
