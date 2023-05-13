package com.shukyurov.BankMicroservice.services;

import com.shukyurov.BankMicroservice.AbstractIntegrationTests;
import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import com.shukyurov.BankMicroservice.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClientServiceImplIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private ClientServiceImpl clientServiceImpl;

    @MockBean
    private ClientRepository clientRepository;

    @Test
    public void whenGetAllClientsWithCleanDB_thenShouldBeEmpty() {
        List<Client> clientList = clientRepository.findAll();

        assertThat(clientList).isEmpty();
    }

}
