interface Observer {
    val transactionQueue: Any

    fun update(message: String)
}