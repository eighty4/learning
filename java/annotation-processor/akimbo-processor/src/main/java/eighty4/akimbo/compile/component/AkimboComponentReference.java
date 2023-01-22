package eighty4.akimbo.compile.component;

import com.squareup.javapoet.TypeName;

import java.util.Optional;

public interface AkimboComponentReference {

    String getName();

    TypeName getType();

    Optional<String> getQualifiedName();

}
