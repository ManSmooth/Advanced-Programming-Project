package se233.project1.model;

import java.util.HashMap;

import org.codehaus.plexus.archiver.tar.TarArchiver.TarCompressionMethod;

public class TarArchiveMethodMap {
    private static HashMap<TarCompressionMethod, String> map = new HashMap<>();
    private static HashMap<String, TarCompressionMethod> inverseMap = new HashMap<>();
    static {
        map.put(TarCompressionMethod.bzip2, "tbz2");
        map.put(TarCompressionMethod.gzip, "tgz");
        map.put(TarCompressionMethod.xz, "txz");
        map.put(TarCompressionMethod.zstd, "tzst");
        map.put(TarCompressionMethod.none, "tar");
        for (TarCompressionMethod method : map.keySet()) {
            inverseMap.put(map.get(method), method);
        }
    }

    public static HashMap<TarCompressionMethod, String> getMap() {
        return map;
    }

    public static HashMap<String, TarCompressionMethod> getInverseMap() {
        return inverseMap;
    }
}
