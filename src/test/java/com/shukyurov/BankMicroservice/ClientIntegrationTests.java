package com.shukyurov.BankMicroservice;

import com.shukyurov.BankMicroservice.model.entity.Client;
import com.shukyurov.BankMicroservice.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClientIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void whenGetAllClientsWithCleanDB_thenShouldBeEmpty() {
        List<Client> clientList = clientRepository.findAll();

        assertThat(clientList).isEmpty();
    }

    // TODO
}
