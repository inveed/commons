package net.inveed.commons.utils;

import java.util.concurrent.CompletableFuture;

public class AsyncHelper {
	public static final <T> CompletableFuture<T> onException(Class<T> type, Throwable e) {
		CompletableFuture<T> ret = new CompletableFuture<>();
		ret.completeExceptionally(e);
		return ret;
	}
}
