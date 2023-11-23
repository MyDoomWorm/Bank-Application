import java.util.*

// ...
class Cashier(val id: Int, val bank: Bank, logger: Logger, override val transactionQueue: Any) : Thread(), Observer, java.util.Observer {

    init {
        bank.addObserver(logger)
    }

    override fun update(message: String) {
        println("Касса $id получила обновление: $message")
    }

    fun deposit(clientId: Int, amount: Double) {
        val client = bank.clients[clientId]
        if (client != null) {
            synchronized(client) {
                client.balance += amount
                bank.notifyObservers("Внесение средст: Клиент $clientId внес $amount ${client.currency}. Новый баланс: ${client.balance}")

                val depositTransaction = Transaction(
                    type = TransactionType.DEPOSIT,
                    clientId = clientId,
                    amount = amount
                )
                bank.transactionQueue.add(depositTransaction)
            }
        } else {
            bank.notifyObservers("Внесение средст: Клиент $clientId не найден.")
        }
    }

    override fun run() {
        while (true) {
            try {
                val transaction = bank.transactionQueue.take()
                processTransaction(transaction)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun processTransaction(transaction: Transaction) {
        when (transaction.type) {
            TransactionType.DEPOSIT -> {
                deposit(transaction.clientId, transaction.amount)
                bank.notifyObservers("Транзакция обработана: Депозит для клиента ${transaction.clientId}")
            }
            TransactionType.WITHDRAW -> {
                // обработка снятия средств
            }
            TransactionType.EXCHANGE_CURRENCY -> {
                // обработка обмена валюты
            }
            TransactionType.TRANSFER_FUNDS -> {
                // обработка перевода средств
            }
        }
    }

    fun withdraw(clientId: Int, amount: Double) {
        val client = bank.clients[clientId]
        if (client != null) {
            synchronized(client) {
                if (client.balance >= amount) {
                    client.balance -= amount
                    bank.notifyObservers("Снятие средств: Клиент $clientId снял $amount ${client.currency}. Новый баланс: ${client.balance}")

                    val withdrawalTransaction = Transaction(
                        type = TransactionType.WITHDRAW,
                        clientId = clientId,
                        amount = amount
                    )
                    bank.transactionQueue.add(withdrawalTransaction)
                } else {
                    bank.notifyObservers("Снятие средств: Недостаточно средств у клиента $clientId.")
                }
            }
        } else {
            bank.notifyObservers("Снятие средств: Клиент $clientId не найден.")
        }
    }

    fun exchangeCurrency(clientId: Int, fromCurrency: String, toCurrency: String, amount: Double): Boolean {
        val client = bank.clients[clientId] ?: return false

        return synchronized(client) {
            if (client.balance < amount || !bank.exchangeRates.containsKey(fromCurrency) || !bank.exchangeRates.containsKey(toCurrency)) {
                false
            } else {
                val exchangeRate = bank.exchangeRates[toCurrency]!! / bank.exchangeRates[fromCurrency]!!
                val convertedAmount = amount * exchangeRate

                client.balance -= amount
                client.balance += convertedAmount

                val exchangeTransaction = Transaction(
                    type = TransactionType.EXCHANGE_CURRENCY,
                    clientId = clientId,
                    amount = amount,
                    additionalInfo = "Из $fromCurrency в $toCurrency"
                )
                bank.transactionQueue.add(exchangeTransaction)

                true
            }
        }
    }

    fun transferFunds(senderId: Int, receiverId: Int, amount: Double): Boolean {
        val sender = bank.clients[senderId]
        val receiver = bank.clients[receiverId]

        return if (sender != null && receiver != null) {
            synchronized(sender) {
                if (sender.balance >= amount) {
                    sender.balance -= amount
                    receiver.balance += amount

                    val transferTransaction = Transaction(
                        type = TransactionType.TRANSFER_FUNDS,
                        clientId = senderId,
                        amount = amount,
                        additionalInfo = "Клиенту $receiverId"
                    )
                    bank.transactionQueue.add(transferTransaction)

                    true
                } else {
                    false
                }
            }
        } else {
            false
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        TODO("Not yet implemented")
    }
}