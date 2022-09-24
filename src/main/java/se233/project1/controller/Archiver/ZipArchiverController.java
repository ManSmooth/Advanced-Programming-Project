package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public class ZipArchiverController {
    public static void zipWithInfo(FileWrapper target, HashMap<String, Object> info) {
        ZipParameters zp = new ZipParameters();
        zp.setCompressionMethod(CompressionMethod.DEFLATE);
        zp.setCompressionLevel((CompressionLevel) info.get("compression"));
        if ((boolean) info.get("encrypted")) {
            zp.setEncryptFiles(true);
            zp.setEncryptionMethod(EncryptionMethod.AES);
            zp.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        }
        ArchiveController.time();
        try {
            ZipFile zf = new ZipFile(target.getFile(), ((String) info.get("password")).toCharArray());
            ArrayList<FileWrapper> fileList = MainController.getFilesList();
            ArchiveController.setTotalFiles(fileList.size());
            for (FileWrapper fileWrapper : fileList) {
                if (fileWrapper.getFile().isFile())
                    zf.addFile(fileWrapper.getFile(), zp);
                else
                    zf.addFolder(fileWrapper.getFile(), zp);
                ArchiveController.progress(fileWrapper.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArchiveController.returnToHome();
        ArchiveController.time();
    }
}
