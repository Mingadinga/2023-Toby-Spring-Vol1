package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

public class CustomUserDuplicateSQLExceptionTranslator implements SQLExceptionTranslator {
    private String errorCode;
    private Class<? extends DataAccessException> exceptionClass;

    public CustomUserDuplicateSQLExceptionTranslator() {
        this.errorCode = "1062";
        this.exceptionClass = DuplicateUserException.class;
    }

    @Override
    public DataAccessException translate(String task, String sql, SQLException sqlException) {
        if (sqlException.getErrorCode() == Integer.parseInt(errorCode)) {
            try {
                return exceptionClass.getDeclaredConstructor(String.class, Throwable.class).newInstance(
                        "Duplicate user: " + sqlException.getMessage(), sqlException);
            } catch (Exception e) {
                // rethrow exception if anything goes wrong
                throw new RuntimeException(e);
            }
        } else {
            return new SQLExceptionSubclassTranslator().translate(task, sql, sqlException);
        }
    }
}
