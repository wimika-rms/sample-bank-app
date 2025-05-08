package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

sealed class CheckDebitTransactionEvent {
    object CheckDebitClick : CheckDebitTransactionEvent()
    object DismissAlert : CheckDebitTransactionEvent()
    data class UpdateSourceAccountNumber(val value: String) : CheckDebitTransactionEvent()
    data class UpdateDestinationAccountNumber(val value: String) : CheckDebitTransactionEvent()
    data class UpdateDestinationBank(val value: String) : CheckDebitTransactionEvent()
    data class UpdateMemo(val value: String) : CheckDebitTransactionEvent()
    data class UpdateAmount(val value: Double) : CheckDebitTransactionEvent()
} 