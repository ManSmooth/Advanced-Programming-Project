package se233.project1.model;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se233.project1.controller.ArchiveController.ArchiveType;
import net.lingala.zip4j.model.enums.CompressionLevel;

public class ArchiveTypeWrapper {
    private ArchiveType type;
    private String iconPath;
    private String description;
    private String extension;
    private ObservableList<Object> options;
    private Boolean canEncrypt;

    public ArchiveTypeWrapper(ArchiveType type) {
        this.options = FXCollections.emptyObservableList();
        switch (type) {
            case ZIP:
                this.type = ArchiveType.ZIP;
                this.iconPath = "assets/icons/zip.png";
                this.description = "Zip Archive";
                this.extension = "zip";
                this.canEncrypt = true;
                this.options = FXCollections.observableArrayList(Arrays.asList(CompressionLevel.values()));
                break;
            case SEVENZIP:
                this.type = ArchiveType.SEVENZIP;
                this.iconPath = "assets/icons/7z.png";
                this.description = "7-Zip Archive";
                this.extension = "7z";
                this.canEncrypt = false;
                ArrayList<Object> oplist = new ArrayList<>();
                oplist.add(SevenZMethod.COPY);
                oplist.add(SevenZMethod.DEFLATE);
                oplist.add(SevenZMethod.LZMA2);
                oplist.add(SevenZMethod.BZIP2);
                this.options = FXCollections.observableArrayList(oplist);
                break;
            case RAR:
                this.type = ArchiveType.RAR;
                this.iconPath = "assets/icons/rar.png";
                this.description = "RAR Archive";
                this.extension = "rar";
                this.canEncrypt = true;
                ArrayList<Object> rarlist = new ArrayList<>();
                rarlist.add("0 - Store");
                rarlist.add("1 - Fastest");
                rarlist.add("2 - Fast");
                rarlist.add("3 - Normal (Default)");
                rarlist.add("4 - Good");
                rarlist.add("5 - Best");
                this.options = FXCollections.observableArrayList(rarlist);
                break;
            case TAR:
                this.type = ArchiveType.TAR;
                this.iconPath = "assets/icons/tar.png";
                this.description = "Tar Archive";
                this.extension = "tar";
                this.canEncrypt = true;
                this.options = FXCollections.observableArrayList(Arrays.asList(TarCompressionMethod.values()).stream()
                        .filter(
                                (method) -> method != TarCompressionMethod.snappy)
                        .collect(java.util.stream.Collectors.toList()));
                break;
        }
    }

    public static ArchiveTypeWrapper getWrapper(ArchiveType type) {
        switch (type) {
            case ZIP:
                return new ArchiveTypeWrapper(ArchiveType.ZIP);
            case SEVENZIP:
                return new ArchiveTypeWrapper(ArchiveType.SEVENZIP);
            case RAR:
                return new ArchiveTypeWrapper(ArchiveType.RAR);
            case TAR:
                return new ArchiveTypeWrapper(ArchiveType.TAR);
        }
        return null;
    }

    public static ArrayList<String> getAllExtensions() {
        ArrayList<String> exts = new ArrayList<>(Arrays.asList(ArchiveType.values()).stream().map((type) -> getWrapper(type).getExtension())
                .collect(java.util.stream.Collectors.toList()));
        exts.addAll(TarArchiveMethodMap.getMap().values());
        exts.add("gpg");
        return exts;
    }
    public ArchiveType getType() {
        return type;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }

    public Boolean canEncrypt() {
        return canEncrypt;
    }

    public ObservableList<Object> getOptions() {
        return options;
    }
}
