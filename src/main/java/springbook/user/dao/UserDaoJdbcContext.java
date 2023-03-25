package springbook.user.dao;

import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.jdbc.support.SQLErrorCodesFactory;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoJdbcContext {

    JdbcContext jdbcContext;

    DataSource dataSource;

    public void setJdbcContext(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public void addWithCustomTranslator(User user) {
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
            SQLErrorCodes sqlErrorCodes = SQLErrorCodesFactory.getInstance().getErrorCodes(dataSource);
            sqlErrorCodes.setCustomSqlExceptionTranslatorClass(CustomUserDuplicateSQLExceptionTranslator.class);

            SQLErrorCodeSQLExceptionTranslator sqlExceptionTranslator = new SQLErrorCodeSQLExceptionTranslator();
            sqlExceptionTranslator.setSqlErrorCodes(sqlErrorCodes);

            throw sqlExceptionTranslator.translate("Add user", sql, e);
        }
    }

    public void addWithCustomSQLErrorCodeTranslator(User user) {
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
            SQLErrorCodeSQLExceptionTranslator sqlExceptionTranslator = new CustomSQLErrorCodeTranslator(dataSource);
            throw sqlExceptionTranslator.translate("Adding user failed", sql, e);
        }
    }
}

