package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saltlux.ctv.tranSS.enums.MigrateEnum;
import saltlux.ctv.tranSS.enums.PaymentEnum;
import saltlux.ctv.tranSS.enums.ProjectProgressEnum;
import saltlux.ctv.tranSS.exception.AppException;
import saltlux.ctv.tranSS.exception.ResourceNotFoundException;
import saltlux.ctv.tranSS.model.*;
import saltlux.ctv.tranSS.payload.payment.PaymentReq;
import saltlux.ctv.tranSS.payload.project.ProjectRequest;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectAssignmentRequest;
import saltlux.ctv.tranSS.repository.MigrateRepository;
import saltlux.ctv.tranSS.repository.PaymentRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateAbilityRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateRepository;
import saltlux.ctv.tranSS.repository.candidate.CandidateTestWaitingRepository;
import saltlux.ctv.tranSS.repository.project.ProjectAssignmentRepository;
import saltlux.ctv.tranSS.repository.project.ProjectRepository;
import saltlux.ctv.tranSS.repository.user.RoleRepository;
import saltlux.ctv.tranSS.repository.user.UserRepository;
import saltlux.ctv.tranSS.util.AppConstants;
import saltlux.ctv.tranSS.util.DuplicatedHandler;
import saltlux.ctv.tranSS.util.ExcelUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MigrateService {

    private final String DUPLICATED_KEY = "D";
    private final int PROJECT_NO_SUFFIX_SIZE = 5;//SP1912345->12345 (5 digits)
    private final int PROJECT_NO_DUPLICATED_DIGIT_SUFFIX_SIZE = 3;//SP1912345->12345 (5 digits)
    private final int PROJECT_NO_YEAR_SIZE = 1;

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final CandidateRepository candidateRepository;
    private final MigrateRepository migrateRepository;
    private final RoleRepository roleRepository;
    private final PaymentRepository paymentRepository;
    private final ProjectAssignmentRepository assignmentRepository;
    private final CandidateAbilityRepository abilityRepo;
    private final CandidateTestWaitingRepository testWaitingRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MigrateService(UserRepository userRepository,
                          CandidateRepository candidateRepository,
                          ProjectRepository projectRepository,
                          MigrateRepository migrateRepository,
                          RoleRepository roleRepository,
                          PaymentRepository paymentRepository,
                          ProjectAssignmentRepository assignmentRepository,
                          CandidateAbilityRepository abilityRepo,
                          CandidateTestWaitingRepository testWaitingRepo, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.projectRepository = projectRepository;
        this.migrateRepository = migrateRepository;
        this.roleRepository = roleRepository;
        this.paymentRepository = paymentRepository;
        this.assignmentRepository = assignmentRepository;
        this.abilityRepo = abilityRepo;
        this.testWaitingRepo = testWaitingRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return number of imported users
     */
    @Transactional
    public List<MigrateData> loadTestWaitingFromFile() throws Exception {
        if (!canStartMigrate(MigrateEnum.TEST_WAITING)) {
            throw new Exception("Data's existed!");
        }

        List<ResourceTestWaiting> testWaitings = ExcelUtil.loadCandidateTestWaiting();
        testWaitings.forEach(testWaiting -> {
            List<ResourceTestWaiting> testWaitingList = testWaitingRepo.findByName(testWaiting.getName());
            if (testWaitingList.isEmpty()) {
                testWaitingRepo.save(testWaiting);
            }else {
                ResourceTestWaiting item = testWaitingList.get(0);
                item.setField(testWaiting.getField());
                item.setProcessStatus(testWaiting.getProcessStatus());
                item.setExpectedRateRange(testWaiting.getExpectedRateRange());
                item.setNegotiationDate(testWaiting.getNegotiationDate());
                item.setShortListDate(testWaiting.getShortListDate());
                testWaitingRepo.save(item);
            }
        });

        this.updateMigration(MigrateEnum.TEST_WAITING);
        return this.getAll();
    }

    /**
     * @return List
     * @throws Exception e
     */

    public List<MigrateData> loadAssignmentFromFile() throws Exception {
        if (!canStartMigrate(MigrateEnum.ASSIGNMENTS)) {
            throw new Exception("Data's existed!");
        }
        Set<ProjectAssignmentRequest> assignmentRequests = ExcelUtil.loadAssignment();

        for (ProjectAssignmentRequest assignmentRequest : assignmentRequests) {
            ProjectAssignment assignment = new ProjectAssignment();
            BeanUtils.copyProperties(assignmentRequest, assignment);
            Optional<Candidate> candidateOptional = candidateRepository.findByCode(assignmentRequest.getCandidateCode());
            if (!candidateOptional.isPresent()) {
                continue;
            }
            assignment.setCandidate(candidateOptional.get());
            List<CandidateAbility> abilities = abilityRepo.
                    findTopByCandidateAndTaskAndSourceLanguageAndTargetLanguage(
                            candidateOptional.get().getId(),
                            assignmentRequest.getTask(),
                            assignment.getSource(),
                            assignment.getTarget());
            if (!abilities.isEmpty()) {
                assignment.setAbilityId(abilities.get(0).getId());
            }

            try {
                assignmentRepository.save(assignment);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        this.updateMigration(MigrateEnum.ASSIGNMENTS);

        return this.getAll();
    }

    /**
     * @return number of imported users
     */
    public List<MigrateData> loadUsersFromFile() throws Exception {
        if (!canStartMigrate(MigrateEnum.USERS)) {
            throw new Exception("Data's existed!");
        }
        Role role = roleRepository.findByName(RoleName.ROLE_PM)
                .orElseThrow(() -> new AppException("User Role not set."));
        Role role1 = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        Role role3 = roleRepository.findByName(RoleName.ROLE_PM_LEADER)
                .orElseThrow(() -> new AppException("User Role not set."));
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        roleSet.add(role1);
        Set<User> users = ExcelUtil.loadPM(roleSet);
        for (User user : users) {
            user.setPassword(passwordEncoder.encode("123456"));
            if (AppConstants.PM_LEADER.equals(user.getCode()) || AppConstants.PM_LEADER_KOREA.equals(user.getCode())) {
                Set<Role> roles = new HashSet<>(user.getRoles());
                roles.add(role3);
                user.setRoles(roles);
            }
        }
        userRepository.saveAll(users);
        this.updateMigration(MigrateEnum.USERS);
        return this.getAll();
    }

    /**
     * @return number of imported project
     */
    public List<MigrateData> loadProjectFromFile(String type) throws Exception {
        if (ProjectProgressEnum.FINISHED.toString().equals(type) && !canStartMigrate(MigrateEnum.FINISHED_PROJECTS)) {
            throw new Exception("Data's existed!");
        } else if (ProjectProgressEnum.ON_GOING.toString().equals(type) && !canStartMigrate(MigrateEnum.ONGOING_PROJECTS)) {
            throw new Exception("Data's existed!");
        }
        Set<ProjectRequest> projectRequests = ExcelUtil.loadProject(type);
        if (null == projectRequests) {
            throw new ResourceNotFoundException("Project", "type", type);
        }
        projectRequests.forEach((projectRequest -> {
            if (null != projectRequest.getCode() && null != projectRequest.getPmCode()) {
                Project project = new Project();
                BeanUtils.copyProperties(projectRequest, project, "pmCode", "id");
                User user = userRepository.findByCode(projectRequest.getPmCode())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "code", projectRequest.getPmCode()));
                project.setPm(user);
                project.setProgressStatus(type);

                DuplicatedHandler duplicatedHandler = new DuplicatedHandler();
                boolean savedDone = false;
                while (!savedDone) {
                    try {
                        projectRepository.save(project);
                        savedDone = true;
                    } catch (Exception e) {
                        savedDone = false;
                        updateLastSuffix(duplicatedHandler);
                        duplicatedHandler.setLastDuplicatedNo(project.getNo());
                        project.setNo(generateNoForWrongDataProject(project, duplicatedHandler.getLastSuffix()));
                        project.setIsWrongCode(true);
                    }
                }
            }
        }));

        if ("FINISHED".equals(type)) {
            this.updateMigration(MigrateEnum.FINISHED_PROJECTS);
        } else {
            this.updateMigration(MigrateEnum.ONGOING_PROJECTS);
        }
        return this.getAll();
    }

    private void updateLastSuffix(DuplicatedHandler handler) {
        String nexLastSuffix;
        if (null == handler.getLastDuplicatedNo()) {
            nexLastSuffix = DUPLICATED_KEY + "00A";
        } else {
            char x = handler.getLastSuffix().charAt(handler.getLastSuffix().length() - 1);
            char y = (char) ((int) x + 1);
            if (y <= 90 && y >= 65) {
                nexLastSuffix = DUPLICATED_KEY + String.format("%0" + PROJECT_NO_DUPLICATED_DIGIT_SUFFIX_SIZE + "d", handler.getLastDigitSuffix()) + y;
            } else {
                handler.setLastDigitSuffix(handler.getLastDigitSuffix() + 1);
                nexLastSuffix = DUPLICATED_KEY + String.format("%0" + PROJECT_NO_DUPLICATED_DIGIT_SUFFIX_SIZE + "d", handler.getLastDigitSuffix()) + "A";
            }
        }

        handler.setLastSuffix(nexLastSuffix);
    }

    /**
     * In project history some project no is same due to human mistake
     * So need to generate other no for project.
     *
     * @param project    project with no SP1912345 or P1912345
     * @param lastSuffix ex: DA
     * @return SPD000A1912345 or PD000A1912345, SPD001Z1912345 or PD100Z1912345
     */
    private String generateNoForWrongDataProject(Project project, String lastSuffix) {
        String projectNo = project.getNo();
        if (null != lastSuffix) {
            boolean isSP = projectNo.contains("SP");
            boolean isP = projectNo.contains("P");
            String suffix = projectNo.substring(projectNo.length() - PROJECT_NO_SUFFIX_SIZE - PROJECT_NO_YEAR_SIZE - 1, projectNo.length());
            if (isSP) {
                return projectNo.substring(0, 2) + lastSuffix + suffix;
            } else if (isP) {
                return projectNo.substring(0, 1) + lastSuffix + suffix;
            }
        }
        return null;
    }

    /**
     * @return number of imported candidates
     */
    public List<MigrateData> loadCandidateFromFile() throws Exception {
        if (!canStartMigrate(MigrateEnum.RESOURCES)) {
            throw new Exception("Data's existed!");
        }
        List<Candidate> candidateSet = ExcelUtil.loadCandidate();
        candidateRepository.saveAll(candidateSet);
        this.updateMigration(MigrateEnum.RESOURCES);

        return this.getAll();
    }

    /**
     * @return number of imported candidates
     */
    public List<MigrateData> loadOverseaPaymentFromFile() throws Exception {
        if (!canStartMigrate(MigrateEnum.OVERSEA_PAYMENT)) {
            throw new Exception("Data's existed!");
        }
        Set<PaymentReq> paymentReqs = ExcelUtil.loadOverseaPayment();
        return savePayments(paymentReqs, PaymentEnum.OVER_SEA, MigrateEnum.OVERSEA_PAYMENT);

    }

    /**
     * @return number of imported candidates
     */
    public List<MigrateData> loadKoreaPaymentFromFile() throws Exception {
        if (!canStartMigrate(MigrateEnum.KOREA_PAYMENT)) {
            throw new Exception("Data's existed!");
        }
        Set<PaymentReq> paymentReqs = ExcelUtil.loadKoreaPayment();
        return savePayments(paymentReqs, PaymentEnum.KOREA, MigrateEnum.KOREA_PAYMENT);
    }

    /**
     * @return number of imported candidates
     */
    private List<MigrateData> savePayments(Set<PaymentReq> paymentReqs, PaymentEnum type, MigrateEnum migrateEnum) throws Exception {
        paymentReqs.forEach(paymentReq -> {
            Optional<Candidate> candidateOptional = candidateRepository.findByCode(paymentReq.getCandidateCode());
            if (candidateOptional.isPresent()) {
                Payment payment = new Payment();
                BeanUtils.copyProperties(paymentReq, payment);
                payment.setType(type.toString());
                Candidate candidate = candidateOptional.get();
                Payment savedPayment = paymentRepository.save(payment);
                candidate.setPayment(savedPayment);
                candidateRepository.save(candidate);
            }
        });

        this.updateMigration(migrateEnum);

        return this.getAll();
    }

    /**
     * @return all migration data
     */
    public List<MigrateData> getAll() {
        return migrateRepository.findAll();
    }

    private void updateMigration(MigrateEnum anEnum) {
        Optional<MigrateData> migrateData = migrateRepository.findByType(anEnum.toString());
        if (!migrateData.isPresent()) {
            MigrateData data1 = new MigrateData();
            data1.setStatus(1);
            data1.setType(anEnum.toString());
            migrateRepository.save(data1);
        }
    }

    /**
     * @param anEnum enum
     * @return boolean
     */
    private boolean canStartMigrate(MigrateEnum anEnum) {
        Optional<MigrateData> migrateData = migrateRepository.findByType(anEnum.toString());
        return !migrateData.isPresent();
    }
}