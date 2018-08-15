package net.inveed.commons.reflection;

public enum AccessLevel {
	ABSENT(-1),
	PRIVATE(1),
	DEFAULT(2),
	PROTECTED(3),
	PUBLIC(4),
	NONE(Integer.MAX_VALUE);
	
	private final int level;
	private AccessLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
}
