/******************************************************************************

Copyright (c) 2013, Mandar Chitre

This file is part of fjage which is released under Simplified BSD License.
See file LICENSE.txt or go to http://www.opensource.org/licenses/BSD-3-Clause
for full license details.

******************************************************************************/

package org.arl.fjage;

import java.util.Random;

/**
 * A behavior that simulates a Poisson arrival process. The {@link #onTick()}
 * method of this behavior is called with exponentially distributed random
 * interarrival time.
 *
 * @author  Mandar Chitre
 */
public abstract class PoissonBehavior extends Behavior {

  //////////// Private attributes

  private int ticks;
  private long expDelay;
  private long wakeupTime;
  private boolean quit;
  private Random rnd = new Random();

  //////////// Interface methods

  /**
   * Creates a behavior that simulates a Poisson arrival process with a
   * specified average interarrival time. The equivalent arrival rate is given
   * by the reciprocal of the average interarrival time.
   *
   * @param millis average interarrival time in milliseconds.
   */
  public PoissonBehavior(long millis) {
    expDelay = millis;
    ticks = 0;
    quit = false;
  }

  /**
   * Terminates the behavior.
   */
  public final void stop() {
    quit = true;
  }

  /**
   * Returns the number of times the {@link #onTick()} method of this behavior
   * has been called (including any ongoing call).
   *
   * @return the number of times the {@link #onTick()} method has been called.
   */
  public final int getTickCount() {
    return ticks;
  }

  //////////// Method to be overridden by subclass

  /**
   * This method is called for each arrival. The method must be overridden by a
   * behavior.
   */
  public abstract void onTick();

  //////////// Overridden methods

  /**
   * Computes the wakeup time for the first execution of this behavior.
   *
   * @see org.arl.fjage.Behavior#onStart()
   */
  @Override
  public void onStart() {
    long delayToNext = Math.round(-Math.log(rnd.nextDouble())*expDelay);
    wakeupTime = agent.currentTimeMillis() + delayToNext;
    block(delayToNext);
  }

  /**
   * This method calls {@link #onTick()} for each Poisson arrival.
   *
   * @see org.arl.fjage.Behavior#action()
   */
  @Override
  public final void action() {
    long dt = wakeupTime - agent.currentTimeMillis();
    if (dt > 0) block(dt);
    else {
      ticks++;
      onTick();
      long delayToNext = Math.round(-Math.log(rnd.nextDouble())*expDelay);
      wakeupTime = agent.currentTimeMillis() + delayToNext;
    }
  }

  /**
   * Returns true once {@link #stop()} is called, false otherwise.
   *
   * @return true once {@link #stop()} is called, false otherwise.
   * @see org.arl.fjage.Behavior#done()
   */
  @Override
  public boolean done() {
    return quit;
  }

  /**
   * Resets the behavior to its initial state, allowing it to be used again.
   *
   * @see org.arl.fjage.Behavior#reset()
   */
  @Override
  public void reset() {
    super.reset();
    ticks = 0;
    quit = false;
  }

}

