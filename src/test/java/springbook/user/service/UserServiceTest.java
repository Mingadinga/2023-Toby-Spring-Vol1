package springbook.user.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
import static org.mockito.Mockito.*;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLR;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "SpringTestContext-context.xml")
public class UserServiceTest {
    UserService userService;
    UserServiceImpl userServiceImpl;
    UserDao userDao;
    List<User> users;
    DataSource dataSource;
    PlatformTransactionManager transactionManager;
    MailSender mailSender;
    ApplicationContext applicationContext;

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

        // userService
        UserServiceTx userServiceTx = new UserServiceTx();
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        this.userServiceImpl = userServiceImpl;

        userServiceImpl.setUserDao(userDao);
        this.mailSender = new DummyMailSender();
        userServiceImpl.setMailSender(mailSender);

        userServiceTx.setUserService(userServiceImpl);
        this.transactionManager = new DataSourceTransactionManager(this.dataSource);
        userServiceTx.setTransactionManager(this.transactionManager);

        this.userService = userServiceTx;

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
        // 목 오브젝트 DI
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);
        // 목 오브젝트 DI
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);
        // 테스트 대상의 동작 실행
        userServiceImpl.upgradeLevels();

        // Mock UserDao 검증
        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndLevel(updated.get(0), "2seeun", Level.SILVER);
        checkUserAndLevel(updated.get(1), "4nyaong", Level.GOLD);

        // Mock MailSender 검증
        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    @Test
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        // Mock UserDao
        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users); // 반환값 정의
        userServiceImpl.setUserDao(mockUserDao);

        // Mock MailSender
        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender); // 반환값이 필요하지 않다면 정의 x

        userServiceImpl.upgradeLevels();

        // mockUserDao의 update가 두번 호출됐는지 검증
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1)); // update에 넘겨준 파라미터는 두번째 User
        assertThat(users.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(users.get(3)); // update에 넘겨준 파라미터는 네번째 User
        assertThat(users.get(3).getLevel(), is(Level.GOLD));

        // 목 오브젝트에 전달된 파라미터를 가져와 내용 검증
        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));

    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getLevel(), is(expectedLevel));
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
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(this.mailSender);

        // 부가기능과 기능위임을 담당하는 TransactionHandler 생성
//        TransactionHandler txHandler = new TransactionHandler();
//        txHandler.setTarget(testUserService);
//        txHandler.setTransactionManager(transactionManager);
//        txHandler.setPattern("upgradeLevels");

        // Proxy 생성
//        UserService txUserService = (UserService) Proxy.newProxyInstance(
//                getClass().getClassLoader(), new Class[] {UserService.class}, txHandler);

        // 컨텍스트에서 TxProxyFactoryBean을 직접 가져와서 타깃 변경
        // 변경된 타깃으로 다이나믹 프록시 다시 생성
        ProxyFactoryBean txProxyFactoryBean = applicationContext.getBean("&userService", ProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for(User user: users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
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

    static class TestUserService extends UserServiceImpl {
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