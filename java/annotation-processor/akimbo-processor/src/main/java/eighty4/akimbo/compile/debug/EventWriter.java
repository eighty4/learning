package eighty4.akimbo.compile.debug;

import eighty4.akimbo.compile.resolution.ComponentGraph;
import eighty4.akimbo.compile.resolution.ComponentResolution;
import eighty4.akimbo.compile.resolution.ModuleResolver;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.TypeElement;

public interface EventWriter {

    void writeSourceAdded(TypeElement typeElement);

    void writeSourceFileWritten(SourceFile sourceFile);

    void writeComponentStateChange(ComponentResolution componentResolution);

    void writeRoundStart();

    void writeRoundState(ComponentGraph componentGraph, ModuleResolver moduleResolver);

    void writeErrorState(ComponentGraph componentGraph, ModuleResolver moduleResolver);

}
