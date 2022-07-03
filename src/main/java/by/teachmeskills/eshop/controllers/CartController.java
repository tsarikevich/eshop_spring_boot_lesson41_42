package by.teachmeskills.eshop.controllers;

import by.teachmeskills.eshop.entities.Cart;
import by.teachmeskills.eshop.entities.User;
import by.teachmeskills.eshop.services.CartService;
import by.teachmeskills.eshop.utils.EshopConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ModelAndView openCartPage(@SessionAttribute(EshopConstants.SHOPPING_CART) Cart cart,
                                     @SessionAttribute(EshopConstants.USER) User user) {
        return cartService.getCartData(cart, user);
    }

    @GetMapping("/add")
    public ModelAndView addProductToCart(@RequestParam(EshopConstants.PRODUCT_ID) int id, @SessionAttribute(EshopConstants.SHOPPING_CART) Cart cart) {
        return cartService.addProductToCart(id, cart);
    }

    @GetMapping("/delete")
    public ModelAndView deleteProductFromCart(@RequestParam(EshopConstants.PRODUCT_ID) int id, @SessionAttribute(EshopConstants.SHOPPING_CART) Cart cart) {
        return cartService.deleteProductFromCart(id, cart);
    }

    @GetMapping("/checkout")
    public ModelAndView checkout(@SessionAttribute(EshopConstants.SHOPPING_CART) Cart cart,
                                 @SessionAttribute(EshopConstants.USER) User user) {
        return cartService.checkout(cart, user);
    }
}
