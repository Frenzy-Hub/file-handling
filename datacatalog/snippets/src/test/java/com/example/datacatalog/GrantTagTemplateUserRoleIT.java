/*
 * Copyright 2020 Google Inc.
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

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import com.google.cloud.datacatalog.v1.CreateTagTemplateRequest;
import com.google.cloud.datacatalog.v1.DataCatalogClient;
import com.google.cloud.datacatalog.v1.DeleteTagTemplateRequest;
import com.google.cloud.datacatalog.v1.FieldType;
import com.google.cloud.datacatalog.v1.LocationName;
import com.google.cloud.datacatalog.v1.TagTemplate;
import com.google.cloud.datacatalog.v1.TagTemplateField;
import com.google.cloud.datacatalog.v1.TagTemplateName;
import com.google.cloud.testing.junit4.MultipleAttemptsRule;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class GrantTagTemplateUserRoleIT {
  @Rule
  public final MultipleAttemptsRule multipleAttemptsRule = new MultipleAttemptsRule(3);

  private static final String ID = UUID.randomUUID().toString().substring(0, 8);
  private static final String LOCATION = "us-central1";
  private final Logger log = Logger.getLogger(this.getClass().getName());
  private String tagTemplateId;
  private ByteArrayOutputStream bout;
  private PrintStream out;
  private PrintStream originalPrintStream;

  private static final String PROJECT_ID = requireEnvVar("GOOGLE_CLOUD_PROJECT");

  private static String requireEnvVar(String varName) {
    String value = System.getenv(varName);
    assertNotNull("Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
    return value;
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT");
  }

  @Before
  public void setUp() throws IOException {
    tagTemplateId = "create_tag_template_id_test_" + ID;
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    originalPrintStream = System.out;
    System.setOut(out);
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {
      LocationName parent = LocationName.of(PROJECT_ID, LOCATION);
      TagTemplateField sourceField =
          TagTemplateField.newBuilder().setDisplayName("Source of data asset")
              .setType(
                  FieldType.newBuilder().setPrimitiveType(FieldType.PrimitiveType.STRING).build())
              .build();
      TagTemplate tagTemplate = TagTemplate.newBuilder().setDisplayName("Demo Tag Template")
          .putFields("source", sourceField).build();
      CreateTagTemplateRequest request =
          CreateTagTemplateRequest.newBuilder().setParent(parent.toString())
              .setTagTemplateId(tagTemplateId).setTagTemplate(tagTemplate).build();
      dataCatalogClient.createTagTemplate(request);
    }
  }

  @After
  public void tearDown() throws IOException {
    // Clean up
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {
      TagTemplateName name = TagTemplateName.of(PROJECT_ID, LOCATION, tagTemplateId);
      boolean force = true;
      DeleteTagTemplateRequest request =
          DeleteTagTemplateRequest.newBuilder().setName(name.toString()).setForce(force).build();
      dataCatalogClient.deleteTagTemplate(request);
    }
    // restores print statements in the original method
    System.out.flush();
    System.setOut(originalPrintStream);
    log.log(Level.INFO, bout.toString());
  }

  @Test
  public void testGrantTagTemplateUserRole() throws IOException {
    GrantTagTemplateUserRole.grantTagTemplateUserRole(PROJECT_ID, tagTemplateId);
    assertThat(bout.toString()).contains("Role successfully granted");
  }
}
