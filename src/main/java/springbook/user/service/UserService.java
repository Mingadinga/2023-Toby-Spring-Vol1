package springbook.user.service;

import springbook.user.dao.Level;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user: users) {
            if(canUpgradeLevel(user)) upgradeLevel(user);
        }
    }

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLR = 30;

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC : return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER: return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLR;
            case GOLD: return false;
            default: throw new IllegalStateException("Unknown Level: "+currentLevel);
        }
    }

    private void upgradeLevel(User user) {
        // user의 레벨을 받아서 다음 레벨로 넘겨주는 작업
        // 비즈니스 로직에서 특정 레벨의 다음 레벨을 결정하는 것보다는
        // User 정보를 가장 잘 알고 있는 User 객체에게 요청하여 다음 레벨을 정보를 얻어오자
        user.upgradeLevel();
        userDao.update(user);
    }

    // 사용자가 등록될 때 적용할만한 비즈니스 로직 : 초기화시 BASIC
    // UserDao : 사용자 정보를 담은 User 오브젝트를 받아 DB에 넣어주는데 충실한 역할
    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
