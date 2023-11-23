fun main() {
    val bank = Bank()
    val logger = Logger() // предполагается, что у вас есть класс Logger, реализующий Observer
    with(bank) {
        addObserver(logger)
    }

    //дефолтный пользователь
    val client = Client(1, 1000.0, "USD")
    bank.addClient(client)


    // взаимодействия с пользователем
    while (true) {
        println("Введите команду (например, 'внесение средств', 'снятие средств', 'обмен валют', 'перевод', 'баланс', 'выход'): ")
        val command = readLine()

        when (command?.toLowerCase()) {
            "внесение средств" -> {
                println("Введите ID клиента:")
                val clientId = readLine()?.toIntOrNull()

                println("Введите сумму:")
                val amount = readLine()?.toDoubleOrNull()

                println("Введите валюту:")
                val currency = readLine()

                if (clientId != null && amount != null && currency != null) {
                    bank.run { deposit(clientId, amount, currency) }
                } else {
                    println("Некорректные данные.")
                }
            }

            "снятие средств" -> {
                println("Введите ID клиента:")
                val clientId = readLine()?.toIntOrNull()

                println("Введите сумму:")
                val amount = readLine()?.toDoubleOrNull()

                println("Введите валюту:")
                val currency = readLine()

                if (clientId != null && amount != null && currency != null)
                    bank.run{ withdraw(clientId, amount, currency) }
                else {
                    println("Некорректные данные.")
                }
            }

            "обмен валют" -> {
                println("Введите ID клиента:")
                val clientId = readLine()?.toIntOrNull()

                println("Введите исходную валюту:")
                val fromCurrency = readLine()

                println("Введите целевую валюту:")
                val toCurrency = readLine()

                println("Введите сумму:")
                val amount = readLine()?.toDoubleOrNull()

                if (clientId != null && fromCurrency != null && toCurrency != null && amount != null) {
                    bank.exchangeCurrency(clientId, fromCurrency, toCurrency, amount)
                } else {
                    println("Некорректные данные.")
                }
            }
            "перевод" -> {
                println("Введите ID отправителя:")
                val senderId = readLine()?.toIntOrNull()

                println("Введите ID получателя:")
                val receiverId = readLine()?.toIntOrNull()

                println("Введите сумму:")
                val amount = readLine()?.toDoubleOrNull()

                if (senderId != null && receiverId != null && amount != null) {
                    bank.transferFunds(senderId, receiverId, amount)
                } else {
                    println("Некорректные данные.")
                }
            }

            "баланс" -> {
                println("Введите ID клиента:")
                val clientId = readLine()?.toIntOrNull()

                if (clientId != null) {
                    val client = bank.clients[clientId]
                    if (client != null) {
                        println("Баланс клиента $clientId: ${client.balance} ${client.currency}")
                    } else {
                        println("Клиент $clientId не найден.")
                    }
                } else {
                    println("Некорректные данные.")
                }
            }

            "выход" -> return
            else -> println("Неизвестная команда.")
        }
    }
}

