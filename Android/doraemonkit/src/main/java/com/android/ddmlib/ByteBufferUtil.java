/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ddmlib;
import android.support.annotation.NonNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
public class ByteBufferUtil {
  @NonNull
  public static ByteBuffer mapFile(@NonNull File f, long offset, @NonNull ByteOrder byteOrder) throws IOException {
    FileInputStream dataFile = new FileInputStream(f);
    try {
      FileChannel fc = dataFile.getChannel();
      MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, offset, f.length() - offset);
      buffer.order(byteOrder);
      return buffer;
    } finally {
      dataFile.close(); // this *also* closes the associated channel, fc
    }
  }
  @NonNull
  public static String getString(@NonNull ByteBuffer buf, int len) {
      char[] data = new char[len];
      for (int i = 0; i < len; i++)
          data[i] = buf.getChar();
      return new String(data);
  }
  public static void putString(@NonNull ByteBuffer buf, @NonNull String str) {
      int len = str.length();
      for (int i = 0; i < len; i++)
          buf.putChar(str.charAt(i));
  }
}