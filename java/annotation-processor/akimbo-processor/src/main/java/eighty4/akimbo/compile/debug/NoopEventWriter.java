package eighty4.akimbo.compile.debug;

import eighty4.akimbo.compile.resolution.ComponentGraph;
import eighty4.akimbo.compile.resolution.ComponentResolution;
import eighty4.akimbo.compile.resolution.ModuleResolver;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.TypeElement;

public class NoopEventWriter implements EventWriter {

    @Override
    public void writeSourceAdded(TypeElement typeElement) {

    }

    @Override
    public void writeSourceFileWritten(SourceFile sourceFile) {

    }

    @Override
    public void writeComponentStateChange(ComponentResolution componentResolution) {

    }

    @Override
    public void writeRoundStart() {

    }

    @Override
    public void writeRoundState(ComponentGraph componentGraph, ModuleResolver moduleResolver) {

    }

    @Override
    public void writeErrorState(ComponentGraph componentGraph, ModuleResolver moduleResolver) {

    }

}
