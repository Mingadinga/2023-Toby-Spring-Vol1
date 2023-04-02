package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.Level;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLR;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "SpringTestContext-context.xml")
public class UserServiceTest {
    UserService userService;
    UserDao userDao;
    List<User> users;
    DataSource dataSource;
    PlatformTransactionManager transactionManager;
    MailSender mailSender;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("1minpearl", "민휘", "lololo", Level.BASIC, 49, 0, "minpearl0826@gmail.com"),
                new User("2seeun", "세은", "030614", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 10, "seeun@gmail.com"),
                new User("3kong", "콩이", "piggy", Level.SILVER, 60, 29, "kong@gmail.com"),
                new User("4nyaong", "냐옹이", "chatter", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLR, "nyong@gmail.com"),
                new User("5ming", "밍밍이", "cheek", Level.GOLD, 100, 100, "ming@gmail.com"));

        this.dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/toby", "root", "star0826", true);
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc();
        userDaoJdbc.setDataSource(dataSource);
        this.userDao = userDaoJdbc;
        this.userService = new UserService();
        this.userService.setUserDao(userDao);
        this.transactionManager = new DataSourceTransactionManager(this.dataSource);
        userService.setTransactionManager(this.transactionManager);
        this.mailSender = new DummyMailSender();
    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    @DirtiesContext // 컨텍스트의 DI 설정을 변경하는 테스트이다
    public void upgradeLevels() {
        userDao.deleteAll();
        for(User user:users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
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

    @Test
    public void upgradeAllOrNothing() {
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setDataSource(this.dataSource);
        testUserService.setTransactionManager(this.transactionManager);
        testUserService.setMailSender(this.mailSender);

        userDao.deleteAll();
        for(User user: users) userDao.add(user);

        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {}

        checkLevelUpgraded(users.get(1), false);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }

    static class TestUserService extends UserService {
        private String id;

        private TestUserService(String id) {
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {}

}