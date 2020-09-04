package com.didichuxing.doraemonkit.kit.toolpanel.bean;

import com.didichuxing.doraemonkit.widget.brvah.entity.JSectionEntity;

import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/21-15:10
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MorePageGroupBean {

    /**
     * success : true
     * data : {"group":[{"group":"本地功能","list":[{"name":"功能管理","desc":"本地功能管理","link":"dokit://native/function_manager","type":"native"}]},{"group":"精品推荐","list":[{"name":"hummer","desc":"新一代跨端方案","link":"https://m.baidu.com","type":"web"}]},{"group":"官方消息","list":[{"name":"dokit操作手册","desc":"dokit操作手册","link":"http://xingyun.xiaojukeji.com/docs/dokit/#/intro","type":"web"}]}]}
     * code : 200
     */

    private boolean success;
    private DataBean data;
    private int code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class DataBean {
        private List<GroupBean> group;

        public List<GroupBean> getGroup() {
            return group;
        }

        public void setGroup(List<GroupBean> group) {
            this.group = group;
        }

        public static class GroupBean {
            /**
             * group : 本地功能
             * list : [{"name":"功能管理","desc":"本地功能管理","link":"dokit://native/function_manager","type":"native"}]
             */

            private String group;
            private List<ListBean> list;

            public String getGroup() {
                return group;
            }

            public void setGroup(String group) {
                this.group = group;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }


            public static class ListBean extends JSectionEntity {
                /**
                 * name : 功能管理
                 * desc : 本地功能管理
                 * link : dokit://native/function_manager
                 * type : native
                 */

                private String name;
                private String desc;
                private String link;
                private String type;
                private boolean header;

                public void setHeader(boolean header) {
                    this.header = header;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getDesc() {
                    return desc;
                }

                public void setDesc(String desc) {
                    this.desc = desc;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                @Override
                public boolean isHeader() {
                    return header;
                }
            }
        }
    }
}
