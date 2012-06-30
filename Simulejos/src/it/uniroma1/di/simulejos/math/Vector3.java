package it.uniroma1.di.simulejos.math;

import java.io.Serializable;

public class Vector3 implements Cloneable, Serializable {
	private static final long serialVersionUID = 4822298356837119742L;

	public final double x;
	public final double y;
	public final double z;

	public static final Vector3 NULL = new Vector3();
	public static final Vector3 I = new Vector3(1, 0, 0);
	public static final Vector3 J = new Vector3(0, 1, 0);
	public static final Vector3 K = new Vector3(0, 0, 1);

	public Vector3() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public Vector3 clone() {
		return new Vector3(x, y, z);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3 normalize() {
		final double h = Math.hypot(x, y);
		return new Vector3(x / h, y / h, z / h);
	}

	public Vector3 invert() {
		return new Vector3(-x, -y, -z);
	}

	public Vector3 plus(Vector3 v) {
		return new Vector3(x + v.x, y + v.y, z + v.z);
	}

	public Vector3 minus(Vector3 v) {
		return new Vector3(x - v.x, y - v.y, z - v.z);
	}

	public Vector3 by(double f) {
		return new Vector3(x * f, y * f, z * f);
	}

	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3 cross(Vector3 v) {
		return new Vector3(y * v.z - z * v.y, x * v.z - z * v.x, x * v.y - y
				* v.x);
	}
}