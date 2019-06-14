package saltlux.ctv.tranSS.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {
    @NotBlank
    @Size(max = 255)
    private String username;
    @Size(max = 255)
    @NotBlank
    private String password;
    @Size(max = 255)
    @NotBlank
    private String passwordNew;
}
