package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import saltlux.ctv.tranSS.enums.AttachmentFolderEnum;
import saltlux.ctv.tranSS.exception.BadRequestException;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
public class FileUploadService {

    private final StorageService storageService;

    @Autowired
    public FileUploadService(StorageService storageService) {
        this.storageService = storageService;
    }


    public String saveAttachment(MultipartFile file, String folder) throws IOException {
        if (!EnumUtils.isValidEnum(AttachmentFolderEnum.class, folder)) {
            throw new BadRequestException("Folder " + folder + " is not allow");
        }
        Path savePath = storageService.createPath(folder);
        return storageService.store(file, savePath).toString();
    }

    public Resource getAttachment(String filePath) {
        return storageService.loadAsResource(filePath);
    }

}