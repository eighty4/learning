package eighty4.akimbo.cassandra;

import com.datastax.oss.driver.api.mapper.entity.naming.NameConverter;

public class AkimboCqlNameConverter implements NameConverter {

    private static final String[] SUFFIX_DROPS = new String[]{"Record", "Table", "Entity"};

    @Override
    public String toCassandraName(String javaName) {
        for (String suffixDrop : SUFFIX_DROPS) {
            if (javaName.endsWith(suffixDrop)) {
                javaName = javaName.substring(0, javaName.length() - suffixDrop.length());
                return toCqlName(javaName);
            }
        }
        return toCqlName(javaName);
    }

    private String toCqlName(String javaName) {
        String[] javaNameSplit = javaName.split("(?=\\p{Upper})");
        return String.join("_", javaNameSplit).toLowerCase();
    }
}
