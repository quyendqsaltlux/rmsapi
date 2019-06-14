package saltlux.ctv.tranSS.concurrency;

import saltlux.ctv.tranSS.service.ProjectMiddleService;
import saltlux.ctv.tranSS.service.ProjectService;

import java.util.TimerTask;

public class ProjectProgressUpdateTask extends TimerTask {
    private ProjectMiddleService projectService;
    private Long projectId;

    public ProjectProgressUpdateTask(ProjectMiddleService projectService, Long projectId) {
        this.projectService = projectService;
        this.projectId = projectId;
    }

    @Override
    public void run() {
        this.projectService.updateProgress(projectId);
    }
}
