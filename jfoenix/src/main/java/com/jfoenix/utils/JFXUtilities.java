/*
 * Copyright (c) 2016 JFoenix
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jfoenix.utils;

import javafx.application.Platform;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;


/**
 * <h1>JavaFX FX Thread utilities</h1>
 * JFXUtilities allow sync mechanism to the FX thread
 * <p>
 *
 * @author pmoufarrej
 * @version 1.0
 * @since 2016-03-09
 */

public class JFXUtilities {

    /**
     * This method is used to run a specified Runnable in the FX Application thread,
     * it returns before the task finished execution
     *
     * @param doRun This is the sepcifed task to be excuted by the FX Application thread
     * @return Nothing
     */
    public static void runInFX(Runnable doRun) {
        if (Platform.isFxApplicationThread()) {
            doRun.run();
            return;
        }
        Platform.runLater(doRun);
    }

    /**
     * This method is used to run a specified Runnable in the FX Application thread,
     * it waits for the task to finish before returning to the main thread.
     *
     * @param doRun This is the sepcifed task to be excuted by the FX Application thread
     * @return Nothing
     */
    public static void runInFXAndWait(Runnable doRun) {
        if (Platform.isFxApplicationThread()) {
            doRun.run();
            return;
        }
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                doRun.run();
            } finally {
                doneLatch.countDown();
            }
        });
        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static <T> T[] concat(T[] a, T[] b, Function<Integer, T[]> supplier) {
        final int aLen = a.length;
        final int bLen = b.length;
        T[] array = supplier.apply(aLen + bLen);
        System.arraycopy(a, 0, array, 0, aLen);
        System.arraycopy(b, 0, array, aLen, bLen);
        return array;
    }
}
