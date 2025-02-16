package parser.identifier;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitHubRepoParser {
    public static void main(String[] args) throws FileNotFoundException {
        String repoUrl = "https://github.com/Identifier-Names-Clones/Identifier-Name-Clones-JavaParser.git";
        String localDir = "./src/main/repo_clones/javaparser/";

        if (cloneRepository(repoUrl, localDir)) {
            File projectDir = new File(localDir);
            List<File> javaFiles = getAllJavaFiles(projectDir);

            System.out.println("Extracting class names from " + javaFiles.size() + " files...");
            parseJavaFiles(javaFiles);
        }
    }

    // Clone the repo at repoURL into localDir
    // Clones only the latest commit and then filters it to only be the java files
    public static boolean cloneRepository(String repoUrl, String localDir) {
        try {
            // Step 1: Perform a shallow clone (depth=1 to get only the latest commit)
            ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", "--depth", "1", "--no-checkout", repoUrl, localDir);
            cloneBuilder.inheritIO();
            Process cloneProcess = cloneBuilder.start();
            int cloneExitCode = cloneProcess.waitFor();

            if (cloneExitCode != 0) {
                return false; // Clone failed
            }

            // Step 2: Enable sparse checkout in the cloned repository (disable cone mode)
            ProcessBuilder sparseCheckoutBuilder = new ProcessBuilder("git", "-C", localDir, "sparse-checkout", "init", "--no-cone");
            sparseCheckoutBuilder.inheritIO();
            Process sparseCheckoutProcess = sparseCheckoutBuilder.start();
            int sparseCheckoutExitCode = sparseCheckoutProcess.waitFor();

            if (sparseCheckoutExitCode != 0) {
                return false; // Sparse checkout init failed
            }

            // Step 3: Set the sparse checkout filter to only include Java files
            ProcessBuilder setFilterBuilder = new ProcessBuilder("git", "-C", localDir, "sparse-checkout", "set", "*.java");
            setFilterBuilder.inheritIO();
            Process setFilterProcess = setFilterBuilder.start();
            int setFilterExitCode = setFilterProcess.waitFor();

            if (setFilterExitCode != 0) {
                return false; // Setting filter failed
            }

            // Step 4: Checkout the files that match the filter
            ProcessBuilder checkoutBuilder = new ProcessBuilder("git", "-C", localDir, "checkout");
            checkoutBuilder.inheritIO();
            Process checkoutProcess = checkoutBuilder.start();
            int checkoutExitCode = checkoutProcess.waitFor();

            return checkoutExitCode == 0; // Return true if checkout succeeded
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Recursively get all of the java files in a directory
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

    // Parse the Java files and extract the class, method names, and identifiers
    public static void parseJavaFiles(List<File> javaFiles) throws FileNotFoundException {
        JavaParser parser = new JavaParser();
        for (File file : javaFiles) {
            ParseResult<CompilationUnit> result = parser.parse(file);
            if (result.isSuccessful() && result.getResult().isPresent()) {
                CompilationUnit cu = result.getResult().get();

                ClassVisitor visitor = new ClassVisitor();
                cu.accept(visitor, null);
            } else {
                System.err.println("Failed to parse the Java file.");
            }
        }
    }
}
