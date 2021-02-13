import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ThreadSynchronizationWithReentrantLock {

    companion object {
        private const val MAX_VALUE = 100
        private val LOCK = ReentrantLock()
        private val condition = LOCK.newCondition()

        private var produced = 0

        @JvmStatic
        fun main(args: Array<String>) {
            val con = consume()

            val prod = produce()

            prod.join()
            con.join()
        }

        private fun consume() = Thread {
            var consumedValue = 0

            while (true) {
                LOCK.withLock {
                    if (consumedValue != produced) {
                        consumedValue = produced
                        println("CONSUMED $produced")
                    }

                    condition.signal()
                    if (produced == MAX_VALUE) return@Thread
                }
            }
        }.apply { start() }

        private fun produce() = Thread {
            while (true) {
                LOCK.withLock {
                    produced++
                    println("PRODUCED $produced")

                    condition.await()
                    if (produced == MAX_VALUE) return@Thread
                }
            }
        }.apply { start() }
    }
}