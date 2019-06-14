package saltlux.ctv.tranSS.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@NoArgsConstructor
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAllowException extends RuntimeException {

}
