package saltlux.ctv.tranSS.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatedHandler {
    private String lastDuplicatedNo = null;
    private String lastSuffix = null;
    private int lastDigitSuffix = 0;
}
