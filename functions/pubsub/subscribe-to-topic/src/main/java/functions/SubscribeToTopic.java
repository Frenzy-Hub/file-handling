/*
 * Copyright 2020 Google LLC
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

package functions;

// [START functions_pubsub_subscribe]

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import functions.eventpojos.PubsubMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class SubscribeToTopic implements BackgroundFunction<PubsubMessage> {
  private static final Logger logger = Logger.getLogger(SubscribeToTopic.class.getName());

  @Override
  public void accept(PubsubMessage message, Context context) {
    if (message.getData() == null) {
      logger.info("No message provided");
      return;
    }

    String messageString = new String(
        Base64.getDecoder().decode(message.getData().getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8);
    logger.info(messageString);
  }
}
// [END functions_pubsub_subscribe]
