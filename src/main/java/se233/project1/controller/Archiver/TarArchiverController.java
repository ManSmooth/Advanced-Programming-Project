package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.ArchiveMaster;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;
import se233.project1.model.TarArchiveMethodMap;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public class TarArchiverController {

    public static void tarWithInfo(FileWrapper target, HashMap<String, Object> info) {
        String extension = TarArchiveMethodMap.getMap().get((TarCompressionMethod) info.get("compression"));
        Path sourcePath = Paths.get(target.getFile().getAbsolutePath());
        target = new FileWrapper(sourcePath.resolveSibling(target.getName() + extension).toFile());
        ArchiveController.time();
        TarArchiver ta = new TarArchiver();
        ta.setCompression((TarCompressionMethod) info.get("compression"));
        ta.setDestFile(target.getFile());
        ArrayList<FileWrapper> fileList = MainController.getFilesList();
        HashMap<FileWrapper, String> fileMap = ArchiveMaster.flattenFileToHashMap(fileList, "");
        ArchiveController.setTotalFiles(fileMap.size());
        for (FileWrapper fw : fileMap.keySet()) {
            ArchiveController.progress(fw.getName());
            ta.addFile(fw.getFile(), fileMap.get(fw) + fw.getName());
        }
        try {
            ta.createArchive();
            if ((Boolean) info.get("encrypted")) {
                FileWrapper toDelete = new FileWrapper(target.getFile());
                extension = ".pgp";
                sourcePath = Paths.get(target.getFile().getAbsolutePath());
                target = new FileWrapper(sourcePath.resolveSibling(target.getName() + extension).toFile());
                try (FileOutputStream fos = new FileOutputStream(target.getFile());
                        FileInputStream fis = new FileInputStream(toDelete.getFile())) {
                    char[] passphrase = ((String) info.get("password")).toCharArray();
                    byte[] data = fis.readAllBytes();
                    PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                            new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setSecureRandom(new SecureRandom())
                                    .setProvider("BC"));
                    encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passphrase).setProvider("BC"));
                    OutputStream encOut = encGen.open(fos, data.length);
                    encOut.write(data);
                    encOut.close();
                }
                toDelete.getFile().delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PGPException e) {
            e.printStackTrace();
        }
        ArchiveController.time();
        ArchiveController.returnToHome();
    }
}
