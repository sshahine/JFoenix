/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

module com.jfoenix{
    requires javafx.controls;
    requires java.logging;
    requires java.xml;
    requires jdk.unsupported;

    exports com.jfoenix.controls;
    exports com.jfoenix.effects;
    exports com.jfoenix.responsive;
    exports com.jfoenix.skins;
    exports com.jfoenix.svg;
    exports com.jfoenix.validation;
    exports com.jfoenix.validation.base;
    exports com.jfoenix.transitions;
    exports com.jfoenix.animation;
    exports com.jfoenix.transitions.hamburger;
    exports com.jfoenix.concurrency;

    exports com.jfoenix.controls.datamodels.treetable;
    exports com.jfoenix.controls.events;
    exports com.jfoenix.controls.cells.editors;
    exports com.jfoenix.controls.cells.editors.base;
    exports com.jfoenix.animation.alert;
}
