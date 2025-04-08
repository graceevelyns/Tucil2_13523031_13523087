import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        
        String inputPath;
        int image[][] = null;
        int height = 0;
        int width = 0;
        int errorMethod;
        double threshold;
        int minBlockSize;
        // int percentageTarget;
        String outputImagePath;
        // String outputGIFPath;

        System.out.println("  _________________________________________________ ");
        System.out.println(" |                                                 |");
        System.out.println(" |     Selamat datang di QuadTree Compression!     |");
        System.out.println(" |_________________________________________________|");
        System.out.println("    (/^-^)/                              \\(^-^\\)  \n\n");
        System.out.println(" Tekan ENTER untuk memulai kompresi gambar! ");

        Scanner scan = new Scanner(System.in);

        // input path;
        while (true) {
            System.out.print("\nalamat absolut gambar yang akan dikompresi: ");
            inputPath = scan.nextLine();

            try {
                File imageFile = new File(inputPath);
                if (!imageFile.exists() || !imageFile.isFile()) {
                    System.out.println("Path yang kamu masukkan tidak valid! o(T-T)o");
                    continue;
                }

                BufferedImage imageToProcess = ImageIO.read(imageFile);
                if (imageToProcess == null) {
                    System.out.println("Path yang kamu masukkan tidak valid! o(T-T)o");
                    continue;
                }
                
                height = imageToProcess.getHeight();
                width = imageToProcess.getWidth();
                int totalPixels = height * width;

                image = new int[totalPixels][3];

                int counter = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int pixel = imageToProcess.getRGB(j, i);

                        image[counter][0] = (pixel >> 16) & 0xff;
                        image[counter][1] = (pixel >> 8) & 0xff;
                        image[counter][2] = pixel & 0xff;
                        counter++;
                    }
                }
                
                break;
        
            } catch (IOException e) {
                System.out.println("Terjadi kesalahan saat membaca file gambar.");
            }
        }

        // metode perhitungan error
        System.out.println("  _______________________________ ");
        System.out.println(" |                               |");
        System.out.println(" |   Metode Perhitungan Error    |");
        System.out.println(" |_______________________________|");
        System.out.println(" |                               |");
        System.out.println(" |   1. variance                 |");
        System.out.println(" |   2. mean absolute deviation  |");
        System.out.println(" |   3. max pixel difference     |");
        System.out.println(" |   4. entropy                  |");
        System.out.println(" |_______________________________|");

        while (true) {
            System.out.print("\nmetode perhitungan error: ");

            if (scan.hasNextInt()) {
                errorMethod = scan.nextInt();
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }

            if (errorMethod >= 0 && errorMethod <= 4) {
                scan.nextLine();
                break;
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;    
            }
        }

        // threshold
        while (true) {
            System.out.print("\nambang batas: ");

            if (scan.hasNextDouble()) {
                threshold = scan.nextDouble();
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }

            if (threshold >= 0 && threshold <= 100) {
                scan.nextLine();
                break;
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;    
            }
        }
        
        // ukuran blok minimum
        while (true) {
            System.out.print("\nukuran blok minimum: ");

            if (scan.hasNextInt()) {
                minBlockSize = scan.nextInt();
                scan.nextLine();
                break;
            } else {
                System.out.println("Nilai yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }
        }
    
        // alamat gambar hasil kompresi
        while (true) {
            System.out.print("\nalamat absolut gambar hasil kompresi: ");
            outputImagePath = scan.nextLine();

            if (!isValidPath(outputImagePath)) {
                System.out.println("Path yang kamu masukkan tidak valid! o(T-T)o");
                continue;
            }

            break;
        }

        // // alamat GIF hasil kompresi
        // while (true) {
        //     System.out.print("\nalamat absolut GIF hasil kompresi: ");
        //     outputGIFPath = scan.nextLine();

        //     if (!isValidPath(outputGIFPath)) {
        //         System.out.println("Path yang kamu masukkan tidak valid! o(T-T)o");
        //         continue;
        //     }

        //     break;
        // }

        scan.close();
    }

    public static boolean isValidPath(String path) {
        String fileExtension = getFileExtension(path);
        
        if (path.toLowerCase().endsWith(fileExtension)) {
            try {
                Path filePath = Paths.get(path);
                Path parentDirectory = filePath.getParent();

                if (parentDirectory != null && Files.isDirectory(parentDirectory)) {
                    return true;
                } else {
                    return false; 
                }

            } catch (InvalidPathException e) {
                return false;
            }
        }

        return false;
    }

    public static String getFileExtension(String path) {
        int lastIndexOfDot = path.lastIndexOf(".");
        return path.substring(lastIndexOfDot);
    }
}