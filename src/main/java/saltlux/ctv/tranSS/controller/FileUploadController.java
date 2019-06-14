package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saltlux.ctv.tranSS.exception.BadRequestException;
import saltlux.ctv.tranSS.payload.common.FileObject;
import saltlux.ctv.tranSS.service.FileUploadService;

import javax.validation.Valid;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
@RestController
@RequestMapping("/api/file-upload")
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upAttachment/{folder}")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<?> upAttachment(@RequestParam("file") MultipartFile file, @PathVariable String folder) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (isNullOrEmpty(folder)) {
            throw new BadRequestException("Folder is empty");
        }
        String filePath = fileUploadService.saveAttachment(file, folder);
        return new ResponseEntity<Object>(new FileObject(filePath), new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/getAttachment")
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public ResponseEntity<Resource> getAttachment(@Valid @RequestBody FileObject fileObject) throws IOException {
        Resource file = fileUploadService.getAttachment(fileObject.getPath());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
