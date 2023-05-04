package com.shukyurov.BankMicroservice.service.impl;

import com.shukyurov.BankMicroservice.exception.ClientsNotFoundException;
import com.shukyurov.BankMicroservice.exception.ResourceAlreadyExistsException;
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
        Client client = clientRepository.save(mapToEntity(clientDTO));
        enrichClient(client);

        return mapToDto(client);
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
    public ClientDTO getClientByBankAccountNumber(String bankAccountNumber) {
        return mapToDto(clientRepository.findByBankAccountNumber(bankAccountNumber).orElseThrow(() ->
                new ResourceNotFoundException("Client", "bankAccountNumber", bankAccountNumber)));
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private Client checkClientsExist(String accountFrom, String accountTo) {
        Optional<Client> client1 = clientRepository.findByBankAccountNumber(accountFrom);
        Optional<Client> client2 = clientRepository.findByBankAccountNumber(accountTo);

        if (client1.isEmpty() || client2.isEmpty()) {
            throw new ClientsNotFoundException(accountFrom, accountTo);
        }

        return client1.get();
    }

    private ClientDTO mapToDto(Client client) {
        ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);

        return clientDTO;
    }

    private Client mapToEntity(ClientDTO clientDTO) {
        Client client = modelMapper.map(clientDTO, Client.class);

        return client;
    }

    private void enrichClient(Client client) {
        client.setClientLimits(new ArrayList<>());
        client.setClientTransactions(new ArrayList<>());
    }

}
