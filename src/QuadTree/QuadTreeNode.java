package QuadTree;

public class QuadTreeNode {
    private int x, y; // koordinat kiri atas blok gambar
    private int width, height; // ukuran blok
    private int[] color; // rata-rata warna blok (R, G, B)
    private QuadTreeNode[] children; // children dari internal nodes

    // ctor untuk leaf nodes
    public QuadTreeNode(int x, int y, int width, int height, int[] color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.children = null;
    }

    // ctor untuk internal nodes
    public QuadTreeNode(int x, int y, int width, int height, QuadTreeNode[] children) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = null;
        this.children = children;
    }

    // getter setter
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getColor() {
        return color;
    }

    public QuadTreeNode[] getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return children == null;
    }
}