package com.kyberswap.android.data.repository

import com.kyberswap.android.data.api.home.TransactionApi
import com.kyberswap.android.data.db.*
import com.kyberswap.android.data.mapper.TransactionMapper
import com.kyberswap.android.domain.model.Token
import com.kyberswap.android.domain.model.Transaction
import com.kyberswap.android.domain.model.TransactionFilter
import com.kyberswap.android.domain.model.Wallet
import com.kyberswap.android.domain.repository.TransactionRepository
import com.kyberswap.android.domain.usecase.transaction.GetTransactionFilterUseCase
import com.kyberswap.android.domain.usecase.transaction.GetTransactionsUseCase
import com.kyberswap.android.domain.usecase.transaction.MonitorPendingTransactionUseCase
import com.kyberswap.android.domain.usecase.transaction.SaveTransactionFilterUseCase
import com.kyberswap.android.util.TokenClient
import com.kyberswap.android.util.ext.toBigDecimalOrDefaultZero
import com.kyberswap.android.util.ext.toDisplayNumber
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Singles
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TransactionDataRepository @Inject constructor(
    private val transactionApi: TransactionApi,
    private val transactionDao: TransactionDao,
    private val transactionMapper: TransactionMapper,
    private val tokenClient: TokenClient,
    private val tokenDao: TokenDao,
    private val swapDao: SwapDao,
    private val sendDao: SendDao,
    private val limitOrderDao: LocalLimitOrderDao,
    private val transactionFilterDao: TransactionFilterDao
) : TransactionRepository {

    override fun monitorPendingTransactionsPolling(param: MonitorPendingTransactionUseCase.Param): Flowable<List<Transaction>> {
        return Flowable.fromCallable {
            val pendingTransactions = tokenClient.monitorPendingTransactions(param.transactions)
            pendingTransactions.forEach { tx ->
                val transaction =
                    transactionDao.findTransaction(tx.hash, Transaction.PENDING_TRANSACTION_STATUS)
                transaction?.let {
                    if (Numeric.decodeQuantity(tx.blockHash) > BigInteger.ZERO) {
                        transactionDao.delete(transaction)
                        transactionDao.insertTransaction(tx.copy(transactionStatus = ""))
                        val tokenSource = tokenDao.getTokenBySymbol(it.tokenSource)
                        val tokenDest = tokenDao.getTokenBySymbol(it.tokenDest)
                        tokenSource?.let { src ->
                            updateTokenBalance(src, param.wallet)

                

                        tokenDest?.let { dest ->
                            updateTokenBalance(dest, param.wallet)
                


            
        
    
            pendingTransactions

            .repeatWhen {
                it.delay(15, TimeUnit.SECONDS)
    
            .retryWhen { throwable ->
                throwable.compose(zipWithFlatMap())
    
    }

    private fun updateTokenBalance(token: Token, wallet: Wallet) {
        val updatedBalanceToken = tokenClient.updateBalance(token)
        tokenDao.updateToken(updatedBalanceToken)

        val swapByAddress = swapDao.findSwapByAddress(wallet.address)
        swapByAddress?.let { swap ->
            if (swap.tokenSource.tokenSymbol == updatedBalanceToken.tokenSymbol) {
                swapDao.updateSwap(swap.copy(tokenSource = updatedBalanceToken))
    


        val sendByAddress = sendDao.findSendByAddress(wallet.address)
        sendByAddress?.let { send ->
            if (send.tokenSource.tokenSymbol == updatedBalanceToken.tokenSymbol) {
                sendDao.updateSend(send.copy(tokenSource = updatedBalanceToken))
    



        val orderByAddress =
            limitOrderDao.findLocalLimitOrderByAddress(wallet.address)
        orderByAddress?.let { order ->
            if (order.tokenSource.symbol == updatedBalanceToken.tokenSymbol) {
                limitOrderDao.updateOrder(
                    order.copy(
                        tokenSource = order.tokenSource.updateBalance(
                            updatedBalanceToken.currentBalance
                        )
                    )
                )
     else if (order.tokenSource.tokenSymbol == Token.ETH_SYMBOL_STAR && updatedBalanceToken.tokenSymbol == Token.ETH_SYMBOL) {
                val wethToken = tokenDao.getTokenBySymbol(Token.WETH_SYMBOL)
                val ethBalance = updatedBalanceToken.currentBalance
                val wethBalance = wethToken?.currentBalance ?: BigDecimal.ZERO

                limitOrderDao.updateOrder(
                    order.copy(
                        tokenSource = order.tokenSource.updateBalance(
                            ethBalance.plus(wethBalance)
                        )
                    )
                )

    

    }

    override fun fetchPendingTransaction(address: String): Flowable<List<Transaction>> {
        return transactionDao.getTransactionByStatus(Transaction.PENDING_TRANSACTION_STATUS)
    }

    override fun fetchERC20TokenTransactions(wallet: Wallet): Single<List<Transaction>> {
        return transactionApi.getTransaction(
            DEFAULT_MODULE,
            TOKEN_TRANSACTION,
            wallet.address,
            DEFAULT_SORT,
            API_KEY
        )
            .map {
                transactionMapper.transform(it.result, wallet.address, TOKEN_TRANSACTION)
    .doAfterSuccess {
                val tokensSymbols = tokenDao.allTokens.filter {
                    !it.isOther
        .map {
                    it.tokenSymbol
        
                val otherTokenList = it.filterNot { tx ->
                    tokensSymbols.contains(tx.tokenSymbol)
        .map { tx ->
                    Token(tx).copy(isOther = true).updateSelectedWallet(wallet)
        

                tokenDao.insertTokens(otherTokenList)
    
    }

    override fun fetchInternalTransactions(address: String): Single<List<Transaction>> {
        return transactionApi.getTransaction(
            DEFAULT_MODULE,
            INTERNAL_TRANSACTION,
            address,
            DEFAULT_SORT,
            API_KEY
        )
            .map {
                it.result
    
            .map {
                transactionMapper.transform(
                    it,
                    Transaction.TransactionType.RECEIVED,
                    INTERNAL_TRANSACTION
                )
    
    }

    override fun fetchNormalTransaction(address: String): Single<List<Transaction>> {
        return transactionApi.getTransaction(
            DEFAULT_MODULE,
            NORMAL_TRANSACTION,
            address,
            DEFAULT_SORT,
            API_KEY
        )
            .map {
                it.result
    
            .map {
                transactionMapper.transform(
                    it,
                    Transaction.TransactionType.SEND,
                    NORMAL_TRANSACTION
                )
    
    }


    private fun <T> zipWithFlatMap(): FlowableTransformer<T, Long> {
        return FlowableTransformer { flowable ->
            flowable.zipWith(
                Flowable.range(COUNTER_START, ATTEMPTS),
                BiFunction<T, Int, Int> { _: T, u: Int -> u })
                .flatMap { t -> Flowable.timer(t * 5L, TimeUnit.SECONDS) }

    }

    override fun fetchAllTransactions(param: GetTransactionsUseCase.Param): Flowable<List<Transaction>> {
        if (param.transactionType == Transaction.PENDING) {
            return fetchPendingTransaction(param.wallet.address)

        return getTransactions(param.wallet)

    }

    private fun getTransactions(wallet: Wallet): Flowable<List<Transaction>> {
        val sendTransaction = fetchNormalTransaction(wallet.address)
            .toFlowable().flatMapIterable { transactions ->
                transactions
    
            .filter {
                it.value.toBigDecimalOrDefaultZero() > BigDecimal.ZERO &&
                    it.from == wallet.address || it.isTransactionFail
    .map {
                it.copy(
                    tokenSymbol = Token.ETH,
                    tokenName = Token.ETH_NAME,
                    tokenDecimal = Token.ETH_DECIMAL.toString()
                )
    
            .toList()
        val receivedTransaction = fetchInternalTransactions(wallet.address)
            .toFlowable().flatMapIterable { transactions ->
                transactions
    
            .map {
                it.copy(
                    tokenSymbol = Token.ETH,
                    tokenName = Token.ETH_NAME,
                    tokenDecimal = Token.ETH_DECIMAL.toString()
                )
    .toList()

        val erc20Transaction = fetchERC20TokenTransactions(wallet)

        return Flowable.mergeDelayError(
            transactionDao.getCompletedTransactions(),
            Singles.zip(
                sendTransaction,
                receivedTransaction,
                erc20Transaction
            )
            { send, received, erc20 ->
                val transactions = send.toMutableList()
                transactions.addAll(received)
                transactions.addAll(erc20)
                transactions.toList().sortedByDescending { it.timeStamp.toLong() }
    
                .map {
                    it.groupBy { transaction -> transaction.hash }
        
                .map {
                    val transactionList = mutableListOf<Transaction>()
                    for ((_, transactions) in it) {
                        if (transactions.size == 2) {
                            val send = transactions.find {
                                it.type == Transaction.TransactionType.SEND
                    

                            val received = transactions.find {
                                it.type == Transaction.TransactionType.RECEIVED
                    

                            val sourceAmount = send?.value.toBigDecimalOrDefaultZero()
                                .divide(
                                    BigDecimal.TEN
                                        .pow(
                                            (send?.tokenDecimal ?: Token.ETH_DECIMAL.toString())
                                                .toBigDecimalOrDefaultZero().toInt()
                                        )
                                )

                            val destAmount = received?.value.toBigDecimalOrDefaultZero()
                                .divide(
                                    BigDecimal.TEN
                                        .pow(
                                            (received?.tokenDecimal ?: Token.ETH_DECIMAL.toString())
                                                .toBigDecimalOrDefaultZero().toInt()
                                        )
                                )
                            val tx =
                                if (transactions.first().gasPrice.isEmpty()) transactions.last() else transactions.first()

                            val transaction = tx.copy(
                                tokenSource = send?.tokenSymbol ?: "",
                                sourceAmount = sourceAmount.toDisplayNumber(),
                                tokenDest = received?.tokenSymbol ?: "",
                                destAmount = destAmount.toDisplayNumber()

                            )

                            transactionList.add(transaction)

                 else {
                            transactionList.addAll(transactions)
                
            
                    transactionList.toList()
        .doAfterSuccess {
                    transactionDao.updateTransactionList(it)
        
                .toFlowable()
        )
    }

    override fun saveTransactionFilter(param: SaveTransactionFilterUseCase.Param): Completable {
        return Completable.fromCallable {
            transactionFilterDao.updateTrasactionFilter(param.transactionFilter)

    }

    override fun getTransactionFilter(param: GetTransactionFilterUseCase.Param): Flowable<TransactionFilter> {
        return Flowable.fromCallable {
            when (val filter =
                transactionFilterDao.findTransactionFilterByAddress(param.walletAddress)) {
                null -> {
                    tokenDao.allTokens.map {
                        it.tokenSymbol
            
                    val newFilter = TransactionFilter(
                        walletAddress = param.walletAddress,
                        types = listOf(
                            Transaction.TransactionType.SWAP,
                            Transaction.TransactionType.SEND,
                            Transaction.TransactionType.RECEIVED
                        ),
                        tokens = tokenDao.allTokens.map {
                            it.tokenSymbol
                
                    )
                    transactionFilterDao.insertTransactionFilter(newFilter)
                    newFilter
        
                else -> filter
    

.flatMap {
            transactionFilterDao.findTransactionFilterByAddressFlowable(param.walletAddress)
                .defaultIfEmpty(it)


    }

    companion object {
        private const val COUNTER_START = 1
        private const val ATTEMPTS = 5
        const val DEFAULT_MODULE = "account"
        const val NORMAL_TRANSACTION = "txlist"
        const val INTERNAL_TRANSACTION = "txlistinternal"
        const val TOKEN_TRANSACTION = "tokentx"
        const val DEFAULT_SORT = "desc"
        const val API_KEY = "7V3E6JSF7941JCB6448FNRI3FSH9HI7HYH"
    }


}