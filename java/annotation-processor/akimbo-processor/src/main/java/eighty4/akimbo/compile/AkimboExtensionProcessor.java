package eighty4.akimbo.compile;

import eighty4.akimbo.compile.module.AkimboModule;
import eighty4.akimbo.compile.source.SourceFile;

import javax.lang.model.element.TypeElement;
import java.util.Set;

public abstract class AkimboExtensionProcessor extends BaseAkimboProcessor {

    private boolean activeFirstRound = false;

    @Override
    public final boolean processSources(Set<TypeElement> elements) {
        boolean firstRound = processorContext.getRound() == 0;
        if (firstRound) {
            registerExtensionModules();
            processUserSources(elements);
            return activeFirstRound;
        } else {
            processGeneratedSources(elements);
            return true;
        }
    }


    protected void registerExtensionModules() {

    }

    protected void processUserSources(Set<TypeElement> elements) {

    }

    protected void processGeneratedSources(Set<TypeElement> elements) {

    }

    @Override
    protected void writeSourceFile(SourceFile sourceFile) {
        super.writeSourceFile(sourceFile);
        activeFirstRound = true;
    }

    @Override
    protected void addModule(AkimboModule module) {
        super.addModule(module);
        activeFirstRound = true;
    }

}
