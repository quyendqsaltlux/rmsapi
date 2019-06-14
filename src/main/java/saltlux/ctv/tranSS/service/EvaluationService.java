package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.*;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.evaluation.*;
import saltlux.ctv.tranSS.payload.project.ProjectsBasicResponse;
import saltlux.ctv.tranSS.repository.candidate.CandidateAbilityRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;
import saltlux.ctv.tranSS.repository.evaluation.GeneralCommentRepository;
import saltlux.ctv.tranSS.repository.evaluation.OtherNoteCommentRepository;
import saltlux.ctv.tranSS.repository.evaluation.SpecificCommentRepository;
import saltlux.ctv.tranSS.repository.project.ProjectAssignmentRepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EvaluationService {
    private final SpecificCommentRepository specificCommentRepo;
    private final ProjectAssignmentRepository assignmentRepo;
    private final ProjectRepository projectRepo;
    private final GeneralCommentRepository generalCommentRepo;
    private final CandidateRepository candidateRepo;
    @Autowired
    private CandidateAbilityRepository abilityRepo;
    private final OtherNoteCommentRepository otherNoteRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public EvaluationService(SpecificCommentRepository specificCommentRepo, ProjectAssignmentRepository assignmentRepo, ProjectRepository projectRepo, GeneralCommentRepository generalCommentRepo, CandidateRepository candidateRepo, OtherNoteCommentRepository otherNoteRepo, ModelMapper modelMapper) {
        this.specificCommentRepo = specificCommentRepo;
        this.assignmentRepo = assignmentRepo;
        this.projectRepo = projectRepo;
        this.generalCommentRepo = generalCommentRepo;
        this.candidateRepo = candidateRepo;
        this.otherNoteRepo = otherNoteRepo;
        this.modelMapper = modelMapper;
    }

    /**
     * @param commentRequest req
     * @return SpecificCommentResponse
     */
    public SpecificCommentResponse saveSpecificComment(SpecificCommentRequest commentRequest, Long candidateId) {
        Long id = commentRequest.getId();
        ProjectAssignment assignment = assignmentRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("assignment", "code", candidateId));
        SpecificComment comment = null == id || id == 0 ? new SpecificComment() : specificCommentRepo.findById(id).get();
        int previousStar = null == comment.getStar() ? 0 : comment.getStar();
        BeanUtils.copyProperties(commentRequest, comment);
        comment.setAssignment(assignment);

        if (null != assignment.getAbilityId()) {
            Optional<CandidateAbility> abilityOptional = abilityRepo.findById(assignment.getAbilityId());
            if (abilityOptional.isPresent()) {
                CandidateAbility ability = abilityOptional.get();
                int count = null == ability.getEvaluateCount() ? 0 : ability.getEvaluateCount();
                int total = null == ability.getEvaluateTotal() ? 0 : ability.getEvaluateTotal();
                if (null == id) {
                    count++;
                    total += commentRequest.getStar();
                } else {
                    total = total - previousStar + commentRequest.getStar();
                }
                ability.setEvaluateCount(count);
                ability.setEvaluateTotal(total);
                if (count > 0) {
                    ability.setEvaluateAvg(total / count);
                }
                abilityRepo.save(ability);
            }
        }

        SpecificComment savedComment = specificCommentRepo.save(comment);
        return modelMapper.map(savedComment, SpecificCommentResponse.class);
    }

    /**
     * @param id id
     * @return SpecificCommentResponse
     */
    public SpecificCommentResponse findSpecificComment(Long id) {
        Optional<SpecificComment> specificComment = specificCommentRepo.findTopByAssignment(id);
        if (!specificComment.isPresent()) {
            throw new ResourceNotFoundException("Specific Comment for Assignment", "id", id);
        }
        return modelMapper.map(specificComment.get(), SpecificCommentResponse.class);
    }

    /**
     * @param id id
     */
    public void deleteSpecificComment(Long id) {
        SpecificComment comment = specificCommentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("comment", "code", id));
        ProjectAssignment assignment = comment.getAssignment();
        if (null != assignment.getAbilityId()) {
            Optional<CandidateAbility> abilityOptional = abilityRepo.findById(assignment.getAbilityId());
            if (abilityOptional.isPresent()) {
                CandidateAbility ability = abilityOptional.get();
                int count = null == ability.getEvaluateCount() ? 0 : ability.getEvaluateCount();
                int total = null == ability.getEvaluateTotal() ? 0 : ability.getEvaluateTotal();
                if (count > 0) {
                    count++;
                }
                if (total - comment.getStar() > 0) {
                    total -= comment.getStar();
                } else {
                    total = 0;
                }
                ability.setEvaluateCount(count);
                ability.setEvaluateTotal(total);
                if (count > 0) {
                    ability.setEvaluateAvg(total / count);
                }
                abilityRepo.save(ability);
            }
        }
        specificCommentRepo.deleteById(id);

    }

    /**
     * @param page
     * @param size
     * @param keyWord
     * @param orderBy
     * @param sortDirection
     * @param filters
     * @param candidateId
     * @return
     */
    public PagedResponse<SpecificCommentResponse> searchSpecificComments(int page, int size, String keyWord, String orderBy,
                                                                         String sortDirection, BaseFilterRequest filters, Long candidateId) {
        PagedResponse<SpecificComment> pagedResponse = specificCommentRepo.searchComment(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
        List<SpecificComment> projectList = pagedResponse.getContent();

        List<SpecificCommentResponse> responses = projectList.stream()
                .map(this::convertToSearchResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, page, size,
                pagedResponse.getTotalElements(), pagedResponse.getTotalPages(), pagedResponse.isLast());
    }

    /**
     * @param comment
     * @return
     */
    private SpecificCommentResponse convertToSearchResponse(SpecificComment comment) {
        SpecificCommentResponse response = modelMapper.map(comment, SpecificCommentResponse.class);
        Project project = projectRepo.findById(comment.getAssignment().getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", comment.getAssignment().getProjectId()));
        response.getAssignment().setProject(modelMapper.map(project, ProjectsBasicResponse.class));
        return response;
    }

//GENERAL

    /**
     * @param commentRequest
     * @param candidateId
     * @return
     */
    public GeneralCommentResponse saveGeneralComment(GeneralCommentRequest commentRequest, Long candidateId) {
        Long id = commentRequest.getId();
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        GeneralComment comment = null == id || id == 0 ? new GeneralComment() : generalCommentRepo.findById(id).get();
        BeanUtils.copyProperties(commentRequest, comment);
        comment.setCandidate(candidate);
        GeneralComment savedComment = generalCommentRepo.save(comment);
        return modelMapper.map(savedComment, GeneralCommentResponse.class);
    }

    /**
     * @param id
     * @return
     */
    public GeneralCommentResponse findGeneralComment(Long id) {
        GeneralComment generalComment = generalCommentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General comment", "id", id));
        return modelMapper.map(generalComment, GeneralCommentResponse.class);
    }

    /**
     * @param id
     */
    public void deleteGeneralComment(Long id) {
        generalCommentRepo.deleteById(id);
    }

    /**
     * @param page
     * @param size
     * @param keyWord
     * @param orderBy
     * @param sortDirection
     * @param filters
     * @param candidateId
     * @return
     */
    public PagedResponse<GeneralCommentResponse> searchGeneralComments(int page, int size, String keyWord, String orderBy,
                                                                       String sortDirection, BaseFilterRequest filters, Long candidateId) {
        PagedResponse<GeneralComment> pagedResponse = generalCommentRepo
                .searchComment(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
        List<GeneralComment> projectList = pagedResponse.getContent();
        List<GeneralCommentResponse> responses = projectList.stream()
                .map(this::convertToGeneralSearchResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, page, size,
                pagedResponse.getTotalElements(), pagedResponse.getTotalPages(), pagedResponse.isLast());
    }

    /**
     * @param comment
     * @return
     */
    private GeneralCommentResponse convertToGeneralSearchResponse(GeneralComment comment) {
        return modelMapper.map(comment, GeneralCommentResponse.class);
    }


    //OTHER NOTE

    /**
     * @param commentRequest
     * @param candidateId
     * @return
     */
    public OtherNoteCommentResponse saveOtherNote(OtherNoteCommentRequest commentRequest, Long candidateId) {
        Long id = commentRequest.getId();
        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        OtherNoteComment comment = null == id || id == 0 ? new OtherNoteComment() : otherNoteRepo.findById(id).get();
        BeanUtils.copyProperties(commentRequest, comment);
        comment.setCandidate(candidate);
        OtherNoteComment savedComment = otherNoteRepo.save(comment);
        return modelMapper.map(savedComment, OtherNoteCommentResponse.class);
    }

    /**
     * @param id
     * @return
     */
    public OtherNoteCommentResponse findOtherNote(Long id) {
        OtherNoteComment generalComment = otherNoteRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("General comment", "id", id));
        return modelMapper.map(generalComment, OtherNoteCommentResponse.class);
    }

    /**
     * @param id
     */
    public void deleteOtherNote(Long id) {
        otherNoteRepo.deleteById(id);
    }

    /**
     * @param page
     * @param size
     * @param keyWord
     * @param orderBy
     * @param sortDirection
     * @param filters
     * @param candidateId
     * @return
     */
    public PagedResponse<OtherNoteCommentResponse> searchOtherNote(int page, int size, String keyWord, String orderBy,
                                                                   String sortDirection, BaseFilterRequest filters, Long candidateId) {
        PagedResponse<OtherNoteComment> pagedResponse = otherNoteRepo
                .searchComment(page, size, keyWord, orderBy, sortDirection, filters, candidateId);
        List<OtherNoteComment> projectList = pagedResponse.getContent();
        List<OtherNoteCommentResponse> responses = projectList.stream()
                .map(this::convertToGeneralSearchResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, page, size,
                pagedResponse.getTotalElements(), pagedResponse.getTotalPages(), pagedResponse.isLast());
    }

    /**
     * @param comment comment
     * @return OtherNoteCommentResponse
     */
    private OtherNoteCommentResponse convertToGeneralSearchResponse(OtherNoteComment comment) {
        return modelMapper.map(comment, OtherNoteCommentResponse.class);
    }

}
