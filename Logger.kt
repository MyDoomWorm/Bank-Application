import java.util.*


class Logger : Observer, java.util.Observer {
    override fun update(o: Observable?, arg: Any?) {
        if (arg is String) update(arg)
    }

    override val transactionQueue: Any
        get() = TODO("Not yet implemented")

    override fun update(message: String) {
        println("Log: $message")
    }
}