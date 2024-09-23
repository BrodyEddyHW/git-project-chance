import java.io.IOException;

public class GitTester {
    public static void main(String[] args) throws IOException {
        // Git.initRepo();
        // Git.initRepo(); // Should output "Git Repository already exists"
        // Git.deleteRepo(); // Should output "Git Repository deleted"

        Blob blob = new Blob("inputText.txt");
        Blob blb = new Blob("input2.txt");
    }
}