package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.Candidate;
import saltlux.ctv.tranSS.model.CandidateAbility;
import saltlux.ctv.tranSS.payload.PagedResponse;
import saltlux.ctv.tranSS.payload.resource.AbilitySearchResponse;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilitiesWithCurrency;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilityRequest;
import saltlux.ctv.tranSS.payload.resource.CandidateAbilitySearchRequest;
import saltlux.ctv.tranSS.repository.candidate.CandidateAbilityRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandidateAbilityService {

    private final CandidateAbilityRepository abilityRepository;
    private final CandidateRepository candidateRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CandidateAbilityService(CandidateAbilityRepository abilityRepository,
                                   CandidateRepository candidateRepository, ModelMapper modelMapper) {
        this.abilityRepository = abilityRepository;
        this.candidateRepository = candidateRepository;
        this.modelMapper = modelMapper;
    }

    public CandidateAbility saveAbility(CandidateAbilityRequest abilityRequest) throws MissingServletRequestParameterException {
        if (abilityRequest == null || abilityRequest.getCandidateId() == null || abilityRequest.getCandidateId() == 0) {
            throw new MissingServletRequestParameterException(null, null);
        }
        Long id = null != abilityRequest.getId() && abilityRequest.getId() > 0 ? abilityRequest.getId() : null;
        CandidateAbility ability = null == id || id == 0 ? new CandidateAbility() : abilityRepository.findById(id).get();
        BeanUtils.copyProperties(abilityRequest, ability);

        Candidate candidate = candidateRepository.findById(abilityRequest.getCandidateId()).get();
        ability.setCandidate(candidate);

        return abilityRepository.save(ability);
    }

    public CandidateAbilitiesWithCurrency getListAll(Long candidateId) {
        List<CandidateAbilityRequest> abilities = new ArrayList<>();

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));
        for (CandidateAbility ability : abilityRepository.findByCandidate(candidateId)) {
            CandidateAbilityRequest abilityRequest = new CandidateAbilityRequest();
            BeanUtils.copyProperties(ability, abilityRequest, "candidate");
            abilityRequest.setCandidateId(ability.getCandidate().getId());
            abilities.add(abilityRequest);
        }
        CandidateAbilitiesWithCurrency response = new CandidateAbilitiesWithCurrency();
        response.setAbilities(abilities);
        response.setCurrency(candidate.getCurrency());

        return response;
    }

    public void delete(Long id) {
        this.abilityRepository.deleteById(id);
    }


    public PagedResponse<AbilitySearchResponse> searchAbility(int page, int size, String keyWord, CandidateAbilitySearchRequest search) {
        PagedResponse<CandidateAbility> abilityPagedResponse = abilityRepository.search(page, size, keyWord, search);
        List<AbilitySearchResponse> list = abilityPagedResponse.getContent().stream()
                .map(this::convertToSearchDto).collect(Collectors.toList());

        return new PagedResponse<>(list, page, size,
                abilityPagedResponse.getTotalElements(), abilityPagedResponse.getTotalPages(), abilityPagedResponse.isLast());

    }

    private AbilitySearchResponse convertToSearchDto(CandidateAbility ability) {
        return modelMapper.map(ability, AbilitySearchResponse.class);
    }
}