package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.LimitDTO;
import com.shukyurov.BankMicroservice.service.impl.LimitServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/limits")
@RequiredArgsConstructor
public class LimitController {

    private final LimitServiceImpl limitService;

    @PostMapping("/add/{bankAccountNumber}")
    public ResponseEntity<LimitDTO> addLimitByBankAccountNumber(@PathVariable("bankAccountNumber") String bankAccountNumber,
                                                                @RequestBody @Valid LimitDTO limitDTO) {
        LimitDTO limitResponse = limitService.addLimitByBankAccountNumber(bankAccountNumber, limitDTO);

        return new ResponseEntity<>(limitResponse, HttpStatus.OK);
    }

}
