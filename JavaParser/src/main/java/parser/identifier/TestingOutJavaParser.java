package parser.identifier;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

public class TestingOutJavaParser {
    public static void main(String[] args) {
        JavaParser parser = new JavaParser();
        ParseResult<CompilationUnit> result = parser.parse("class A {}");

        // Commented out code is a second example
        // String code = "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        // ParseResult<CompilationUnit> result = parser.parse(ParseStart.COMPILATION_UNIT, new StringProvider(code));

        CompilationUnit cu = result.getResult().get();
        System.out.println(cu);

        // ParseResult<CompilationUnit> result2 = parser.parse("class A {}");
    }
}