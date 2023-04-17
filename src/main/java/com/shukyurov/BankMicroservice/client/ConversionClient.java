package com.shukyurov.BankMicroservice.client;

import com.shukyurov.BankMicroservice.config.FeignConfig;
import com.shukyurov.BankMicroservice.model.dto.ConversionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "conversion", url = "${spring.client.url}", configuration = FeignConfig.class)
public interface ConversionClient {

    @GetMapping("/exchange_rate")
    ConversionDTO getConversionDTO(@RequestParam("symbol") String symbol, @RequestParam("apikey") String apiKey);

}
