package com.cctintl.jfx.concurrency;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;

/**
 * @author pmoufarrej
 */
public class JFXUtilities {

	public static void runInFX(Runnable doRun) {
		if (Platform.isFxApplicationThread()) {
			doRun.run();
			return;
		}
		Platform.runLater(doRun);
	}

	public static void runInFXAndWait(Runnable doRun) {
		if (Platform.isFxApplicationThread()) {
			doRun.run();
			return;
		}
		final CountDownLatch doneLatch = new CountDownLatch(1);
		Platform.runLater(() -> {
			try {
				doRun.run();
			}
			finally {
				doneLatch.countDown();
			}
		});
		try {
			doneLatch.await();
		}
		catch (InterruptedException e) {
		}
	}
}
