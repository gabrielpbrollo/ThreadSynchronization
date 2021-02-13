class ThreadSynchronization {

    companion object {
        private const val MAX_VALUE = 100
        private val LOCK = Object()

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
                synchronized(LOCK) {
                    if (consumedValue != produced) {
                        consumedValue = produced
                        println("CONSUMED $produced")
                    }

                    LOCK.notify()
                    if (produced == MAX_VALUE) return@Thread
                }
            }
        }.apply { start() }

        private fun produce() = Thread {
            while (true) {
                synchronized(LOCK) {
                    produced++
                    println("PRODUCED $produced")

                    LOCK.wait()
                    if (produced == MAX_VALUE) return@Thread
                }
            }
        }.apply { start() }
    }
}