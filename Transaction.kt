class Transaction(
    val type: TransactionType,
    private val fromCurrency: String = "",
    private val toCurrency: String = "",
    val amount: Double = 0.0,
    clientId: Int,
    additionalInfo: String = "Из $fromCurrency в $toCurrency"
) {
    val clientId: Int = 0
}