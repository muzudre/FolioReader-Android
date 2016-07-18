/*
* Copyright (C) 2016 Pedro Paulo de Amorim
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.folioreader.view;

import android.view.View;

import android.support.v4.widget.ViewDragHelper;

public class AudioViewHelperCallback extends ViewDragHelper.Callback {

  private int dragState = 0;
  private int dragOffset = 0;
  private AudioView audioView;

  /**
   * The constructor get the instance of AudioView
   *
   * @param AudioView provide the instance of AudioView
   */
  public AudioViewHelperCallback(AudioView AudioView) {
    this.audioView = AudioView;
  }

  /**
   * Check if view on focus is the AudioView
   *
   * @param child return the view on focus
   * @param pointerId return the id of view
   * @return if the child on focus is equals the AudioView
   */
  @Override public boolean tryCaptureView(View child, int pointerId) {
    return child.equals(audioView.getContainer());
  }

  /**
   * Return the value of slide based
   * on top and height of the element
   *
   * @param child return the view on focus
   * @param top return the top size of AudioView
   * @param dy return the scroll on y-axis
   * @return the offset of slide
   */
  @Override public int clampViewPositionVertical(View child, int top, int dy) {
    return Math.min(Math.max(top, audioView.getPaddingTop()),
            audioView.getContainer().getHeight());
  }

  /**
   * Return the max value of view that can slide
   * based on #clampViewPositionVertical
   *
   * @param child return the view on focus
   * @return max vertical distance that view on focus can slide
   */
  @Override public int getViewVerticalDragRange(View child) {
    return audioView != null ? (int) audioView.getVerticalDragRange() : 0;
  }

  /**
   * Verify if container is dragging or idle and
   * check dragOffset is bigger than dragRange,
   * if true, set the visible to gone.
   *
   * @param state return the touch state of view
   */
  @Override public void onViewDragStateChanged(int state) {
    if (state == dragState) {
      return;
    }
    if ((dragState == ViewDragHelper.STATE_DRAGGING
        || dragState == ViewDragHelper.STATE_SETTLING)
        && state == ViewDragHelper.STATE_IDLE
        && (dragOffset == audioView.getVerticalDragRange())) {
      audioView.hideView();
    }
    dragState = state;
  }

  /**
   * Override method used notify the drag value
   * based on position and dragRange
   *
   * @param left position.
   * @param top position.
   * @param dx change in X position from the last call.
   * @param dy change in Y position from the last call.
   */
  @Override public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
    super.onViewPositionChanged(changedView, left, top, dx, dy);
    dragOffset = Math.abs(top);
    float fractionScreen = (float) dragOffset / audioView.getVerticalDragRange();
    audioView.onViewPositionChanged(fractionScreen >= 1 ? 1 : fractionScreen);
  }

  /**
   * This is called only the touch on container is released.
   *
   * @param releasedChild return the view on focus
   * @param xVel return the speed of X animation
   * @param yVel return the speed of Y animation
   */
  @Override public void onViewReleased(View releasedChild, float xVel, float yVel) {
    super.onViewReleased(releasedChild, xVel, yVel);
    if (audioView.isDragViewAboveTheLimit()) {
      audioView.moveOffScreen();
    } else {
      audioView.moveToOriginalPosition();
    }
  }

}