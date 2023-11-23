import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.Observer
import java.util.concurrent.ScheduledThreadPoolExecutor

class Bank : Observer {
    val clients = ConcurrentHashMap<Int, Client>()
    val exchangeRates = ConcurrentHashMap<String, Double>()
    val transactionQueue = LinkedBlockingQueue<Transaction>()
    private val observers = mutableListOf<java.util.Observer>()

    init {
        exchangeRates["USD"] = getRandomExchangeRate()
        val executor = ScheduledThreadPoolExecutor(1)
        executor.scheduleAtFixedRate({
            exchangeRates["USD"] = getRandomExchangeRate()
            notifyObservers("Курсы валют обновлены: $exchangeRates")
        }, 0, 1, TimeUnit.HOURS)

        // клиент для перевода средст
        val additionalClient = Client(2, 10.0, "USD")
        addClient(additionalClient)
    }

    fun addClient(client: Client) {
        clients[client.id] = client
    }

    fun addObserver(cashier: Logger) {
        observers.add(cashier)
    }

    fun notifyObservers(message: String) {
        observers.forEach { it.update(null, message) }
    }

    override fun update(o: java.util.Observable?, message: Any?) {
        println("Обновление банка: $message")
    }


    fun deposit(clientId: Int, amount: Double, currency: String) {
        val client = clients[clientId]
        if (client != null) {
            synchronized(client) {
                client.balance += amount
                notifyObservers("Внесение средст: Клиент $clientId внес $amount $currency. Новый баланс: ${client.balance}")

                val depositTransaction = Transaction(
                    type = TransactionType.DEPOSIT,
                    clientId = clientId,
                    amount = amount
                )
                transactionQueue.add(depositTransaction)
            }
        } else {
            notifyObservers("Внесение средств: Клиент $clientId не найден.")
        }
    }
    fun withdraw(i: Int, d: Double, s: String) {
        val client = clients[i]
        if (client != null) {
            synchronized(client) {
                if (client.balance >= d) {
                    client.balance -= d
                    notifyObservers("Снятие: Клиент $i снял $d $s. Новый баланс: ${client.balance}")

                    val withdrawalTransaction = Transaction(
                        type = TransactionType.WITHDRAW,
                        clientId = i,
                        amount = d
                    )
                    transactionQueue.add(withdrawalTransaction)
                } else {
                    notifyObservers("Снятие: Недостаточно средств у клиента $i.")
                }
            }
        } else {
            notifyObservers("Снятие: Клиент $i не найден.")
        }
    }

    fun exchangeCurrency(clientId: Int, fromCurrency: String, toCurrency: String, amount: Double) {
        val client = clients[clientId]
        if (client != null) {
            synchronized(client) {
                if (client.balance >= amount && exchangeRates.containsKey(fromCurrency) && exchangeRates.containsKey(toCurrency)) {
                    val exchangeRate = exchangeRates[toCurrency]!! / exchangeRates[fromCurrency]!!
                    val convertedAmount = amount * exchangeRate

                    client.balance -= amount
                    client.balance += convertedAmount

                    notifyObservers("Валютный обмен: Клиент $clientId обменял $amount $fromCurrency на $convertedAmount $toCurrency. Новый баланс: ${client.balance}")

                    val exchangeTransaction = Transaction(
                        type = TransactionType.EXCHANGE_CURRENCY,
                        clientId = clientId,
                        amount = amount,
                        additionalInfo = "Из $fromCurrency в $toCurrency"
                    )
                    transactionQueue.add(exchangeTransaction)
                } else {
                    notifyObservers("Валютный обмен: Недостаточно средств или некорректные валюты.")
                }
            }
        } else {
            notifyObservers("Валютный обмен: Клиент $clientId не найден.")
        }
    }

    fun transferFunds(senderId: Int, receiverId: Int, amount: Double) {
        val sender = clients[senderId]
        val receiver = clients[receiverId]

        if (sender != null && receiver != null) {
            synchronized(sender) {
                if (sender.balance >= amount) {
                    sender.balance -= amount
                    receiver.balance += amount

                    notifyObservers("Перевод средств: Клиент $senderId перевел $amount $receiverId клиенту $receiverId. Новый баланс отправителя: ${sender.balance}. Новый баланс получателя: ${receiver.balance}")

                    val transferTransaction = Transaction(
                        type = TransactionType.TRANSFER_FUNDS,
                        clientId = senderId,
                        amount = amount,
                        additionalInfo = "Клиенту $receiverId"
                    )
                    transactionQueue.add(transferTransaction)
                } else {
                    notifyObservers("Перевод средств: Недостаточно средств у клиента $senderId.")
                }
            }
        } else {
            notifyObservers("Перевод средств: Клиент $senderId или $receiverId не найден.")
        }
    }


}

fun getRandomExchangeRate(): Double {
    // заглушка
    return 1.0
}