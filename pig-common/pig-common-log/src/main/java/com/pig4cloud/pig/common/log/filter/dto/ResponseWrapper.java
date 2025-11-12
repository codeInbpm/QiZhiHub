package com.pig4cloud.pig.common.log.filter.dto;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName ResponseWrapper
 * @Description 响应包装器
 * @Author hanql
 * @Date 2025/2/16
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayOutputStream buffer = null;
	private ServletOutputStream out = null;
	private PrintWriter writer = null;

	public ResponseWrapper(HttpServletResponse response) {
		super(response);
		buffer = new ByteArrayOutputStream();
		out = new WrapperOutputStream(buffer);
		writer = new PrintWriter(out);  // 初始化 writer
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return out;
	}

	@Override
	public PrintWriter getWriter() {
		return writer;
	}

	@Override
	public void flushBuffer() throws IOException {
		if (out != null){
			out.flush();
		}
		if (writer != null){
			writer.flush();
		}
	}

	@Override
	public void reset() {
		buffer.reset();
	}

	public byte[] getResponseData() throws IOException {
		flushBuffer();
		return buffer.toByteArray();
	}

	private static class WrapperOutputStream extends ServletOutputStream {

		private final ByteArrayOutputStream bos;

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {

		}

		@Override
		public void write(int b) throws IOException {
			bos.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			bos.write(b, 0, b.length);
		}

		public WrapperOutputStream(ByteArrayOutputStream buffer) {
			bos = buffer;
		}
	}
}