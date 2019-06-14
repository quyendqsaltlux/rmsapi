package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import saltlux.ctv.tranSS.concurrency.ProjectProgressUpdateTask;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.payload.ApiResponse;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.projectAssignment.*;
import saltlux.ctv.tranSS.service.ProjectAssignmentService;
import saltlux.ctv.tranSS.service.ProjectMiddleService;
import saltlux.ctv.tranSS.service.ProjectService;
import saltlux.ctv.tranSS.util.AppConstants;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Timer;

import static saltlux.ctv.tranSS.util.ValidationUtil.isListNullOrEmpty;

@Slf4j
@RestController
@RequestMapping("/api/projectAssignment")
public class ProjectAssignmentController {
    private final ProjectService projectService;
    private final ProjectMiddleService projectMiddleService;
    private final ProjectAssignmentService projectAssignmentService;


    @Autowired
    public ProjectAssignmentController(ProjectAssignmentService projectAssignmentService,
                                       ProjectService projectService,
                                       ProjectMiddleService projectMiddleService) {
        this.projectAssignmentService = projectAssignmentService;
        this.projectService = projectService;
        this.projectMiddleService = projectMiddleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ProjectAssignmentCandidateResponse create(@Valid @RequestBody ProjectAssignmentRequest projectRequest) throws Exception {
        if (projectRequest == null) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return projectAssignmentService.create(projectRequest);
    }

    @GetMapping("/getListByProjectWithStatus/{projectId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ProjectAssignmentWithStatusResponse getListByProjectWithStatus(@PathVariable Long projectId) {
        return projectAssignmentService.getListByProjectWithStatus(projectId);
    }

    @PostMapping("/search/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public PagedResponse<ProjectHistoryResponse> search(@RequestParam(value = "keyword") String keyWord,
                                                        @RequestParam(value = "orderBy") String orderBy,
                                                        @RequestParam(value = "sortDirection") String sortDirection,
                                                        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                                        @Valid @RequestBody BaseFilterRequest filters,
                                                        @PathVariable Long candidateId) {
        return projectAssignmentService.search(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        projectAssignmentService.delete(id);
        return ResponseEntity.created(null)
                .body(new ApiResponse(true, "Delete successfully"));
    }

    @GetMapping("/changeStatus/{id}/{status}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ProjectAssignmentCandidateResponse changeStatus(@PathVariable Long id, @PathVariable String status) {
        return projectAssignmentService.changeStatus(status, id);
    }


    @GetMapping("/changeProgress/{id}/{progress}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ProjectAssignmentCandidateResponse changeProgress(@PathVariable Long id, @PathVariable String progress) {
        ProjectAssignmentCandidateResponse response = projectAssignmentService.changeProgress(progress, id);
        Timer timer = new Timer("Timer");
        timer.schedule(new ProjectProgressUpdateTask(projectMiddleService, response.getProjectId()), 0);
        return response;
    }

    @PostMapping("/computeNetOrHour")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public BigDecimal computeNetOrHour(@Valid @RequestBody NetOrHourParam param) {
        if (isListNullOrEmpty(param.getRep()) || isListNullOrEmpty(param.getWf())) {
            throw new BadRequestException("2 list need to has values");
        }
        if (param.getRep().size() != param.getWf().size()) {
            throw new BadRequestException("2 list need to has same size");
        }
        return projectAssignmentService.computeNetOrHour(param.getRep(), param.getWf());
    }

    @GetMapping("/computeTotalMoney")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public BigDecimal computeTotalMoney(@RequestParam(value = "netOrHour") BigDecimal netOrHour,
                                    @RequestParam(value = "unitPrice") BigDecimal unitPrice) {
        return projectAssignmentService.computeTotalMoney(unitPrice, netOrHour);
    }

    @GetMapping("/getUnitPrice")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public BigDecimal computeTotalMoney(@RequestParam(value = "netOrHour") BigDecimal netOrHour,
                                    @RequestParam(value = "abilityId") Long abilityId) {
        return projectAssignmentService.getUnitPrice(abilityId, netOrHour);
    }

    @GetMapping("/findActiveAssignment/{candidateId}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    List<ProjectHistoryResponse> changeStatus(@PathVariable Long candidateId) {
        return projectAssignmentService.findByCandidate(candidateId);
    }

}
