package com.shukyurov.BankMicroservice;

import com.shukyurov.BankMicroservice.model.entity.Conversion;
import com.shukyurov.BankMicroservice.repository.ConversionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ConversionIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private ConversionRepository conversionRepository;

    @Test
    public void whenGetAllConversionsAfterStart_thenShouldNotBeEmpty() {
        List<Conversion> conversionList = conversionRepository.findAll();

        assertThat(conversionList).isNotEmpty();
    }

    // TODO
}
