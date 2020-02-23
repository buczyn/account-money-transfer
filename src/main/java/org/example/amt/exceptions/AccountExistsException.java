package org.example.amt.exceptions;

import java.text.MessageFormat;

public class AccountExistsException extends AccountException {

    public AccountExistsException(long id) {
        super(MessageFormat.format("Account {0} already exists.", id));
    }
}
