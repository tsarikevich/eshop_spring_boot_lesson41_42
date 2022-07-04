package by.teachmeskills.eshop.repositories.impl;

import by.teachmeskills.eshop.entities.Image;
import by.teachmeskills.eshop.entities.Product;
import by.teachmeskills.eshop.repositories.ImageRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ImageRepositoryImpl implements ImageRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_NEW_IMAGE = "INSERT INTO ESHOP.IMAGES (CATEGORY_ID, PRODUCT_ID, PRIMARY_FLAG, IMAGE_PATH) values (?, ?, ?, ?)";
    private static final String UPDATE_IMAGE_BY_ID = "UPDATE ESHOP.IMAGES SET CATEGORY_ID=?, PRODUCT_ID=?, PRIMARY_FLAG=?, IMAGE_PATH=? WHERE ID=?";
    private static final String DELETE_IMAGE_BY_ID = "DELETE FROM ESHOP.IMAGES WHERE ID=?";
    private static final String GET_IMAGE_BY_ID = "SELECT * FROM ESHOP.IMAGES WHERE ID=?";
    private static final String GET_LAST_INSERT_IMAGE = "SELECT * FROM ESHOP.IMAGES WHERE ID=(SELECT MAX(ID) FROM ESHOP.IMAGES)";
    private static final String GET_ALL_IMAGES = "SELECT * FROM ESHOP.IMAGES";
    private static final String GET_PRIMARY_IMAGES_BY_CATEGORY_ID = "SELECT * FROM ESHOP.IMAGES WHERE CATEGORY_ID = ? AND PRODUCT_ID IS NOT NULL";
    private static final String GET_PRIMARY_IMAGE_BY_PRODUCT_ID = "SELECT * FROM ESHOP.IMAGES WHERE PRIMARY_FLAG=1 AND PRODUCT_ID = ?";
    private static final String GET_ALL_IMAGE_BY_PRODUCT_ID = "SELECT * FROM ESHOP.IMAGES WHERE PRODUCT_ID = ?";
    private static final String GET_ALL_CATEGORIES_IMAGES = "SELECT * FROM ESHOP.IMAGES WHERE PRODUCT_ID IS NULL";
    private static final String GET_ALL_ORDER_PRIMARY_IMAGES_BY_USER_ID = "SELECT * FROM ESHOP.IMAGES I JOIN ESHOP.ORDER_PRODUCTS OP ON I.PRODUCT_ID=OP.PRODUCT_ID JOIN ESHOP.ORDERS O ON O.ID=OP.ORDER_ID WHERE I.PRIMARY_FLAG=1 AND O.USER_ID=? GROUP BY I.IMAGE_PATH";

    public ImageRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Image create(Image entity) {
        jdbcTemplate.update(INSERT_NEW_IMAGE, entity.getCategoryId(), entity.getProductId(),
                entity.isPrimaryFlag(), entity.getImagePath());
        return getLastInsertImageFromDB();
    }

    @Override
    public List<Image> read() {
        return jdbcTemplate.query(GET_ALL_IMAGES, (rs, rowNum) -> getImageFromResultSet(rs));
    }

    @Override
    public Image update(Image entity) {
        jdbcTemplate.update(UPDATE_IMAGE_BY_ID, entity.getCategoryId(), entity.getProductId(),
                entity.isPrimaryFlag(), entity.getImagePath(), entity.getId());
        return getImageById(entity.getId());
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_IMAGE_BY_ID, id);
    }

    @Override
    public List<Image> getAllCategoriesImages() {
        return jdbcTemplate.query(GET_ALL_CATEGORIES_IMAGES, (rs, rowNum) -> getImageFromResultSet(rs));
    }

    @Override
    public List<Image> getImagesByProductId(int productId) {
        return jdbcTemplate.query(GET_ALL_IMAGE_BY_PRODUCT_ID,
                (rs, rowNum) -> getImageFromResultSet(rs), productId);
    }

    @Override
    public List<Image> getAllOrderPrimaryImagesByUserId(int userId) {
        return jdbcTemplate.query(GET_ALL_ORDER_PRIMARY_IMAGES_BY_USER_ID,
                (rs, rowNum) -> getImageFromResultSet(rs), userId);
    }

    @Override
    public List<Image> getPrimaryImagesByCategoryId(int categoryId) {
        return jdbcTemplate.query(GET_PRIMARY_IMAGES_BY_CATEGORY_ID,
                (rs, rowNum) -> getImageFromResultSet(rs), categoryId);
    }

    @Override
    public List<Image> getPrimaryImagesByListProducts(List<Product> products) {
        List<Image> images = new ArrayList<>();
        for (Product product : products) {
            images.add(jdbcTemplate.query(GET_PRIMARY_IMAGE_BY_PRODUCT_ID,
                            (rs, rowNum) -> getImageFromResultSet(rs), product.getId())
                    .stream()
                    .findAny()
                    .orElse(null));
        }
        return images;
    }

    private Image getImageById(int imageId) {
        return jdbcTemplate.query(GET_IMAGE_BY_ID,
                        (rs, rowNum) -> getImageFromResultSet(rs), imageId)
                .stream()
                .findAny()
                .orElse(null);
    }

    private Image getLastInsertImageFromDB() {
        return jdbcTemplate.query(GET_LAST_INSERT_IMAGE,
                        (rs, rowNum) -> getImageFromResultSet(rs))
                .stream()
                .findAny()
                .orElse(null);
    }

    private Image getImageFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int categoryId = rs.getInt("category_id");
        int productId = rs.getInt("product_id");
        boolean primaryFlag = rs.getBoolean("primary_flag");
        String imagePath = rs.getString("image_path");
        return Image.builder()
                .id(id)
                .categoryId(categoryId)
                .productId(productId)
                .primaryFlag(primaryFlag)
                .imagePath(imagePath)
                .build();
    }
}

