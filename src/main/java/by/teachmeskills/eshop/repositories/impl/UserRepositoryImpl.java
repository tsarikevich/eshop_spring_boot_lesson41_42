package by.teachmeskills.eshop.repositories.impl;

import by.teachmeskills.eshop.entities.User;
import by.teachmeskills.eshop.repositories.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_ALL_USERS = "SELECT * FROM ESHOP.USERS";
    private static final String GET_LAST_INSERT_USER = "SELECT * FROM ESHOP.USERS WHERE ID=(SELECT MAX(ID) FROM ESHOP.USERS)";
    private static final String INSERT_NEW_USER = "INSERT INTO ESHOP.USERS (LOGIN, NAME, SURNAME, PASSWORD, DATE_OF_BIRTH, EMAIL) values (?,?,?,?,?,?)";
    private static final String UPDATE_BALANCE_USER_BY_ID = "UPDATE ESHOP.USERS SET BALANCE=? WHERE ID=?";
    private static final String DELETE_USER_BY_ID = "DELETE FROM ESHOP.USERS WHERE ID=?";
    private static final String GET_USER_BY_ID = "SELECT * FROM ESHOP.USERS WHERE ID=?";
    private static final String GET_USER_BY_LOGIN_AND_PASSWORD = "SELECT * FROM ESHOP.USERS WHERE LOGIN=? AND PASSWORD=?";
    private static final String GET_USER_BY_LOGIN = "SELECT * FROM ESHOP.USERS WHERE LOGIN=?";

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User entity) {
        jdbcTemplate.update(INSERT_NEW_USER, entity.getLogin(), entity.getName(), entity.getSurname(),
                entity.getPassword(), entity.getBirthDate(), entity.getEmail());
        return getLastInsertUserFromDB();
    }

    @Override
    public List<User> read() {
        return jdbcTemplate.query(GET_ALL_USERS, (rs, rowNum) -> getUserFromResultSet(rs));
    }

    @Override
    public User update(User entity) {
        jdbcTemplate.update(UPDATE_BALANCE_USER_BY_ID, entity.getBalance(), entity.getId());
        return getUserById(entity.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_USER_BY_ID, id);
    }

    @Override
    public User getUserFromBaseByLoginAndPassword(User user) {
        return jdbcTemplate.query(GET_USER_BY_LOGIN_AND_PASSWORD,
                        (rs, rowNum) -> getUserFromResultSet(rs), user.getLogin(), user.getPassword())
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public User getUserFromBaseByLogin(User user) {
        return jdbcTemplate.query(GET_USER_BY_LOGIN, (rs, rowNum) -> getUserFromResultSet(rs),
                user.getLogin())
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public User getUserById(int userId) {
        return jdbcTemplate.query(GET_USER_BY_ID,
                (rs, rowNum) -> getUserFromResultSet(rs), userId)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean isUserSuitByLoginAndPassword(User user) {
        User userFromBase = getUserFromBaseByLoginAndPassword(user);
        return Optional.ofNullable(userFromBase).isPresent();
    }

    private User getLastInsertUserFromDB() {
        return jdbcTemplate.query(GET_LAST_INSERT_USER,
                (rs, rowNum) -> getUserFromResultSet(rs))
                .stream()
                .findAny()
                .orElse(null);
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        String surname = resultSet.getString("surname");
        String password = resultSet.getString("password");
        LocalDate birthDate = resultSet.getObject("date_of_birth", LocalDate.class);
        String email = resultSet.getString("email");
        BigDecimal balance = resultSet.getBigDecimal("balance");
        return User.builder()
                .id(id)
                .login(login)
                .name(name)
                .surname(surname)
                .password(password)
                .birthDate(birthDate)
                .email(email)
                .balance(balance)
                .build();
    }
}
