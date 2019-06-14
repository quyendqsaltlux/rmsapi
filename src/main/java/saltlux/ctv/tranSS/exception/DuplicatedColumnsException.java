package saltlux.ctv.tranSS.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@Setter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedColumnsException extends Exception {
    private List<String> duplicatedColumns;

    public DuplicatedColumnsException(List<String> duplicatedColumns) {
        this.duplicatedColumns = duplicatedColumns;
    }
}
