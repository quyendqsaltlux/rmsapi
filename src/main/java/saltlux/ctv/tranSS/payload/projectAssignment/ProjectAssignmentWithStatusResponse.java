package saltlux.ctv.tranSS.payload.projectAssignment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectAssignmentWithStatusResponse {
    private List<ProjectAssignmentCandidateResponse> list;
    private boolean ableToChange;
}
