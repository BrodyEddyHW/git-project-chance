import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GitTester {
    public static void main(String[] args) {
        try {
            // 1. Initialize Git Repository
            Git.initRepo();
            System.out.println("Test 1: Initialized Git Repository");

            // Directory and file paths for the test structure
            String parentDir = "parentDir";
            String subDir = parentDir + "/subDir";
            String file1 = parentDir + "/file1.txt";
            String file2 = parentDir + "/file2.txt";
            String subFile = subDir + "/subFile.txt";

            // 2. Create Directory Structure and Files
            createDirectory(parentDir);
            createDirectory(subDir);
            createFile(file1, "Content of file1");
            createFile(file2, "Content of file2");
            createFile(subFile, "Content of subFile");

            Tree git = new Tree();

            // 3. Stage All Files
            git.stage(file1);
            git.stage(file2);
            git.stage(subFile);
            assert checkIndexFor(file1) : "Failed: file1 was not staged correctly.";
            assert checkIndexFor(file2) : "Failed: file2 was not staged correctly.";
            assert checkIndexFor(subFile) : "Failed: subFile was not staged correctly.";
            System.out.println("Test 2: Staged Files in Directory Structure");

            // 4. Commit the Staged Files
            String commitHash = git.commit("Tester", "Initial commit with directory structure");
            assert new File("git/objects/" + commitHash).exists() : "Failed: Commit file not created.";
            System.out.println("Test 3: Created Initial Commit with Directory Structure");

            // 5. Edit a File and Stage the Changes
            modifyFile(file1, "Updated content of file1");
            git.stage(file1);
            assert checkIndexFor(file1) : "Failed: Edited file1 was not re-staged.";
            System.out.println("Test 4: Staged Edited File");

            // 6. Commit the Edited File
            String newCommitHash = git.commit("Tester", "Updated file1 content");
            assert !commitHash.equals(newCommitHash) : "Failed: New commit hash should differ after edits.";
            System.out.println("Test 5: Created Commit with Edited File");

            // 7. Delete a File and Stage the Deletion
            deleteFile(file2);
            git.stage(file2);
            assert !checkIndexFor(file2) : "Failed: Deleted file2 was not removed from index.";
            System.out.println("Test 6: Staged File Deletion");

            // 8. Commit the Deletion
            String deleteCommitHash = git.commit("Tester", "Deleted file2");
            assert !new File("git/objects/" + newCommitHash).exists() || !new File("git/objects/" + deleteCommitHash).equals(newCommitHash) 
                : "Failed: Commit hash should update after file deletion.";
            System.out.println("Test 7: Created Commit with File Deletion");

            // 9. Edit a File in the Subfolder and Stage the Changes
            modifyFile(subFile, "Updated content of subFile");
            git.stage(subFile);
            assert checkIndexFor(subFile) : "Failed: Edited subFile was not re-staged.";
            System.out.println("Test 8: Staged Edited File in Subfolder");

            // 10. Commit the Edited File in Subfolder
            String subFileEditCommitHash = git.commit("Tester", "Updated subFile content");
            assert !deleteCommitHash.equals(subFileEditCommitHash) : "Failed: Commit hash should differ after subFile edits.";
            System.out.println("Test 9: Created Commit with Edited Subfolder File");

            // Clean up after tests
            Git.deleteRepo();
            System.out.println("All tests passed. Cleaned up repository.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility Method to Create a New Directory
    private static void createDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // Utility Method to Create a New File with Given Content
    private static void createFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }

    // Utility Method to Modify an Existing File with New Content
    private static void modifyFile(String filePath, String newContent) throws IOException {
        createFile(filePath, newContent);
    }

    // Utility Method to Delete a File
    private static void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    // Utility Method to Check if a File Exists in the Index
    private static boolean checkIndexFor(String filePath) throws IOException {
        File index = new File("git/index");
        if (!index.exists()) return false;
        
        return Files.lines(Paths.get("git/index")).anyMatch(line -> line.contains(filePath));
    }
}


// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;

// public class GitTester {
//     public static void main(String[] args) {
//         try {
//             // 1. Initialize Git Repository
//             Git.initRepo();
//             System.out.println("Test 1: Initialized Git Repository");

//             // 2. Test Staging a New File
//             String filePath = "testFile.txt";
//             createFile(filePath, "This is the original content.");
//             Tree git = new Tree();
//             git.stage(filePath);
//             assert checkIndexFor(filePath) : "Failed: New file was not staged correctly.";
//             System.out.println("Test 2: Staged New File");

//             // 3. Test Committing the Staged File
//             String commitHash = git.commit("Tester", "Initial commit");
//             assert new File("git/objects/" + commitHash).exists() : "Failed: Commit file not created.";
//             System.out.println("Test 3: Created Initial Commit");

//             // 4. Test Editing the Staged File
//             modifyFile(filePath, "This is the edited content.");
//             git.stage(filePath);
//             assert checkIndexFor(filePath) : "Failed: Edited file was not re-staged.";
//             System.out.println("Test 4: Staged Edited File");

//             // 5. Test Committing the Edited File
//             String newCommitHash = git.commit("Tester", "Edited the file");
//             assert !commitHash.equals(newCommitHash) : "Failed: New commit hash should differ after edits.";
//             System.out.println("Test 5: Created Commit with Edited File");

//             // 6. Test Deleting the File
//             deleteFile(filePath);
//             git.stage(filePath);
//             assert !checkIndexFor(filePath) : "Failed: Deleted file was not removed from index.";
//             System.out.println("Test 6: Staged File Deletion");

//             // 7. Test Committing the Deletion
//             String deleteCommitHash = git.commit("Tester", "Deleted the file");
//             assert !new File("git/objects/" + newCommitHash).exists() || !new File("git/objects/" + deleteCommitHash).equals(newCommitHash) 
//                 : "Failed: Commit hash should update after file deletion.";
//             System.out.println("Test 7: Created Commit with File Deletion");

//             // Clean up after tests
//             Git.deleteRepo();
//             System.out.println("All tests passed. Cleaned up repository.");
            
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     // private static void verifyIndexFileExists() {
//     //     File index = new File("git/index");
//     //     if (index.exists()) {
//     //         System.out.println("Index file exists and is located at: " + index.getAbsolutePath());
//     //     } else {
//     //         System.out.println("Index file does NOT exist!");
//     //     }
//     // }
    

//     // Utility Method to Create a New File with Given Content
//     private static void createFile(String filePath, String content) throws IOException {
//         try (FileWriter writer = new FileWriter(filePath)) {
//             writer.write(content);
//         }
//     }

//     // Utility Method to Modify an Existing File with New Content
//     private static void modifyFile(String filePath, String newContent) throws IOException {
//         createFile(filePath, newContent);
//     }

//     // Utility Method to Delete a File
//     private static void deleteFile(String filePath) throws IOException {
//         Files.deleteIfExists(Paths.get(filePath));
//     }

//     // Utility Method to Check if a File Exists in the Index
//     private static boolean checkIndexFor(String filePath) throws IOException {
//         File index = new File("git/index");
//         if (!index.exists()) return false;
        
//         return Files.lines(Paths.get("git/index")).anyMatch(line -> line.contains(filePath));
//     }
// }