/*
 * Copyright 2016 jeasonlzy(廖子尧)
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
package com.didichuxing.doraemonkit.okgo.cookie;

import android.content.ContentValues;
import android.database.Cursor;

import com.didichuxing.doraemonkit.okgo.utils.OkLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

import okhttp3.Cookie;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 6374381323722046732L;

    public static final String HOST = "host";
    public static final String NAME = "name";
    public static final String DOMAIN = "domain";
    public static final String COOKIE = "cookie";

    public String host;
    public String name;
    public String domain;
    private transient Cookie cookie;
    private transient Cookie clientCookie;

    public SerializableCookie(String host, Cookie cookie) {
        this.cookie = cookie;
        this.host = host;
        this.name = cookie.name();
        this.domain = cookie.domain();
    }

    public Cookie getCookie() {
        Cookie bestCookie = cookie;
        if (clientCookie != null) {
            bestCookie = clientCookie;
        }
        return bestCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(cookie.name());
        out.writeObject(cookie.value());
        out.writeLong(cookie.expiresAt());
        out.writeObject(cookie.domain());
        out.writeObject(cookie.path());
        out.writeBoolean(cookie.secure());
        out.writeBoolean(cookie.httpOnly());
        out.writeBoolean(cookie.hostOnly());
        out.writeBoolean(cookie.persistent());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        long expiresAt = in.readLong();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();
        boolean secure = in.readBoolean();
        boolean httpOnly = in.readBoolean();
        boolean hostOnly = in.readBoolean();
        boolean persistent = in.readBoolean();
        Cookie.Builder builder = new Cookie.Builder();
        builder = builder.name(name);
        builder = builder.value(value);
        builder = builder.expiresAt(expiresAt);
        builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
        builder = builder.path(path);
        builder = secure ? builder.secure() : builder;
        builder = httpOnly ? builder.httpOnly() : builder;
        clientCookie = builder.build();
    }

    public static SerializableCookie parseCursorToBean(Cursor cursor) {
        String host = cursor.getString(cursor.getColumnIndex(HOST));
        byte[] cookieBytes = cursor.getBlob(cursor.getColumnIndex(COOKIE));
        Cookie cookie = bytesToCookie(cookieBytes);
        return new SerializableCookie(host, cookie);
    }

    public static ContentValues getContentValues(SerializableCookie serializableCookie) {
        ContentValues values = new ContentValues();
        values.put(SerializableCookie.HOST, serializableCookie.host);
        values.put(SerializableCookie.NAME, serializableCookie.name);
        values.put(SerializableCookie.DOMAIN, serializableCookie.domain);
        values.put(SerializableCookie.COOKIE, cookieToBytes(serializableCookie.host, serializableCookie.getCookie()));
        return values;
    }

    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化
     * @return 序列化之后的string
     */
    public static String encodeCookie(String host, Cookie cookie) {
        if (cookie == null) return null;
        byte[] cookieBytes = cookieToBytes(host, cookie);
        return byteArrayToHexString(cookieBytes);
    }

    public static byte[] cookieToBytes(String host, Cookie cookie) {
        SerializableCookie serializableCookie = new SerializableCookie(host, cookie);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(serializableCookie);
        } catch (IOException e) {
            OkLogger.printStackTrace(e);
            return null;
        }
        return os.toByteArray();
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    public static Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        return bytesToCookie(bytes);
    }

    public static Cookie bytesToCookie(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie) objectInputStream.readObject()).getCookie();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
        return cookie;
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /** host, name, domain 标识一个cookie是否唯一 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableCookie that = (SerializableCookie) o;

        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return domain != null ? domain.equals(that.domain) : that.domain == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }
}
