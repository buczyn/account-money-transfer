package org.example.amt.rest.dto;

import org.example.amt.model.Account;
import org.example.amt.model.TransferCompleted;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DtoMapper {

    @Mapping(source = "id", target = "accountId")
    AccountDto toAccountDto(Account account);

    @Mapping(source = "receiverAccountId", target = "accountTo")
    TransfersDto.TransferDoneDto toTransferDoneDto(TransferCompleted transferCompleted);
}
