/*
 * Copyright 2017 DiffPlug
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
 */
package com.diffplug.gradle.imagegrinder;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ref which makes it possible to pass references
 * through a serializable roundtrip.  Value can only
 * be read once per ref, across all serialized copies.
 */
public class SerializableRef<T> implements Serializable {
	private static final long serialVersionUID = -5868935924393148402L;

	private final int key;

	private SerializableRef(int key) {
		this.key = key;
	}

	public static <T> SerializableRef<T> create(T value) {
		Objects.requireNonNull(value);
		SerializableRef<T> ref = new SerializableRef<>(count.incrementAndGet());
		valueMap.put(ref.key, new WeakReference<>(value));
		return ref;
	}

	@SuppressWarnings("unchecked")
	public T value() {
		WeakReference<Object> ref = valueMap.remove(key);
		Objects.requireNonNull(ref, "You can only read the value from a ref once.");
		return (T) Objects.requireNonNull(ref.get(), "Object was gc'ed earlier than we expected");
	}

	private static final AtomicInteger count = new AtomicInteger();
	static final Map<Integer, WeakReference<Object>> valueMap = new HashMap<>();

}
