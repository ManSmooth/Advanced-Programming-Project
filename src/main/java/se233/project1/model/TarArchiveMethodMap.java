package se233.project1.model;

import java.util.HashMap;

import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;

public class TarArchiveMethodMap {
    private static HashMap<TarCompressionMethod, String> map = new HashMap<>();
    static {
        map.put(TarCompressionMethod.bzip2, ".bz2");
        map.put(TarCompressionMethod.gzip, ".gz");
        map.put(TarCompressionMethod.xz, ".xz");
        map.put(TarCompressionMethod.zstd, ".zst");
        map.put(TarCompressionMethod.none, "");
    }

    public static HashMap<TarCompressionMethod, String> getMap() {
        return map;
    }
}
