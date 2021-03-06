package it.uniroma1.di.simulejos.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2GL3;

public class Arrays {
	private volatile int nextIndex;
	private final GL2GL3 gl;
	private final int count;
	private final List<VertexArray> arrays = Collections
			.synchronizedList(new LinkedList<VertexArray>());

	public Arrays(GL2GL3 gl) {
		this.gl = gl;
		this.count = 0;
	}

	public Arrays(GL2GL3 gl, int count) {
		this.gl = gl;
		this.count = count;
	}

	public void add(int components, byte[] data) {
		arrays.add(new ByteArray(gl, nextIndex++, components, data));
	}

	public void add(int components, ByteBuffer data) {
		arrays.add(new ByteArray(gl, nextIndex++, components, data));
	}

	public void add(int components, short[] data) {
		arrays.add(new ShortArray(gl, nextIndex++, components, data));
	}

	public void add(int components, ShortBuffer data) {
		arrays.add(new ShortArray(gl, nextIndex++, components, data));
	}

	public void add(int components, int[] data) {
		arrays.add(new IntArray(gl, nextIndex++, components, data));
	}

	public void add(int components, IntBuffer data) {
		arrays.add(new IntArray(gl, nextIndex++, components, data));
	}

	public void add(int components, float[] data) {
		arrays.add(new FloatArray(gl, nextIndex++, components, data));
	}

	public void add(int components, FloatBuffer data) {
		arrays.add(new FloatArray(gl, nextIndex++, components, data));
	}

	public void add(int components, double[] data) {
		arrays.add(new DoubleArray(gl, nextIndex++, components, data));
	}

	public void add(int components, DoubleBuffer data) {
		arrays.add(new DoubleArray(gl, nextIndex++, components, data));
	}

	public void share(GL2GL3 gl) {
		for (VertexArray array : arrays) {
			array.share(gl);
		}
	}

	public void bind(GL2GL3 gl) {
		for (VertexArray array : arrays) {
			array.bind(gl);
		}
	}

	public void bind() {
		for (VertexArray array : arrays) {
			array.bind();
		}
	}

	public void draw(GL2GL3 gl, int mode) {
		gl.glDrawArrays(mode, 0, count);
	}

	public void draw(int mode) {
		gl.glDrawArrays(mode, 0, count);
	}

	public void draw(GL2GL3 gl, int mode, int first, int count) {
		gl.glDrawArrays(mode, first, count);
	}

	public void draw(int mode, int first, int count) {
		gl.glDrawArrays(mode, first, count);
	}

	public void bindAndDraw(GL2GL3 gl, int mode) {
		for (VertexArray array : arrays) {
			array.bind(gl);
		}
		gl.glDrawArrays(mode, 0, count);
	}

	public void bindAndDraw(int mode) {
		for (VertexArray array : arrays) {
			array.bind();
		}
		gl.glDrawArrays(mode, 0, count);
	}

	public void bindAndDraw(GL2GL3 gl, int mode, int first, int count) {
		for (VertexArray array : arrays) {
			array.bind(gl);
		}
		gl.glDrawArrays(mode, first, count);
	}

	public void bindAndDraw(int mode, int first, int count) {
		for (VertexArray array : arrays) {
			array.bind();
		}
		gl.glDrawArrays(mode, first, count);
	}

	public void delete() {
		for (VertexArray array : arrays) {
			array.delete();
		}
	}
}
