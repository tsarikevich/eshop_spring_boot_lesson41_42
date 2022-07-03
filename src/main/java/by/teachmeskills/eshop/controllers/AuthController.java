package by.teachmeskills.eshop.controllers;

import by.teachmeskills.eshop.entities.Cart;
import by.teachmeskills.eshop.entities.User;
import by.teachmeskills.eshop.exceptions.AuthorizationException;
import by.teachmeskills.eshop.services.UserService;
import by.teachmeskills.eshop.utils.EshopConstants;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static by.teachmeskills.eshop.utils.PagesPathEnum.SIGN_IN_PAGE;

@RestController
@SessionAttributes({EshopConstants.USER, EshopConstants.SHOPPING_CART})
@RequestMapping("/login")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView openLoginPage() {
        return new ModelAndView(SIGN_IN_PAGE.getPath());
    }

    @PostMapping
    public ModelAndView login(@Valid @ModelAttribute(EshopConstants.USER) User user, BindingResult bindingResult) throws AuthorizationException {
            return userService.authenticate(user,bindingResult);
    }

    @ModelAttribute(EshopConstants.USER)
    public User setUpUserForm() {
        return new User();
    }

    @ModelAttribute(EshopConstants.SHOPPING_CART)
    public Cart shoppingCart() {
        return new Cart();
    }
}
