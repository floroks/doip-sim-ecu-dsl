import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

open class SimulatedEcu(val config: EcuConfig) {
    protected val logger: Logger = LoggerFactory.getLogger(SimulatedEcu::class.java)

    private val isBusy: AtomicBoolean = AtomicBoolean(false)

    open fun handleRequest(request: UdsMessage) {
    }

    open fun handleRequestIfBusy(request: UdsMessage) {
        // Busy NRC
        request.respond(byteArrayOf(0x7f, request.message[0], 0x21))
    }

    open fun onIncomingUdsMessage(request: UdsMessage) {
        return if (isBusy.compareAndSet(false, true)) {
            try {
                handleRequest(request)
            } finally {
                isBusy.set(false)
            }
        } else {
            handleRequestIfBusy(request)
        }
    }
}
