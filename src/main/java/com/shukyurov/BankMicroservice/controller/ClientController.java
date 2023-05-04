package com.shukyurov.BankMicroservice.controller;

import com.shukyurov.BankMicroservice.model.dto.ClientDTO;
import com.shukyurov.BankMicroservice.service.impl.ClientServiceImpl;
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

    private final ClientServiceImpl clientService;

    @PostMapping("/add")
    public ResponseEntity<ClientDTO> addClient(@RequestBody @Valid ClientDTO clientDTO) {
        ClientDTO clientResponse = clientService.addClient(clientDTO);

        return new ResponseEntity<>(clientResponse, HttpStatus.OK);
    }

    @GetMapping("/{bankAccountNumber}")
    public ResponseEntity<ClientDTO> getClientByBankAccountNumber(@PathVariable("bankAccountNumber") String bankAccountNumber) {
        ClientDTO clientResponse = clientService.getClientByBankAccountNumber(bankAccountNumber);

        return new ResponseEntity<>(clientResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clientResponseList = clientService.getAllClients();

        return new ResponseEntity<>(clientResponseList, HttpStatus.OK);
    }

}
