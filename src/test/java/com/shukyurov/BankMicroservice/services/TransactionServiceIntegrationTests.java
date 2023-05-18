package com.shukyurov.BankMicroservice.services;

import com.shukyurov.BankMicroservice.AbstractIntegrationTests;
import com.shukyurov.BankMicroservice.client.ConversionClient;
import com.shukyurov.BankMicroservice.exception.ExchangeRateException;
import com.shukyurov.BankMicroservice.exception.LimitExceededException;
import com.shukyurov.BankMicroservice.exception.ResourceNotFoundException;
import com.shukyurov.BankMicroservice.mapper.*;
import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.model.dto.TransactionRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.TransactionResponseDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import com.shukyurov.BankMicroservice.repository.ConversionRepository;
import com.shukyurov.BankMicroservice.repository.LimitRepository;
import com.shukyurov.BankMicroservice.repository.TransactionRepository;
import com.shukyurov.BankMicroservice.service.ClientService;
import com.shukyurov.BankMicroservice.service.ConversionService;
import com.shukyurov.BankMicroservice.service.LimitService;
import com.shukyurov.BankMicroservice.service.TransactionService;
import com.shukyurov.BankMicroservice.service.impl.ConversionServiceImpl;
import com.shukyurov.BankMicroservice.service.impl.LimitServiceImpl;
import com.shukyurov.BankMicroservice.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionServiceIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private ClientService clientService;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ConversionServiceImpl conversionService;

    @Autowired
    private TransactionRequestMapper transactionRequestMapper;

    @Autowired
    private TransactionResponseMapper transactionResponseMapper;

    @Autowired
    private ConversionRepository conversionRepository;

    @BeforeEach
    @Transactional
    public void init() {
        ClientDTO client1 = new ClientDTO();
        client1.setBankAccountNumber("1234567890");

        ClientDTO client2 = new ClientDTO();
        client2.setBankAccountNumber("0987654321");

        clientService.addClient(client1);
        clientService.addClient(client2);

        Client existedClient = clientService.getClientByBankAccountNumber("1234567890");

        Limit productLimit = existedClient.getClientLimits().get(0);
        productLimit.setLimitSum(BigDecimal.TEN);
        productLimit.setRemainingMonthLimit(BigDecimal.TEN);

        Limit serviceLimit = existedClient.getClientLimits().get(1);
        serviceLimit.setLimitSum(BigDecimal.TEN);
        serviceLimit.setRemainingMonthLimit(BigDecimal.TEN);
    }

    @AfterEach
    @Transactional
    public void resetDB() {
        transactionRepository.deleteAll();
        limitRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    public void givenCorrectTransactionDTOWithCorrectSum_whenAddTransaction_thenTransactionReturnedAndNoExceptionThrown() {
        TransactionRequestDTO transactionWithCorrectSum = getTransactionRequestDTO();
        transactionWithCorrectSum.setSum(300d);
        transactionService.addTransaction(transactionWithCorrectSum);
        Client clientWithTransaction = clientService.getClientByBankAccountNumber("1234567890");
        Transaction addedTransaction = clientWithTransaction.getClientTransactions().get(0);
        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate("USD", transactionWithCorrectSum.getCurrency_shortname());
        BigDecimal correctRemainingMonthLimit = BigDecimal.TEN.subtract(BigDecimal.valueOf(transactionWithCorrectSum.getSum()).divide(lastExchangeRate, 2, RoundingMode.HALF_UP));

        assertEquals(addedTransaction.getTransactionClient().getId(), clientWithTransaction.getId());
        assertThat(addedTransaction.getTransactionLimit().getLimitExpenseCategory().getExpenseCategoryType()).isEqualTo("product");
        assertThat(addedTransaction.getTransactionLimit().getRemainingMonthLimit()).isEqualByComparingTo(correctRemainingMonthLimit);
        assertThat(addedTransaction.getTransactionLimit().getLimitSum()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(addedTransaction.isLimitExceeded()).isFalse();
    }

    @Test
    @Transactional
    public void givenCorrectTransactionDTOWithLimitExceededSum_whenAddTransaction_thenExceptionThrown() {
        TransactionRequestDTO limitExceededSumTransaction = getTransactionRequestDTO();
        limitExceededSumTransaction.setSum(5000d);
        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate("USD", limitExceededSumTransaction.getCurrency_shortname());
        BigDecimal correctRemainingMonthLimit = BigDecimal.TEN.subtract(BigDecimal.valueOf(limitExceededSumTransaction.getSum()).divide(lastExchangeRate, 2, RoundingMode.HALF_UP));

        Throwable ex = assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededSumTransaction));

        Client client = clientService.getClientByBankAccountNumber("1234567890");
        Transaction addedTransaction = client.getClientTransactions().get(0);

        assertEquals(String.format("Your current remaining month limit has been exceeded  : '%s'", correctRemainingMonthLimit), ex.getMessage());
        assertThat(addedTransaction.isLimitExceeded()).isTrue();
        assertThat(addedTransaction.getTransactionClient().getId()).isEqualTo(client.getId());
        assertThat(addedTransaction.getTransactionLimit().getId()).isEqualTo(client.getClientLimits().get(0).getId());
    }

    @Test
    @Transactional
    public void givenCorrectTransactionDTOAndNoLimitsExist_whenAddTransaction_thenExceptionThrown() {
        TransactionRequestDTO correctTransaction = getTransactionRequestDTO();
        limitRepository.deleteAll();

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                transactionService.addTransaction(correctTransaction));
        Client existedClient = clientService.getClientByBankAccountNumber("1234567890");

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Limit", "clientId and expenseCategory",
                existedClient.getId() + " and " + correctTransaction.getExpense_category()), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenCorrectTransactionDTOWithNonExistedClient_whenAddTransaction_thenExceptionThrown() {
        TransactionRequestDTO incorrectTransaction = getTransactionRequestDTO();
        incorrectTransaction.setAccount_from("incorrect ba");

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                transactionService.addTransaction(incorrectTransaction));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Clients", "bankAccountNumber",
                incorrectTransaction.getAccount_from() + " and " + incorrectTransaction.getAccount_to()), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenTwoLimitExceededTransactions_whenGetAllLimitExceededTransactionsWithCorrectClientAndExpenseAndCurrency_thenListReturned() {
        TransactionRequestDTO limitExceededTransaction1 = getTransactionRequestDTO();
        limitExceededTransaction1.setSum(1000d);
        TransactionRequestDTO limitExceededTransaction2 = getTransactionRequestDTO();
        limitExceededTransaction2.setSum(1000d);

        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate("USD","RUB");
        BigDecimal correctRemainingMonthLimit = BigDecimal.TEN.subtract(BigDecimal.valueOf(2000d).divide(lastExchangeRate, 2, RoundingMode.HALF_UP));

        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransaction1));
        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransaction2));

        Client clientWithTransactions = clientService.getClientByBankAccountNumber("1234567890");
        Limit limitWithTransactions = clientWithTransactions.getClientLimits().get(0);
        List<TransactionResponseDTO> limitExceededTransactionsList = transactionService
                .getAllLimitExceededTransactions("1234567890", "RUB", "product");

        assertThat(limitExceededTransactionsList).isNotEmpty().hasSize(2);
        assertThat(limitExceededTransactionsList.get(0).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(0).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(0).getSum()).isEqualTo(1000d);
        assertThat(limitExceededTransactionsList.get(0).getExpense_category()).isEqualTo("product");
        assertThat(limitExceededTransactionsList.get(0).getCurrency_shortname()).isEqualTo("RUB");
        assertThat(limitExceededTransactionsList.get(1).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(1).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(1).getSum()).isEqualTo(1000d);
        assertThat(limitExceededTransactionsList.get(1).getExpense_category()).isEqualTo("product");
        assertThat(limitExceededTransactionsList.get(1).getCurrency_shortname()).isEqualTo("RUB");
        assertThat(limitWithTransactions.getRemainingMonthLimit().toBigInteger()).isEqualByComparingTo(correctRemainingMonthLimit.toBigInteger());
    }

    @Test
    @Transactional
    public void givenTwoLimitExceededTransactions_whenGetAllLimitExceededTransactionsWithCorrectClientAndExpense_thenListReturned() {
        TransactionRequestDTO limitExceededTransactionRub = getTransactionRequestDTO();
        limitExceededTransactionRub.setSum(1000d);
        TransactionRequestDTO limitExceededTransactionKzt = getTransactionRequestDTO();
        limitExceededTransactionKzt.setSum(100000d);
        limitExceededTransactionKzt.setCurrency_shortname("KZT");

        BigDecimal lastExchangeRateRub = conversionService.getLastExchangeRate("USD","RUB");
        BigDecimal lastExchangeRateKzt = conversionService.getLastExchangeRate("USD","KZT");

        BigDecimal sumTransactionRubInUsd = BigDecimal.valueOf(limitExceededTransactionRub.getSum())
                .divide(lastExchangeRateRub, 2, RoundingMode.HALF_UP);
        BigDecimal sumTransactionKztInUsd = BigDecimal.valueOf(limitExceededTransactionKzt.getSum())
                .divide(lastExchangeRateKzt, 2, RoundingMode.HALF_UP);

        BigDecimal correctRemainingMonthLimit = BigDecimal.TEN.subtract(sumTransactionKztInUsd.add(sumTransactionRubInUsd))
                .setScale(2, RoundingMode.HALF_UP);

        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransactionRub));
        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransactionKzt));

        Client clientWithTransactions = clientService.getClientByBankAccountNumber("1234567890");
        Limit limitWithTransactions = clientWithTransactions.getClientLimits().get(0);
        List<TransactionResponseDTO> limitExceededTransactionsList = transactionService
                .getAllLimitExceededTransactions("1234567890", null, "product");

        assertThat(limitExceededTransactionsList).isNotEmpty().hasSize(2);
        assertThat(limitExceededTransactionsList.get(0).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(0).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(0).getSum()).isEqualTo(1000d);
        assertThat(limitExceededTransactionsList.get(0).getExpense_category()).isEqualTo("product");
        assertThat(limitExceededTransactionsList.get(0).getCurrency_shortname()).isEqualTo("RUB");
        assertThat(limitExceededTransactionsList.get(1).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(1).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(1).getSum()).isEqualTo(100000d);
        assertThat(limitExceededTransactionsList.get(1).getExpense_category()).isEqualTo("product");
        assertThat(limitExceededTransactionsList.get(1).getCurrency_shortname()).isEqualTo("KZT");

        assertThat(limitWithTransactions.getRemainingMonthLimit()).isEqualTo(correctRemainingMonthLimit);
    }

    @Test
    @Transactional
    public void givenTwoLimitExceededTransactions_whenGetAllLimitExceededTransactionsWithCorrectClientAndCurrency_thenListReturned() {
        TransactionRequestDTO limitExceededTransactionProduct = getTransactionRequestDTO();
        limitExceededTransactionProduct.setSum(1000d);
        TransactionRequestDTO limitExceededTransactionService = getTransactionRequestDTO();
        limitExceededTransactionService.setSum(1000d);
        limitExceededTransactionService.setExpense_category("service");

        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate("USD","RUB");
        BigDecimal correctRemainingMonthLimit = BigDecimal.TEN.subtract(BigDecimal.valueOf(1000d)
                .divide(lastExchangeRate, 2, RoundingMode.HALF_UP));

        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransactionProduct));
        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransactionService));

        Client clientWithTransactions = clientService.getClientByBankAccountNumber("1234567890");
        Limit limitWithTransactionsProduct = clientWithTransactions.getClientLimits().get(0);
        Limit limitWithTransactionService = clientWithTransactions.getClientLimits().get(1);
        List<TransactionResponseDTO> limitExceededTransactionsList = transactionService
                .getAllLimitExceededTransactions("1234567890", "RUB", null);

        assertThat(limitExceededTransactionsList).isNotEmpty().hasSize(2);
        assertThat(limitExceededTransactionsList.get(0).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(0).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(0).getSum()).isEqualTo(1000d);
        assertThat(limitExceededTransactionsList.get(0).getExpense_category()).isEqualTo("product");
        assertThat(limitExceededTransactionsList.get(0).getCurrency_shortname()).isEqualTo("RUB");
        assertThat(limitExceededTransactionsList.get(1).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(1).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(1).getSum()).isEqualTo(1000d);
        assertThat(limitExceededTransactionsList.get(1).getExpense_category()).isEqualTo("service");
        assertThat(limitExceededTransactionsList.get(1).getCurrency_shortname()).isEqualTo("RUB");

        assertThat(limitWithTransactionsProduct.getRemainingMonthLimit()).isEqualTo(correctRemainingMonthLimit);
        assertThat(limitWithTransactionService.getRemainingMonthLimit()).isEqualTo(correctRemainingMonthLimit);
    }

    @Test
    @Transactional
    public void givenTwoLimitExceededTransactions_whenGetAllLimitExceededTransactionsWithCorrectClient_thenListReturned() {
        TransactionRequestDTO limitExceededTransactionRubProduct = getTransactionRequestDTO();
        limitExceededTransactionRubProduct.setSum(1000d);
        TransactionRequestDTO limitExceededTransactionKztService = getTransactionRequestDTO();
        limitExceededTransactionKztService.setSum(100000d);
        limitExceededTransactionKztService.setCurrency_shortname("KZT");
        limitExceededTransactionKztService.setExpense_category("service");

        BigDecimal lastExchangeRateRub = conversionService.getLastExchangeRate("USD","RUB");
        BigDecimal lastExchangeRateKzt = conversionService.getLastExchangeRate("USD","KZT");

        BigDecimal correctRemainingMonthLimitRubProduct = BigDecimal.TEN.subtract(BigDecimal.valueOf(limitExceededTransactionRubProduct.getSum())
                .divide(lastExchangeRateRub, 2, RoundingMode.HALF_UP));
        BigDecimal correctRemainingMonthLimitKztService = BigDecimal.TEN.subtract(BigDecimal.valueOf(limitExceededTransactionKztService.getSum())
                .divide(lastExchangeRateKzt, 2, RoundingMode.HALF_UP));

        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransactionRubProduct));
        assertThrows(LimitExceededException.class, () ->
                transactionService.addTransaction(limitExceededTransactionKztService));

        Client clientWithTransactions = clientService.getClientByBankAccountNumber("1234567890");
        Limit limitWithTransactionsProduct = clientWithTransactions.getClientLimits().get(0);
        Limit limitWithTransactionsService = clientWithTransactions.getClientLimits().get(1);
        List<TransactionResponseDTO> limitExceededTransactionsList = transactionService
                .getAllLimitExceededTransactions("1234567890", null, null);

        assertThat(limitExceededTransactionsList).isNotEmpty().hasSize(2);
        assertThat(limitExceededTransactionsList.get(0).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(0).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(0).getSum()).isEqualTo(1000d);
        assertThat(limitExceededTransactionsList.get(0).getExpense_category()).isEqualTo("product");
        assertThat(limitExceededTransactionsList.get(0).getCurrency_shortname()).isEqualTo("RUB");
        assertThat(limitExceededTransactionsList.get(1).getAccount_from()).isEqualTo("1234567890");
        assertThat(limitExceededTransactionsList.get(1).getAccount_to()).isEqualTo("0987654321");
        assertThat(limitExceededTransactionsList.get(1).getSum()).isEqualTo(100000d);
        assertThat(limitExceededTransactionsList.get(1).getExpense_category()).isEqualTo("service");
        assertThat(limitExceededTransactionsList.get(1).getCurrency_shortname()).isEqualTo("KZT");

        assertThat(limitWithTransactionsProduct.getRemainingMonthLimit()).isEqualTo(correctRemainingMonthLimitRubProduct);
        assertThat(limitWithTransactionsService.getRemainingMonthLimit()).isEqualTo(correctRemainingMonthLimitKztService);
    }

    @Test
    @Transactional
    public void givenIncorrectBankAccountNumber_whenGetAllLimitExceededTransactions_thenExceptionThrown() {
        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getAllLimitExceededTransactions("incorrect ba", "RUB", "product"));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Client", "bankAccountNumber", "incorrect ba"),
                ex.getMessage());
    }

    @Test
    @Transactional
    public void givenTwoTransactions_whenGetAllTransactions_thenListReturned() {
        TransactionRequestDTO transaction1 = getTransactionRequestDTO();
        TransactionRequestDTO transaction2 = getTransactionRequestDTO();

        transactionService.addTransaction(transaction1);
        transactionService.addTransaction(transaction2);

        List<TransactionResponseDTO> correctList = transactionService.getAllTransactions("1234567890");

        assertThat(correctList).isNotEmpty().hasSize(2)
                .extracting(TransactionResponseDTO::getAccount_from)
                .containsOnly(transaction1.getAccount_from(), transaction2.getAccount_from());
        assertThat(correctList)
                .extracting(TransactionResponseDTO::getAccount_to)
                .containsOnly(transaction1.getAccount_to(), transaction2.getAccount_to());
    }

    @Test
    @Transactional
    public void givenIncorrectBankAccountNumber_whenGetAllTransactions_thenExceptionThrown() {
        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getAllTransactions("incorrect ba"));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Client", "bankAccountNumber", "incorrect ba"),
                ex.getMessage());
    }

    @Test
    @Transactional
    public void givenCorrectTransactionDTOAndNoConversionsExist_whenAddTransaction_thenExceptionThrown() {
        TransactionRequestDTO correctTransaction = getTransactionRequestDTO();
        correctTransaction.setSum(7d);
        conversionRepository.deleteAll();
        ConversionClient mockClient = Mockito.mock(ConversionClient.class);
        Mockito.when(mockClient.getConversionDTO(Mockito.any(), Mockito.any())).thenReturn(null);

        ConversionService conversionServiceWithMockClient = new ConversionServiceImpl(new ConversionMapper(), mockClient,
                conversionRepository);
        LimitService ls = new LimitServiceImpl(limitRepository, clientService, new LimitRequestMapper(),
                conversionServiceWithMockClient, clientRepository, new LimitResponseMapper());
        TransactionService ts = new TransactionServiceImpl(clientService, ls,
                transactionRepository, transactionRequestMapper, transactionResponseMapper);

        Throwable ex = assertThrows(ExchangeRateException.class, () ->
                ts.addTransaction(correctTransaction));

        assertEquals("Could not get exchange rate from resource : 'https://api.twelvedata.com/'",
                ex.getMessage());
        Mockito.verify(mockClient, Mockito.times(1)).getConversionDTO(Mockito.any(), Mockito.any());
    }

    private TransactionRequestDTO getTransactionRequestDTO() {
        TransactionRequestDTO transaction = new TransactionRequestDTO();
        transaction.setAccount_from("1234567890");
        transaction.setAccount_to("0987654321");
        transaction.setExpense_category("product");
        transaction.setCurrency_shortname("RUB");
        transaction.setSum(0d);
        transaction.setDatetime(LocalDateTime.now());

        return transaction;
    }

}
