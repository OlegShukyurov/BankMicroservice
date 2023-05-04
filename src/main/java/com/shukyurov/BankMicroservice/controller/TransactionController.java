package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.TransactionDTO;
import com.shukyurov.BankMicroservice.service.impl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping("/add")
    public ResponseEntity<TransactionDTO> addTransaction(@RequestBody @Valid TransactionDTO transactionDTO) {
        TransactionDTO transactionResponse = transactionService.addTransaction(transactionDTO);

        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }

}
