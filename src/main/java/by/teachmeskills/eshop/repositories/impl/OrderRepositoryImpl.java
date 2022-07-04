package by.teachmeskills.eshop.repositories.impl;

import by.teachmeskills.eshop.entities.Order;
import by.teachmeskills.eshop.entities.Product;
import by.teachmeskills.eshop.repositories.OrderRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProductRepositoryImpl productRepository;
    private static final String GET_ALL_ORDERS = "SELECT * FROM ESHOP.ORDERS";
    private static final String UPDATE_USER_ID_ORDER_BY_ID = "UPDATE ESHOP.ORDERS SET USER_ID=? WHERE ID=?";
    private static final String DELETE_ORDER_BY_ID = "DELETE FROM ESHOP.ORDERS WHERE ID=?";
    private static final String GET_ORDERS_BY_USER_ID = "SELECT * FROM ESHOP.ORDERS WHERE USER_ID=?";
    private static final String GET_ORDER_BY_ID = "SELECT * FROM ESHOP.ORDERS WHERE ID=?";
    private static final String GET_LAST_INSERT_ORDER_BY_USER_ID = "SELECT * FROM ESHOP.ORDERS WHERE ID=(SELECT MAX(ID) FROM ESHOP.ORDERS WHERE USER_ID=?)";
    private static final String INSERT_NEW_ORDER = "INSERT INTO ESHOP.ORDERS (PRICE, DATE, USER_ID) values (?,?,?)";
    private static final String INSERT_ORDER_PRODUCTS = "INSERT INTO ESHOP.ORDER_PRODUCTS (ORDER_ID, PRODUCT_ID, NUMBERS_PRODUCT) VALUES (?, ?, ?)";

    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate, ProductRepositoryImpl productRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRepository = productRepository;
    }

    @Override
    public Order create(Order entity) {
        jdbcTemplate.update(INSERT_NEW_ORDER, entity.getPrice(), entity.getDate(), entity.getUserId());
        Order order = getLastInsertOrderByUserId(entity.getUserId());
        for (Map.Entry<Product, Integer> entry : entity.getProducts().entrySet()) {
            jdbcTemplate.update(INSERT_ORDER_PRODUCTS, order.getId(), entry.getKey().getId(), entry.getValue());
        }
        Map<Product, Integer> products = productRepository.getLastInsertOrderProductsByUserId(entity.getUserId());
        order.setProducts(products);
        return order;
    }

    @Override
    public List<Order> read() {
        return jdbcTemplate.query(GET_ALL_ORDERS, (rs, rowNum) -> getOrderWithProductsFromResultSet(rs));
    }

    @Override
    public Order update(Order entity) {
        jdbcTemplate.update(UPDATE_USER_ID_ORDER_BY_ID, entity.getUserId(), entity.getId());
        return getOrderById(entity.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_ORDER_BY_ID, id);
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return jdbcTemplate.query(GET_ORDERS_BY_USER_ID, (rs, rowNUm) -> getOrderWithProductsFromResultSet(rs), userId);
    }

    private Order getOrderById(int orderId) {
        return jdbcTemplate.query(GET_ORDER_BY_ID,
                        (rs, rowNUm) -> getOrderWithProductsFromResultSet(rs), orderId)
                .stream()
                .findAny()
                .orElse(null);
    }

    private Order getOrderWithProductsFromResultSet(ResultSet resultSet) throws SQLException {
        int orderId = resultSet.getInt("id");
        BigDecimal price = resultSet.getBigDecimal("price");
        LocalDateTime date = resultSet.getObject("date", LocalDateTime.class);
        int userIdFromBase = resultSet.getInt("user_id");
        Map<Product, Integer> products = productRepository.getProductsByOrderId(orderId);
        return Order.builder()
                .id(orderId)
                .price(price)
                .date(date)
                .products(products)
                .userId(userIdFromBase)
                .build();
    }

    private Order getLastInsertOrderByUserId(int userId) {
        return jdbcTemplate.query(GET_LAST_INSERT_ORDER_BY_USER_ID,
                        (rs, rowNum) -> getOrderFromResultSet(rs), userId)
                .stream()
                .findAny()
                .orElse(null);
    }

    private Order getOrderFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        BigDecimal price = resultSet.getBigDecimal("price");
        LocalDateTime date = resultSet.getObject("date", LocalDateTime.class);
        int userIdFromBase = resultSet.getInt("user_id");
        return Order.builder()
                .id(id)
                .price(price)
                .date(date)
                .userId(userIdFromBase)
                .build();
    }
}
