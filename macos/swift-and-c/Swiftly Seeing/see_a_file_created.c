#include "see_a_file_created.h"
#include <unistd.h>
#include <limits.h>

void create_file(char *filename) {
    char cwd[PATH_MAX];
    if (getcwd(cwd, sizeof(cwd)) == NULL) {
        perror("getcwd() error");
        return;
    }

    printf("c code write %s/%s\n", cwd, filename);

    FILE *file = fopen(filename, "wb");
    fwrite("here is some text\n", 18, 1, file);
    fclose(file);
}

void do_nothing(void) {

}
