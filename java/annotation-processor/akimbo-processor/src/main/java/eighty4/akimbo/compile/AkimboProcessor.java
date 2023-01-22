package eighty4.akimbo.compile;

import javax.lang.model.element.TypeElement;
import java.util.Set;

public class AkimboProcessor extends BaseAkimboProcessor {

    @Override
    protected final boolean processSources(Set<TypeElement> userSources) {
        return true;
    }

}
