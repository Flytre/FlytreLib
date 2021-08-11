package net.flytre.flytre_lib.api.base.math;

public final class Rectangle {

    public static final Rectangle DEFAULT = new Rectangle(0, 0, 0, 0);

    private final int x;
    private final int y;
    private final int width;
    private final int height;

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


    /**
     * Creates a rectangle from the given bounds
     */
    public static Rectangle fromBounds(int left, int top, int right, int bottom) {
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
        return contains(x, (double) y);
    }

    /**
     * Checks whether the given point is inside the rectangle
     */
    public boolean contains(double x, double y) {
        return this.x <= x && this.y <= y && this.x + width >= x && this.y + height >= y;
    }


    /**
     * @return a new rectangle with its bounds reduced by the amount
     */
    public Rectangle reducedBy(int amount) {
        return new Rectangle(x + amount, y + amount, width - amount * 2, height - amount * 2);
    }

    /**
     * @return a new rectangle with its bounds expanded by the amount
     */
    public Rectangle expandedBy(int amount) {
        return reducedBy(-amount);
    }

    /**
     * @return a new rectangle with its horizontal bounds reduced by the amount
     */
    public Rectangle horizontallyExpandedBy(int amount) {
        return new Rectangle(x - amount, y, width + amount * 2, height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Rectangle rectangle = (Rectangle) o;

        if (x != rectangle.x)
            return false;
        if (y != rectangle.y)
            return false;
        if (width != rectangle.width)
            return false;

        return height == rectangle.height;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
