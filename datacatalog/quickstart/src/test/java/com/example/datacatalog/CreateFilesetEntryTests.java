/*
 * Copyright 2019 Google LLC
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

package com.example.datacatalog;

import static org.junit.Assert.fail;

import com.google.cloud.datacatalog.v1.DataCatalogClient;
import com.google.cloud.datacatalog.v1.EntryGroupName;
import com.google.cloud.datacatalog.v1.EntryName;
import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Integration (system) tests for {@link CreateFilesetEntry}. */
@RunWith(JUnit4.class)
public class CreateFilesetEntryTests {
  @Rule
  public final MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(3);

  private ByteArrayOutputStream bout;

  private static String LOCATION = "us-central1";
  private static String PROJECT_ID = System.getenv().get("GOOGLE_CLOUD_PROJECT");

  private static List<String> entryGroupsPendingDeletion = new ArrayList<>();
  private static List<String> entriesPendingDeletion = new ArrayList<>();

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bout));
  }

  @After
  public void tearDown() {
    System.setOut(null);
    bout.reset();
  }

  @AfterClass
  public static void tearDownClass() {
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {
      // Must delete Entries before deleting the Entry Group.
      if (entriesPendingDeletion.isEmpty() || entryGroupsPendingDeletion.isEmpty()) {
        fail("Something went wrong, no entries were generated");
      }

      for (String entryName : entriesPendingDeletion) {
        dataCatalogClient.deleteEntry(entryName);
      }

      for (String entryGroupName : entryGroupsPendingDeletion) {
        dataCatalogClient.deleteEntryGroup(entryGroupName);
      }
    } catch (Exception e) {
      System.out.println("Error in cleaning up test data:\n" + e.toString());
    }
  }

  @Test
  public void testCreateEntryQuickStart() {
    String entryGroupId = "fileset_entry_group_parent_" + getUuid8Chars();
    String entryId = "fileset_entry_id_" + getUuid8Chars();

    CreateFilesetEntry.createEntry(PROJECT_ID, entryGroupId, entryId);

    // Store names for clean up on teardown
    String expectedEntryGroupName =
        EntryGroupName.of(PROJECT_ID, LOCATION, entryGroupId).toString();
    entryGroupsPendingDeletion.add(expectedEntryGroupName);

    String expectedEntryName = EntryName.of(PROJECT_ID, LOCATION, entryGroupId, entryId).toString();
    entriesPendingDeletion.add(expectedEntryName);

    String output = bout.toString();

    String entryTemplate = "Entry created with name: %s";
    String entryGroupTemplate = "Entry Group created with name: %s";
    MatcherAssert.assertThat(output,
        CoreMatchers.containsString(String.format(entryGroupTemplate, expectedEntryGroupName)));
    MatcherAssert.assertThat(output,
        CoreMatchers.containsString(String.format(entryTemplate, expectedEntryName)));
  }

  private String getUuid8Chars() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
