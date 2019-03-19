/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.reflection;

import groovy.lang.MetaClass;

public class ClassInfo {

    ClassInfo(Class klazz) {
    }

    public int getVersion() {
        return 0;
    }

    public void incVersion() {
    }

    public static ClassInfo getClassInfo(Class clazz) {
        return new ClassInfo(clazz);
    }

    public CachedClass getCachedClass() {
        return null;
    }

    public MetaClass getMetaClass() {
        return null;
    }
}
