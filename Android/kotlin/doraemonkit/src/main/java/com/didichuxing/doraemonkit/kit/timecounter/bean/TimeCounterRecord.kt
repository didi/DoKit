package com.didichuxing.doraemonkit.kit.timecounter.bean

/**
 * app和activity 耗时接口
 */
interface TimeCounterRecord {
    /**
     * 这条记录发起的时间
     */
    fun recordTime(): Long

    /**
     * 这条记录 title
     */
    fun title(): String

    /**
     * 总耗时
     */
    fun totalCost(): Long

    /**
     * 耗时详情
     */
    fun detail(): String
}

/**
 * activity耗时记录bean
 *
 * @param time 本条记录时间
 */
class ActivityTimeCounterRecord(val time: Long) : TimeCounterRecord {

    companion object {
        const val ACTIVITY_COST_FAST = 500
        const val ACTIVITY_COST_SLOW = 1000
    }

    var mPreviousActivity: String? = null
    var mCurrentActivity: String? = null

    var pauseStartTime = 0L
    var pauseEndTime = 0L
    var launchStartTime = 0L
    var launchEndTime = 0L
    var renderStartTime = 0L
    var renderEndTime = 0L

    override fun recordTime() = time

    override fun title() = "$mPreviousActivity->$mCurrentActivity"

    override fun totalCost() = renderEndTime - pauseStartTime
    override fun detail(): String {
        val stringBuilder = StringBuilder()
        val pauseCost = pauseEndTime - pauseStartTime
        val launchCost = launchEndTime - launchStartTime
        val renderCost = renderEndTime - renderStartTime
        val otherCost = totalCost() - pauseCost - launchCost - renderCost

        stringBuilder.append("Pause Cost: ${pauseCost}ms\r\n")
        stringBuilder.append("Launch Cost: ${launchCost}ms\r\n")
        stringBuilder.append("Render Cost: ${renderCost}ms\r\n")
        stringBuilder.append("Other Cost: ${otherCost}ms")
        return stringBuilder.toString()
    }
}

/**
 * app耗时记录bean
 */
class ApplicationTimeCounterRecord(private val time: Long) : TimeCounterRecord {
    var attachStartTime = 0L
    var attachEndTime = 0L
    var createStartTime = 0L
    var createEndTime = 0L

    /**
     * 数据收集完成
     */
    fun isFinish() = attachStartTime > 0 && attachEndTime > 0 && createStartTime > 0 && createEndTime > 0

    override fun recordTime(): Long = time

    override fun title(): String = "App Setup Cost"

    override fun totalCost() = (attachEndTime - attachStartTime) + (createEndTime - createStartTime)

    override fun detail(): String {
        val stringBuilder = StringBuilder()
        val attachCost = attachEndTime - attachStartTime
        val createCost = createEndTime - createStartTime

        stringBuilder.append("Attach Cost: ${attachCost}ms\r\n")
        stringBuilder.append("Create Cost: ${createCost}ms\r\n")
        return stringBuilder.toString()
    }
}