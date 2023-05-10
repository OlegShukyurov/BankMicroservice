package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/add")
    public ResponseEntity<ClientDTO> addClient(@RequestBody @Valid ClientDTO clientDTO) {
        return new ResponseEntity<>(clientService.addClient(clientDTO), HttpStatus.OK);
    }

    @GetMapping("/{bankAccountNumber}")
    public ResponseEntity<ClientDTO> getClientByBankAccountNumber(@PathVariable("bankAccountNumber") String bankAccountNumber) {
        return new ResponseEntity<>(clientService.getClientDTOByBankAccountNumber(bankAccountNumber), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return new ResponseEntity<>(clientService.getAllClients(), HttpStatus.OK);
    }

}
