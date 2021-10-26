package com.didichuxing.doraemondemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okio.ByteString.Companion.encodeUtf8
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Proxy
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {


    @Test
    fun toDex() {
        val DEX =
            "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TGFuZHJvaWQvdXRpbC9Mb2c7ABFMamF2YS9sYW5nL0NsYXNzOwAUTGphdmEvbGFuZy9DbGFzczwqPjsAEkxqYXZhL2xhbmcvT2JqZWN0OwASTGphdmEvbGFuZy9TdHJpbmc7ABJMamF2YS9sYW5nL1N5c3RlbTsAFUxqYXZhL2xhbmcvVGhyb3dhYmxlOwAaTGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZDsAJkxtZS93ZWlzaHUvZnJlZXJlZmxlY3Rpb24vQnVpbGRDb25maWc7ACVMbWUvd2Vpc2h1L3JlZmxlY3Rpb24vQm9vdHN0cmFwQ2xhc3M7ACFMbWUvd2Vpc2h1L3JlZmxlY3Rpb24vUmVmbGVjdGlvbjsAClJlZmxlY3Rpb24AD1JlZmxlY3Rpb24uamF2YQAHU0RLX0lOVAADVEFHAAFWAAxWRVJTSU9OX0NPREUADFZFUlNJT05fTkFNRQACVkwAAVoAAlpMABJbTGphdmEvbGFuZy9DbGFzczsAE1tMamF2YS9sYW5nL09iamVjdDsAE1tMamF2YS9sYW5nL1N0cmluZzsAB2NvbnRleHQAF2RhbHZpay5zeXN0ZW0uVk1SdW50aW1lAAFlAAZleGVtcHQACWV4ZW1wdEFsbAAHZm9yTmFtZQAPZnJlZS1yZWZsZWN0aW9uABJnZXRBcHBsaWNhdGlvbkluZm8AEWdldERlY2xhcmVkTWV0aG9kAApnZXRSdW50aW1lAAZpbnZva2UAC2xvYWRMaWJyYXJ5ABhtZS53ZWlzaHUuZnJlZXJlZmxlY3Rpb24ABm1ldGhvZAAHbWV0aG9kcwAZcmVmbGVjdCBib290c3RyYXAgZmFpbGVkOgAHcmVsZWFzZQAKc1ZtUnVudGltZQAWc2V0SGlkZGVuQXBpRXhlbXB0aW9ucwAQdGFyZ2V0U2RrVmVyc2lvbgAEdGhpcwAGdW5zZWFsAAx1bnNlYWxOYXRpdmUADnZtUnVudGltZUNsYXNzAAYABw4AFgAHDmr/AwEyCwEVEAMCNQvwBAREBhcBEg8DAzYLARsPqQUCBQMFBBkeAwAvCgAOAAcOACwBOgcOADYBOwcsnRriAQEDAC8KHgBIAAcOAA0ABw4AEwEtBx1yGWtaAAYXOBc8HxcABAEXAQEXBgEXHwYAAQACGQEZARkBGQEZARkGgYAEiAcDAAUACBoBCgEKB4iABKAHAYGABLQJAQnMCQGJAfQJAQnMCgEAAwALGgyBgAT4CgEJkAsBigIAAAAADgAAAAAAAAABAAAAAAAAAAEAAABEAAAAcAAAAAIAAAATAAAAgAEAAAMAAAALAAAAzAEAAAQAAAAMAAAAUAIAAAUAAAAPAAAAsAIAAAYAAAADAAAAKAMAAAEgAAAIAAAAiAMAAAEQAAAHAAAA3AUAAAIgAABEAAAAFgYAAAMgAAAIAAAAAwoAAAUgAAADAAAAZgoAAAAgAAADAAAAeAoAAAAQAAABAAAAyAoAAA=="
        val bytes = Base64.getDecoder().decode(DEX)
        val code: File = File("/Users/didi", "test.dex")

        FileOutputStream(code).use { fos -> fos.write(bytes) }
    }

    @Test
    @Throws(Exception::class)
    fun hex() {
//        val originKey =
//            "method=POST&path=/gateway&fragment=null&query={\"api\":\"lj.u.d.changeOnline\",\"appKey\":\"b4f945fe780140d8a0d19d1f2d021db7\"}&contentType=application/json; charset=utf-8&requestBody={\"type\":1.0}"

        val originKey =
            "method=GET&path=/test&fragment=null&query={}&contentType=jsona&requestBody={\"1\":\"111\",\"a\":\"aaa\",\"b\":\"bbb\",\"c\":\"ccc\",\"是\":\"是是是\"}"
        val hex = originKey.encodeUtf8().md5().hex()

        println("hex===>${hex}")


    }


    @Test
    fun testMapSort() {
        val maps = mapOf(
            "Nepal" to "Kathmandu",
            "India" to "New Delhi",
            "United States" to "Washington",
            "England" to "London",
            "Australia" to "Canberra",
            "bb" to "bbb",
            "aa" to "aaa",
            "222" to "222",
            "111" to "111"
        )

        val result = maps.toList().sortedBy { (key, _) -> key }.toMap()

        result.forEach { (key, value) ->
            println("key===>$key  value===>$value")
        }

    }

    @Test
    fun testJson() {
        val maps = mapOf(
            "Nepal" to "Kathmandu",
            "India" to "New Delhi",
            "United States" to "Washington",
            "England" to "London",
            "Australia" to "Canberra",
            "bb" to "bbb",
            "aa" to "aaa",
            "222" to "222",
            "111" to "111"
        )

        val result = maps.toList().sortedBy { (key, _) -> key }.toMap()

        result.forEach { (key, value) ->
            println("key===>$key  value===>$value")
        }

    }

    @Test
    fun urlEncode() {
        val json = "{\n" +
            "    \"data\": {\n" +
            "        \"curPage\": 1,\n" +
            "        \"datas\": [\n" +
            "            {\n" +
            "                \"apkLink\": \"\",\n" +
            "                \"audit\": 1,\n" +
            "                \"author\": \"\",\n" +
            "                \"canEdit\": false,\n" +
            "                \"chapterId\": 494,\n" +
            "                \"chapterName\": \"广场\",\n" +
            "                \"collect\": false,\n" +
            "                \"courseId\": 13,\n" +
            "                \"desc\": \"\",\n" +
            "                \"descMd\": \"\",\n" +
            "                \"envelopePic\": \"\",\n" +
            "                \"fresh\": false,\n" +
            "                \"host\": \"\",\n" +
            "                \"id\": 18661,\n" +
            "                \"link\": \"https://juejin.cn/post/6973900070358319135\",\n" +
            "                \"niceDate\": \"2天前\",\n" +
            "                \"niceShareDate\": \"2天前\",\n" +
            "                \"origin\": \"\",\n" +
            "                \"prefix\": \"\",\n" +
            "                \"projectLink\": \"\",\n" +
            "                \"publishTime\": 1624099393000,\n" +
            "                \"realSuperChapterId\": 493,\n" +
            "                \"selfVisible\": 0,\n" +
            "                \"shareDate\": 1624099393000,\n" +
            "                \"shareUser\": \"鸿洋\",\n" +
            "                \"superChapterId\": 494,\n" +
            "                \"superChapterName\": \"广场Tab\",\n" +
            "                \"tags\": [],\n" +
            "                \"title\": \"源码篇：Flutter Bloc背后的思想，一篇纠结的文章\",\n" +
            "                \"type\": 0,\n" +
            "                \"userId\": 2,\n" +
            "                \"visible\": 0,\n" +
            "                \"zan\": 0\n" +
            "            },\n" +
            "            {\n" +
            "                \"apkLink\": \"\",\n" +
            "                \"audit\": 1,\n" +
            "                \"author\": \"\",\n" +
            "                \"canEdit\": false,\n" +
            "                \"chapterId\": 494,\n" +
            "                \"chapterName\": \"广场\",\n" +
            "                \"collect\": false,\n" +
            "                \"courseId\": 13,\n" +
            "                \"desc\": \"\",\n" +
            "                \"descMd\": \"\",\n" +
            "                \"envelopePic\": \"\",\n" +
            "                \"fresh\": false,\n" +
            "                \"host\": \"\",\n" +
            "                \"id\": 18660,\n" +
            "                \"link\": \"https://juejin.cn/post/6974734070999679013\",\n" +
            "                \"niceDate\": \"2天前\",\n" +
            "                \"niceShareDate\": \"2天前\",\n" +
            "                \"origin\": \"\",\n" +
            "                \"prefix\": \"\",\n" +
            "                \"projectLink\": \"\",\n" +
            "                \"publishTime\": 1624090904000,\n" +
            "                \"realSuperChapterId\": 493,\n" +
            "                \"selfVisible\": 0,\n" +
            "                \"shareDate\": 1624090904000,\n" +
            "                \"shareUser\": \"goweii\",\n" +
            "                \"superChapterId\": 494,\n" +
            "                \"superChapterName\": \"广场Tab\",\n" +
            "                \"tags\": [],\n" +
            "                \"title\": \"我又开发了一个非常好用的开源库，调试Android数据库有救了\",\n" +
            "                \"type\": 0,\n" +
            "                \"userId\": 20382,\n" +
            "                \"visible\": 0,\n" +
            "                \"zan\": 0\n" +
            "            }\n" +
            "        ],\n" +
            "        \"offset\": 0,\n" +
            "        \"over\": false,\n" +
            "        \"pageCount\": 154,\n" +
            "        \"size\": 20,\n" +
            "        \"total\": 3063\n" +
            "    },\n" +
            "    \"errorCode\": 0,\n" +
            "    \"errorMsg\": \"\"\n" +
            "}"


        val encode = Base64.getEncoder().encodeToString(json.toByteArray())

        println(encode)
        val base64 =
            "ewogICAgImRhdGEiOiB7CiAgICAgICAgImN1clBhZ2UiOiAxLAogICAgICAgICJkYXRhcyI6IFsKICAgICAgICAgICAgewogICAgICAgICAgICAgICAgImFwa0xpbmsiOiAiIiwKICAgICAgICAgICAgICAgICJhdWRpdCI6IDEsCiAgICAgICAgICAgICAgICAiYXV0aG9yIjogIiIsCiAgICAgICAgICAgICAgICAiY2FuRWRpdCI6IGZhbHNlLAogICAgICAgICAgICAgICAgImNoYXB0ZXJJZCI6IDQ5NCwKICAgICAgICAgICAgICAgICJjaGFwdGVyTmFtZSI6ICLlub/lnLoiLAogICAgICAgICAgICAgICAgImNvbGxlY3QiOiBmYWxzZSwKICAgICAgICAgICAgICAgICJjb3Vyc2VJZCI6IDEzLAogICAgICAgICAgICAgICAgImRlc2MiOiAiIiwKICAgICAgICAgICAgICAgICJkZXNjTWQiOiAiIiwKICAgICAgICAgICAgICAgICJlbnZlbG9wZVBpYyI6ICIiLAogICAgICAgICAgICAgICAgImZyZXNoIjogZmFsc2UsCiAgICAgICAgICAgICAgICAiaG9zdCI6ICIiLAogICAgICAgICAgICAgICAgImlkIjogMTg2NjEsCiAgICAgICAgICAgICAgICAibGluayI6ICJodHRwczovL2p1ZWppbi5jbi9wb3N0LzY5NzM5MDAwNzAzNTgzMTkxMzUiLAogICAgICAgICAgICAgICAgIm5pY2VEYXRlIjogIjLlpKnliY0iLAogICAgICAgICAgICAgICAgIm5pY2VTaGFyZURhdGUiOiAiMuWkqeWJjSIsCiAgICAgICAgICAgICAgICAib3JpZ2luIjogIiIsCiAgICAgICAgICAgICAgICAicHJlZml4IjogIiIsCiAgICAgICAgICAgICAgICAicHJvamVjdExpbmsiOiAiIiwKICAgICAgICAgICAgICAgICJwdWJsaXNoVGltZSI6IDE2MjQwOTkzOTMwMDAsCiAgICAgICAgICAgICAgICAicmVhbFN1cGVyQ2hhcHRlcklkIjogNDkzLAogICAgICAgICAgICAgICAgInNlbGZWaXNpYmxlIjogMCwKICAgICAgICAgICAgICAgICJzaGFyZURhdGUiOiAxNjI0MDk5MzkzMDAwLAogICAgICAgICAgICAgICAgInNoYXJlVXNlciI6ICLpuL/mtIsiLAogICAgICAgICAgICAgICAgInN1cGVyQ2hhcHRlcklkIjogNDk0LAogICAgICAgICAgICAgICAgInN1cGVyQ2hhcHRlck5hbWUiOiAi5bm/5Zy6VGFiIiwKICAgICAgICAgICAgICAgICJ0YWdzIjogW10sCiAgICAgICAgICAgICAgICAidGl0bGUiOiAi5rqQ56CB56+H77yaRmx1dHRlciBCbG9j6IOM5ZCO55qE5oCd5oOz77yM5LiA56+H57qg57uT55qE5paH56ugIiwKICAgICAgICAgICAgICAgICJ0eXBlIjogMCwKICAgICAgICAgICAgICAgICJ1c2VySWQiOiAyLAogICAgICAgICAgICAgICAgInZpc2libGUiOiAwLAogICAgICAgICAgICAgICAgInphbiI6IDAKICAgICAgICAgICAgfSwKICAgICAgICAgICAgewogICAgICAgICAgICAgICAgImFwa0xpbmsiOiAiIiwKICAgICAgICAgICAgICAgICJhdWRpdCI6IDEsCiAgICAgICAgICAgICAgICAiYXV0aG9yIjogIiIsCiAgICAgICAgICAgICAgICAiY2FuRWRpdCI6IGZhbHNlLAogICAgICAgICAgICAgICAgImNoYXB0ZXJJZCI6IDQ5NCwKICAgICAgICAgICAgICAgICJjaGFwdGVyTmFtZSI6ICLlub/lnLoiLAogICAgICAgICAgICAgICAgImNvbGxlY3QiOiBmYWxzZSwKICAgICAgICAgICAgICAgICJjb3Vyc2VJZCI6IDEzLAogICAgICAgICAgICAgICAgImRlc2MiOiAiIiwKICAgICAgICAgICAgICAgICJkZXNjTWQiOiAiIiwKICAgICAgICAgICAgICAgICJlbnZlbG9wZVBpYyI6ICIiLAogICAgICAgICAgICAgICAgImZyZXNoIjogZmFsc2UsCiAgICAgICAgICAgICAgICAiaG9zdCI6ICIiLAogICAgICAgICAgICAgICAgImlkIjogMTg2NjAsCiAgICAgICAgICAgICAgICAibGluayI6ICJodHRwczovL2p1ZWppbi5jbi9wb3N0LzY5NzQ3MzQwNzA5OTk2NzkwMTMiLAogICAgICAgICAgICAgICAgIm5pY2VEYXRlIjogIjLlpKnliY0iLAogICAgICAgICAgICAgICAgIm5pY2VTaGFyZURhdGUiOiAiMuWkqeWJjSIsCiAgICAgICAgICAgICAgICAib3JpZ2luIjogIiIsCiAgICAgICAgICAgICAgICAicHJlZml4IjogIiIsCiAgICAgICAgICAgICAgICAicHJvamVjdExpbmsiOiAiIiwKICAgICAgICAgICAgICAgICJwdWJsaXNoVGltZSI6IDE2MjQwOTA5MDQwMDAsCiAgICAgICAgICAgICAgICAicmVhbFN1cGVyQ2hhcHRlcklkIjogNDkzLAogICAgICAgICAgICAgICAgInNlbGZWaXNpYmxlIjogMCwKICAgICAgICAgICAgICAgICJzaGFyZURhdGUiOiAxNjI0MDkwOTA0MDAwLAogICAgICAgICAgICAgICAgInNoYXJlVXNlciI6ICJnb3dlaWkiLAogICAgICAgICAgICAgICAgInN1cGVyQ2hhcHRlcklkIjogNDk0LAogICAgICAgICAgICAgICAgInN1cGVyQ2hhcHRlck5hbWUiOiAi5bm/5Zy6VGFiIiwKICAgICAgICAgICAgICAgICJ0YWdzIjogW10sCiAgICAgICAgICAgICAgICAidGl0bGUiOiAi5oiR5Y+I5byA5Y+R5LqG5LiA5Liq6Z2e5bi45aW955So55qE5byA5rqQ5bqT77yM6LCD6K+VQW5kcm9pZOaVsOaNruW6k+acieaVkeS6hiIsCiAgICAgICAgICAgICAgICAidHlwZSI6IDAsCiAgICAgICAgICAgICAgICAidXNlcklkIjogMjAzODIsCiAgICAgICAgICAgICAgICAidmlzaWJsZSI6IDAsCiAgICAgICAgICAgICAgICAiemFuIjogMAogICAgICAgICAgICB9CiAgICAgICAgXSwKICAgICAgICAib2Zmc2V0IjogMCwKICAgICAgICAib3ZlciI6IGZhbHNlLAogICAgICAgICJwYWdlQ291bnQiOiAxNTQsCiAgICAgICAgInNpemUiOiAyMCwKICAgICAgICAidG90YWwiOiAzMDYzCiAgICB9LAogICAgImVycm9yQ29kZSI6IDAsCiAgICAiZXJyb3JNc2ciOiAiIgp9"
        val decode = String(Base64.getDecoder().decode(base64))
        println(decode)

//        val encode = EncodeUtils.binaryEncode(aa)
//        val binary = "1111011 100010 1100001 100010 111010 100010 1100001 1100001 100010 101100 100010 1100010 100010 111010 100010 1100010 1100010 100010 1111101"
//        val decode = EncodeUtils.binaryDecode(binary)
//        println(encode)
//        println(decode)

    }


}

