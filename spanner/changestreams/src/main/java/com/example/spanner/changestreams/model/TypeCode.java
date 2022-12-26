/*
 * Copyright 2022 Google LLC
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

package com.example.spanner.changestreams.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a type of a column within Cloud Spanner. The type itself is encoded in a String code.
 */
public class TypeCode implements Serializable {

  private static final long serialVersionUID = -1935648338090036611L;

  private String code;

  /** Default constructor for serialization only. */
  private TypeCode() {}

  /**
   * Constructs a type code from the given String code.
   *
   * @param code the code of the column type
   */
  public TypeCode(String code) {
    this.code = code;
  }

  /**
   * Returns the type code of the column.
   *
   * @return the type code of the column
   */
  public String getCode() {
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TypeCode typeCode = (TypeCode) o;
    return Objects.equals(code, typeCode.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  @Override
  public String toString() {
    return "TypeCode{" + "code='" + code + '\'' + '}';
  }
}
