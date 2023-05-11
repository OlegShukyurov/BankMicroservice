package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.LimitRequestDTO;
import com.shukyurov.BankMicroservice.model.dto.LimitResponseDTO;
import com.shukyurov.BankMicroservice.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping("/increase/{bankAccountNumber}")
    public ResponseEntity<LimitResponseDTO> increaseLimitByBankAccountNumber(@PathVariable("bankAccountNumber") String bankAccountNumber,
                                                                             @RequestParam(name = "expense") String expense,
                                                                             @RequestParam(name = "sum") Double sum) {
        return new ResponseEntity<>(limitService.increaseLimitByBankAccountNumber(bankAccountNumber, expense, sum), HttpStatus.OK);
    }

    @GetMapping("/getAll/{bankAccountNumber}")
    public ResponseEntity<List<LimitResponseDTO>> getAllLimitsByBankAccountNumber(@PathVariable("bankAccountNumber") String bankAccountNumber,
                                                                                  @RequestParam(name = "expense", required = false) String expense) {
        return new ResponseEntity<>(limitService.getAllLimitsByBankAccountNumber(bankAccountNumber, expense), HttpStatus.OK);
    }

}
