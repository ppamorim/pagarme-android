package me.pagar.runnable;

import android.os.Handler;
import android.os.Looper;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import me.pagar.interfaces.CheckoutListener;

public class CheckoutManager  {

  private static final Handler HANDLER = new Handler(Looper.getMainLooper());

  // A queue of Runnables for the image decoding pool
  private final BlockingQueue<Runnable> checkoutWorkQueue;

  // A queue of PhotoManager tasks. Tasks are handed to a ThreadPool.
  private final Queue<CheckoutTask> checkoutTask;

  // Sets the size of the storage that's used to cache images
  private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;

  // Sets the amount of time an idle thread will wait for a task before terminating
  private static final int KEEP_ALIVE_TIME = 60;

  // Sets the Time Unit to seconds
  private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

  // Sets the initial threadpool size to 8
  private static final int CORE_POOL_SIZE = 8;

  // Sets the maximum threadpool size to 8
  private static final int MAXIMUM_POOL_SIZE = 8;

  // A managed pool of background decoder threads
  private final ThreadPoolExecutor checkoutThreadPool;

  // A single instance of PhotoManager, used to implement the singleton pattern
  private static CheckoutManager singleton = null;

  private CheckoutListener checkoutListener;

  /**
   * NOTE: This is the number of total available cores. On current versions of
   * Android, with devices that use plug-and-play cores, this will return less
   * than the total number of cores. The total number of cores is not
   * available in current Android implementations.
   */
  private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

  static {

    // The time unit for "keep alive" is in seconds
    KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // Creates a single static instance of CheckoutManager
    singleton = new CheckoutManager();
  }

  private CheckoutManager() {
    checkoutWorkQueue = new LinkedBlockingQueue<>();
    checkoutTask = new LinkedBlockingQueue<>();
    checkoutThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
        KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, checkoutWorkQueue);
  }

  public static CheckoutManager getInstance() {
    return singleton;
  }

  public static CheckoutTask startCheckout(CheckoutListener checkoutListener) {
    singleton.checkoutListener = checkoutListener;
    CheckoutTask checkoutTask = singleton.checkoutTask.poll();
    if(checkoutTask == null) {
      checkoutTask = new CheckoutTask();
    }
    checkoutTask.initialize(CheckoutManager.singleton);
    singleton.checkoutThreadPool.execute(checkoutTask.getCheckoutRunnable());
    return checkoutTask;
  }

  public void onRequestSuccess(final InputStream inputStream) {
    HANDLER.post(new Runnable() {
      @Override public void run() {
        if(checkoutListener != null) {
          checkoutListener.onCheckoutSuccess(inputStream);
        }
      }
    });
  }

  public void onIOException() {

  }

  public void onJSONException() {

  }

  public void onInterruptedException() {

  }

}
