package eighty4.learning.graalvm.js;

public class FunWithScriptEngine {

    public static void main(String[] args) throws Exception {
        FunInvokingJavaMethod.main(args);
        FunImportingDynamicModule.main(args);
        FunExportingFromModule.main(args);
    }
}
