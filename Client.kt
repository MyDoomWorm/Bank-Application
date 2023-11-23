data class Client(val id: Int, var balance: Double, var currency: String)
val transaction = Transaction(
    type = TransactionType.DEPOSIT,
    clientId = 123,
    amount = 100.0
)