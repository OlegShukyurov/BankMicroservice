package com.shukyurov.BankMicroservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExistsException extends RuntimeException {

    private String resourceName;

    public ResourceAlreadyExistsException(String resourceName) {
        super(String.format("%s already exists", resourceName));
        this.resourceName = resourceName;
    }

}
