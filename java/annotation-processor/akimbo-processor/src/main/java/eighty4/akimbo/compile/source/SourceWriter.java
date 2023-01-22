package eighty4.akimbo.compile.source;

import eighty4.akimbo.compile.ProcessorContext;
import eighty4.akimbo.compile.component.AkimboComponentDefinition;
import eighty4.akimbo.compile.resolution.ComponentResolution;
import eighty4.akimbo.compile.resolution.ComponentResolutionException;
import eighty4.akimbo.compile.resolution.ModuleResolution;
import eighty4.akimbo.compile.service.ServiceRegistrar;
import eighty4.akimbo.compile.source.application.AkimboApplicationMainFile;
import eighty4.akimbo.compile.source.application.ApplicationHostComponentFile;
import eighty4.akimbo.compile.source.application.ApplicationHostModuleFile;
import eighty4.akimbo.compile.source.module.ResolvedModuleFile;
import eighty4.akimbo.compile.source.module.ComponentModuleFile;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static eighty4.akimbo.compile.resolution.ResolutionState.RESOLVING;

public class SourceWriter {

    private final ProcessorContext processorContext;

    private final Filer filer;

    private final Set<String> writtenSources = new HashSet<>();

    private boolean wiringWritten = false;

    public SourceWriter(ProcessorContext processorContext, Filer filer) {
        this.processorContext = processorContext;
        this.filer = filer;
    }

    public boolean isWrittenSourceFile(TypeElement typeElement) {
        return writtenSources.contains(typeElement.getQualifiedName().toString());
    }

    public void writeModules(Collection<ModuleResolution> modules) {
        modules.forEach(moduleResolution -> {
            try {
                moduleResolution.getComponentResolutions().stream()
                        .map(ComponentResolution::getDefinition)
                        .forEach(this::writeComponentServiceFiles);
                writeSourceFile(new ResolvedModuleFile(moduleResolution, processorContext));
                moduleResolution.setResolving();
            } catch (Exception e) {
                throw new ComponentResolutionException("Failed to write module " + moduleResolution.getModuleType(), e);
            }
        });
    }

    public void writeComponentModules(Collection<ComponentResolution> components) {
        components.forEach(component -> {
            try {
                writeComponentServiceFiles(component.getDefinition());
                component.setState(RESOLVING);
                writeSourceFile(new ComponentModuleFile(component.getDefinition(), processorContext));
            } catch (Exception e) {
                throw new ComponentResolutionException("Failed to write module for component " + component.getReference(), e);
            }
        });
    }

    private void writeComponentServiceFiles(AkimboComponentDefinition componentDefinition) {
        processorContext.getServiceRegistrars().forEach(serviceRegistrar -> serviceRegistrar.buildComponentSourceFiles(componentDefinition).forEach(this::writeSourceFile));
    }

    public void writeAkimboWiring() {
        if (wiringWritten) {
            return;
        }
        String akimboSourcesPackageName = processorContext.getAkimboSourcesPackageName();
        processorContext.getServiceRegistrars().stream()
                .filter(ServiceRegistrar::hasRegisteredComponents)
                .map(ServiceRegistrar::buildHostSourceFile)
                .forEach(this::writeSourceFile);

        writeSourceFile(new ApplicationHostModuleFile(akimboSourcesPackageName, processorContext.getServiceRegistrars()));
        writeSourceFile(new ApplicationHostComponentFile(akimboSourcesPackageName));
        writeSourceFile(new AkimboApplicationMainFile(akimboSourcesPackageName));
        wiringWritten = true;
    }

    public void writeSourceFile(SourceFile sourceFile) {
        writtenSources.add(sourceFile.getType().reflectionName());
        sourceFile.writeTo(processorContext, filer);
        processorContext.getEventWriter().writeSourceFileWritten(sourceFile);
    }
}
