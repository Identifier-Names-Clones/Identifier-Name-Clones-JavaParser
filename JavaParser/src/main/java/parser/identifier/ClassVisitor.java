package parser.identifier;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassVisitor extends VoidVisitorAdapter<Void> {
    private String fileName;
    private int projectID;

    public ClassVisitor(String filename, int projectID) {
        this.fileName = filename;
        this.projectID = projectID;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        String className = n.getNameAsString();
        Database.insertIdentifier(projectID, fileName, "CLASS", className, null);
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        String methodName = n.getNameAsString();
        String returnType = n.getType().asString();
        Database.insertIdentifier(projectID, fileName, "METHOD", methodName, returnType);
        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarator n, Void arg) {
        String variableName = n.getNameAsString();
        String datatype = n.getType().asString();
        Database.insertIdentifier(projectID, fileName, "VARIABLE", variableName, datatype);
        super.visit(n, arg);
    }

    @Override
    public void visit(RecordDeclaration n, Void arg) {
        String variableName = n.getNameAsString();
        Database.insertIdentifier(projectID, fileName, "RECORD", variableName, null);
        super.visit(n, arg);
        // relative filepath column
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        n.getVariables().forEach(var -> {
            String variableName = var.getNameAsString();
            String datatype = var.getType().asString();
            Database.insertIdentifier(projectID, fileName, "VARIABLE", variableName, datatype);
        });

        super.visit(n, arg);
    }

    @Override
    public void visit(Parameter n, Void arg) {
        String variableName = n.getNameAsString();
        String datatype = n.getType().asString();
        Database.insertIdentifier(projectID, fileName, "PARAMETER", variableName, datatype);
        super.visit(n, arg);
    }

    @Override
    public void visit(EnumDeclaration n, Void arg) {
        String variableName = n.getNameAsString();
        Database.insertIdentifier(projectID, fileName, "ENUM", variableName, null);
        super.visit(n, arg);
    }

    @Override
    public void visit(LocalRecordDeclarationStmt n, Void arg) {
        String variableName = n.getRecordDeclaration().getNameAsString();
        Database.insertIdentifier(projectID, fileName, "LOCAL RECORD", variableName, null);
        super.visit(n, arg);
    }


}