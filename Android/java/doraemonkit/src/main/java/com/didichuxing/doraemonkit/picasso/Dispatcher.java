/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.picasso;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.didichuxing.doraemonkit.picasso.BitmapHunter.forRequest;
import static com.didichuxing.doraemonkit.picasso.MemoryPolicy.shouldWriteToMemoryCache;
import static com.didichuxing.doraemonkit.picasso.Utils.OWNER_DISPATCHER;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_BATCHED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_CANCELED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_DELIVERED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_ENQUEUED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_IGNORED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_PAUSED;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_REPLAYING;
import static com.didichuxing.doraemonkit.picasso.Utils.VERB_RETRYING;
import static com.didichuxing.doraemonkit.picasso.Utils.getLogIdsForHunter;
import static com.didichuxing.doraemonkit.picasso.Utils.getService;
import static com.didichuxing.doraemonkit.picasso.Utils.hasPermission;
import static com.didichuxing.doraemonkit.picasso.Utils.log;

class Dispatcher {
  private static final int RETRY_DELAY = 500;
  private static final int AIRPLANE_MODE_ON = 1;
  private static final int AIRPLANE_MODE_OFF = 0;

  static final int REQUEST_SUBMIT = 1;
  static final int REQUEST_CANCEL = 2;
  static final int REQUEST_GCED = 3;
  static final int HUNTER_COMPLETE = 4;
  static final int HUNTER_RETRY = 5;
  static final int HUNTER_DECODE_FAILED = 6;
  static final int HUNTER_DELAY_NEXT_BATCH = 7;
  static final int HUNTER_BATCH_COMPLETE = 8;
  static final int NETWORK_STATE_CHANGE = 9;
  static final int AIRPLANE_MODE_CHANGE = 10;
  static final int TAG_PAUSE = 11;
  static final int TAG_RESUME = 12;
  static final int REQUEST_BATCH_RESUME = 13;

  private static final String DISPATCHER_THREAD_NAME = "Dispatcher";
  private static final int BATCH_DELAY = 200; // ms

  final DispatcherThread dispatcherThread;
  final Context context;
  final ExecutorService service;
  final Downloader downloader;
  final Map<String, BitmapHunter> hunterMap;
  final Map<Object, Action> failedActions;
  final Map<Object, Action> pausedActions;
  final Set<Object> pausedTags;
  final Handler handler;
  final Handler mainThreadHandler;
  final Cache cache;
  final Stats stats;
  final List<BitmapHunter> batch;
  final NetworkBroadcastReceiver receiver;
  final boolean scansNetworkChanges;

  boolean airplaneMode;

  Dispatcher(Context context, ExecutorService service, Handler mainThreadHandler,
      Downloader downloader, Cache cache, Stats stats) {
    this.dispatcherThread = new DispatcherThread();
    this.dispatcherThread.start();
    Utils.flushStackLocalLeaks(dispatcherThread.getLooper());
    this.context = context;
    this.service = service;
    this.hunterMap = new LinkedHashMap<String, BitmapHunter>();
    this.failedActions = new WeakHashMap<Object, Action>();
    this.pausedActions = new WeakHashMap<Object, Action>();
    this.pausedTags = new HashSet<Object>();
    this.handler = new DispatcherHandler(dispatcherThread.getLooper(), this);
    this.downloader = downloader;
    this.mainThreadHandler = mainThreadHandler;
    this.cache = cache;
    this.stats = stats;
    this.batch = new ArrayList<BitmapHunter>(4);
    this.airplaneMode = Utils.isAirplaneModeOn(this.context);
    this.scansNetworkChanges = hasPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
    this.receiver = new NetworkBroadcastReceiver(this);
    receiver.register();
  }

  void shutdown() {
    // Shutdown the thread pool only if it is the one created by Picasso.
    if (service instanceof PicassoExecutorService) {
      service.shutdown();
    }
    downloader.shutdown();
    dispatcherThread.quit();
    // Unregister network broadcast receiver on the main thread.
    DokitPicasso.HANDLER.post(new Runnable() {
      @Override public void run() {
        receiver.unregister();
      }
    });
  }

  void dispatchSubmit(Action action) {
    handler.sendMessage(handler.obtainMessage(REQUEST_SUBMIT, action));
  }

  void dispatchCancel(Action action) {
    handler.sendMessage(handler.obtainMessage(REQUEST_CANCEL, action));
  }

  void dispatchPauseTag(Object tag) {
    handler.sendMessage(handler.obtainMessage(TAG_PAUSE, tag));
  }

  void dispatchResumeTag(Object tag) {
    handler.sendMessage(handler.obtainMessage(TAG_RESUME, tag));
  }

  void dispatchComplete(BitmapHunter hunter) {
    handler.sendMessage(handler.obtainMessage(HUNTER_COMPLETE, hunter));
  }

  void dispatchRetry(BitmapHunter hunter) {
    handler.sendMessageDelayed(handler.obtainMessage(HUNTER_RETRY, hunter), RETRY_DELAY);
  }

  void dispatchFailed(BitmapHunter hunter) {
    handler.sendMessage(handler.obtainMessage(HUNTER_DECODE_FAILED, hunter));
  }

  void dispatchNetworkStateChange(NetworkInfo info) {
    handler.sendMessage(handler.obtainMessage(NETWORK_STATE_CHANGE, info));
  }

  void dispatchAirplaneModeChange(boolean airplaneMode) {
    handler.sendMessage(handler.obtainMessage(AIRPLANE_MODE_CHANGE,
        airplaneMode ? AIRPLANE_MODE_ON : AIRPLANE_MODE_OFF, 0));
  }

  void performSubmit(Action action) {
    performSubmit(action, true);
  }

  void performSubmit(Action action, boolean dismissFailed) {
    if (pausedTags.contains(action.getTag())) {
      pausedActions.put(action.getTarget(), action);
      if (action.getPicasso().loggingEnabled) {
        log(OWNER_DISPATCHER, VERB_PAUSED, action.request.logId(),
            "because tag '" + action.getTag() + "' is paused");
      }
      return;
    }

    BitmapHunter hunter = hunterMap.get(action.getKey());
    if (hunter != null) {
      hunter.attach(action);
      return;
    }

    if (service.isShutdown()) {
      if (action.getPicasso().loggingEnabled) {
        log(OWNER_DISPATCHER, VERB_IGNORED, action.request.logId(), "because shut down");
      }
      return;
    }

    hunter = forRequest(action.getPicasso(), this, cache, stats, action);
    hunter.future = service.submit(hunter);
    hunterMap.put(action.getKey(), hunter);
    if (dismissFailed) {
      failedActions.remove(action.getTarget());
    }

    if (action.getPicasso().loggingEnabled) {
      log(OWNER_DISPATCHER, VERB_ENQUEUED, action.request.logId());
    }
  }

  void performCancel(Action action) {
    String key = action.getKey();
    BitmapHunter hunter = hunterMap.get(key);
    if (hunter != null) {
      hunter.detach(action);
      if (hunter.cancel()) {
        hunterMap.remove(key);
        if (action.getPicasso().loggingEnabled) {
          log(OWNER_DISPATCHER, VERB_CANCELED, action.getRequest().logId());
        }
      }
    }

    if (pausedTags.contains(action.getTag())) {
      pausedActions.remove(action.getTarget());
      if (action.getPicasso().loggingEnabled) {
        log(OWNER_DISPATCHER, VERB_CANCELED, action.getRequest().logId(),
            "because paused request got canceled");
      }
    }

    Action remove = failedActions.remove(action.getTarget());
    if (remove != null && remove.getPicasso().loggingEnabled) {
      log(OWNER_DISPATCHER, VERB_CANCELED, remove.getRequest().logId(), "from replaying");
    }
  }

  void performPauseTag(Object tag) {
    // Trying to pause a tag that is already paused.
    if (!pausedTags.add(tag)) {
      return;
    }

    // Go through all active hunters and detach/pause the requests
    // that have the paused tag.
    for (Iterator<BitmapHunter> it = hunterMap.values().iterator(); it.hasNext();) {
      BitmapHunter hunter = it.next();
      boolean loggingEnabled = hunter.getPicasso().loggingEnabled;

      Action single = hunter.getAction();
      List<Action> joined = hunter.getActions();
      boolean hasMultiple = joined != null && !joined.isEmpty();

      // Hunter has no requests, bail early.
      if (single == null && !hasMultiple) {
        continue;
      }

      if (single != null && single.getTag().equals(tag)) {
        hunter.detach(single);
        pausedActions.put(single.getTarget(), single);
        if (loggingEnabled) {
          log(OWNER_DISPATCHER, VERB_PAUSED, single.request.logId(),
              "because tag '" + tag + "' was paused");
        }
      }

      if (hasMultiple) {
        for (int i = joined.size() - 1; i >= 0; i--) {
          Action action = joined.get(i);
          if (!action.getTag().equals(tag)) {
            continue;
          }

          hunter.detach(action);
          pausedActions.put(action.getTarget(), action);
          if (loggingEnabled) {
            log(OWNER_DISPATCHER, VERB_PAUSED, action.request.logId(),
                "because tag '" + tag + "' was paused");
          }
        }
      }

      // Check if the hunter can be cancelled in case all its requests
      // had the tag being paused here.
      if (hunter.cancel()) {
        it.remove();
        if (loggingEnabled) {
          log(OWNER_DISPATCHER, VERB_CANCELED, getLogIdsForHunter(hunter), "all actions paused");
        }
      }
    }
  }

  void performResumeTag(Object tag) {
    // Trying to resume a tag that is not paused.
    if (!pausedTags.remove(tag)) {
      return;
    }

    List<Action> batch = null;
    for (Iterator<Action> i = pausedActions.values().iterator(); i.hasNext();) {
      Action action = i.next();
      if (action.getTag().equals(tag)) {
        if (batch == null) {
          batch = new ArrayList<Action>();
        }
        batch.add(action);
        i.remove();
      }
    }

    if (batch != null) {
      mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(REQUEST_BATCH_RESUME, batch));
    }
  }

  void performRetry(BitmapHunter hunter) {
    if (hunter.isCancelled()) return;

    if (service.isShutdown()) {
      performError(hunter, false);
      return;
    }

    NetworkInfo networkInfo = null;
    if (scansNetworkChanges) {
      ConnectivityManager connectivityManager = getService(context, CONNECTIVITY_SERVICE);
      networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    boolean hasConnectivity = networkInfo != null && networkInfo.isConnected();
    boolean shouldRetryHunter = hunter.shouldRetry(airplaneMode, networkInfo);
    boolean supportsReplay = hunter.supportsReplay();

    if (!shouldRetryHunter) {
      // Mark for replay only if we observe network info changes and support replay.
      boolean willReplay = scansNetworkChanges && supportsReplay;
      performError(hunter, willReplay);
      if (willReplay) {
        markForReplay(hunter);
      }
      return;
    }

    // If we don't scan for network changes (missing permission) or if we have connectivity, retry.
    if (!scansNetworkChanges || hasConnectivity) {
      if (hunter.getPicasso().loggingEnabled) {
        log(OWNER_DISPATCHER, VERB_RETRYING, getLogIdsForHunter(hunter));
      }
      //noinspection ThrowableResultOfMethodCallIgnored
      if (hunter.getException() instanceof NetworkRequestHandler.ContentLengthException) {
        hunter.networkPolicy |= NetworkPolicy.NO_CACHE.index;
      }
      hunter.future = service.submit(hunter);
      return;
    }

    performError(hunter, supportsReplay);

    if (supportsReplay) {
      markForReplay(hunter);
    }
  }

  void performComplete(BitmapHunter hunter) {
    if (shouldWriteToMemoryCache(hunter.getMemoryPolicy())) {
      cache.set(hunter.getKey(), hunter.getResult());
    }
    hunterMap.remove(hunter.getKey());
    batch(hunter);
    if (hunter.getPicasso().loggingEnabled) {
      log(OWNER_DISPATCHER, VERB_BATCHED, getLogIdsForHunter(hunter), "for completion");
    }
  }

  void performBatchComplete() {
    List<BitmapHunter> copy = new ArrayList<BitmapHunter>(batch);
    batch.clear();
    mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(HUNTER_BATCH_COMPLETE, copy));
    logBatch(copy);
  }

  void performError(BitmapHunter hunter, boolean willReplay) {
    if (hunter.getPicasso().loggingEnabled) {
      log(OWNER_DISPATCHER, VERB_BATCHED, getLogIdsForHunter(hunter),
          "for error" + (willReplay ? " (will replay)" : ""));
    }
    hunterMap.remove(hunter.getKey());
    batch(hunter);
  }

  void performAirplaneModeChange(boolean airplaneMode) {
    this.airplaneMode = airplaneMode;
  }

  void performNetworkStateChange(NetworkInfo info) {
    if (service instanceof PicassoExecutorService) {
      ((PicassoExecutorService) service).adjustThreadCount(info);
    }
    // Intentionally check only if isConnected() here before we flush out failed actions.
    if (info != null && info.isConnected()) {
      flushFailedActions();
    }
  }

  private void flushFailedActions() {
    if (!failedActions.isEmpty()) {
      Iterator<Action> iterator = failedActions.values().iterator();
      while (iterator.hasNext()) {
        Action action = iterator.next();
        iterator.remove();
        if (action.getPicasso().loggingEnabled) {
          log(OWNER_DISPATCHER, VERB_REPLAYING, action.getRequest().logId());
        }
        performSubmit(action, false);
      }
    }
  }

  private void markForReplay(BitmapHunter hunter) {
    Action action = hunter.getAction();
    if (action != null) {
      markForReplay(action);
    }
    List<Action> joined = hunter.getActions();
    if (joined != null) {
      //noinspection ForLoopReplaceableByForEach
      for (int i = 0, n = joined.size(); i < n; i++) {
        Action join = joined.get(i);
        markForReplay(join);
      }
    }
  }

  private void markForReplay(Action action) {
    Object target = action.getTarget();
    if (target != null) {
      action.willReplay = true;
      failedActions.put(target, action);
    }
  }

  private void batch(BitmapHunter hunter) {
    if (hunter.isCancelled()) {
      return;
    }
    batch.add(hunter);
    if (!handler.hasMessages(HUNTER_DELAY_NEXT_BATCH)) {
      handler.sendEmptyMessageDelayed(HUNTER_DELAY_NEXT_BATCH, BATCH_DELAY);
    }
  }

  private void logBatch(List<BitmapHunter> copy) {
    if (copy == null || copy.isEmpty()) return;
    BitmapHunter hunter = copy.get(0);
    DokitPicasso picasso = hunter.getPicasso();
    if (picasso.loggingEnabled) {
      StringBuilder builder = new StringBuilder();
      for (BitmapHunter bitmapHunter : copy) {
        if (builder.length() > 0) builder.append(", ");
        builder.append(Utils.getLogIdsForHunter(bitmapHunter));
      }
      log(OWNER_DISPATCHER, VERB_DELIVERED, builder.toString());
    }
  }

  private static class DispatcherHandler extends Handler {
    private final Dispatcher dispatcher;

    public DispatcherHandler(Looper looper, Dispatcher dispatcher) {
      super(looper);
      this.dispatcher = dispatcher;
    }

    @Override public void handleMessage(final Message msg) {
      switch (msg.what) {
        case REQUEST_SUBMIT: {
          Action action = (Action) msg.obj;
          dispatcher.performSubmit(action);
          break;
        }
        case REQUEST_CANCEL: {
          Action action = (Action) msg.obj;
          dispatcher.performCancel(action);
          break;
        }
        case TAG_PAUSE: {
          Object tag = msg.obj;
          dispatcher.performPauseTag(tag);
          break;
        }
        case TAG_RESUME: {
          Object tag = msg.obj;
          dispatcher.performResumeTag(tag);
          break;
        }
        case HUNTER_COMPLETE: {
          BitmapHunter hunter = (BitmapHunter) msg.obj;
          dispatcher.performComplete(hunter);
          break;
        }
        case HUNTER_RETRY: {
          BitmapHunter hunter = (BitmapHunter) msg.obj;
          dispatcher.performRetry(hunter);
          break;
        }
        case HUNTER_DECODE_FAILED: {
          BitmapHunter hunter = (BitmapHunter) msg.obj;
          dispatcher.performError(hunter, false);
          break;
        }
        case HUNTER_DELAY_NEXT_BATCH: {
          dispatcher.performBatchComplete();
          break;
        }
        case NETWORK_STATE_CHANGE: {
          NetworkInfo info = (NetworkInfo) msg.obj;
          dispatcher.performNetworkStateChange(info);
          break;
        }
        case AIRPLANE_MODE_CHANGE: {
          dispatcher.performAirplaneModeChange(msg.arg1 == AIRPLANE_MODE_ON);
          break;
        }
        default:
          DokitPicasso.HANDLER.post(new Runnable() {
            @Override public void run() {
              throw new AssertionError("Unknown handler message received: " + msg.what);
            }
          });
      }
    }
  }

  static class DispatcherThread extends HandlerThread {
    DispatcherThread() {
      super(Utils.THREAD_PREFIX + DISPATCHER_THREAD_NAME, THREAD_PRIORITY_BACKGROUND);
    }
  }

  static class NetworkBroadcastReceiver extends BroadcastReceiver {
    static final String EXTRA_AIRPLANE_STATE = "state";

    private final Dispatcher dispatcher;

    NetworkBroadcastReceiver(Dispatcher dispatcher) {
      this.dispatcher = dispatcher;
    }

    void register() {
      IntentFilter filter = new IntentFilter();
      filter.addAction(ACTION_AIRPLANE_MODE_CHANGED);
      if (dispatcher.scansNetworkChanges) {
        filter.addAction(CONNECTIVITY_ACTION);
      }
      dispatcher.context.registerReceiver(this, filter);
    }

    void unregister() {
      dispatcher.context.unregisterReceiver(this);
    }

    @Override public void onReceive(Context context, Intent intent) {
      // On some versions of Android this may be called with a null Intent,
      // also without extras (getExtras() == null), in such case we use defaults.
      if (intent == null) {
        return;
      }
      final String action = intent.getAction();
      if (ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
        if (!intent.hasExtra(EXTRA_AIRPLANE_STATE)) {
          return; // No airplane state, ignore it. Should we query Utils.isAirplaneModeOn?
        }
        dispatcher.dispatchAirplaneModeChange(intent.getBooleanExtra(EXTRA_AIRPLANE_STATE, false));
      } else if (CONNECTIVITY_ACTION.equals(action)) {
        ConnectivityManager connectivityManager = getService(context, CONNECTIVITY_SERVICE);
        dispatcher.dispatchNetworkStateChange(connectivityManager.getActiveNetworkInfo());
      }
    }
  }
}
