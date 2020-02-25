package org.example.amt.exceptions;

import java.text.MessageFormat;

public class AccountNotFoundException extends AccountException {

    public AccountNotFoundException(long id) {
        super(MessageFormat.format("Account {0} does not exist.", id));
    }
}
