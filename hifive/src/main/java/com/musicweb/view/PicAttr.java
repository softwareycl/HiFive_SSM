package com.musicweb.view;

public class PicAttr {
	private double x;
	private double y;
	private double height;
	private double width;
	private int rotate;

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public int getRotate() {
		return rotate;
	}
	public void setRotate(int rotate) {
		this.rotate = rotate;
	}

	@Override
	public String toString() {
		return "PicAttr [x=" + x + ", y=" + y + ", height=" + height
				+ ", width=" + width + ", rotate=" + rotate + "]";
	}
}
