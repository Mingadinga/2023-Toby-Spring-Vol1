package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import java.util.ArrayList;
import java.util.List;

public class MockUserDao implements UserDao {
    private List<User> users; // 레벨 업그레이드 후보

    private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트

    public MockUserDao(List<User> users) {
        this.users = users;
    }

    public List<User> getAll() {
        return users;
    }

    public List<User> getUpdated() {
        return updated;
    }

    // 업데이트가 호출되면 해당 User을 업그레이드 요청 목록에 추가하여
    // 간접적으로 업데이트 수행됨을 알림
    @Override
    public void update(User user) {
        updated.add(user);
    }

    @Override
    public void add(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
        throw new UnsupportedOperationException();
    }

}
