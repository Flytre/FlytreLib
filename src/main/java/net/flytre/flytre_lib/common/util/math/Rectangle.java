package net.flytre.flytre_lib.common.util.math;

public class Rectangle implements Cloneable {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(Rectangle other) {
        this(other.x, other.y, other.width, other.height);
    }

    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public Rectangle(int x, int y, int width, int height) {

        if (width < 0) {
            x += width;
            width *= -1;
        }

        if (height < 0) {
            y += height;
            height *= -1;
        }

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public static Rectangle ofBounds(int left, int top, int right, int bottom) {
        return new Rectangle(left, top, right - left, bottom - top);
    }


    public int getLeft() {
        return x;
    }

    public int getRight() {
        return x + width;
    }

    public int getCenterX() {
        return x + width / 2;
    }

    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + height;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public boolean contains(int x, int y) {
        return this.x <= x && this.y <= y && this.x + width >= x && this.y + height >= y;
    }

    public boolean contains(double x, double y) {
        return this.x <= x && this.y <= y && this.x + width >= x && this.y + height >= y;
    }


    public Rectangle shrink(int amount) {
        return new Rectangle(x + amount, y + amount, width - amount * 2, height - amount * 2);
    }

    public Rectangle grow(int amount) {
        return shrink(-amount);
    }

    public Rectangle growHorizontal(int amount) {
        return new Rectangle(x - amount, y, width + amount * 2, height);
    }

    @Override
    public Rectangle clone() {
        return new Rectangle(x, y, width, height);
    }
}
