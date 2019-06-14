package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.Specification.CandidateTestWaitingSpecification;
import saltlux.ctv.tranSS.Specification.SearchCriteria;
import saltlux.ctv.tranSS.model.ResourceTestWaiting;
import saltlux.ctv.tranSS.payload.BaseFilterRequest;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.common.FilterRequest;
import saltlux.ctv.tranSS.payload.resource.ResourceTestWaitingRequest;
import saltlux.ctv.tranSS.repository.candidate.CandidateTestWaitingRepository;
import saltlux.ctv.tranSS.util.TransformUtil;
import saltlux.ctv.tranSS.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static saltlux.ctv.tranSS.util.TransformUtil.createPageable;

@Slf4j
@Service
public class CandidateTestWaitingService {
    private final CandidateTestWaitingRepository testWaitingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CandidateTestWaitingService(CandidateTestWaitingRepository testWaitingRepository, ModelMapper modelMapper) {
        this.testWaitingRepository = testWaitingRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * @param candidateRequest candidateRequest
     * @return ResourceTestWaiting
     */
    public ResourceTestWaiting create(ResourceTestWaitingRequest candidateRequest) {

        Long id = candidateRequest.getId();
        ResourceTestWaiting candidate = null == id || id == 0 ? new ResourceTestWaiting() : testWaitingRepository.findById(id).get();
        BeanUtils.copyProperties(candidateRequest, candidate);
        if(null == candidate.getIsShortList()){
            candidate.setIsShortList(0);
        }

        return testWaitingRepository.save(candidate);
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
    public PagedResponse<ResourceTestWaiting> search(int page, int size, String keyWord, String orderBy, String sortDirection, BaseFilterRequest filter) {
        Page<ResourceTestWaiting> candidates;
        Pageable pageable = createPageable(page, size, orderBy, sortDirection, "updatedAt");

        if ((null == filter || filter.hasNoFilter()) && isNullOrEmpty(keyWord)) {
            candidates = testWaitingRepository.findAll(pageable);
        } else {
            Specification spec = buildFilterSpec(filter, keyWord);
            candidates = testWaitingRepository.findAll(spec, pageable);
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
        Specification candidateSpec = buildSpec(candidateFilters);
        Specification keyWordSpec = buildSpec(candidateFilters, keyWord);

        return TransformUtil.integrate(keyWordSpec, candidateSpec);
    }

    /**
     * @param id id
     * @return ResourceTestWaiting
     */
    public ResourceTestWaiting findById(Long id) {
        return testWaitingRepository.findById(id).get();
    }


    /**
     * @param filters filters
     * @return Specification
     */
    private static Specification buildSpec(List<FilterRequest> filters) {
        Specification rootSpec = null;
        if (null != filters && filters.size() > 0) {
            for (FilterRequest field : filters) {
                CandidateTestWaitingSpecification spec =
                        new CandidateTestWaitingSpecification(
                                new SearchCriteria(field.getField(), field.getOperation(), field.getValue()));
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
            List<Field> fields = TransformUtil.ignoreNeedFilterFields(new ResourceTestWaiting(), rootFilters);
            for (Field field : fields) {
                CandidateTestWaitingSpecification spec =
                        new CandidateTestWaitingSpecification(
                                new SearchCriteria(field.getName(), ":", keyWord));
                keyWordSpec = keyWordSpec == null ? spec : Specification.where(keyWordSpec).or(spec);
            }

        }
        return keyWordSpec;
    }

    /**
     * @param id id
     */
    public void delete(Long id) {
        testWaitingRepository.deleteById(id);
    }
}