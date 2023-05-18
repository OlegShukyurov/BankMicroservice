package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.exception.ResourceAlreadyExistsException;
import com.shukyurov.BankMicroservice.model.CurrencyType;
import com.shukyurov.BankMicroservice.model.ExpenseCategoryType;
import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.model.entity.Transaction;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import com.shukyurov.BankMicroservice.repository.LimitRepository;
import com.shukyurov.BankMicroservice.repository.TransactionRepository;
import com.shukyurov.BankMicroservice.service.ClientService;
import com.shukyurov.BankMicroservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;
    private final LimitRepository limitRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ClientDTO addClient(ClientDTO clientDTO) {
        if (clientRepository.findByBankAccountNumber(clientDTO.getBankAccountNumber()).isPresent()) {
            throw new ResourceAlreadyExistsException("Client");
        }
        Client savedClient = clientRepository.save(mapToEntity(clientDTO));
        enrichClient(savedClient);

        return mapToDto(savedClient);
    }

    @Override
    @Transactional
    public Transaction addTransactionToClient(Transaction transaction) {
        Client client = checkClientsExist(transaction.getAccountFrom(), transaction.getAccountTo());
        Transaction savedTransaction = transactionRepository.save(transaction);
        client.getClientTransactions().add(savedTransaction);
        savedTransaction.setTransactionClient(client);

        return savedTransaction;
    }

    @Override
    @Transactional
    public Limit addLimitToClient(String bankAccountNumber, Limit limit) {
        Client client = checkClientsExist(bankAccountNumber, bankAccountNumber);
        Limit savedLimit = limitRepository.save(limit);
        client.getClientLimits().add(savedLimit);
        savedLimit.setLimitClient(client);

        return savedLimit;
    }

    @Override
    public ClientDTO getClientDTOByBankAccountNumber(String bankAccountNumber) {
        return mapToDto(clientRepository.findByBankAccountNumber(bankAccountNumber).orElseThrow(() ->
                new ResourceNotFoundException("ClientDTO", "bankAccountNumber", bankAccountNumber)));
    }

    @Override
    public Client getClientByBankAccountNumber(String bankAccountNumber) {
        return clientRepository.findByBankAccountNumber(bankAccountNumber).orElseThrow(() ->
                new ResourceNotFoundException("Client", "bankAccountNumber", bankAccountNumber));
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private Client checkClientsExist(String accountFrom, String accountTo) {
        Optional<Client> client1 = clientRepository.findByBankAccountNumber(accountFrom);
        Optional<Client> client2 = clientRepository.findByBankAccountNumber(accountTo);

        if ((client1.isEmpty() && client2.isEmpty()) || (client1.isEmpty() && client2.isPresent()) || (client1.isPresent() && client2.isEmpty())) {
            throw new ResourceNotFoundException("Clients", "bankAccountNumber", accountFrom + " and " + accountTo);
        }

        return client1.get();
    }

    private void enrichClient(Client client) {
        client.setClientLimits(new ArrayList<>());
        client.setClientTransactions(new ArrayList<>());
        Arrays.stream(ExpenseCategoryType.values()).forEach(expenseCategoryType -> {
            Limit defaultLimit = createDefaultLimit();
            defaultLimit.setLimitExpenseCategory(expenseCategoryType);
            limitRepository.save(defaultLimit);
            defaultLimit.setLimitClient(client);
            client.getClientLimits().add(defaultLimit);
        });
    }

    private Limit createDefaultLimit() {
        Limit defaultLimit = new Limit();
        defaultLimit.setLimitSum(BigDecimal.ZERO);
        defaultLimit.setLimitCurrencyShortname(CurrencyType.USD);
        defaultLimit.setLimitDateTime(LocalDateTime.now());
        defaultLimit.setRemainingMonthLimit(defaultLimit.getLimitSum());
        defaultLimit.setLimitTransactions(new ArrayList<>());

        return defaultLimit;
    }

    private ClientDTO mapToDto(Client client) {
        return modelMapper.map(client, ClientDTO.class);
    }

    private Client mapToEntity(ClientDTO clientDTO) {
        return modelMapper.map(clientDTO, Client.class);
    }

}
