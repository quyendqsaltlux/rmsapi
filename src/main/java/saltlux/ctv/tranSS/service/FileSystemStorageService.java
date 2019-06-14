package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import saltlux.ctv.tranSS.exception.StorageException;
import saltlux.ctv.tranSS.exception.StorageFileExistedException;
import saltlux.ctv.tranSS.exception.StorageFileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private Path rootLocation;

    public FileSystemStorageService() {
        log.info("catalina.base" + System.getProperty("catalina.base") + "/tranSSUploadDir");
        this.rootLocation = Paths.get(System.getProperty("catalina.base") + "/tranSSUploadDir");
    }


    @Override
    public Path store(MultipartFile file, Path savePath) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }
            File f = new File(savePath.resolve(filename).toString());
            if (f.exists() && !f.isDirectory()) {
                throw new StorageFileExistedException("File " + filename + " is existed");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, savePath.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        return savePath.resolve(filename);
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = Paths.get(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public Path createPath(String path) throws IOException {
        Path savePath = this.rootLocation.resolve(path);
        if (!Files.exists(savePath)) {
            Files.createDirectories(savePath);
        }
        return savePath;
    }

    @Override
    public boolean deleteFile(String path) {
        File file = new File(path);
        if (file.delete()) {
            log.info("File deleted successfully: " + path);
            return true;
        } else {
            log.error("Failed to delete the file: " + path);
            return false;
        }
    }

}
