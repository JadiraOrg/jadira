/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.phonenumber;

import org.jadira.cdt.phonenumber.impl.E164PhoneNumberWithExtension;
import org.jadira.usertype.phonenumber.columnmapper.StringColumnE164PhoneNumberWithExtensionMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;

/**
 * Maps {@link E164PhoneNumberWithExtension} to and from a String column
 */
public class PersistentE164PhoneNumberWithExtension extends AbstractSingleColumnUserType<E164PhoneNumberWithExtension, String, StringColumnE164PhoneNumberWithExtensionMapper> {

	private static final long serialVersionUID = -1951216050297163695L;
}
