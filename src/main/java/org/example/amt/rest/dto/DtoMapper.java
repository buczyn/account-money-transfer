package org.example.amt.rest.dto;

import org.example.amt.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DtoMapper {

    @Mapping(source = "id", target = "accountId")
    AccountDto toAccountDto(Account account);
}
