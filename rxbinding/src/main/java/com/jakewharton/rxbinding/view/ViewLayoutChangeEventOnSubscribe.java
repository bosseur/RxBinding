package com.jakewharton.rxbinding.view;

import android.view.View;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

final class ViewLayoutChangeEventOnSubscribe
    implements Observable.OnSubscribe<ViewLayoutChangeEvent> {
  private final View view;

  ViewLayoutChangeEventOnSubscribe(View view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super ViewLayoutChangeEvent> subscriber) {
    checkUiThread();

    final View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() {
      @Override public void onLayoutChange(View v, int left, int top, int right, int bottom,
          int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(ViewLayoutChangeEvent.create(view, left, top, right, bottom,
              oldLeft, oldTop, oldRight, oldBottom));
        }
      }
    };
    view.addOnLayoutChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.removeOnLayoutChangeListener(listener);
      }
    });
  }
}
