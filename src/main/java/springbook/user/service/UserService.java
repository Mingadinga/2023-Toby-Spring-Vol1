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
            Boolean changed = null;
            if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
                user.setLevel(Level.SILVER);
                changed = true;
            }
            else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
                user.setLevel(Level.GOLD);
                changed = true;
            }
            else if (user.getLevel() == Level.GOLD) { changed = false;}
            else { changed = false;}

            if(changed) { userDao.update(user); }
        }
    }

    // 사용자가 등록될 때 적용할만한 비즈니스 로직 : 초기화시 BASIC
    // UserDao : 사용자 정보를 담은 User 오브젝트를 받아 DB에 넣어주는데 충실한 역할
    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
