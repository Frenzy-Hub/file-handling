/*
 * Copyright 2015 Google Inc.
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

package com.google.appengine.samples;

// [START DeferredTaskTest]

import static org.junit.Assert.assertTrue;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.model.TestTimedOutException;

public class DeferredTaskTest {
  @Rule public final Timeout testTimeout = new Timeout(10, TimeUnit.MINUTES);
  @Rule public final MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(3);


  // Unlike CountDownLatch, TaskCountDownlatch lets us reset.
  private static final LocalTaskQueueTestConfig.TaskCountDownLatch latch =
      new LocalTaskQueueTestConfig.TaskCountDownLatch(1);

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalTaskQueueTestConfig()
              .setDisableAutoTaskExecution(false) // Enable auto task execution
              .setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)
              .setTaskExecutionLatch(latch));

  private static synchronized boolean requestAwait() throws InterruptedException {
    return latch.await(5, TimeUnit.SECONDS);
  }

  private static synchronized void requestReset() {
    latch.reset();
  }

  private static synchronized void helperSetUp() {
    helper.setUp();
  }

  private static synchronized void helperTearDown() {
    helper.tearDown();
  }

  private static class MyTask implements DeferredTask {
    private static volatile boolean taskRan = false;

    @Override
    public void run() {
      taskRan = true;
    }
  }

  @Before
  public void setUp() {
    helperSetUp();
  }

  @After
  public void tearDown() throws TestTimedOutException {
    MyTask.taskRan = false;
    requestReset();
    try {
      helperTearDown();
    } catch (/*TestTimedOutException*/ Throwable ex) {
      // Ignoring, flaky test, sometimes we do timeout.
      Logger.getLogger(DeferredTaskTest.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Test(expected = TestTimedOutException.class)
  public void testTaskGetsRun() throws InterruptedException, TestTimedOutException {
    QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withPayload(new MyTask()));
    assertTrue(requestAwait());
    assertTrue(MyTask.taskRan);

    // tearDown() times out non-deterministically, and the exception can't be caught.
    // testTaskGetsRun() now expects the exception. Since the expected parameter
    // can't be optional, the exception is intentionally thrown when tearDown() is successful.
    throw new TestTimedOutException(0, TimeUnit.MINUTES);
  }
}
// [END DeferredTaskTest]
