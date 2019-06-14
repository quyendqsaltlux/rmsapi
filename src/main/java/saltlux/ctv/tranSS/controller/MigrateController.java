package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saltlux.ctv.tranSS.enums.ProjectProgressEnum;
import saltlux.ctv.tranSS.model.MigrateData;
import saltlux.ctv.tranSS.service.MigrateService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/migrate")
public class MigrateController {

    private final MigrateService migrateService;


    @Autowired
    public MigrateController(MigrateService migrateService) {
        this.migrateService = migrateService;
    }


    @GetMapping("/loadCandidateFromRDB")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadCandidateFromFile() throws Exception {
        return migrateService.loadCandidateFromFile();
    }

    @GetMapping("/loadProjectFromFile")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadFinishedProjectFromFile(
            @RequestParam(value = "type") String type) throws Exception {
        if (null == type || (!ProjectProgressEnum.FINISHED.toString().equals(type) &&
                !ProjectProgressEnum.ON_GOING.toString().equals(type))) {
            throw new MissingServletRequestParameterException("type", "need to be FINISHED or ON_GOING");
        }
        return migrateService.loadProjectFromFile(type);
    }

    @GetMapping("/loadUsersFromFile")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadUsersFromFile() throws Exception {
        return migrateService.loadUsersFromFile();
    }

    @GetMapping("/loadKoreaPaymentFromFile")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadKoreaPaymentFromFile() throws Exception {
        return migrateService.loadKoreaPaymentFromFile();
    }

    @GetMapping("/loadOverseaPaymentFromFile")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadOverseaPaymentFromFile() throws Exception {
        return migrateService.loadOverseaPaymentFromFile();
    }

    @GetMapping("/loadAssignmentFromFile")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadAssignmentFromFile() throws Exception {
        return migrateService.loadAssignmentFromFile();
    }

    @GetMapping("/loadTestWaitingFromFile")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> loadTestWaitingFromFile() throws Exception {
        return migrateService.loadTestWaitingFromFile();
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MigrateData> getAll() throws Exception {
        return migrateService.getAll();
    }
}
