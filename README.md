# Tucil2_13523031_13523087

## 📄 Deskripsi Singkat - QuadTree Image Compressor
Program ini merupakan implementasi algoritma kompresi gambar menggunakan struktur data QuadTree yang bekerja dengan membagi gambar ke dalam blok-blok homogen berdasarkan berbagai metode perhitungan error seperti Variance, MAD, MPD, Entropy, dan SSIM. Tujuan utama dari program ini adalah menghasilkan gambar terkompresi dengan efisiensi tinggi dengan tetap mempertahankan kualitas visual semirip mungkin dengan gambar asli. Program ini menyediakan fitur pengukuran waktu proses, kedalaman pohon Quadtree, jumlah simpul yang dihasilkan, dan rasio kompresi gambar. Hasil kompresi disimpan dalam format .png / .jpg / .jpeg.

## 📦 Requirement
Untuk dapat menjalankan program, Anda perlu memiliki Java Development Kit (JDK) 8 atau lebih baru.

## 🛠️ Cara Kompilasi
1. Lakukan clone pada repository dengan mengetikkan command
```bash
https://github.com/graceevelyns/Tucil2_13523031_13523087.git
```
2. Pastikan Anda berada di folder root project.
3. Jalankan perintah berikut untuk mengkompilasi semua file Java:

```bash
javac -d bin -cp "lib/animated-gif-lib-1.4.jar" src/*.java src/QuadTree/*.java
```
4. Setelah proses kompilasi berhasil, jalankan program dengan perintah:
```bash
java -cp "bin;lib/animated-gif-lib-1.4.jar" Main
```

## Author(s)
Rafen Max Alessandro | 13523031 </br>
Grace Evelyn Simon | 13523087