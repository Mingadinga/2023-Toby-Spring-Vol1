package springbook.user.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class CustomSQLErrorCodeTranslator extends SQLErrorCodeSQLExceptionTranslator {

    private static final String DUPLICATE_ERROR_CODE = "1062";
    private static final Class<? extends DataAccessException> DUPLICATE_EXCEPTION_CLASS = DuplicateUserException.class;

    public CustomSQLErrorCodeTranslator(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected DataAccessException customTranslate(String task, String sql, SQLException ex) {
        if (DUPLICATE_ERROR_CODE.equals(String.valueOf(ex.getErrorCode()))) {
            try {
                return DUPLICATE_EXCEPTION_CLASS.getDeclaredConstructor(String.class, Throwable.class).newInstance("Duplicate user", ex);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return super.customTranslate("Add user", sql, ex); // null
    }
}
