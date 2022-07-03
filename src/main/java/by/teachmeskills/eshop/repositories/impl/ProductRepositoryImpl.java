package by.teachmeskills.eshop.repositories.impl;

import by.teachmeskills.eshop.entities.Product;
import by.teachmeskills.eshop.repositories.ProductRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_NEW_PRODUCT = "INSERT INTO ESHOP.PRODUCTS (NAME, DESCRIPTION, PRICE, CATEGORY_ID) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_PRODUCT_BY_ID = "UPDATE ESHOP.PRODUCTS SET NAME=?, DESCRIPTION=?, PRICE=?, CATEGORY_ID=? WHERE ID=?";
    private static final String DELETE_PRODUCT_BY_ID = "DELETE FROM ESHOP.PRODUCTS WHERE ID=?";
    private static final String GET_LAST_INSERT_PRODUCT = "SELECT * FROM ESHOP.PRODUCTS WHERE ID=(SELECT MAX(ID) FROM ESHOP.PRODUCTS)";
    private static final String GET_ALL_PRODUCTS = "SELECT * FROM ESHOP.PRODUCTS";
    private static final String GET_PRODUCT_BY_ID = "SELECT * FROM ESHOP.PRODUCTS WHERE ID=?";
    private static final String GET_PRODUCTS_BY_CATEGORY_ID = "SELECT * FROM ESHOP.PRODUCTS P WHERE P.CATEGORY_ID=?";
    private static final String GET_PRODUCTS_BY_ORDER_ID = "SELECT * FROM ESHOP.PRODUCTS P JOIN ESHOP.ORDER_PRODUCTS OP ON P.ID = OP.PRODUCT_ID JOIN ESHOP.ORDERS O ON O.ID = OP.ORDER_ID WHERE O.ID = ?";
    private static final String FIND_ALL_PRODUCTS_BY_REQUEST = "SELECT * FROM ESHOP.PRODUCTS WHERE DESCRIPTION LIKE ? OR NAME LIKE ?";
    private static final String GET_LAST_INSERT_ORDER_PRODUCTS_BY_USER_ID = "SELECT * FROM ESHOP.PRODUCTS P JOIN ESHOP.ORDER_PRODUCTS OP ON P.ID = OP.PRODUCT_ID JOIN ESHOP.ORDERS O ON O.ID = OP.ORDER_ID WHERE O.ID=(SELECT MAX(ID) FROM ESHOP.ORDERS WHERE USER_ID=?)";

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product create(Product entity) {
        jdbcTemplate.update(INSERT_NEW_PRODUCT, entity.getName(), entity.getDescription(),
                entity.getPrice(), entity.getCategoryId());
        return getLastInsertProductFromBase();
    }

    @Override
    public List<Product> read() {
        return jdbcTemplate.query(GET_ALL_PRODUCTS, (rs, rowNum) -> getResultSetProduct(rs));
    }

    @Override
    public Product update(Product entity) {
        jdbcTemplate.update(UPDATE_PRODUCT_BY_ID, entity.getName(), entity.getDescription(),
                entity.getPrice(), entity.getCategoryId(), entity.getId());
        return getProductById(entity.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_PRODUCT_BY_ID, id);
    }

    @Override
    public Product getProductById(int productId) {
        return jdbcTemplate.query(GET_PRODUCT_BY_ID,
                        (rs, rowNum) -> getResultSetProduct(rs), productId)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId) {
        return jdbcTemplate.query(GET_PRODUCTS_BY_CATEGORY_ID, (rs, rowNum) -> getResultSetProduct(rs), categoryId);
    }

    @Override
    public Map<Product, Integer> getProductsByOrderId(int orderId) {
        return getProductsWithNumbersById(orderId, GET_PRODUCTS_BY_ORDER_ID);
    }

    @Override
    public Map<Product, Integer> getLastInsertOrderProductsByUserId(int userId) {
        return getProductsWithNumbersById(userId, GET_LAST_INSERT_ORDER_PRODUCTS_BY_USER_ID);
    }

    @Override
    public List<Product> findAllProductsByRequest(String request) {
        String setRequest = "%" + request + "%";
        return jdbcTemplate.query(FIND_ALL_PRODUCTS_BY_REQUEST,
                (rs, rowNum) -> getResultSetProduct(rs), setRequest, setRequest);
    }

    private Product getLastInsertProductFromBase() {
        return jdbcTemplate.query(GET_LAST_INSERT_PRODUCT,
                        (rs, rowNum) -> getResultSetProduct(rs))
                .stream()
                .findAny()
                .orElse(null);
    }

    private Map<Product, Integer> getProductsWithNumbersById(int id, String request) {
        Map<Product, Integer> products = new HashMap<>();
        List<Product> productList = jdbcTemplate.query(request,(rs, rowNum) -> getResultSetProduct(rs), id);
        List<Integer> numbersList = jdbcTemplate.query(request, (rs, rowNum) -> rs.getInt("numbers_product"),id);
        for (int i = 0; i < productList.size(); i++) {
            products.put(productList.get(i), numbersList.get(i));
        }
        return products;
    }

    private Product getResultSetProduct(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        BigDecimal price = resultSet.getBigDecimal("price");
        int categoryId = resultSet.getInt("category_id");
        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .build();
    }
}

