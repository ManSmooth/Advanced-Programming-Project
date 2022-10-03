package se233.project1.controller.Archiver;

import se233.project1.controller.ArchiveController;
import se233.project1.controller.ArchiveMaster;
import se233.project1.controller.MainController;
import se233.project1.model.FileWrapper;
import org.codehaus.plexus.archiver.tar.TarArchiver;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public class TarArchiverController {

    public static void tarWithInfo(FileWrapper target, HashMap<String, Object> info) {
        ArchiveController.time();
        TarArchiver ta = new TarArchiver();
        ta.setCompression((TarCompressionMethod) info.get("compression"));
        ta.setDestFile(target.getFile());
        ArrayList<FileWrapper> fileList = MainController.getFilesList();
        HashMap<FileWrapper, String> fileMap = ArchiveMaster.flattenFileToHashMap(fileList, "");
        ArchiveController.setTotalFiles(fileMap.size());
        for (FileWrapper fw : fileMap.keySet()) {
            ArchiveController.setProgress(fw.getName());
            ta.addFile(fw.getFile(), fileMap.get(fw) + fw.getName());
            ArchiveController.progress();
        }
        try {
            ta.createArchive();
            if ((Boolean) info.get("encrypted")) {
                FileWrapper toDelete = new FileWrapper(target.getFile());
                Path sourcePath = Paths.get(target.getPath());
                target = new FileWrapper(sourcePath.resolveSibling(target.getName() + ".tmp").toFile());
                try (FileOutputStream fos = new FileOutputStream(target.getFile());
                        FileInputStream fis = new FileInputStream(toDelete.getFile())) {
                    char[] passphrase = ((String) info.get("password")).toCharArray();
                    byte[] data = fis.readAllBytes();
                    PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                            new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setSecureRandom(new SecureRandom())
                                    .setWithIntegrityPacket(true).setProvider("BC"));
                    encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passphrase).setProvider("BC"));
                    OutputStream encOut = encGen.open(fos, data.length);
                    encOut.write(data);
                    encOut.close();
                }
                toDelete.getFile().delete();
                Files.move(Paths.get(target.getPath()), Paths.get(toDelete.getPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PGPException e) {
            e.printStackTrace();
        }
        ArchiveController.time();
    }

    public static void untar(FileWrapper source, FileWrapper target) {
        System.out.println(source.getExtension());
        InputStream in = null;
        String name = source.getName();
        if (source.getExtension().equals("gpg")) {
            in = decryptPGP(source, MainController.showPasswordPane(source).toCharArray());
            name = FilenameUtils.removeExtension(name);
        } else if (in == null) {
            try {
                in = new FileInputStream(source.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        in = new BufferedInputStream(in);
        try {
            switch (FilenameUtils.getExtension(name)) {
                case "tgz":
                    in = new GzipCompressorInputStream(in);
                    break;
                case "tbz2":
                    in = new BZip2CompressorInputStream(in);
                    break;
                case "txz":
                    in = new XZCompressorInputStream(in);
                    break;
                case "tzst":
                    in = new ZstdCompressorInputStream(in);
                    break;
                case "tar":
                    break;
                default:
                    System.out.println("Unsupported Format:" + FilenameUtils.getExtension(name));
                    return;
            }
            TarArchiveInputStream tais = new TarArchiveInputStream(in);
            TarArchiveEntry entry;
            FileOutputStream fos = null;
            while ((entry = tais.getNextTarEntry()) != null) {
                File file = new File(target.getFile(), entry.getName());
                ArchiveController.setProgress(entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    try {
                        File parent = new File(file.getParent());
                        if(!parent.exists()) parent.mkdirs();
                        file.createNewFile();
                        fos = new FileOutputStream(file);
                        fos.write(tais.readNBytes(tais.available()));
                        IOUtils.closeQuietly(fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            tais.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(in);
    }

    private static InputStream decryptPGP(FileWrapper source, char[] passphrase) {
        InputStream fis = null;
        try {
            fis = PGPUtil.getDecoderStream(new BufferedInputStream(new FileInputStream(source.getFile())));
            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(fis);
            PGPEncryptedDataList enc;
            Object o = pgpF.nextObject();
            if (o instanceof PGPEncryptedDataList) {
                enc = (PGPEncryptedDataList) o;
            } else {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }
            PGPPBEEncryptedData pbe = (PGPPBEEncryptedData) enc.get(0);
            InputStream clear = pbe.getDataStream(new JcePBEDataDecryptorFactoryBuilder(
                    new JcaPGPDigestCalculatorProviderBuilder().setProvider("BC").build()).setProvider("BC")
                    .build(passphrase));
            return clear;
        } catch (IOException | PGPException e) {
            e.printStackTrace();
            IOUtils.closeQuietly(fis);
            Thread.currentThread().interrupt();
        }
        return fis;
    }
}
