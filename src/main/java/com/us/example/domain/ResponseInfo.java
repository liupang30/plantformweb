package com.us.example.domain;

import java.io.Serializable;

public class ResponseInfo<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static enum Status {
		SUCCEED(0, "\u6210\u529f" /*成功*/), FAILURE(1, "\u5931\u8d25" /*失败*/);

		public final int status;
		public final String message;

		private Status(int status, String message) {
			this.status = status;
			this.message = message;
		}
	}

	public ResponseInfo() {}
	
    public ResponseInfo(Status status) {
        this.status = status.status;
        this.message = status.message;
    }
    
	public ResponseInfo(Status status, T data) {
		this.status = status.status;
		this.message = status.message;
		this.data = data;
	}
	
    public ResponseInfo(Status status, String message, T data) {
        this.status = status.status;
        this.message = message;
        this.data = data;
    }
	
	/**
	 * 返回状态
	 */
	private int status = Status.SUCCEED.status;
	/*
	 * 返回信息
	 */
	private String message = Status.SUCCEED.message;

	/**
	 * 返回的数据
	 */
	private T data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
