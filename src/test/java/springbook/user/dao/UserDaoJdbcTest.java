package springbook.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;

public class UserDaoJdbcTest {
    private UserDaoJdbcContext daoJdbcContext;
    private DataSource dataSource;
    private User user1;

    @Before
    public void setUp() throws SQLException {
        this.user1 = new User("minpearl", "민휘", "lololo");

        DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/toby", "root", "star0826", true);
        this.daoJdbcContext = new UserDaoJdbcContext();
        daoJdbcContext.setJdbcContext(dataSource);
    }

    @Test(expected = DuplicateUserException.class)
    public void add() {
        daoJdbcContext.add(user1);
        daoJdbcContext.add(user1);
    }

    @Test(expected = DuplicateUserException.class)
    public void addWithCustomTranslator() {
        daoJdbcContext.addWithCustomTranslator(user1);
        daoJdbcContext.addWithCustomTranslator(user1);
    }

    @Test(expected = DuplicateUserException.class)
    public void addWithCustomSQLErrorCodeTranslator() {
        daoJdbcContext.addWithCustomSQLErrorCodeTranslator(user1);
        daoJdbcContext.addWithCustomSQLErrorCodeTranslator(user1);
    }

}
