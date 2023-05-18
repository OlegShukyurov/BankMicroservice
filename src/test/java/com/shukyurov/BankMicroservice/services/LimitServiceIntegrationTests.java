package com.shukyurov.BankMicroservice.services;

import com.shukyurov.BankMicroservice.AbstractIntegrationTests;
import com.shukyurov.BankMicroservice.exception.ResourceNotFoundException;
import com.shukyurov.BankMicroservice.exception.SumIncorrectValueException;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.LimitResponseDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import com.shukyurov.BankMicroservice.repository.LimitRepository;
import com.shukyurov.BankMicroservice.repository.TransactionRepository;
import com.shukyurov.BankMicroservice.service.ClientService;
import com.shukyurov.BankMicroservice.service.ConversionService;
import com.shukyurov.BankMicroservice.service.impl.LimitServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LimitServiceIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitServiceImpl limitService;

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
    public void givenExistedLimitAndCorrectTransaction_whenUpdateTransactionLimit_thenTransactionHasCorrectLimit() {
        Transaction correctTransaction = getTransaction();
        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate("USD","RUB");
        BigDecimal correctRemainingMonthLimit = BigDecimal.TEN.subtract(correctTransaction.getSum()
                .divide(lastExchangeRate, 2, RoundingMode.HALF_UP));
        limitService.updateTransactionLimit(correctTransaction);

        assertThat(correctTransaction.getTransactionLimit()).isNotNull();
        assertThat(correctTransaction.getTransactionLimit().getLimitTransactions().get(0).getId()).isEqualTo(correctTransaction.getId());
        assertThat(correctTransaction.getTransactionLimit().getLimitClient().getBankAccountNumber()).isEqualTo("1234567890");
        assertThat(correctTransaction.getTransactionLimit().getLimitExpenseCategory()).isEqualTo(ExpenseCategoryType.PRODUCT);
        assertThat(correctTransaction.getTransactionLimit().getRemainingMonthLimit().toBigInteger()).isEqualByComparingTo(correctRemainingMonthLimit.toBigInteger());
    }

    @Test
    @Transactional
    public void givenNonExistedLimitAndCorrectTransaction_whenUpdateTransactionLimit_thenExceptionThrown() {
        Transaction correctTransaction = getTransaction();
        limitRepository.deleteAll();

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                limitService.updateTransactionLimit(correctTransaction));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Limit", "clientId and expenseCategory",
                correctTransaction.getTransactionClient().getId() + " and " + correctTransaction.getExpenseCategory().getExpenseCategoryType()),
                ex.getMessage());
    }

    @Test
    @Transactional
    public void givenExistedClientAndCorrectLimitDTO_whenAddLimitByBankAccountNumber_thenLimitAdded() {
        LimitRequestDTO correctLimit = getLimitRequestDTO();
        limitService.addLimitByBankAccountNumber("1234567890", correctLimit);
        Client clientWithAddedLimit = clientService.getClientByBankAccountNumber("1234567890");
        Limit addedLimit = clientWithAddedLimit.getClientLimits().get(2);

        assertThat(addedLimit.getLimitSum()).isEqualByComparingTo(BigDecimal.valueOf(correctLimit.getLimit_sum()).setScale(2, RoundingMode.HALF_UP));
        assertThat(addedLimit.getLimitExpenseCategory().getExpenseCategoryType()).isEqualTo(correctLimit.getLimit_expense_category());
        assertThat(addedLimit.getLimitCurrencyShortname().getCurrencyType()).isEqualTo(correctLimit.getLimit_currency_shortname());
        assertThat(addedLimit.getLimitDateTime()).isNotNull();
        assertThat(addedLimit.getRemainingMonthLimit()).isEqualByComparingTo(BigDecimal.valueOf(correctLimit.getLimit_sum()).setScale(2, RoundingMode.HALF_UP));
        assertThat(addedLimit.getLimitTransactions()).isNotNull();
    }

    @Test
    @Transactional
    public void givenNonExistedClientAndCorrectLimitDTO_whenAddLimitByBankAccountNumber_thenExceptionThrown() {
        LimitRequestDTO correctLimit = getLimitRequestDTO();

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                limitService.addLimitByBankAccountNumber("incorrect ba", correctLimit));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Clients", "bankAccountNumber",
                "incorrect ba and incorrect ba"), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenCorrectClientAndExpenseAndSum_whenIncreaseLimitByBankAccountNumber_thenLimitIncreased() {
        limitService.increaseLimitByBankAccountNumber("1234567890", "product", 50d);
        Client clientWithIncreasedLimit = clientService.getClientByBankAccountNumber("1234567890");
        Limit increasedLimit = clientWithIncreasedLimit.getClientLimits().get(0);

        assertThat(increasedLimit.getLimitSum()).isEqualByComparingTo(BigDecimal.valueOf(60).setScale(2, RoundingMode.HALF_UP));
        assertThat(increasedLimit.getRemainingMonthLimit()).isEqualByComparingTo(BigDecimal.valueOf(60).setScale(2, RoundingMode.HALF_UP));
        assertThat(increasedLimit.getLimitExpenseCategory().getExpenseCategoryType()).isEqualTo("product");
    }

    @Test
    @Transactional
    public void givenNonExistedClient_whenIncreaseLimitByBankAccountNumber_thenExceptionThrown() {
        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                limitService.increaseLimitByBankAccountNumber("non existed", "product", 10d));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Client", "bankAccountNumber", "non existed"),
                ex.getMessage());
    }

    @Test
    @Transactional
    public void givenIncorrectSum_whenIncreaseLimitByBankAccountNumber_thenExceptionThrown() {
        Throwable ex = assertThrows(SumIncorrectValueException.class, () ->
                limitService.increaseLimitByBankAccountNumber("1234567890", "product", -10d));

        assertEquals(String.format("Sum to increase or decrease limit is incorrect : '%s'", Double.valueOf(-10)), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenIncorrectExpense_whenIncreaseLimitByBankAccountNumber_thenExceptionThrown() {
        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                limitService.increaseLimitByBankAccountNumber("1234567890", "incorrect expense", 10d));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Limit", "expenseCategory", "incorrect expense"), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenIncorrectClient_whenGetAllLimitsByBankAccountNumber_thenExceptionThrown() {
        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                limitService.getAllLimitsByBankAccountNumber("non existed", "product"));

        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Client", "bankAccountNumber", "non existed"),
                ex.getMessage());
    }

    @Test
    @Transactional
    public void givenLimitAndCorrectClientAndExpense_whenGetAllLimitsByBankAccountNumber_thenListReturned() {
        List<LimitResponseDTO> listWithOneProductLimit = limitService
                .getAllLimitsByBankAccountNumber("1234567890", "product");

        assertThat(listWithOneProductLimit).isNotEmpty().hasSize(1)
                .extracting(LimitResponseDTO::getLimit_sum).containsOnly(10d);
        assertThat(listWithOneProductLimit.get(0).getLimit_expense_category()).isEqualTo("product");
    }

    @Test
    @Transactional
    public void givenTwoLimitsAndClientAndNoExpense_whenGetAllLimitsByBankAccountNumber_thenListReturned() {
        List<LimitResponseDTO> listWithTwoDifferentByExpenseLimits = limitService
                .getAllLimitsByBankAccountNumber("1234567890", "incorrect expense");

        assertThat(listWithTwoDifferentByExpenseLimits).isNotEmpty().hasSize(2)
                .extracting(LimitResponseDTO::getLimit_expense_category).containsOnly("product", "service");
        assertThat(listWithTwoDifferentByExpenseLimits)
                .extracting(LimitResponseDTO::getLimit_sum).containsOnly(Double.valueOf(10), Double.valueOf(10));
    }

    @Test
    @Transactional
    public void givenExistedClientWithTwoLimits_whenUpdateAllLastLimits_thenLimitsHasCorrectUpdatedTime() {
        Client client = clientService.getClientByBankAccountNumber("1234567890");
        Limit limitProductExpense = client.getClientLimits().get(0);
        Limit limitServiceExpense = client.getClientLimits().get(1);

        limitProductExpense.setLimitDateTime(LocalDateTime.now().minusDays(31));
        limitServiceExpense.setLimitDateTime(LocalDateTime.now().minusDays(31));

        limitService.updateAllLastLimits();

        assertThat(limitProductExpense.getLimitDateTime().toLocalDate()).isEqualTo(LocalDateTime.now().toLocalDate());
        assertThat(limitServiceExpense.getLimitDateTime().toLocalDate()).isEqualTo(LocalDateTime.now().toLocalDate());
    }

    @Transactional
    public Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAccountFrom("1234567890");
        transaction.setAccountTo("0987654321");
        transaction.setExpenseCategory(ExpenseCategoryType.PRODUCT);
        transaction.setCurrencyShortname(CurrencyType.RUB);
        transaction.setSum(BigDecimal.valueOf(500));
        transaction.setDatetime(LocalDateTime.now());
        transaction.setTransactionClient(clientService.getClientByBankAccountNumber("1234567890"));

        return transactionRepository.save(transaction);
    }

    private LimitRequestDTO getLimitRequestDTO() {
        LimitRequestDTO limit = new LimitRequestDTO();
        limit.setLimit_sum(1000d);
        limit.setLimit_currency_shortname("USD");
        limit.setLimit_expense_category("product");

        return limit;
    }

}
