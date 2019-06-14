package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saltlux.ctv.tranSS.enums.AssignmentProgressEnum;
import saltlux.ctv.tranSS.enums.AssignmentStatusEnum;
import saltlux.ctv.tranSS.enums.ProgressEnum;
import saltlux.ctv.tranSS.enums.ProjectProgressEnum;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.*;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.evaluation.PlainSpecificCommentResponse;
import saltlux.ctv.tranSS.payload.project.ProjectLinkAssignmentResponse;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectAssignmentCandidateResponse;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectAssignmentRequest;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectAssignmentWithStatusResponse;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectHistoryResponse;
import saltlux.ctv.tranSS.payload.resource.AbilityBasicResponse;
import saltlux.ctv.tranSS.repository.candidate.CandidateAbilityRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;
import saltlux.ctv.tranSS.repository.evaluation.SpecificCommentRepository;
import saltlux.ctv.tranSS.repository.po.PORepository;
import saltlux.ctv.tranSS.repository.project.ProjectAssignmentRepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static saltlux.ctv.tranSS.util.ValidationUtil.isPositiveId;
import static saltlux.ctv.tranSS.util.ValidationUtil.isTrue;

@Slf4j
@Service
public class ProjectAssignmentService {
    private final CandidateAbilityRepository abilityRepository;
    private final ProjectAssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final CandidateRepository candidateRepository;
    private final EvaluationService evaluationService;
    private final PORepository poRepository;
    private final ModelMapper modelMapper;
    private final ProjectMiddleService projectMiddleService;
    private final SpecificCommentRepository specificCommentRepo;

    @Autowired
    public ProjectAssignmentService(ProjectAssignmentRepository assignmentRepository,
                                    CandidateRepository candidateRepository,
                                    ProjectRepository projectRepository,
                                    ModelMapper modelMapper,
                                    CandidateAbilityRepository abilityRepository,
                                    EvaluationService evaluationService,
                                    PORepository poRepository,
                                    SpecificCommentRepository specificCommentRepo,
                                    ProjectMiddleService projectMiddleService) {

        this.assignmentRepository = assignmentRepository;
        this.candidateRepository = candidateRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.abilityRepository = abilityRepository;
        this.evaluationService = evaluationService;
        this.poRepository = poRepository;
        this.specificCommentRepo = specificCommentRepo;
        this.projectMiddleService = projectMiddleService;
    }

    /**
     * @param page          p
     * @param size          s
     * @param keyWord       k
     * @param orderBy       o
     * @param sortDirection d
     * @param filters       f
     * @param candidateId   c
     * @return p
     */
    public PagedResponse<ProjectHistoryResponse> search(int page, int size, String keyWord,
                                                        String orderBy, String sortDirection,
                                                        BaseFilterRequest filters, Long candidateId) {

        PagedResponse<ProjectAssignment> pagedResponse = assignmentRepository.search(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
        List<ProjectAssignment> projectList = pagedResponse.getContent();
        List<ProjectHistoryResponse> projectResponses = projectList
                .stream()
                .map(this::convertToProjectHistory)
                .collect(Collectors.toList());

        return new PagedResponse<>(projectResponses, page, size,
                pagedResponse.getTotalElements(), pagedResponse.getTotalPages(), pagedResponse.isLast());
    }

    /**
     * Save Assignment
     *
     * @param assignmentRequest assignment request
     * @return ProjectAssignmentCandidateResponse
     */
    @Transactional
    public ProjectAssignmentCandidateResponse create(ProjectAssignmentRequest assignmentRequest) {
        Long id = assignmentRequest.getId();
        ProjectAssignment assignment = !isPositiveId(id) ? new ProjectAssignment() : assignmentRepository.findById(id).get();

        /*Check if assign to old project*/
        if (null != id) {
            Project project = projectRepository.findById(assignmentRequest.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", assignmentRequest.getProjectId()));
            if (isTrue(project.getIsOld()) ||
                    ProjectProgressEnum.FINISHED.toString().equals(project.getProgressStatus())) {
                throw new BadRequestException("Can't assign a historical project or finished project.");
            }
        }
        if (!projectRepository.existsByCode(assignmentRequest.getProjectCode())) {
            throw new ResourceNotFoundException("Project", "code", assignmentRequest.getProjectCode());
        }

        BeanUtils.copyProperties(assignmentRequest, assignment);
        if (!isTrue(assignmentRequest.getExternalResource())) {
            Candidate candidate = candidateRepository.findByCode(assignmentRequest.getCandidateCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate", "code", assignmentRequest.getCandidateCode()));
            assignment.setCandidate(candidate);
            assignment.setExternalResourceName(null);
        } else {
            assignment.setCandidate(null);
        }
        if (null == assignment.getId()) {
            assignment.setStatus(AssignmentStatusEnum.NOT_CONFIRMED.toString());
            assignment.setProgress(ProgressEnum.NOT_START.toString());
        }
        /*Re compute some value*/
        computeSomeField(assignmentRequest, assignment);
        ProjectAssignment savedAssignment = assignmentRepository.save(assignment);
        projectMiddleService.updateProgress(assignment.getProjectId());
        return convertToDto(savedAssignment);
    }

    /**
     * @param assignmentRequest source
     * @param assignment        target
     */
    private void computeSomeField(ProjectAssignmentRequest assignmentRequest, ProjectAssignment assignment) {
        Integer[] reps = new Integer[]{
                assignmentRequest.getReprep(),
                assignmentRequest.getRep100(),
                assignmentRequest.getRep99_95(),
                assignmentRequest.getRep94_85(),
                assignmentRequest.getRep84_75(),
                assignmentRequest.getRepnoMatch()
        };
        assignment.setTotalRep(computeTotalRep(Arrays.asList(reps)));

        if (!isTrue(assignmentRequest.getNotAutoComputeNetHour())) {
            BigDecimal[] wfs = new BigDecimal[]{
                    assignmentRequest.getWrep(),
                    assignmentRequest.getW100(),
                    assignmentRequest.getW99_95(),
                    assignmentRequest.getW94_85(),
                    assignmentRequest.getW84_75(),
                    assignmentRequest.getWnoMatch()
            };
            assignment.setNetOrHour(computeNetOrHour(Arrays.asList(reps), Arrays.asList(wfs)));
        }
        Long abilityId = assignmentRequest.getAbilityId();
        if (isPositiveId(abilityId)) {
            assignment.setUnitPrice(getUnitPrice(abilityId, assignment.getNetOrHour()));
        }

        assignment.setTotal(computeTotalMoney(assignment.getUnitPrice(), assignment.getNetOrHour()));

    }

    /**
     * @param projectId project id
     * @return List<ProjectAssignmentCandidateResponse>
     */
    public List<ProjectAssignmentCandidateResponse> getListByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "code", projectId));
        List<ProjectAssignment> assignments = isTrue(project.getIsOld()) ?
                assignmentRepository.findByProjectCode(project.getCode()) :
                assignmentRepository.findByProjectId(projectId);

        return assignments
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * @param projectId project id
     * @return ProjectAssignmentWithStatusResponse
     */
    public ProjectAssignmentWithStatusResponse getListByProjectWithStatus(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "code", projectId));
        List<ProjectAssignment> assignments = isTrue(project.getIsOld()) ?
                assignmentRepository.findByProjectCode(project.getCode()) :
                assignmentRepository.findByProjectId(projectId);
        ProjectAssignmentWithStatusResponse response = new ProjectAssignmentWithStatusResponse();
        response.setAbleToChange(!isTrue(project.getIsOld()));
        response.setList(assignments
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
        return response;
    }

    /**
     * @param assignment assignment
     * @return ProjectAssignmentCandidateResponse
     */
    private ProjectAssignmentCandidateResponse convertToDto(ProjectAssignment assignment) {
        ProjectAssignmentCandidateResponse response = modelMapper
                .map(assignment, ProjectAssignmentCandidateResponse.class);
        if (null != assignment.getCandidate()) {
            response.setCandidateCode(response.getCandidate().getCode());
        }
        poRepository.findTopByAssignmentId(assignment.getId())
                .ifPresent(purchaseOrder -> response.setPoId(purchaseOrder.getId()));

        if (null != assignment.getAbilityId()) {
            CandidateAbility ability = abilityRepository.findById(assignment.getAbilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidate Rate", "id", assignment.getAbilityId()));
            response.setAbility(modelMapper.map(ability, AbilityBasicResponse.class));
        }
        return response;
    }

    /**
     * @param assignment assignment
     * @return ProjectHistoryResponse
     */
    private ProjectHistoryResponse convertToProjectHistory(ProjectAssignment assignment) {
        List<Project> projects = projectRepository.findByCode(assignment.getProjectCode());
        ProjectHistoryResponse response = modelMapper.map(assignment, ProjectHistoryResponse.class);
        specificCommentRepo.findTopByAssignment(assignment.getId())
                .ifPresent(specificComment -> response.setSpecificComment(
                        modelMapper.map(specificComment, PlainSpecificCommentResponse.class)));
        if (!projects.isEmpty()) {
            ProjectLinkAssignmentResponse linkAssignmentResponse = new ProjectLinkAssignmentResponse();
            BeanUtils.copyProperties(projects.get(0), linkAssignmentResponse);
            response.setProject(linkAssignmentResponse);
        }
        return response;
    }

    /**
     * @param assignmentId id
     */
    @Transactional
    public void delete(Long assignmentId) {
        Optional<SpecificComment> comment = specificCommentRepo.findTopByAssignment(assignmentId);
        comment.ifPresent(specificComment -> evaluationService.deleteSpecificComment(specificComment.getId()));
        ProjectAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
        assignmentRepository.deleteById(assignmentId);
        projectMiddleService.updateProgress(assignment.getProjectId());
    }

    /**
     * @param status status
     * @param id     id
     * @return ProjectAssignment
     */
    public ProjectAssignmentCandidateResponse changeStatus(String status, Long id) {
        if (!EnumUtils.isValidEnum(AssignmentStatusEnum.class, status)) {
            throw new BadRequestException("Status is invalid");
        }
        ProjectAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
        assignment.setStatus(status);
        if (AssignmentStatusEnum.NOT_CONFIRMED.toString().equals(status)) {
            assignment.setProgress(ProgressEnum.NOT_START.toString());
        }
        return convertToDto(this.assignmentRepository.save(assignment));
    }

    /**
     * @param progress progress
     * @param id       id
     * @return ProjectAssignmentCandidateResponse
     */
    public ProjectAssignmentCandidateResponse changeProgress(String progress, Long id) {
        if (!EnumUtils.isValidEnum(AssignmentProgressEnum.class, progress)) {
            throw new BadRequestException("Progress is invalid");
        }
        ProjectAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        Optional<PurchaseOrder> order = poRepository.findTopByAssignmentId(assignment.getId());
        order.ifPresent(purchaseOrder -> {
            Invoice invoice = purchaseOrder.getInvoice();
            if (null != invoice && null != invoice.getId()) {
                throw new BadRequestException("Not allow due to existed invoice");
            }
        });

        if (AssignmentProgressEnum.FINISHED.toString().equals(progress)) {
            assignment.setFinishedAt(new Date());
        }
        assignment.setProgress(progress);
        return convertToDto(this.assignmentRepository.save(assignment));
    }

    /**
     * @param rep rep
     * @param wf  wf
     * @return float
     */
    public BigDecimal computeNetOrHour(List<Integer> rep, List<BigDecimal> wf) {
        BigDecimal netOrHour = new BigDecimal("0");
        for (int i = 0; i < rep.size(); i++) {
            if (null == rep.get(i) || null == wf.get(i)) {
                throw new BadRequestException("Null value is not allowed");
            }
            netOrHour = netOrHour.add(wf.get(i).multiply(BigDecimal.valueOf(rep.get(i))));
        }
        return netOrHour.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * @param rep rep
     * @return float
     */
    private int computeTotalRep(List<Integer> rep) {
        return rep.stream().reduce(Integer::sum)
                .orElseThrow(() -> new BadRequestException("Null value is not allowed"));
    }

    /**
     * @param unitPrice unitPrice
     * @param netOrHour netOrHour
     * @return
     */
    public BigDecimal computeTotalMoney(BigDecimal unitPrice, BigDecimal netOrHour) {
        return unitPrice.multiply(netOrHour);
    }

    /**
     * @param abilityId abilityId
     * @param netOrHour netOrHour
     * @return float
     */
    public BigDecimal getUnitPrice(Long abilityId, BigDecimal netOrHour) {
        CandidateAbility ability = abilityRepository.findById(abilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Rate", "id", abilityId));
        if (null == ability.getMinimumVolum()) {
            throw new BadRequestException("Minimum volume need has a value");
        }
        if (null == ability.getRate()) {
            throw new BadRequestException("Rate (word/char) need has a value");
        }
        if (null == ability.getRate2()) {
            throw new BadRequestException("Rate (hour) need has a value");
        }
        if (netOrHour.compareTo(BigDecimal.valueOf(ability.getMinimumVolum())) > 0) {
            return ability.getRate();
        }
        return ability.getRate2();
    }

    /**
     * @param candidateId candidateId
     * @return List<ProjectHistoryResponse>
     */
    public List<ProjectHistoryResponse> findByCandidate(Long candidateId) {
        List<ProjectAssignment> assignments = assignmentRepository.findByCandidate(candidateId);

        return assignments
                .stream()
                .map(this::convertToActiveProjects)
                .collect(Collectors.toList());
    }

    /**
     * @param assignment assignment
     * @return ProjectHistoryResponse
     */
    private ProjectHistoryResponse convertToActiveProjects(ProjectAssignment assignment) {
        Project project = projectRepository.findById(assignment.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("project", "id", assignment.getProjectId()));

        ProjectHistoryResponse response = modelMapper.map(assignment, ProjectHistoryResponse.class);
        ProjectLinkAssignmentResponse linkAssignmentResponse = new ProjectLinkAssignmentResponse();
        BeanUtils.copyProperties(project, linkAssignmentResponse);
        response.setProject(linkAssignmentResponse);
        return response;
    }
}