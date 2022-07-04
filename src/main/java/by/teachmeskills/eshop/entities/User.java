package by.teachmeskills.eshop.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class User extends BaseEntity {
    @NotEmpty(message = "Login must not be empty")
    @Size(min=2, max=30, message = "Login must be between 2 and 30 characters")
    @Pattern(regexp = "\\S+",message = "Spaces are not allowed")
    private String login;
    @NotEmpty(message = "Password must not be empty")
    @Pattern(regexp = "\\S+",message = "Spaces are not allowed")
    private String password;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private BigDecimal balance;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
