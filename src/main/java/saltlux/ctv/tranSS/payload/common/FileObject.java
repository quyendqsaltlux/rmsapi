package saltlux.ctv.tranSS.payload.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class FileObject {
    @NotBlank
    @NotNull
    private String path;

    public FileObject(String path) {
        this.path = path;
    }
}
