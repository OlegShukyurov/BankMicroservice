package com.shukyurov.BankMicroservice.services;

import com.shukyurov.BankMicroservice.AbstractIntegrationTests;
import com.shukyurov.BankMicroservice.client.ConversionClient;
import com.shukyurov.BankMicroservice.exception.ExchangeRateException;
import com.shukyurov.BankMicroservice.mapper.ConversionMapper;
import com.shukyurov.BankMicroservice.model.ExchangeType;
import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;
import com.shukyurov.BankMicroservice.model.entity.Conversion;
import com.shukyurov.BankMicroservice.repository.ConversionRepository;
import com.shukyurov.BankMicroservice.service.ConversionService;
import com.shukyurov.BankMicroservice.service.impl.ConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConversionServiceIntegrationTests extends AbstractIntegrationTests {

    @Value("${spring.client.apikey}")
    private String apiKey;

    @Value("${spring.client.url}")
    private String clientUrl;

    @Autowired
    private ConversionServiceImpl conversionService;

    @Autowired
    private ConversionRepository conversionRepository;

    @Test
    public void givenCorrectSymbolAndApikey_whenGetConversionDTO_thenConversionDTOReturned() {
        ConversionDTO returned = conversionService.getConversionDTO("USD/RUB", apiKey);

        assertThat(returned).isNotNull();
        assertThat(returned.getSymbol()).isEqualTo("USD/RUB");
        assertThat(returned.getRate()).isNotNull();
        assertThat(returned.getRate()).isNotNegative();
        assertThat(returned.getRate()).isNotZero();
        LocalDate madeAt = LocalDate.ofInstant(Instant.ofEpochSecond(returned.getTimestamp()), ZoneId.systemDefault());
        assertThat(madeAt).isBeforeOrEqualTo(LocalDate.now());
    }

    @Test
    public void givenIncorrectSymbolAndApikey_whenGetConversionDTO_thenExceptionThrown() {
        Throwable ex = assertThrows(ExchangeRateException.class, () ->
                conversionService.getConversionDTO("incorrect symbol", "incorrect apiKey"));

        assertEquals(String.format("Could not get exchange rate from resource : '%s'", clientUrl),
                ex.getMessage());
    }

    @Test
    public void givenIncorrectFeignClient_whenGetConversionDTO_thenExceptionThrown() {
        ConversionClient mockClient = Mockito.mock(ConversionClient.class);
        Mockito.when(mockClient.getConversionDTO(Mockito.any(), Mockito.any())).thenReturn(null);
        ConversionService conversionServiceWithMockClient = new ConversionServiceImpl(new ConversionMapper(), mockClient,
               conversionRepository);

        Throwable ex = assertThrows(ExchangeRateException.class, () ->
                conversionServiceWithMockClient.getConversionDTO("USD/RUB", apiKey));

        assertEquals(String.format("Could not get exchange rate from resource : '%s'", clientUrl),
                ex.getMessage());

        Mockito.verify(mockClient, Mockito.times(1)).getConversionDTO(Mockito.any(), Mockito.any());
    }

    @Test
    public void givenCorrectLimitCurrencyAndTransactionCurrency_whenGetLastExchangeRate_thenLastExchangeRateReturned() {
        BigDecimal lastExchangeRate = conversionService.getLastExchangeRate("USD", "RUB");

        assertThat(lastExchangeRate).isNotNull();
        assertThat(lastExchangeRate).isNotNegative();
        assertThat(lastExchangeRate).isNotZero();
    }

    @Test
    public void givenIncorrectLimitCurrencyAndTransactionCurrency_whenGetLastExchangeRate_thenExceptionThrown() {
        assertThrows(Exception.class, () -> conversionService.getLastExchangeRate
                ("incorrect limit currency", "incorrect transaction currency"));
    }

    @Test
    public void givenEmptyConversionRepository_whenSaveAllConversions_thenListReturned() {
        conversionRepository.deleteAll();
        conversionService.saveAllConversions();

        List<Conversion> allSavedConversions = conversionRepository.findAll();

        assertThat(allSavedConversions).isNotEmpty().hasSize(2)
                .extracting(Conversion::getSymbol).containsOnly(ExchangeType.USD_KZT, ExchangeType.USD_RUB);
        assertThat(allSavedConversions.get(0).getRateOnPreviousClose()).isNotNull();
        assertThat(allSavedConversions.get(0).getRateOnPreviousClose()).isNotNegative();
        assertThat(allSavedConversions.get(0).getRateOnPreviousClose()).isNotZero();
        assertThat(allSavedConversions.get(1).getRateOnPreviousClose()).isNotNull();
        assertThat(allSavedConversions.get(1).getRateOnPreviousClose()).isNotNegative();
        assertThat(allSavedConversions.get(1).getRateOnPreviousClose()).isNotZero();
    }

}
