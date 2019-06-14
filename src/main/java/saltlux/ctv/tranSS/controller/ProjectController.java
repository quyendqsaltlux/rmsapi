package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.payload.ApiResponse;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.project.ProjectFilterRequest;
import saltlux.ctv.tranSS.payload.project.ProjectRequest;
import saltlux.ctv.tranSS.payload.project.ProjectResponse;
import saltlux.ctv.tranSS.payload.project.ProjectsItemResponse;
import saltlux.ctv.tranSS.security.CurrentUser;
import saltlux.ctv.tranSS.security.UserPrincipal;
import saltlux.ctv.tranSS.service.ProjectMiddleService;
import saltlux.ctv.tranSS.service.ProjectService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectMiddleService middleService;
    private final ProjectService projectService;


    @Autowired
    public ProjectController(ProjectService projectService, ProjectMiddleService middleService) {
        this.projectService = projectService;
        this.middleService = middleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ProjectResponse create(@Valid @RequestBody ProjectRequest projectRequest) throws Exception {
        if (projectRequest == null) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return projectService.create(projectRequest);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<ProjectsItemResponse> search(@RequestParam(value = "keyword") String keyWord,
                                                      @RequestParam(value = "orderBy") String orderBy,
                                                      @RequestParam(value = "sortDirection") String sortDirection,
                                                      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                      @Valid @RequestBody ProjectFilterRequest filters) {
        return projectService.search(page, size, keyWord, orderBy, sortDirection, filters);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ProjectRequest getCandidateById(@PathVariable Long id) {
        return projectService.findCandidateById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@CurrentUser UserPrincipal currentUser, @PathVariable Long id) {
        middleService.deleteProject(currentUser, id);
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Delete successfully"));
    }


}
