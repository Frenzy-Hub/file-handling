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

package com.example.translate;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for Translate Text With Glossary sample. */
@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class TranslateTextWithGlossaryTests {
  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");
  private static final String GLOSSARY_INPUT_URI =
      "gs://cloud-samples-data/translation/glossary_ja.csv";
  private static final String GLOSSARY_ID = "DO_NOT_DELETE_TEST_GLOSSARY";

  private ByteArrayOutputStream bout;
  private PrintStream out;
  private PrintStream originalPrintStream;

  private static void requireEnvVar(String varName) {
    assertNotNull(
        "Environment variable '%s' is required to perform these tests.".format(varName),
        System.getenv(varName));
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_APPLICATION_CREDENTIALS");
    requireEnvVar("GOOGLE_CLOUD_PROJECT");
  }

  @Before
  public void setUp() {
    PrintStream temp = new PrintStream(new ByteArrayOutputStream());
    System.setOut(temp);

    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    originalPrintStream = System.out;
    System.setOut(out);
  }

  @After
  public void tearDown() {
    // restores print statements in the original method
    System.out.flush();
    System.setOut(originalPrintStream);
  }

  @Test
  public void testTranslateTextWithGlossary() throws IOException {
    TranslateTextWithGlossary.translateTextWithGlossary(
        PROJECT_ID, "en", "ja", "account", GLOSSARY_ID);
    String got = bout.toString();
    assertThat(got).contains("???????????????");
  }
}
