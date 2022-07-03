package by.teachmeskills.eshop.services.impl;

import by.teachmeskills.eshop.entities.Image;
import by.teachmeskills.eshop.entities.Product;
import by.teachmeskills.eshop.entities.User;
import by.teachmeskills.eshop.repositories.impl.ImageRepositoryImpl;
import by.teachmeskills.eshop.repositories.impl.ProductRepositoryImpl;
import by.teachmeskills.eshop.services.ProductService;
import by.teachmeskills.eshop.utils.EshopConstants;
import by.teachmeskills.eshop.utils.PagesPathEnum;
import by.teachmeskills.eshop.utils.RequestParamsEnum;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private ProductRepositoryImpl productRepository;
    private ImageRepositoryImpl imageRepository;

    public ProductServiceImpl(ProductRepositoryImpl productRepository, ImageRepositoryImpl imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public Product create(Product entity) {
        return productRepository.create(entity);
    }

    @Override
    public List<Product> read() {
        return productRepository.read();
    }

    @Override
    public Product update(Product entity) {
        return productRepository.update(entity);
    }

    @Override
    public void delete(int id) {
        productRepository.delete(id);
    }

    @Override
    public List<Product> getAllForCategory(int categoryId) {
        return null;
    }

    @Override
    public ModelAndView getProductData(User user, int id) {
        if (Optional.ofNullable(user.getLogin()).isPresent()
                && Optional.ofNullable(user.getPassword()).isPresent()
                && Optional.ofNullable(user.getEmail()).isPresent()) {
            ModelMap modelMap = new ModelMap();
            Product product = productRepository.getProductById(id);
            List<Image> productImages = imageRepository.getImagesByProductId(id);
            modelMap.addAttribute(RequestParamsEnum.ONE_PRODUCT.getValue(), product);
            modelMap.addAttribute(RequestParamsEnum.ONE_PRODUCT_IMAGES.getValue(), productImages);
            return new ModelAndView(PagesPathEnum.PRODUCT_PAGE.getPath(), modelMap);
        } else {
            return new ModelAndView(EshopConstants.REDIRECT_TO_LOGIN_PAGE);
        }
    }

    @Override
    public ModelAndView getCategoryProductsData(int id, String nameCategory) {
        ModelMap modelMap = new ModelMap();
        List<Product> categoryProducts = productRepository.getProductsByCategoryId(id);
        List<Image> productsImages = imageRepository.getPrimaryImagesByCategoryId(id);
        modelMap.addAttribute(RequestParamsEnum.PRODUCTS.getValue(), categoryProducts);
        modelMap.addAttribute(RequestParamsEnum.PRODUCTS_IMAGES.getValue(), productsImages);
        modelMap.addAttribute(RequestParamsEnum.NAME_CATEGORY.getValue(), nameCategory);
        return new ModelAndView(PagesPathEnum.CATEGORY_PAGE.getPath(), modelMap);
    }

    @Override
    public ModelAndView findAllProductsByRequest(String request) {
        ModelMap modelMap = new ModelMap();
        List<Product> products = productRepository.findAllProductsByRequest(request);
        List<Image> images = imageRepository.getPrimaryImagesByListProducts(products);
        modelMap.addAttribute(RequestParamsEnum.IMAGES_FROM_SEARCH.getValue(), images);
        modelMap.addAttribute(RequestParamsEnum.PRODUCTS_FROM_SEARCH.getValue(), products);
        return new ModelAndView(PagesPathEnum.SEARCH_PAGE.getPath(), modelMap);
    }
}
