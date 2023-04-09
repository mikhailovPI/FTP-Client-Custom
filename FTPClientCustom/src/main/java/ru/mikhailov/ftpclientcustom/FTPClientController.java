package ru.mikhailov.ftpclientcustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FTPClientController {

    private static final String FTP_SERVER_URL = "185.27.134.11";
    private static final String FTP_USERNAME = "epiz_33891104";
    private static final String FTP_PASSWORD = "CLc195rPV8h3cv";
    private static final String PATH_TO_PHOTOS = "фотографии";

    @GetMapping("/photos")
    public List<Photo> searchPhotos(
            @RequestParam String prefix,
            @RequestParam List<String> folders) {

        FTPClient ftpClient = new FTPClient();
        List<Photo> photos = new ArrayList<>();

        try {
            ftpClient.connect(FTP_SERVER_URL, 21);
            ftpClient.enterLocalPassiveMode();
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            for (String folder : folders) {
                FTPFile[] files = ftpClient.listFiles(PATH_TO_PHOTOS + "/" + folder);
                for (FTPFile file : files) {
                    if (file.getName().startsWith(prefix)) {
                        Date createdDate = file.getTimestamp().getTime();
                        long fileSize = file.getSize();
                        String fullPath = PATH_TO_PHOTOS + "/" + folder + "/" + file.getName();
                        Photo photo = new Photo(fullPath, createdDate, fileSize);
                        photos.add(photo);
                    }
                }
            }

            ftpClient.logout();
            ftpClient.disconnect();

        } catch (IOException ex) {
            throw new RuntimeException("Что-то пошло не так. Повторите запрос.");
        }
        return photos;
    }
}