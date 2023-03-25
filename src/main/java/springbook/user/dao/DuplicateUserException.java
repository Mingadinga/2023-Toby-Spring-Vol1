package springbook.user.dao;

import org.springframework.dao.DataAccessException;

public class DuplicateUserException extends DataAccessException {

    public DuplicateUserException(String msg) {
        super(msg);
    }

    public DuplicateUserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

