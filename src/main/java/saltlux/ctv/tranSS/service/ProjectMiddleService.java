package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saltlux.ctv.tranSS.enums.ProjectProgressEnum;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.exception.NotAllowException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.Project;
import saltlux.ctv.tranSS.model.ProjectAssignment;
import saltlux.ctv.tranSS.model.User;
import saltlux.ctv.tranSS.repository.project.ProjectAssignmentRepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;
import saltlux.ctv.tranSS.repository.user.UserRepository;
import saltlux.ctv.tranSS.security.UserPrincipal;
import saltlux.ctv.tranSS.util.AppConstants;
import saltlux.ctv.tranSS.util.AuthUtil;

import java.util.List;

@Slf4j
@Service
public class ProjectMiddleService {
    private final ProjectAssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectMiddleService(ProjectAssignmentRepository assignmentRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public void updateProjectCode(String oldProjectCode, String newProjectCode) {
        List<ProjectAssignment> assignments = assignmentRepository.findByProjectCode(oldProjectCode);
        assignments.forEach(projectAssignment -> projectAssignment.setProjectCode(newProjectCode));
        assignmentRepository.saveAll(assignments);
    }

    public void updateProgress(Long projectId) {
        log.info(String.format("update Progress for project id %s: ", projectId));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        List<ProjectAssignment> assignments = assignmentRepository.findByProjectId(projectId);
        float total = 0;
        for (ProjectAssignment assignment : assignments) {
            if (ProjectProgressEnum.ON_GOING.toString().equals(assignment.getProgress())) {
                total += AppConstants.PROJECT_PROGRESS_ONGOING_POINT;
            } else if (ProjectProgressEnum.FINISHED.toString().equals(assignment.getProgress())) {
                total += AppConstants.PROJECT_PROGRESS_MAX;
            }
        }
        float progressPoint = 0 == total ? 0 : total / assignments.size() / AppConstants.PROJECT_PROGRESS_MAX;
        project.setProgressPoint(progressPoint);
        log.info(String.format("update progressPoint for project id %s equal : ", progressPoint));
        projectRepository.save(project);

    }

    /**
     * @param id id
     */
    public void deleteProject(UserPrincipal currentUser, Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("project", "id", id));
        User pm = userRepository.findByCode(project.getPmVtc())
                .orElseThrow(() -> new ResourceNotFoundException("PM", "Code", project.getPmVtc()));
        if (!AuthUtil.isPmLeader(currentUser) &&
                currentUser.getId().longValue() != pm.getId().longValue()) {
            throw new NotAllowException();
        }
        List<ProjectAssignment> assignments = assignmentRepository.findByProjectId(id);
        if (!assignments.isEmpty()) {
            throw new BadRequestException("Not allow delete project includes assignments");
        }
        projectRepository.deleteByProjectId(id);
    }
}