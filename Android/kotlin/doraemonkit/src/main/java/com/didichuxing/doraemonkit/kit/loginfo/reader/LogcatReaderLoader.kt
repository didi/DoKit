package com.didichuxing.doraemonkit.kit.loginfo.reader

import android.os.Parcel
import android.os.Parcelable
import com.didichuxing.doraemonkit.kit.loginfo.LogcatReader
import com.didichuxing.doraemonkit.kit.loginfo.helper.LogcatHelper
import java.io.IOException

/**
 * @author lostjobs created on 2020/6/28
 */
class LogcatReaderLoader(private val lastLines: Map<String, String?> = HashMap(),
                         private var recordingMode: Boolean = false,
                         private var multiple: Boolean = false) : Parcelable {


    companion object CREATOR : Parcelable.Creator<LogcatReaderLoader> {
        override fun createFromParcel(parcel: Parcel): LogcatReaderLoader {
            return LogcatReaderLoader(parcel)
        }

        override fun newArray(size: Int): Array<LogcatReaderLoader?> {
            return arrayOfNulls(size)
        }

        fun create(recordingMode: Boolean): LogcatReaderLoader {
            val buffers = mutableListOf<String>()
            buffers.add("main")
            return LogcatReaderLoader(buffers, recordingMode)
        }
    }

    constructor(parcel: Parcel) : this(
            parcel.readBundle(LogcatReaderLoader::class.java.classLoader)?.let { bundle -> bundle.keySet().map { it to bundle[it].toString() }.toMap() }
                    ?: emptyMap(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte())

    constructor(buffers: List<String>, recordingMode: Boolean) : this(buffers.map { it to if (recordingMode) LogcatHelper.getLastLogLine(it) else "" }.toMap(), recordingMode, buffers.size > 1)


    @Throws(IOException::class)
    fun loadReader(): LogcatReader {
        return SingleLogcatReader(recordingMode = recordingMode, logBuffer = lastLines.keys.last(), lastLine = lastLines.values.last())
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (recordingMode) 1 else 0)
        parcel.writeByte(if (multiple) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

}