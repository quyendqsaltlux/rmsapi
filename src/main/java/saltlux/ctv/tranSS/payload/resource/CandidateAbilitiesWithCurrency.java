package saltlux.ctv.tranSS.payload.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateAbilitiesWithCurrency {
    private List<CandidateAbilityRequest> abilities;
    private String currency;
}
