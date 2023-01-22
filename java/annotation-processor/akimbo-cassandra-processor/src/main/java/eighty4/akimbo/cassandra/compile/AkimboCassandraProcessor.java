package eighty4.akimbo.cassandra.compile;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.squareup.javapoet.ClassName;
import eighty4.akimbo.cassandra.AkimboCassandraSession;
import eighty4.akimbo.cassandra.compile.dao.CassandraDaoMapper;
import eighty4.akimbo.compile.AkimboExtensionProcessor;

import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.component.AkimboComponent.of;
import static eighty4.akimbo.compile.module.AkimboModule.at;
import static eighty4.akimbo.compile.util.AkimboElementUtils.hasAnnotation;
import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;

public class AkimboCassandraProcessor extends AkimboExtensionProcessor {

    @Override
    protected void registerExtensionModules() {
        addModule(
                at("@@cassandra", "AkimboCassandraSession")
                        .providingComponent(of(AkimboCassandraSession.class))
                        .providingComponent(of("#akimboCassandraSession.cqlSession"))
                        .providingComponent(of("new @@cassandra.AkimboDaoMapperBuilder(#cqlSession).build()").withName("akimboDaoMapper"))
        );
    }

    @Override
    protected void processUserSources(Set<TypeElement> elements) {
        Set<ClassName> daoTypes = elements.stream()
                .filter(e -> hasAnnotation(e, Dao.class))
                .map(ClassName::get)
                .peek(daoType -> {
                    String modulePackageName = "@" + daoType.packageName();
                    String moduleName = daoType.simpleName() + "_Module";
                    String daoResolutionExpression = String.format(
                            "#akimboDaoMapper.%s(@%s.fromCql(${akimbo.cassandra.keyspace}))",
                            lowerCaseFirstChar(daoType.simpleName()),
                            CqlIdentifier.class.getCanonicalName()
                    );
                    addModule(at(modulePackageName, moduleName).providingComponent(of(daoResolutionExpression)));
                })
                .collect(Collectors.toSet());
        writeSourceFile(new CassandraDaoMapper("@cassandra", daoTypes));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getName());
    }

}
