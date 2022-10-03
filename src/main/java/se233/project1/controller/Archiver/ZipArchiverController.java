package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public class ZipArchiverController {
    public static void zipWithInfo(FileWrapper target, HashMap<String, Object> info) throws IOException {
        ZipParameters zp = new ZipParameters();
        zp.setCompressionMethod(CompressionMethod.DEFLATE);
        zp.setCompressionLevel((CompressionLevel) info.get("compression"));
        if ((boolean) info.get("encrypted")) {
            zp.setEncryptFiles(true);
            zp.setEncryptionMethod(EncryptionMethod.AES);
            zp.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        }
        ZipFile zf = new ZipFile(target.getFile(), ((String) info.get("password")).toCharArray());
        ArrayList<FileWrapper> fileList = MainController.getFilesList();
        ArchiveController.setTotalFiles(fileList.size());
        for (FileWrapper fileWrapper : fileList) {
            ArchiveController.setProgress(fileWrapper.getName());
            if (fileWrapper.getFile().isFile())
                zf.addFile(fileWrapper.getFile(), zp);
            else
                zf.addFolder(fileWrapper.getFile(), zp);
            ArchiveController.progress();
        }
    }

    public static void unzip(FileWrapper source, FileWrapper target) throws ZipException {
        ZipFile zipFile = new ZipFile(source.getFile());
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(MainController.showPasswordPane(source).toCharArray());
        }
        zipFile.extractAll(target.getPath());
    }
}
