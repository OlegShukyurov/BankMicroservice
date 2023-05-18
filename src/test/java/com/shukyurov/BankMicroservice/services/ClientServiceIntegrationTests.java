package com.shukyurov.BankMicroservice.services;

import com.shukyurov.BankMicroservice.AbstractIntegrationTests;
import com.shukyurov.BankMicroservice.exception.ResourceAlreadyExistsException;
import com.shukyurov.BankMicroservice.exception.ResourceNotFoundException;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import com.shukyurov.BankMicroservice.repository.LimitRepository;
import com.shukyurov.BankMicroservice.repository.TransactionRepository;
import com.shukyurov.BankMicroservice.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClientServiceIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private ClientServiceImpl clientServiceImpl;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitRepository limitRepository;

    @AfterEach
    @Transactional
    public void resetDB() {
        transactionRepository.deleteAll();
        limitRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    public void givenCorrectClientDTO_whenAddClient_thenClientReturnedAndNoExceptionThrown() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBankAccountNumber("1234567890");
        ClientDTO returned = clientServiceImpl.addClient(clientDTO);
        Optional<Client> client = clientRepository.findByBankAccountNumber("1234567890");

        assertThat(returned.getBankAccountNumber()).isEqualTo("1234567890");
        assertThat(client.get().getBankAccountNumber()).isEqualTo("1234567890");
        assertThat(client.get().getClientLimits()).isNotNull();
        assertThat(client.get().getClientLimits()).hasSize(2);
        assertThat(client.get().getId()).isNotNull();
        assertThat(client.get().getClientTransactions()).isNotNull();
        assertThat(client.get().getClientLimits().get(0).getLimitSum()).isZero();
        assertThat(client.get().getClientLimits().get(0).getRemainingMonthLimit()).isZero();
        assertThat(client.get().getClientLimits().get(0).getLimitClient().getId()).isEqualTo(client.get().getId());
        assertThat(client.get().getClientLimits().get(0).getLimitTransactions()).isNotNull();
        assertThat(client.get().getClientLimits().get(0).getLimitExpenseCategory().getExpenseCategoryType()).isEqualTo("product");
        assertThat(client.get().getClientLimits().get(1).getLimitSum()).isZero();
        assertThat(client.get().getClientLimits().get(1).getRemainingMonthLimit()).isZero();
        assertThat(client.get().getClientLimits().get(1).getLimitClient().getId()).isEqualTo(client.get().getId());
        assertThat(client.get().getClientLimits().get(1).getLimitTransactions()).isNotNull();
        assertThat(client.get().getClientLimits().get(1).getLimitExpenseCategory().getExpenseCategoryType()).isEqualTo("service");
    }

    @Test
    @Transactional
    public void givenExistedClient_whenAddClient_thenExceptionThrown() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setBankAccountNumber("1234567890");
        clientServiceImpl.addClient(clientDTO);

        Throwable ex = assertThrows(ResourceAlreadyExistsException.class, () ->
                clientServiceImpl.addClient(clientDTO));

        assertEquals("'Client' already exists", ex.getMessage());
    }

    @Test
    @Transactional
    public void givenCorrectTransaction_whenAddTransactionToClient_thenTransactionReturnedAndNoExceptionThrown() {
        ClientDTO correctClient1 = new ClientDTO();
        correctClient1.setBankAccountNumber("1234567890");
        ClientDTO correctClient2 = new ClientDTO();
        correctClient2.setBankAccountNumber("0987654321");

        clientServiceImpl.addClient(correctClient1);
        clientServiceImpl.addClient(correctClient2);

        Transaction transaction = getTransaction();
        Transaction returned = clientServiceImpl.addTransactionToClient(transaction);

        assertThat(returned).isNotNull();
        assertThat(returned.getTransactionClient()).isNotNull();
        assertThat(returned.getTransactionClient().getBankAccountNumber()).isEqualTo("1234567890");
        assertThat(returned.getAccountFrom()).isEqualTo("1234567890");
        assertThat(returned.getTransactionClient().getClientTransactions().get(0)).isNotNull();
    }

    @Test
    public void givenTransactionWithNonExistedClients_whenAddTransactionToClient_thenExceptionThrown() {
        Transaction transaction = getTransaction();

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                clientServiceImpl.addTransactionToClient(transaction));
        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Clients", "bankAccountNumber",
                transaction.getAccountFrom() + " and " + transaction.getAccountTo()), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenTransactionWithOneExistedClient_whenAddTransactionToClient_thenExceptionThrown() {
        ClientDTO correctClient1 = new ClientDTO();
        correctClient1.setBankAccountNumber("1234567890");
        clientServiceImpl.addClient(correctClient1);
        Transaction transaction = getTransaction();

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                clientServiceImpl.addTransactionToClient(transaction));
        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Clients", "bankAccountNumber",
                transaction.getAccountFrom() + " and " + transaction.getAccountTo()), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenCorrectBankAccountNumber_whenFindByBankAccountNumber_thenClientDTOReturnedAndNoExceptionThrown() {
        ClientDTO correctClient1 = new ClientDTO();
        correctClient1.setBankAccountNumber("1234567890");
        clientServiceImpl.addClient(correctClient1);
        ClientDTO returned = clientServiceImpl.getClientDTOByBankAccountNumber("1234567890");

        assertEquals(returned.getBankAccountNumber(), correctClient1.getBankAccountNumber());
    }

    @Test
    public void givenIncorrectBankAccountNumber_whenFindByBankAccountNumber_thenExceptionThrownAndNoClientDTOReturned() {
        String ba = new String("incorrect bankAccountNumber");

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                clientServiceImpl.getClientDTOByBankAccountNumber(ba));
        assertEquals(String.format("'%s' not found with '%s' : '%s'", "ClientDTO", "bankAccountNumber", ba), ex.getMessage());
    }

    @Test
    @Transactional
    public void givenCorrectBankAccountNumber_whenFindByBankAccountNumber_thenClientReturnedAndNoExceptionThrown() {
        ClientDTO correctClient1 = new ClientDTO();
        correctClient1.setBankAccountNumber("1234567890");
        clientServiceImpl.addClient(correctClient1);
        Client returned = clientServiceImpl.getClientByBankAccountNumber("1234567890");

        assertEquals(returned.getBankAccountNumber(), correctClient1.getBankAccountNumber());
    }

    @Test
    public void givenIncorrectBankAccountNumber_whenFindByBankAccountNumber_thenExceptionThrownAndNoClientReturned() {
        String ba = new String("incorrect bankAccountNumber");

        Throwable ex = assertThrows(ResourceNotFoundException.class, () ->
                clientServiceImpl.getClientByBankAccountNumber(ba));
        assertEquals(String.format("'%s' not found with '%s' : '%s'", "Client", "bankAccountNumber", ba), ex.getMessage());
    }

    @Test
    public void givenTwoCorrectClients_whenGetAllClients_thenNotEmptyListReturned() {
        ClientDTO correctClient1 = new ClientDTO();
        correctClient1.setBankAccountNumber("1234567890");
        ClientDTO correctClient2 = new ClientDTO();
        correctClient2.setBankAccountNumber("0987654321");

        clientServiceImpl.addClient(correctClient1);
        clientServiceImpl.addClient(correctClient2);
        List<ClientDTO> notEmptyList = clientServiceImpl.getAllClients();

        assertThat(notEmptyList).isNotEmpty().hasSize(2).extracting(ClientDTO::getBankAccountNumber)
                .containsOnly(correctClient1.getBankAccountNumber(), correctClient2.getBankAccountNumber());
    }

    @Test
    public void givenNoClients_whenGetAllClients_thenEmptyListReturned() {
        List<ClientDTO> emptyList = clientServiceImpl.getAllClients();

        assertThat(emptyList).isEmpty();
    }

    private Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAccountFrom("1234567890");
        transaction.setAccountTo("0987654321");
        transaction.setExpenseCategory(ExpenseCategoryType.SERVICE);
        transaction.setCurrencyShortname(CurrencyType.RUB);
        transaction.setSum(BigDecimal.ONE);
        transaction.setDatetime(LocalDateTime.now());

        return transaction;
    }

}
