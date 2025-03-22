package parser.identifier;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassVisitor extends VoidVisitorAdapter<Void> {
    private String fileName;
    private int projectID;
    private String currentClassName = null;
    private String currentMethodName = null;

    public ClassVisitor(String filename, int projectID) {
        this.fileName = filename;
        this.projectID = projectID;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        currentClassName = n.getNameAsString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "CLASS", currentClassName, null, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
        currentClassName = null;
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        currentMethodName = n.getNameAsString();
        String returnType = n.getType().asString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "METHOD", currentMethodName, returnType, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
        currentMethodName = null;
    }

    @Override
    public void visit(VariableDeclarator n, Void arg) {
        String variableName = n.getNameAsString();
        String datatype = n.getType().asString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "VARIABLE", variableName, datatype, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
    }

    @Override
    public void visit(RecordDeclaration n, Void arg) {
        String variableName = n.getNameAsString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "RECORD", variableName, null, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
        // relative filepath column
    }

    @Override
    public void visit(FieldDeclaration n, Void arg) {
        n.getVariables().forEach(var -> {
            String variableName = var.getNameAsString();
            String datatype = var.getType().asString();
            int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
            Database.insertIdentifier(projectID, fileName, "VARIABLE", variableName, datatype, currentClassName, currentMethodName, lineNumber);
        });

        super.visit(n, arg);
    }

    @Override
    public void visit(Parameter n, Void arg) {
        String variableName = n.getNameAsString();
        String datatype = n.getType().asString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "PARAMETER", variableName, datatype, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
    }

    @Override
    public void visit(EnumDeclaration n, Void arg) {
        String variableName = n.getNameAsString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "ENUM", variableName, null, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
    }

    @Override
    public void visit(LocalRecordDeclarationStmt n, Void arg) {
        String variableName = n.getRecordDeclaration().getNameAsString();
        int lineNumber = n.getBegin().isPresent() ? n.getBegin().get().line : -1;
        Database.insertIdentifier(projectID, fileName, "LOCAL RECORD", variableName, null, currentClassName, currentMethodName, lineNumber);
        super.visit(n, arg);
    }


}