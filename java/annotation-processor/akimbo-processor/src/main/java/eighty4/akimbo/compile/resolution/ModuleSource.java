package eighty4.akimbo.compile.resolution;

enum ModuleSource {

    /**
     * A module included from an @AkimboApp annotation or a child modules' @Module annotation
     */
    INCLUDED,

    /**
     * A module generated to provide Akimbo service components
     */
    GENERATED,

    /**
     * A module created by an extension
     */
    EXTENSION,

    /**
     * A module created with an AkimboModule declaration
     */
    DECLARED,

    /**
     * A user source module not included in the application modules
     */
    USER_SOURCE;
}
