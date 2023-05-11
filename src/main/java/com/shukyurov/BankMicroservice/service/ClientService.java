package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.model.entity.Limit;
import com.shukyurov.BankMicroservice.model.entity.Transaction;

import java.util.List;

public interface ClientService {

    ClientDTO addClient(ClientDTO clientDTO);

    ClientDTO getClientDTOByBankAccountNumber(String bankAccountNumber);

    Client getClientByBankAccountNumber(String bankAccountNumber);

    List<ClientDTO> getAllClients();

    Transaction addTransactionToClient(Transaction transaction);

    Limit addLimitToClient(String bankAccountNumber, Limit limit);

}
