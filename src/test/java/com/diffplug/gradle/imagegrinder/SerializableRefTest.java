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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import org.junit.Test;

public class SerializableRefTest {
	@Test
	public void testBehavior() {
		assertThat(SerializableRef.valueMap).isEmpty();
		// object identity as expected
		Object a = new Object();
		Object b = new Object();
		assertThat(a).isSameAs(a);
		assertThat(a).isNotSameAs(b);

		// create two refs of the same objects, and the refs will not be equal in the same way
		SerializableRef<Object> a1 = SerializableRef.create(a);
		SerializableRef<Object> a2 = SerializableRef.create(a);
		assertThat(a1).isNotSameAs(a2);
		assertThat(a1).isNotEqualTo(a2);

		// when you roundtrip that ref through serialization...
		SerializableRef<Object> a1clone = roundtrip(a1);
		// the ref is not the same
		assertThat(a1).isNotSameAs(a1clone);
		// but the underlying object is
		assertThat(a).isSameAs(a1clone.value());

		// to make sure it doesn't get gc'ed
		a.hashCode();
		a2.value();
		assertThat(SerializableRef.valueMap).isEmpty();
	}

	@SuppressWarnings("unchecked")
	static <T extends Serializable> T roundtrip(T object) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		SerializableMisc.toStream(object, output);
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		return (T) SerializableMisc.fromStream(object.getClass(), input);
	}
}
