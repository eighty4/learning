package eighty4.learning.graalvm.js;

import org.graalvm.polyglot.Context;

import java.io.IOException;

import static eighty4.learning.graalvm.js.ScriptEngineUtil.sourceFromString;

public class FunExportingFromModule {

    public static void main(String[] args) throws IOException {

        try (var context = Context.newBuilder("js")
                .allowExperimentalOptions(true)
                .option("js.ecmascript-version", "2022")
                .option("js.esm-eval-returns-exports", "true")
                .build()) {

            var result = context.eval(sourceFromString("""
                    export const str = 'abc'
                    export const num = 42
                    export const map = {va: 'blue', wv: 'red'}
                    class JsType {
                        data
                        constructor(data) {
                            this.data = data
                        }
                    }
                    export const obj = new JsType('data')
                    """));

            assert (result.getMember("str").asString().equals("abc"));
            assert (result.getMember("num").asInt() == 42);
            assert (result.getMember("map").getMember("wv").asString().equals("red"));
            assert (result.getMember("map").getMember("va").asString().equals("blue"));
            assert (result.getMember("obj").getMember("data").asString().equals("data"));
            assert (result.getMember("obj").getMetaObject().getMetaSimpleName().equals("JsType"));

            System.out.printf("str=%s num=%d map=%s obj=%s typeof obj=%s\n",
                    result.getMember("str").asString(),
                    result.getMember("num").asInt(),
                    result.getMember("map").getMember("wv").asString(),
                    result.getMember("obj"),
                    result.getMember("obj").getMetaObject().getMetaSimpleName());
        }
    }
}
