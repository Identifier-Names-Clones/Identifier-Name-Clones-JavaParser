package parser.identifier;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassVisitor extends VoidVisitorAdapter<Void> {
    private String filename;

    public ClassVisitor(String filename) {
        this.filename = filename;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        String className = n.getNameAsString();
        Database.insertIdentifier(filename, "CLASS", className, null);
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        String methodName = n.getNameAsString();
        String returnType = n.getType().asString();
        Database.insertIdentifier(filename, "METHOD", methodName, returnType);
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarator n, Void arg) {
        String variableName = n.getNameAsString();
        String datatype = n.getType().asString();
        Database.insertIdentifier(filename, "VARIABLE", variableName, datatype);
        super.visit(n, arg);
    }
}