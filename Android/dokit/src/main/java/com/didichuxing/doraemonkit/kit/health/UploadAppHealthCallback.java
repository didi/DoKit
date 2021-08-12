package com.didichuxing.doraemonkit.kit.health;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-07-15:42
 * 描    述：
 * 修订历史：
 * ================================================
 */
public interface UploadAppHealthCallback {
    /**
     * @param response
     */
    void onSuccess(String response);

    /**
     * @param response
     */

    void onError(String response);
}
