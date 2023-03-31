package springbook.user.service;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.Level;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "SpringTestContext-context.xml")
public class UserServiceTest {
    UserService userService;
    UserDao userDao;
    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("minpearl", "민휘", "lololo", Level.BASIC, 49, 0),
                new User("seeun", "세은", "030614", Level.BASIC, 50, 10),
                new User("kong", "콩이", "piggy", Level.SILVER, 60, 29),
                new User("nyaong", "냐옹이", "chatter", Level.SILVER, 60, 30),
                new User("ming", "밍밍이", "cheek", Level.GOLD, 100, 100));

        DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/toby", "root", "star0826", true);
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        userDaoJdbc.setDataSource(dataSource);
        this.userDao = userDaoJdbc;
        this.userService = new UserService();
        this.userService.setUserDao(userDao);
    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    public void upgradeLevels() {
        userDao.deleteAll();
        for(User user:users) userDao.add(user);

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    @Test
    public void add() {
        userDao.deleteAll();
        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevelRead.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }
}