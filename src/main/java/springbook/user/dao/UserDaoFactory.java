package springbook.user.dao;

public class UserDaoFactory {
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    private NConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }
}
