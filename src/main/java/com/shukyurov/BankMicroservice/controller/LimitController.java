package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/limits")
@RequiredArgsConstructor
public class LimitController {

    private final LimitService limitService;

    @PostMapping("/add/{bankAccountNumber}")
    public ResponseEntity<LimitRequestDTO> addLimitByBankAccountNumber(@PathVariable("bankAccountNumber") String bankAccountNumber,
                                                                       @RequestBody @Valid LimitRequestDTO limitRequestDTO) {
        return new ResponseEntity<>(limitService.addLimitByBankAccountNumber(bankAccountNumber, limitRequestDTO), HttpStatus.OK);
    }

}
