package eighty4.akimbo.cassandra.compile.dao;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import static eighty4.akimbo.compile.util.AkimboProcessorUtils.lowerCaseFirstChar;

public class CassandraDaoMapper extends SourceFile {

    private final Set<ClassName> daoTypes;

    public CassandraDaoMapper(String packageName, Set<ClassName> daoTypes) {
        super(packageName, "AkimboDaoMapper");
        this.daoTypes = daoTypes;
    }

    @Override
    public TypeSpec toSpec() {
        return TypeSpec.interfaceBuilder("AkimboDaoMapper")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Mapper.class)
                .addMethods(daoTypes.stream().map(this::buildDaoFactoryMethod).collect(Collectors.toList()))
                .build();
    }

    private MethodSpec buildDaoFactoryMethod(ClassName daoType) {
        return MethodSpec.methodBuilder(lowerCaseFirstChar(daoType.simpleName()))
                .addAnnotation(DaoFactory.class)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                .returns(daoType)
                .addParameter(ParameterSpec.builder(CqlIdentifier.class, "keyspace").addAnnotation(DaoKeyspace.class).build())
                .build();
    }
}
