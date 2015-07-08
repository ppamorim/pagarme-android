package me.pagar.runnable;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CheckoutManager {

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

  // An object that manages Messages in a Thread
  private Handler handler;

  // A single instance of PhotoManager, used to implement the singleton pattern
  private static CheckoutManager singleton = null;

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
    handler = new Handler(Looper.getMainLooper()) {
      @Override public void handleMessage(Message msg) {
        super.handleMessage(msg);
        CheckoutTask checkoutTask = (CheckoutTask) msg.obj;
      }
    };
  }

  /**
   * Returns the CheckoutManager object
   * @return The global CheckoutManager object
   */
  public static CheckoutManager getInstance() {
    return singleton;
  }


  public static CheckoutTask startCheckout() {
    CheckoutTask checkoutTask = singleton.checkoutTask.poll();
    if(checkoutTask == null) {
      checkoutTask = new CheckoutTask();
    }
    checkoutTask.initialize(CheckoutManager.singleton);
    singleton.checkoutThreadPool.execute(checkoutTask.getCheckoutRunnable());
    return checkoutTask;
  }

}
