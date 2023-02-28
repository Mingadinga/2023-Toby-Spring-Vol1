package springbook.user.dao;

public class DaoFactory {
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    private NConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }
}
