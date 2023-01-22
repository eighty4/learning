package eighty4.akimbo.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AkimboRunTaskTest {

    @Test
    public void addsRunTask() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("application"); // todo remove dep
        project.getPlugins().apply("eighty4.akimbo");

        assertNotNull(project.getTasks().findByName("akimboRun"));
    }
}
