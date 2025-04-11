import QuadTree.QuadTreeProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("  _________________________________________________ ");
        System.out.println(" |                                                 |");
        System.out.println(" |     Selamat datang di QuadTree Compression!     |");
        System.out.println(" |_________________________________________________|");
        System.out.println("    (/^-^)/                              \\(^-^\\)  \n\n");
        System.out.println(" Tekan ENTER untuk memulai kompresi gambar! ");

        Scanner scan = new Scanner(System.in);
        scan.nextLine();

        String inputPath;
        int errorMethod;
        double threshold;
        int minBlockSize;
        String outputImagePath;

        while (true) {
            System.out.print("\nalamat absolut gambar yang akan dikompresi: ");
            inputPath = scan.nextLine();

            File inputFile = new File(inputPath);
            if (!inputFile.exists()) {
                System.out.println("Path yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }
            
            try {
                BufferedImage testImage = ImageIO.read(inputFile);
                if (testImage == null) {
                    System.out.println("Path yang kamu masukkan tidak valid! o(T-T)o");
                    continue;
                }
                break;
            } catch (IOException e) {
                System.out.println("Terjadi kesalahan saat membaca file gambar.");
            }
        }

        System.out.println("  _______________________________ ");
        System.out.println(" |                               |");
        System.out.println(" |   Metode Perhitungan Error    |");
        System.out.println(" |_______________________________|");
        System.out.println(" |                               |");
        System.out.println(" |   1. variance                 |");
        System.out.println(" |   2. mean absolute deviation  |");
        System.out.println(" |   3. max pixel difference     |");
        System.out.println(" |   4. entropy                  |");
        System.out.println(" |   5. SSIM                     |");
        System.out.println(" |_______________________________|");

        while (true) {
            System.out.print("\nmetode perhitungan error: ");

            if (scan.hasNextInt()) {
                errorMethod = scan.nextInt();
            } else {
                scan.next();
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }

            if (errorMethod >= 1 && errorMethod <= 5) {
                scan.nextLine();
                break;
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;    
            }
        }

        while (true) {
            System.out.print("\nambang batas: ");

            if (scan.hasNextDouble()) {
                threshold = scan.nextDouble();
            } else {
                scan.next();
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }

            if (threshold >= 0) {
                scan.nextLine();
                break;
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;    
            }
        }

        while (true) {
            System.out.print("\nukuran blok minimum: ");

            if (scan.hasNextInt()) {
                minBlockSize = scan.nextInt();
                scan.nextLine();
                break;
            } else {
                scan.next();
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }
        }

        while (true) {
            System.out.print("\nalamat absolut gambar hasil kompresi: ");
            outputImagePath = scan.nextLine();

            if (!isValidPath(outputImagePath, true)) {
                System.out.println("Path yang kamu masukkan tidak valid untuk gambar! o(T-T)o");
                continue;
            }

            break;
        }

        // while (true) {
        //     System.out.print("\nalamat absolut GIF hasil kompresi: ");
        //     outputImagePath = scan.nextLine();

        //     if (!isValidPath(outputImagePath, false)) {
        //         System.out.println("Path yang kamu masukkan tidak valid untuk GIF! o(T-T)o");
        //         continue;
        //     }

        //     break;
        // }

        scan.close();

        QuadTreeProcessor processor = new QuadTreeProcessor();
        processor.processImage(inputPath, outputImagePath, errorMethod, threshold, minBlockSize);
    }

    public static boolean isValidPath(String path, boolean gambar) {
        try {
            Path filePath = Paths.get(path);
            Path parentDirectory = filePath.getParent();
            
            if (parentDirectory != null && !Files.isDirectory(parentDirectory)) {
                return false;
            }

            String extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            String[] validExtensions = {"jpg", "jpeg", "png", "bmp", "gif"};
            
            if (gambar) {
                for (int i = 0; i < 3; i++) {
                    if (extension.equals(validExtensions[i])) {
                        return true;
                    }
                }
            } else {
                for (int i = 0; i < 2; i++) {
                    if (extension.equals(validExtensions[i + 3])) {
                        return true;
                    }
                } 
            }
    
            return false;
    
        } catch (InvalidPathException e) {
            return false;
        }
    }
}