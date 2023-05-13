package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.TransactionRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.TransactionResponseDTO;
import com.shukyurov.BankMicroservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/add")
    public ResponseEntity<TransactionRequestDTO> addTransaction(@RequestBody @Valid TransactionRequestDTO transactionRequestDTO) {
        return new ResponseEntity<>(transactionService.addTransaction(transactionRequestDTO), HttpStatus.OK);
    }

    @GetMapping("/getAll/{bankAccountNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable("bankAccountNumber") String bankAccountNumber) {
        return new ResponseEntity<>(transactionService.getAllTransactions(bankAccountNumber), HttpStatus.OK);
    }

    @GetMapping("/getAllLimitExceeded/{bankAccountNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> getAllLimitExceededTransactions(@PathVariable("bankAccountNumber") String bankAccountNumber,
                                                                                        @RequestParam(name = "currency", required = false) String currency,
                                                                                        @RequestParam(name = "expense", required = false) String expense) {
        return new ResponseEntity<>(transactionService.getAllLimitExceededTransactions(bankAccountNumber, currency, expense), HttpStatus.OK);
    }

}
