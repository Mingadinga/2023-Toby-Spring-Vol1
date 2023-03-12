package springbook.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {

//    DataSource dataSource;
//
//    public void setDataSource(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    JdbcContext jdbcContext;
//
//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // update()
    // 반환값 x
    // 치환자 o
    public void add(User user) throws SQLException {
        // 커스텀 전략 클래스
//        StatementStrategy addStatementStrategy = c -> {
//            PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
//            ps.setString(1, user.getId());
//            ps.setString(2, user.getName());
//            ps.setString(3, user.getPassword());
//            return ps;
//        };

        // PreparedStatement를 사용하는 콜백
//        jdbcTemplate.update(con -> {
//            PreparedStatement ps = con.prepareStatement("insert into users(id, name, password) values(?,?,?)");
//            ps.setString(1, user.getId());
//            ps.setString(2, user.getName());
//            ps.setString(3, user.getPassword());
//            return ps;
//        });

        // static sql을 사용하는 콜백
        jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }


    // queryForObject(sql, parameters, RowMapper)
    // 반환값 o
    // 치환자 o
    public User get(String id) throws SQLException, EmptyResultDataAccessException {
        return this.jdbcTemplate.queryForObject(
                "select * from users where id = ?",
                new Object[]{id},
                getUserRowMapper()
        );
    }

    // update()
    // 반환값 x
    // 치환자 x
    public void deleteAll() throws SQLException {
        // Methods dealing with prepared statements
        // PreparedStatementCallback 사용
//        jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                return con.prepareStatement("delete from users");
//            }
//        });
        // Methods dealing with static SQL (java.sql.Statement)
        // StatementCallback 사용
        jdbcTemplate.update("delete from users");
    }


    // query(PreparedStatement, ResultSetExtractor<Integer>)
    // 반환값 o
    // 치환자 x
    public int getCount() throws SQLException {
        // PreparedStatement 사용
        // PreparedStatement, ResultSetExtractor<Integer> 넘겨줌
        return this.jdbcTemplate.query(
                con -> con.prepareStatement("select count(*) from users"),
                rs1 -> { rs1.next(); return rs1.getInt(1); }
        );
    }

    // query(sql, RowMapper)
    // 반환값 o
    // 치환자 x
    public List<User> getAll() {
        return this.jdbcTemplate.query(
                "select * from users order by id",
                getUserRowMapper()
        );

    }

    private static RowMapper<User> getUserRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        };
    }
}
