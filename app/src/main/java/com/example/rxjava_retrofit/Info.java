package com.example.rxjava_retrofit;

/**
 * @创建者 mingyan.su
 * @创建时间 2019/6/4 17:58
 * @类描述 ${TODO}
 */
public class Info {

    /**
     * id : 1
     * unique_id : 15033024077728990250
     * title : 第一条
     * resource : http://image.coolapk.com/picture/2017/0821/535189_1503299292_8488.png
     */

    private String id;
    private String unique_id;
    private String title;
    private String resource;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
