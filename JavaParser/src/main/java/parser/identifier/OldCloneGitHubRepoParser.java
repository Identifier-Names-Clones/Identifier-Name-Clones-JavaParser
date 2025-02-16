package parser.identifier;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class OldCloneGitHubRepoParser {
    public static void main(String[] args) {
        String repoUrl = "https://github.com/Identifier-Names-Clones/Identifier-Name-Clones-Refactoring.git"; // Update this URL
        String localDir = "./src/main/repo_clones/refactoring_miner/"; // Update the path

        if (cloneRepository(repoUrl, localDir)) {
            File projectDir = new File(localDir);
            List<File> javaFiles = getAllJavaFiles(projectDir);

            System.out.println("Extracting class names from " + javaFiles.size() + " files...");
            parseJavaFiles(javaFiles);
        } else {
            System.out.println("Failed to clone the repository.");
        }
    }

    public static boolean cloneRepository(String repoUrl, String localDir) {
        try {
            ProcessBuilder builder = new ProcessBuilder("git", "clone", repoUrl, localDir);
            builder.inheritIO();
            Process process = builder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static List<File> getAllJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(getAllJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

    private static void parseJavaFiles(List<File> javaFiles) {
        JavaParser parser = new JavaParser();

        for (File file : javaFiles) {
            try {
                ParseResult<CompilationUnit> result = parser.parse(file);
                result.getResult().ifPresent(cu -> {
                    System.out.println("Parsing file: " + file.getPath());

                    // Extract class names
                    cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls ->
                            System.out.println("Class: " + cls.getName())
                    );

                    // Extract method names
                    cu.findAll(MethodDeclaration.class).forEach(method ->
                            System.out.println("Method: " + method.getName())
                    );

                    // Extract all identifier names
                    cu.findAll(SimpleName.class).forEach(identifier ->
                            System.out.println("Identifier: " + identifier.getIdentifier())
                    );
                });
            } catch (Exception e) {
                System.err.println("Error parsing " + file.getPath());
            }
        }
    }
}
