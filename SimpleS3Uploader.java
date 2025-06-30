import java.io.*;
import java.util.Scanner;

public class SimpleS3Uploader {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter full file path to upload:");
        String filePath = scanner.nextLine();

        System.out.println("Enter S3 bucket name:");
        String bucketName = scanner.nextLine();

        System.out.println("Enter the key (file name in S3):");
        String s3Key = scanner.nextLine();

        // Construct the AWS CLI command
        String command = String.format("aws s3 cp \"%s\" s3://%s/%s", filePath, bucketName, s3Key);

        try {
            // Use ProcessBuilder instead of Runtime
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("cmd.exe", "/c", command); // Use "sh", "-c" for Linux/Mac
            builder.redirectErrorStream(true);

            Process process = builder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("AWS CLI Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\n✅ Process exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.out.println("❌ Error running AWS CLI: " + e.getMessage());
        }
    }
}
