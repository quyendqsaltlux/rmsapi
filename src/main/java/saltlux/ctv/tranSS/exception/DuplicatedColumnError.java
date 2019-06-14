package saltlux.ctv.tranSS.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DuplicatedColumnError extends ApiSubError {
    private List<String> duplicatedColumns;

    public DuplicatedColumnError(List<String> duplicatedColumns) {
        this.duplicatedColumns = duplicatedColumns;
    }
}
