package by.teachmeskills.eshop.repositories.impl;

import by.teachmeskills.eshop.entities.Category;
import by.teachmeskills.eshop.repositories.CategoryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_ALL_CATEGORIES = "SELECT * FROM ESHOP.CATEGORIES";
    private static final String GET_LAST_INSERT_CATEGORY = "SELECT * FROM ESHOP.CATEGORIES WHERE ID=(SELECT MAX(ID) FROM ESHOP.CATEGORIES)";
    private static final String INSERT_NEW_CATEGORY = "INSERT INTO ESHOP.CATEGORIES (NAME, RATING) values (?, ?)";
    private static final String GET_CATEGORY_BY_ID = "SELECT * FROM ESHOP.CATEGORIES WHERE ID=?";
    private static final String UPDATE_CATEGORY_BY_ID = "UPDATE ESHOP.CATEGORIES SET NAME=?, RATING=? WHERE ID=?";
    private static final String DELETE_CATEGORY_BY_ID = "DELETE FROM ESHOP.CATEGORIES WHERE ID=?";

    public CategoryRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Category create(Category entity) {
        jdbcTemplate.update(INSERT_NEW_CATEGORY, entity.getName(), entity.getRating());
        return getLastInsertCategoryFromBase();
    }

    @Override
    public List<Category> read() {
        return jdbcTemplate.query(GET_ALL_CATEGORIES, (rs, rowNum) -> getResultSetCategory(rs));
    }

    @Override
    public Category update(Category entity) {
        return jdbcTemplate.queryForObject(UPDATE_CATEGORY_BY_ID,
                (rs, rowNum) -> getResultSetCategory(rs), entity.getName(), entity.getRating(), entity.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_CATEGORY_BY_ID, id);
    }

    private Category getLastInsertCategoryFromBase() {
        return jdbcTemplate.query(GET_LAST_INSERT_CATEGORY,
                        (rs, rowNum) -> getResultSetCategory(rs))
                .stream()
                .findAny()
                .orElse(null);
    }

    private Category getCategoryById(int id) {
        return jdbcTemplate.query(GET_CATEGORY_BY_ID,
                        (rs, rowNum) -> getResultSetCategory(rs), id)
                .stream()
                .findAny()
                .orElse(null);
    }

    private Category getResultSetCategory(ResultSet resultSet) throws SQLException {
        int idFromBase = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int rating = resultSet.getInt("rating");
        return Category.builder()
                .id(idFromBase)
                .name(name)
                .rating(rating)
                .build();
    }
}
