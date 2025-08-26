package kr.bi.greenmate.common.enums;

public enum ImageType {
	COMMUNITY("community");

	private final String value;

	ImageType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
