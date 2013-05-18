package com.quickride.customer.common.domain;

import static ac.mm.android.util.coder.impl.ByteUtils.binaryToInt;
import static ac.mm.android.util.coder.impl.ByteUtils.contactArray;
import static ac.mm.android.util.coder.impl.ByteUtils.hexStringToBytes;
import static ac.mm.android.util.coder.impl.ByteUtils.intToHexBytes;
import static ac.mm.android.util.coder.impl.ByteUtils.longToHexBytes;
import static ac.mm.android.util.coder.impl.ByteUtils.rightSubArray;
import static ac.mm.android.util.coder.impl.ByteUtils.subArray;
import static ac.mm.android.util.coder.impl.ByteUtils.toHexString;

import java.util.Calendar;

public class AppMessage {
	private static volatile String SESSION_ID;

	private Long id;

	private String rawHex;

	private Integer totalLength;

	private Integer cmdCode;

	private Integer seqNo;

	private String sessionId;

	private String statusCode;

	private String body;

	private Calendar invokeTime;

	public AppMessage() {
		super();
	}

	public AppMessage(byte[] src) {
		this.rawHex = toHexString(src);

		this.totalLength = binaryToInt(subArray(src, 0, 2));
		this.cmdCode = binaryToInt(subArray(src, 2, 4));
		this.seqNo = binaryToInt(subArray(src, 4, 8));
		this.sessionId = toHexString(subArray(src, 8, 24));

		if (isMoRequest()) {
			this.statusCode = toHexString(subArray(src, 24, 26));
		} else {
			this.statusCode = StatusCode.SUCCESS;
		}

		this.body = toHexString(rightSubArray(src, 24));

		this.invokeTime = Calendar.getInstance();
	}

	public AppMessage(BusinessCode cmdCode, int seqNo, byte[] appMessageBody) {
		this.cmdCode = cmdCode.getCode();
		this.seqNo = seqNo;
		this.sessionId = SESSION_ID;

		byte[] cmdCodeBytes = intToHexBytes(cmdCode.getCode(), 2);
		byte[] seqNobytes = longToHexBytes(seqNo, 4);

		byte[] sessionIdBytes = hexStringToBytes(sessionId);

		byte[] headerBytes = contactArray(contactArray(cmdCodeBytes, seqNobytes), sessionIdBytes);
		if (isMoRequest()) {
			headerBytes = contactArray(headerBytes, hexStringToBytes("0000"));
		}

		if (isMtResponse()) {
			headerBytes = contactArray(headerBytes, hexStringToBytes("0000"));
		}

		this.body = toHexString(appMessageBody);

		this.totalLength = 2 + headerBytes.length + appMessageBody.length;
		byte[] totalLengthBytes = intToHexBytes(this.totalLength, 2);

		byte[] rawHexBytes = contactArray(contactArray(totalLengthBytes, headerBytes), appMessageBody);
		this.rawHex = toHexString(rawHexBytes);

		this.invokeTime = Calendar.getInstance();
	}

	public byte[] toByteArray() {
		return hexStringToBytes(this.rawHex);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(Integer totalLength) {
		this.totalLength = totalLength;
	}

	public String getRawHex() {
		return rawHex;
	}

	public void setRawHex(String rawHex) {
		this.rawHex = rawHex;
	}

	public int getCmdCode() {
		return cmdCode;
	}

	public void setCmdCode(int cmdCode) {
		this.cmdCode = cmdCode;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Calendar getInvokeTime() {
		return invokeTime;
	}

	public void setInvokeTime(Calendar invokeTime) {
		this.invokeTime = invokeTime;
	}

	public boolean isMoRequest() {
		return BusinessCode.codeOf(this.cmdCode).isMo();
	}

	public boolean isMtResponse() {
		return BusinessCode.codeOf(this.cmdCode).isMt();
	}

	public String getStatusCode() {
		return statusCode;
	}

	public static String complementString(String s, int length) {
		String returnString = s;

		if (length > s.length()) {
			StringBuilder stringBuilder = new StringBuilder(s);
			for (int i = 0; i < length - s.length(); i++) {
				stringBuilder.append(" ");
			}

			returnString = stringBuilder.toString();
		} else if (length < s.length()) {
			returnString = s.substring(0, length);
		}

		return returnString;
	}

	public static void setSESSION_ID(String sESSION_ID) {
		if (!sESSION_ID.equals(SESSION_ID)) {
			SESSION_ID = sESSION_ID;
		}
	}
}
