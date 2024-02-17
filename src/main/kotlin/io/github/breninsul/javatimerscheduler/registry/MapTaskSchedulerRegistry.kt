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
import java.util.concurrent.Semaphore

/**
 * Abstract class for a specific task scheduler registry.
 * It contains a map of tasks and a semaphore for thread-safe operations.
 *
 * @constructor Creates an empty MapTaskSchedulerRegistry.
 *
 * @param T The type of the tasks to be scheduled.
 */
abstract class MapTaskSchedulerRegistry<T> : SpecificTaskSchedulerRegistry {
    /**
     * A map of tasks.
     */
    protected val tasksMap = mutableMapOf<Long, T>()

    /**
     * A semaphore for thread-safe operations.
     */
    protected val semaphore = Semaphore(1)

    override fun remove(id: Long): Boolean {
        return semaphore.sync {
            return@sync clearInternal(id)
        }
    }

    protected open fun clearInternal(id: Long): Boolean {
        val timer = tasksMap.remove(id)
        timer?.let { cancelInternal(it) }
        return timer != null
    }

    /**
     * Cancels a given task.
     *
     * @param task The task to cancel
     */
    abstract fun cancelInternal(task: T)

    override fun clear() {
        semaphore.sync {
            tasksMap.keys.forEach { clearInternal(it) }
            tasksMap.clear()
        }
    }
}
