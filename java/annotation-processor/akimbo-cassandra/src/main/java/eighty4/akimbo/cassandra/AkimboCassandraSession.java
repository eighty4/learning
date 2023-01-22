package eighty4.akimbo.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.internal.core.config.typesafe.DefaultDriverConfigLoader;
import com.google.common.flogger.FluentLogger;
import eighty4.akimbo.annotation.Value;

import javax.inject.Inject;
import java.net.InetSocketAddress;

public class AkimboCassandraSession {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final CqlIdentifier keyspaceIdentifier;

    private final CqlSession cqlSession;

    @Inject
    public AkimboCassandraSession(@Value("akimbo.cassandra.host:localhost") String host, @Value("akimbo.cassandra.keyspace") String keyspace) {
        logger.atInfo().log("Creating cassandra session for host %s and keyspace %s", host, keyspace);
        keyspaceIdentifier = CqlIdentifier.fromCql(keyspace);
        cqlSession = CqlSession.builder()
                .addContactPoint(InetSocketAddress.createUnresolved(host, 9042))
                .withLocalDatacenter("datacenter1")
                .withConfigLoader(new DefaultDriverConfigLoader()).build();
    }

    public CqlIdentifier getKeyspaceIdentifier() {
        return keyspaceIdentifier;
    }

    public CqlSession getCqlSession() {
        return cqlSession;
    }

}
