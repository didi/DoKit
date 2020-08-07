package com.didichuxing.doraemonkit.plugin

import com.android.build.gradle.internal.pipeline.TransformTask
import com.didiglobal.booster.kotlinx.call
import com.didiglobal.booster.kotlinx.get
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionAdapter

/**
 * @author neighbWang
 */
class DoKitTransformTaskExecutionListener(private val project: Project) : TaskExecutionAdapter() {

    override fun beforeExecute(task: Task) {
        task.takeIf {
            it.project == project && it is TransformTask && it.transform.scopes.isNotEmpty()
        }?.run {
            task["outputStream"]?.call<Unit>("init")
        }
    }

}