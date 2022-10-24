/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.lucas.search;

import com.arangodb.*;
import com.arangodb.entity.BaseDocument;
import com.arangodb.mapping.ArangoJack;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.util.MapBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mark Vollmary
 */
public class Database {

  private static final String DB_NAME = "lucasdb";
  protected static final String COLLECTION_NAME = "compras";

  private static ArangoDB arangoDB;
  protected static ArangoDatabase db;
  protected static ArangoCollection collection;
  private static Database dbIsntance;

  private Database(){
    setUp();
  }

  public static Database getInstance(){
    if (dbIsntance == null){
      dbIsntance = new Database();
    }
    return dbIsntance;
  }

  static void setUp() {
    arangoDB = new ArangoDB.Builder().user("root").password("root").serializer(new ArangoJack()).build();
    if (arangoDB.db(DB_NAME).exists())
      arangoDB.db(DB_NAME).drop();
    arangoDB.createDatabase(DB_NAME);
    db = arangoDB.db(DB_NAME);
    db.createCollection(COLLECTION_NAME);
    collection = db.collection(COLLECTION_NAME);
  }

  static void tearDown() {
    db.drop();
    arangoDB.shutdown();
  }

  public String getItemsCollection(String collectionName) {
    // AQL query exemplo
    String res = "";
    try {
      final String query = "FOR a IN "+collectionName.trim()+" RETURN a";

      ArangoCursor<BaseDocument> cursor = db.query(
        query,
        new HashMap<>(),
        null,
        BaseDocument.class);

      for (; cursor.hasNext(); ) {
        System.err.println(cursor.next().getKey().toString());
        res += "Chave" + cursor.next().getKey().toString() +" | Valor: " + cursor.next().getKey().toString() + "|| ";
      }

    } catch (final ArangoDBException e) {
      System.err.println("Falha ao executar query." + e.getMessage());
    }
    System.out.println(res);
    return res;
  }
}
