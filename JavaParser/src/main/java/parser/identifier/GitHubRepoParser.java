package parser.identifier;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.*;
import java.util.Scanner;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;


public class GitHubRepoParser {
    private static final String LOG_FILE_PATH = "./error_log.log";

    // Delete repo if it's already cloned
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Database.initializeDatabase();

        // Edit to take in multiple
        //String repoUrl = "https://github.com/Identifier-Names-Clones/Identifier-Name-Clones-JavaParser.git";
        //String localDir = "./src/main/repo_clones/javaparser/";

        try {
            File myObj = new File("./src/main/java/parser/identifier/project_repos.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String nextName = myReader.nextLine();
                String repoUrl = "https://github.com/" + nextName;
                String directory = "./src/main/repo_clones/" + nextName;

                // delete repo if cloned already, and re-clone
                if (Files.exists(Path.of(directory))) {
                    deleteFolder(new File(directory));
                }

                if (cloneRepository(repoUrl, directory)) {
                    File projectDir = new File(directory);

                    // add to projects database!! doesn't need its own classvisitor
                    int projectID = Database.insertProject(directory);
                    List<File> javaFiles = getAllJavaFiles(projectDir);

                    System.out.println("Extracting class names from " + javaFiles.size() + " files...");
                    parseJavaFiles(javaFiles, projectID, directory);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // Clone the repo at repoURL into localDir
    // Clones only the latest commit and then filters it to only be the java files
    public static boolean cloneRepository(String repoUrl, String localDir) {
        try {
            // Perform a shallow clone (depth=1 to get only the latest commit)
            ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", "--depth", "1", "--no-checkout", repoUrl, localDir);
            cloneBuilder.inheritIO();
            Process cloneProcess = cloneBuilder.start();
            int cloneExitCode = cloneProcess.waitFor();

            if (cloneExitCode != 0) {
                return false; // Clone failed
            }

            // Sparse checkout in the cloned repository (disable cone mode)
            ProcessBuilder sparseCheckoutBuilder = new ProcessBuilder("git", "-C", localDir, "sparse-checkout", "init", "--no-cone");
            sparseCheckoutBuilder.inheritIO();
            Process sparseCheckoutProcess = sparseCheckoutBuilder.start();
            int sparseCheckoutExitCode = sparseCheckoutProcess.waitFor();

            if (sparseCheckoutExitCode != 0) {
                return false; // Sparse checkout init failed
            }

            // Only include Java files
            ProcessBuilder setFilterBuilder = new ProcessBuilder("git", "-C", localDir, "sparse-checkout", "set", "*.java");
            setFilterBuilder.inheritIO();
            Process setFilterProcess = setFilterBuilder.start();
            int setFilterExitCode = setFilterProcess.waitFor();

            if (setFilterExitCode != 0) {
                return false; // Setting filter failed
            }

            // Checkout the files that match the filter
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
    public static void parseJavaFiles(List<File> javaFiles, int projectID, String basePath) throws FileNotFoundException {
        JavaParser parser = new JavaParser();
        for (File file : javaFiles) {
            try {
                ParseResult<CompilationUnit> result = parser.parse(file);

                String relativePath = new File(basePath).toURI().relativize(file.toURI()).getPath();

                CompilationUnit cu = result.getResult().get();

                ClassVisitor visitor = new ClassVisitor(relativePath, projectID);
                cu.accept(visitor, null);
            }
            catch (Exception e) {
                logExceptionToFile(e, file.getName());

            }

        }
    }

    private static void logExceptionToFile(Exception e, String fileName) {
        System.out.println("Exception occurred: " + e.getMessage());
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("Exception occurred while processing file: " + fileName);
            e.printStackTrace(pw); // Write the stack trace to the file
            pw.println(); // Add a blank line for separation
        } catch (IOException ioException) {
            ioException.printStackTrace(); // Handle the case where logging fails
        }
    }
}
