package springbook.user.dao;

import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLErrorCodes;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoJdbcContext {

    JdbcContext jdbcContext;

    public void setDataSource(DataSource dataSource) {
        this.jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(dataSource);
    }

    private CustomSQLErrorCodesTranslation getCustomSQLErrorCodesTranslation() {
        CustomSQLErrorCodesTranslation c = new CustomSQLErrorCodesTranslation();
        c.setErrorCodes(new String[] {"1062"}); // MySQL DuplicateKeyError
        c.setExceptionClass(DuplicateUserException.class);
        return c;
    }

    public void add(User user) {
        String sql = "insert into users(id, name, password) values(?,?,?)";
        StatementStrategy addStatementStrategy = c -> {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            return ps;
        };
        try {
            jdbcContext.workWithStatementStrategy(addStatementStrategy);
        } catch (SQLException e) {
            CustomSQLErrorCodesTranslation addErrorCode = getCustomSQLErrorCodesTranslation();

            SQLErrorCodes sqlErrorCodes = new SQLErrorCodes();
            sqlErrorCodes.setCustomTranslations(new CustomSQLErrorCodesTranslation[] {addErrorCode});
            SQLErrorCodeSQLExceptionTranslator sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator();

            sqlExceptionTranslator.setSqlErrorCodes(sqlErrorCodes);

            throw sqlExceptionTranslator.translate("Add user", sql, e);
        }
    }
}

/*
public void add(User user) {
        String sql = "";
        try {
            // static sql을 사용하는 콜백
            sql = "insert into users(id, name, password) values(?,?,?)";
            jdbcTemplate.update(sql, user.getId(), user.getName(), user.getPassword());
        } catch (DataAccessException e) {
            addErrorCode = getAddErrorCode();

            SQLErrorCodes sqlErrorCodes = new SQLErrorCodes();
            sqlErrorCodes.setCustomTranslations(new CustomSQLErrorCodesTranslation[] {addErrorCode});
            SQLErrorCodeSQLExceptionTranslator sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator();

            sqlExceptionTranslator.setSqlErrorCodes(sqlErrorCodes);

            sqlExceptionTranslator.translate("Add user", sql, )
            throw sqlExceptionTranslator.translate("Add user", sql, e.getCause());
        }
    }

 */