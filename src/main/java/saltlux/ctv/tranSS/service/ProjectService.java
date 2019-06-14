package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saltlux.ctv.tranSS.Specification.ProjectSpecification;
import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.exception.DuplicatedColumnsException;
import saltlux.ctv.tranSS.model.Project;
import saltlux.ctv.tranSS.model.User;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.payload.project.ProjectFilterRequest;
import saltlux.ctv.tranSS.payload.project.ProjectRequest;
import saltlux.ctv.tranSS.payload.project.ProjectResponse;
import saltlux.ctv.tranSS.payload.project.ProjectsItemResponse;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;
import saltlux.ctv.tranSS.repository.user.UserRepository;
import saltlux.ctv.tranSS.security.UserPrincipal;
import saltlux.ctv.tranSS.util.TransformUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static saltlux.ctv.tranSS.util.TransformUtil.createPageable;
import static saltlux.ctv.tranSS.util.ValidationUtil.isTrue;
import static saltlux.ctv.tranSS.util.ValidationUtil.isValidId;

@Slf4j
@Service
public class ProjectService {
    private final String[] COLUMNS = {};
    private List<String> needCheckDuplicatedColumns = Arrays.asList(COLUMNS);
    private final ProjectRepository projectRepository;
    private final ProjectMiddleService projectMiddleService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
                          ModelMapper modelMapper,
                          ProjectMiddleService projectMiddleService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.projectMiddleService = projectMiddleService;
    }

    /**
     * @param projectRequest projectRequest
     * @return ProjectResponse
     * @throws DuplicatedColumnsException e
     * @throws IllegalAccessException     e
     * @throws NoSuchMethodException      e
     * @throws InvocationTargetException  e
     */
    @Transactional
    public ProjectResponse create(ProjectRequest projectRequest)
            throws DuplicatedColumnsException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Long id = projectRequest.getId();
        Project project = null == id || id == 0 ? new Project() : projectRepository.findById(id).get();
        if (isValidId(project.getId()) &&
                isTrue(project.getIsOld()) &&
                !project.getCode().equals(projectRequest.getCode())) {
            throw new BadRequestException("Can't change Code of historical project");
        }
        /**
         * There is no Foreign key so we need change manually
         */
        projectMiddleService.updateProjectCode(project.getCode(), projectRequest.getCode());
        BeanUtils.copyProperties(projectRequest, project);
        /*VALIDATE DUPLICATE*/
        List<String> duplicatedColumns = TransformUtil
                .getDuplicatedColumns(
                        project, project.getId(), needCheckDuplicatedColumns, projectRepository
                );
        if (null != duplicatedColumns && duplicatedColumns.size() > 0) {
            throw new DuplicatedColumnsException(duplicatedColumns);
        }

        if (null == id || id == 0) {
            project.setNo(generateNo(project.getCompany()));
        }

        Optional<User> pmOptional = userRepository.findByCode(projectRequest.getPmCode());
        pmOptional.ifPresent(project::setPm);

        Project savedProject = projectRepository.save(project);

        ProjectResponse projectResponse = new ProjectResponse();
        BeanUtils.copyProperties(savedProject, projectResponse, "pmCode", "pm");
        projectResponse.setPmCode(savedProject.getPm().getCode());
        return projectResponse;
    }

    /**
     * @param department department
     * @return String
     */
    private String generateNo(String department) {
        String maxNo = projectRepository.getMaxNo(department).get(0);
        return TransformUtil.generateCode(department, maxNo, 5, 2);
    }

    /**
     * @param page          page
     * @param size          size
     * @param keyWord       keyWord
     * @param orderBy       orderBy
     * @param sortDirection asc or desc
     * @param filters       filters
     * @return list of project
     */
    public PagedResponse<ProjectsItemResponse> search(int page, int size, String keyWord,
                                                      String orderBy, String sortDirection, ProjectFilterRequest filters) {
        Pageable pageable = createPageable(page, size, orderBy, sortDirection, "updatedAt");
        Page<Project> projectPage;

        if ((null == filters || filters.hasNoFilter()) && isNullOrEmpty(keyWord)) {
            projectPage = projectRepository.findAll(pageable);
        } else {
            Specification spec = buildFilterSpec(filters, keyWord);
            projectPage = projectRepository.findAll(spec, pageable);
        }
        List<Project> projectList = projectPage.getContent();
        List<ProjectsItemResponse> projectResponses = projectList
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());


        return new PagedResponse<>(projectResponses, projectPage.getNumber(),
                projectPage.getSize(), projectPage.getTotalElements(), projectPage.getTotalPages(), projectPage.isLast());

    }

    /**
     * @param project project
     * @return ProjectsItemResponse
     */
    private ProjectsItemResponse convertToDto(Project project) {
        ProjectsItemResponse response = modelMapper.map(project, ProjectsItemResponse.class);
        return response;
    }

    /**
     * @param baseFilters baseFilters
     * @param keyWord     keyWord
     * @return Specification
     */
    private Specification buildFilterSpec(ProjectFilterRequest baseFilters, String keyWord) {
        List<FilterRequest> rootFilters = baseFilters.getRootFilters();
        Specification rootSpec = buildSpec(rootFilters, false, false);
        List<FilterRequest> joinFilters = baseFilters.getJoinFilters();
        Specification joinSpec = buildSpec(joinFilters, true, false);
        List<FilterRequest> assignmentFilters = baseFilters.getAssignFilters();
        Specification assignmentSpec = buildSpec(assignmentFilters, false, true);
        Specification keyWordSpec = buildSpec(rootFilters, keyWord);
        Specification spec = TransformUtil.integrate(TransformUtil.integrate(joinSpec, rootSpec), assignmentSpec);
        return TransformUtil.integrate(spec, keyWordSpec);
    }

    /**
     * @param id id
     * @return ProjectRequest
     */
    public ProjectRequest findCandidateById(Long id) {
        Project project = projectRepository.findById(id).get();
        ProjectRequest projectRequest = new ProjectRequest();
        BeanUtils.copyProperties(project, projectRequest);
        projectRequest.setPmCode(project.getPm().getCode());
        return projectRequest;
    }

    /**
     * @param filters      f
     * @param isJoin       j
     * @param isAssignment ass
     * @return Specification
     */
    private static Specification buildSpec(List<FilterRequest> filters, boolean isJoin, boolean isAssignment) {
        Specification rootSpec = null;
        if (null != filters && filters.size() > 0) {
            for (FilterRequest field : filters) {
                ProjectSpecification spec =
                        new ProjectSpecification(
                                new SearchCriteria(
                                        field.getField(),
                                        field.getOperation(),
                                        field.getValue()),
                                isJoin, isAssignment);
                rootSpec = rootSpec == null ? spec : Specification.where(rootSpec).and(spec);
            }
        }
        return rootSpec;
    }

    /**
     * @param rootFilters rootFilters
     * @param keyWord     keyWord
     * @return Specification
     */
    private static Specification buildSpec(List<FilterRequest> rootFilters, String keyWord) {
        Specification keyWordSpec = null;
        if (!isNullOrEmpty(keyWord)) {
            List<Field> fields = TransformUtil.ignoreNeedFilterFields(new Project(), rootFilters);
            for (Field field : fields) {
                ProjectSpecification spec =
                        new ProjectSpecification(
                                new SearchCriteria(field.getName(), ":", keyWord));
                keyWordSpec = keyWordSpec == null ? spec : Specification.where(keyWordSpec).or(spec);
            }

        }
        return keyWordSpec;
    }

}