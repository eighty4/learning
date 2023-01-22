package eighty4.akimbo.compile.debug;

import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentReference;
import eighty4.akimbo.compile.resolution.ComponentGraph;
import eighty4.akimbo.compile.resolution.ComponentResolution;
import eighty4.akimbo.compile.resolution.ModuleResolver;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsoleWriter implements EventWriter {

    private static final int COMPACTED_PACKAGE_LENGTH = 2;

    private final ProcessorContext processorContext;

    public ConsoleWriter(ProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    @Override
    public void writeSourceAdded(TypeElement typeElement) {
        write("adding " + compactFqn(typeElement.getQualifiedName().toString()));
    }

    @Override
    public void writeSourceFileWritten(SourceFile sourceFile) {
        write(compactFqn(sourceFile.getType().simpleName()) + " was written");
    }

    @Override
    public void writeComponentStateChange(ComponentResolution componentResolution) {
        write(componentResolution + ": " + componentResolution.getState());
    }

    @Override
    public void writeRoundStart() {
        write("start");
    }

    @Override
    public void writeRoundState(ComponentGraph componentGraph, ModuleResolver moduleResolver) {
        write("\nModule and component state after round\n======================================");
        printModuleAndComponentState(componentGraph, moduleResolver);
    }

    @Override
    public void writeErrorState(ComponentGraph componentGraph, ModuleResolver moduleResolver) {
        write("\nModule and component state on error\n===================================");
        printModuleAndComponentState(componentGraph, moduleResolver);
    }

    private void printModuleAndComponentState(ComponentGraph componentGraph, ModuleResolver moduleResolver) {
        List<AkimboComponentReference> printedComponents = new ArrayList<>();
        moduleResolver.getModuleResolutions().forEach(moduleResolution -> {
            System.out.println(moduleResolution + ": " + moduleResolution.getState());
            moduleResolution.getComponentResolutions().forEach(componentResolution -> {
                printedComponents.add(componentResolution.getReference());
                System.out.println(" - " + componentResolution + ": " + componentResolution.getState());
            });
        });
        componentGraph.getComponents().forEach(componentResolution -> {
            if (!printedComponents.contains(componentResolution.getReference())) {
                System.out.println(componentResolution + ": " + componentResolution.getState());
            }
        });
    }

    private void write(String message) {
        String prefix = (processorContext.isAkimboProcessor() ? "AkimboProcessor" : "CassandraProcessor") + ":" + processorContext.getRound() + ": ";
        System.out.println(prefix + message);
    }

    private String compactFqn(String fqn) {
        int lastPeriodIndex = fqn.lastIndexOf(".");
        if (lastPeriodIndex == -1) {
            return fqn;
        } else {
            String[] splitFqn = fqn.substring(0, lastPeriodIndex - 1).split("\\.");
            String packageName = Stream.of(splitFqn).map(sf -> sf.length() < (COMPACTED_PACKAGE_LENGTH + 1) ? sf : sf.substring(0, COMPACTED_PACKAGE_LENGTH)).collect(Collectors.joining("."));
            String typeName = fqn.substring(lastPeriodIndex + 1);
            return packageName + "." + typeName;
        }
    }

}
