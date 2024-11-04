import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tree implements GitInterface {
    //Stages a file for the next commit.
    // public void stage(String filePath) {
    //     try {
    //         File file = new File(filePath);
    //         if (!file.exists()) {
    //             throw new IllegalArgumentException("File does not exist: " + filePath);
    //         }
    //         String blobHash = createBlob(file);
    //         writeToIndexFile("blob", file.getPath(), blobHash);
    //         File directory = file.getParentFile();
    //         while (directory != null) {
    //             String treeHash = addDirectory(directory.getPath());
    //             writeToIndexFile("tree", directory.getPath(), treeHash);
    //             directory = directory.getParentFile();
    //         }

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    public void stage(String filePath) {
        try {
            File file = new File(filePath);
            
            // Check if the file has been deleted
            if (!file.exists()) {
                markFileAsDeleted(filePath);
                return;
            }
    
            // Create a new blob hash for the file
            String newHash = createBlob(file);
    
            // Check if the file's hash in the index matches the new hash
            if (!isFileInIndexOrModified(filePath, newHash)) {
                writeToIndexFile("blob", filePath, newHash);
            }
    
            // Traverse up the directory structure and update trees
            File directory = file.getParentFile();
            while (directory != null) {
                String treeHash = addDirectory(directory.getPath());
                writeToIndexFile("tree", directory.getPath(), treeHash);
                directory = directory.getParentFile();
            }
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Method to check if a file is in the index and has a different hash (indicating modification)
    private boolean isFileInIndexOrModified(String filePath, String newHash) throws IOException {
        File index = new File("git/index");
        if (!index.exists()) return false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3 && parts[2].equals(filePath)) {
                    return parts[1].equals(newHash); // Returns true if the file is unmodified
                }
            }
        }
        return false; // If the file isn't in the index, or has a new hash, consider it modified
    }
    
    // Method to mark a file as deleted in the index
    private void markFileAsDeleted(String filePath) throws IOException {
        File index = new File("git/index");
        StringBuilder newIndexContent = new StringBuilder();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Only add lines that do not match the deleted file path
                if (!line.contains(filePath)) {
                    newIndexContent.append(line).append("\n");
                }
            }
        }
    
        // Rewrite the index file without the deleted file entry
        try (FileWriter writer = new FileWriter(index)) {
            writer.write(newIndexContent.toString());
        }
    
        System.out.println("File marked as deleted: " + filePath);
    }
    

    //Method to create a commit with the given author and message
    // public String commit(String author, String message) throws IOException {
    //     File directory = new File("git/objects/root");
    //     directory.mkdirs();
    //     String rootTreeHash = addDirectory("git/objects/root");
    //     String parentHash = readHead();
    //     String commitContent = createCommitContent(author, message, rootTreeHash, parentHash);
    //     String commitHash = hashContents(commitContent);
    //     File commitFile = new File("git/objects/" + commitHash);
    //     commitFile.getParentFile().mkdirs();
    //     try (Writer writer = new FileWriter(commitFile)) {
    //         writer.write(commitContent);
    //     }
    //     File headFile = new File("git/HEAD");
    //     try (Writer writer = new FileWriter(headFile)) {
    //         writer.write(commitHash);
    //     }
    //     directory.delete();
    //     return commitHash;
    // }

    // public String commit(String author, String message) throws IOException {
    //     // Create the root directory and add all files that are currently staged
    //     File directory = new File("git/objects/root");
    //     directory.mkdirs();
    
    //     // Generate a tree hash for the current root directory state
    //     String rootTreeHash = addDirectory("git/objects/root");
    
    //     // Retrieve the previous commit hash from HEAD
    //     String parentHash = readHead();
    
    //     // Create the commit content using the current tree and parent information
    //     String commitContent = createCommitContent(author, message, rootTreeHash, parentHash);
    
    //     // Generate a SHA1 hash for the commit file content
    //     String commitHash = hashContents(commitContent);
    
    //     // Save the commit file in objects directory
    //     File commitFile = new File("git/objects/" + commitHash);
    //     commitFile.getParentFile().mkdirs();
    //     try (Writer writer = new FileWriter(commitFile)) {
    //         writer.write(commitContent);
    //     }
    
    //     // Update HEAD with the new commit hash
    //     File headFile = new File("git/HEAD");
    //     try (Writer writer = new FileWriter(headFile)) {
    //         writer.write(commitHash);
    //     }
    
    //     // Clean up temporary directory after commit
    //     directory.delete();
    //     return commitHash;
    // }
    
    public String commit(String author, String message) throws IOException {
        // Generate the root tree hash based on the current index contents
        String rootTreeHash = generateRootTreeHash();
    
        // Retrieve the previous commit hash from HEAD
        String parentHash = readHead();
    
        // Create the commit content using the current tree and parent information
        String commitContent = createCommitContent(author, message, rootTreeHash, parentHash);
    
        // Generate a SHA1 hash for the commit file content
        String commitHash = hashContents(commitContent);
    
        // Save the commit file in objects directory
        File commitFile = new File("git/objects/" + commitHash);
        commitFile.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(commitFile)) {
            writer.write(commitContent);
        }
    
        // Update HEAD with the new commit hash
        File headFile = new File("git/HEAD");
        try (Writer writer = new FileWriter(headFile)) {
            writer.write(commitHash);
        }
    
        return commitHash;
    }
    
    // Helper function to recursively generate the tree hash for the root directory
    // Helper function to recursively generate the tree hash for the root directory
    private String generateRootTreeHash() throws IOException {
        // Map to store the hash for each directory path
        Map<String, String> directoryHashes = new HashMap<>();

        // Process each entry in the index to build the directory structure
        try (BufferedReader reader = new BufferedReader(new FileReader("git/index"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String type = parts[0];
                    String hash = parts[1];
                    String path = parts[2];

                    // Recursively build the directory structure and generate tree hashes
                    String directoryPath = new File(path).getParent();
                    buildTree(directoryPath, type, path, hash, directoryHashes);
                }
            }
        }

        // Return the hash of the root tree (represented by the empty string)
        return directoryHashes.getOrDefault("", null);  // Root directory is represented by an empty string
    }

    // Recursive method to build and hash directories
    private void buildTree(String directoryPath, String type, String path, String hash,
                        Map<String, String> directoryHashes) throws IOException {
        if (directoryPath == null) {
            directoryPath = ""; // Use an empty string to represent the root directory
        }

        // Retrieve the current directory's content or create a new one if not yet processed
        StringBuilder treeContents = new StringBuilder();
        if (directoryHashes.containsKey(directoryPath)) {
            String existingHash = directoryHashes.get(directoryPath);
            treeContents.append(readTreeFromFile(existingHash));
        }

        // Add the entry to the current directory based on type (blob or tree)
        if (type.equals("blob")) {
            treeContents.append("blob ").append(hash).append(" ").append(new File(path).getName()).append("\n");
        } else if (type.equals("tree")) {
            treeContents.append("tree ").append(hash).append(" ").append(new File(path).getName()).append("\n");
        }

        // Generate and save the hash for the current directory (tree)
        String treeHash = hashTreeContents(treeContents.toString());
        writeTreeToFile(treeHash, treeContents.toString());

        // Store the hash in the directoryHashes map
        directoryHashes.put(directoryPath, treeHash);

        // Recursively process the parent directory if there is one
        String parentPath = new File(directoryPath).getParent();
        if (parentPath != null) {
            buildTree(parentPath, "tree", directoryPath, treeHash, directoryHashes);
        }
    }

    // Helper method to read the contents of a tree from file, given its hash
    private String readTreeFromFile(String treeHash) throws IOException {
        File treeFile = new File("git/objects/" + treeHash);
        if (!treeFile.exists()) return "";

        StringBuilder contents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(treeFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contents.append(line).append("\n");
            }
        }
        return contents.toString();
    }   

    
    private String createCommitContent(String author, String message, String treeHash, String parentHash) {
        String commitContent = "";
        commitContent += "tree: " + treeHash + "\n";
        if (parentHash != null)
          commitContent += "parent: " + parentHash + "\n";
        else
        commitContent += "parent: \n";
        commitContent += "author: " + author + "\n";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        commitContent += "date: " + sdf.format(new Date()) + "\n";
        commitContent += "message: " + message + "\n";
        return commitContent;
    }

    //reads current head commit hash
    // reads the current head commit hash
    private String readHead() throws IOException {
        File headFile = new File("git/HEAD");
        
        // Check if HEAD exists; if not, return null (no parent for initial commit)
        if (!headFile.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(headFile))) {
            String line = reader.readLine();
            // Return null if HEAD is empty (initial commit scenario)
            return line != null ? line.trim() : null;
        }
    }


    // Method to add a directory and recursively create blobs and trees
    public String addDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        // Ensure the directory exists
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        // StringBuilder to accumulate the contents of the tree file
        StringBuilder treeContents = new StringBuilder();

        // Traverse the directory
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                // Create a blob for the file
                String fileHash = createBlob(file);
                treeContents.append("blob ").append(fileHash).append(" ").append(file.getName()).append("\n");
                // Add the blob entry to the index
                writeToIndexFile("blob", directoryPath + "/" + file.getName(), fileHash);
            } else if (file.isDirectory()) {
                // Recursively create a tree for the subdirectory
                String dirHash = addDirectory(file.getPath());
                treeContents.append("tree ").append(dirHash).append(" ").append(file.getName()).append("\n");
                // Add the tree entry to the index
                writeToIndexFile("tree", directoryPath + "/" + file.getName(), dirHash);
            }
        }

        // Hash the contents of the tree and create a tree object
        String treeHash = hashTreeContents(treeContents.toString());
        writeTreeToFile(treeHash, treeContents.toString());

        return treeHash;
    }

    // Method to create a Blob from a file
    private String createBlob(File file) throws IOException {
        // Read the file contents and generate the SHA1 hash
        String fileContents = getFileContents(file.getPath());
        String fileHash = hashContents(fileContents);

        // Write the file contents to the objects directory
        copyFileToObjects(fileHash, fileContents);

        return fileHash;
    }

    // Helper method to read file contents
    private String getFileContents(String inputFile) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
        }
        return fileContents.toString().trim();
    }

    // Part 1 requirement
    private void writeToIndexFile(String type, String path, String hash) throws IOException {
        File index = new File("git/index");
        
        // Step 1: Read existing entries and filter out any old entry for the given path
        StringBuilder updatedIndexContent = new StringBuilder();
        if (index.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Only keep lines that do not match the path we are updating
                    if (!line.endsWith(" " + path)) {
                        updatedIndexContent.append(line).append("\n");
                    }
                }
            }
        }
        
        // Step 2: Add the new entry
        updatedIndexContent.append(type).append(" ").append(hash).append(" ").append(path).append("\n");
    
        // Step 3: Write the updated content back to the index file
        try (FileWriter writer = new FileWriter(index)) {
            writer.write(updatedIndexContent.toString());
        }
    }
    

    // Method to hash the tree contents using SHA1
    private String hashTreeContents(String treeContents) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(treeContents.getBytes(StandardCharsets.UTF_8));
            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to write the tree contents to the objects directory
    private void writeTreeToFile(String treeHash, String treeContents) throws IOException {
        File treeFile = new File("git/objects/" + treeHash);
        treeFile.getParentFile().mkdirs(); // Ensure the directory exists
        try (Writer writer = new FileWriter(treeFile)) {
            writer.write(treeContents);
        }
    }

    // Helper method to write blob contents to the objects directory
    private void copyFileToObjects(String hash, String fileContents) throws IOException {
        File file = new File("git/objects/" + hash);
        file.getParentFile().mkdirs();  // Ensure the directory exists
        try (Writer writer = new FileWriter(file)) {
            writer.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to hash file contents using SHA1
    private String hashContents(String content) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(content.getBytes(StandardCharsets.UTF_8));
            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to convert bytes to hexadecimal (for SHA1)
    private static String byteToHex(final byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void checkout(String commitHash) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkout'");
    }
}
