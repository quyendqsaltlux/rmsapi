package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.Specification.CandidateSpecification;
import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.exception.DuplicatedColumnsException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.Payment;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.payload.resource.CandidateRequest;
import saltlux.ctv.tranSS.payload.resource.ResourceForProjectResponse;
import saltlux.ctv.tranSS.repository.PaymentRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;
import saltlux.ctv.tranSS.util.TransformUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static saltlux.ctv.tranSS.util.TransformUtil.createPageable;

@Slf4j
@Service
public class CandidateService {
    private final String[] COLUMNS = {"name", "code", "email", "email2"};
    private List<String> needCheckDuplicatedColumns = Arrays.asList(COLUMNS);

    private final CandidateRepository candidateRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, PaymentRepository paymentRepository, ModelMapper modelMapper) {
        this.candidateRepository = candidateRepository;
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param candidateRequest candidateRequest
     * @return Candidate
     * @throws DuplicatedColumnsException e
     * @throws IllegalAccessException     e
     * @throws NoSuchMethodException      e
     * @throws InvocationTargetException  e
     */
    public Candidate createCandidate(CandidateRequest candidateRequest)
            throws DuplicatedColumnsException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Long id = candidateRequest.getId();
        Candidate candidate = null == id || id == 0 ? new Candidate() : candidateRepository.findById(id).get();
        BeanUtils.copyProperties(candidateRequest, candidate, "abilities", "payment");

        List<String> duplicatedColumns = TransformUtil
                .getDuplicatedColumns(
                        candidate, candidate.getId(), needCheckDuplicatedColumns, candidateRepository
                );
        if (null != duplicatedColumns && duplicatedColumns.size() > 0) {
            throw new DuplicatedColumnsException(duplicatedColumns);
        }
        if (null == id || id == 0) {
            candidate.setCode(generateCandidateCode(candidate.getType()));
        }
        if (null != candidateRequest.getPaymentId() && candidateRequest.getPaymentId() > 0) {
            Optional<Payment> paymentOptional = this.paymentRepository.findById(candidateRequest.getPaymentId());
            paymentOptional.ifPresent(candidate::setPayment);
        }

        return candidateRepository.save(candidate);
    }

    /**
     * @param type type of candidate
     * @return CODE
     */
    private String generateCandidateCode(String type) {
        String maxCodeCandidate = candidateRepository.getMaxCode(type).get(0);
        return TransformUtil.generateCode(type, maxCodeCandidate, 4, 2);
    }

    /**
     * @param page          page
     * @param size          size
     * @param keyWord       keyWord
     * @param orderBy       orderBy
     * @param sortDirection sortDirection
     * @param filter        filter
     * @return PagedResponse
     */
    public PagedResponse<Candidate> searchCandidate(int page, int size, String keyWord, String orderBy, String sortDirection, BaseFilterRequest filter) {
        Page<Candidate> candidates;
        Pageable pageable = createPageable(page, size, orderBy, sortDirection, "updatedAt");

        if ((null == filter || filter.hasNoFilter()) && isNullOrEmpty(keyWord)) {
            candidates = candidateRepository.findAll(pageable);
        } else {
            Specification spec = buildFilterSpec(filter, keyWord);
            candidates = candidateRepository.findAll(spec, pageable);
        }

        return new PagedResponse<>(candidates.getContent(), candidates.getNumber(),
                candidates.getSize(), candidates.getTotalElements(), candidates.getTotalPages(), candidates.isLast());

    }

    /**
     * @param filters filters
     * @param keyWord keyWord
     * @return Specification
     */
    private Specification buildFilterSpec(BaseFilterRequest filters, String keyWord) {
        if (isNullOrEmpty(keyWord) && (null == filters || filters.hasNoFilter())) {
            return null;
        }

        List<FilterRequest> candidateFilters = filters.getRootFilters();
        Specification candidateSpec = buildSpec(candidateFilters, false);
        List<FilterRequest> abilityFilters = filters.getJoinFilters();
        Specification abilitySpec = buildSpec(abilityFilters, true);
        Specification keyWordSpec = buildSpec(candidateFilters, keyWord);

        Specification candidateSpecification = TransformUtil.integrate(candidateSpec, abilitySpec);

        return TransformUtil.integrate(keyWordSpec, candidateSpecification);
    }

    public Candidate findCandidateById(Long id) {
        return candidateRepository.findById(id).get();
    }

    private static Specification buildSpec(List<FilterRequest> filters, boolean isJoin) {
        Specification rootSpec = null;
        if (null != filters && filters.size() > 0) {
            for (FilterRequest field : filters) {
                CandidateSpecification spec =
                        new CandidateSpecification(
                                new SearchCriteria(field.getField(), field.getOperation(), field.getValue()), isJoin);
                rootSpec = rootSpec == null ? spec : Specification.where(rootSpec).and(spec);
            }
        }
        return rootSpec;
    }

    private static Specification buildSpec(List<FilterRequest> rootFilters, String keyWord) {
        Specification keyWordSpec = null;
        if (!isNullOrEmpty(keyWord)) {
            List<Field> fields = TransformUtil.ignoreNeedFilterFields(new Candidate(), rootFilters);
            for (Field field : fields) {
                CandidateSpecification spec =
                        new CandidateSpecification(
                                new SearchCriteria(field.getName(), ":", keyWord));
                keyWordSpec = keyWordSpec == null ? spec : Specification.where(keyWordSpec).or(spec);
            }

        }
        return keyWordSpec;
    }

    public Candidate getByCode(String code) {
        return candidateRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "code", code));
    }

    public List<ResourceForProjectResponse> findCandidateByCodeOrNameLike(String keyword) {
        List<Candidate> candidates = candidateRepository.findTop100ByCodeOrNameContains(keyword);
        return candidates.stream()
                .map(candidate -> modelMapper.map(candidate, ResourceForProjectResponse.class))
                .collect(Collectors.toList());
    }


    public void delete(Long id) {
        candidateRepository.deleteById(id);
    }
}