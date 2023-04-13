package com.shukyurov.BankMicroservice.service;

import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;

public interface ConversionService {

    ConversionDTO getConversionDTO(String symbol, String apikey);

}
