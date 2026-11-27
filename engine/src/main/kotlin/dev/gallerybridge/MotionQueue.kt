package dev.gallerybridge

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Reserve one slot per job. Only a finished slot is needed to dispatch the next job. */
fun <I, O> runMotionQueue(
    eligible: Iterator<I>, workers: Int, mayDispatch: () -> Boolean,
    process: (I) -> O, completed: (O) -> Unit
) {
    require(workers in 1..4)
    val pool = Executors.newFixedThreadPool(workers)
    val queue = ExecutorCompletionService<O>(pool)
    fun schedule(): Boolean {
        if (!eligible.hasNext() || !mayDispatch()) return false
        val input = eligible.next()
        queue.submit(Callable { process(input) })
        return true
    }
    try {
        var running = (0 until workers).count { schedule() }
        while (running > 0) {
            val result = queue.take().get()
            running--
            completed(result)
            if (schedule()) running++
        }
    } finally {
        pool.shutdownNow()
        pool.awaitTermination(3, TimeUnit.SECONDS)
    }
}
