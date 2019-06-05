package com.example.rxjava_retrofit;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @创建者 mingyan.su
 * @创建时间 2019/5/31 17:44
 * @类描述 ${TODO}步骤四：创建网络接口类(封装Url地址和网络数据请求)
 */
public interface Api {
    //这里特别说明Url的组成，retrofit把网络请求的Url分成两部分设置：第一部分在创建Retrofit实例时通过.baseUrl()设置，
    //第二部分在网络接口注解中设置，如下面的"/user"，网络请求的完整地址Url = Retrofit实例.baseUrl()+网络请求接口注解()


    //1.get请求

    //(无参数)
    //get请求网络方法，需要在方法头部添加@GET注解，表示采用get方法访问网络请求，括号内的是请求的地址(Url的一部分)
    //其中返回类型是Call<*>，*表示接收数据的类，如果想直接获取ResponseBody中的内容，可以定义网络请求返回值为Call<ResponseBody>，
    //ResponseBody是请求网络后返回的原始数据，如果网络请求没有参数，不用写
    @GET("user")
    Call<ResponseBody> getData1();

    //(有参数)
    //get请求网络方法，添加参数在方法括号内添加@Query，后面是参数类型和参数字段，其实就是键值对，Retrofit会把两个字段拼接到接口中
    //表示后面idLon的取值作为"id"的值，nameStr的取值作为"name"的值
    @GET("user")
    Call<ResponseBody> getData2(@Query("id") long idLon, @Query("name") String nameStr);

    //(多个不指定参数)
    //如果有多个不确定的参数可以使用@QueryMap注解，通过将参数传入Map中，再统一提交参数，类似多个Query
    @GET("user")
    Call<ResponseBody> getData3(@QueryMap Map<String, Object> map);


    //2.post请求(无参数)

    //(无参数)
    //post请求网络方法，需要在方法头部添加@POST注解，表示采用post方法访问网络请求
    @POST("user/emails")
    Call<ResponseBody> getPostData();

    //(有参数)
    //如果有参数需要在头部添加@FormUrlEncoded注解，表示请求实体是一个From表单，每个键值对需要使用@File注解
    //在方法括号内使用@Field添加参数，这是发送Post请求时，提交请求的表单字段，必须要添加的，而且需要配合@FormUrlEncoded使用
    @FormUrlEncoded
    @POST("user/emails")
    Call<ResponseBody> getPostData2(@Field("name") String nameStr, @Field("sex") String sexStr);

    //(多个参数)
    //@FieldMap与@Field的作用一致，可以用于添加多个不确定的参数，类似@QueryMap，Map的key作为表单的键，Map的value作为表单的值
    @FormUrlEncoded
    @POST("user/emails")
    Call<ResponseBody> getPostData3(@FieldMap Map<String, Object> map);

    //@Body可以传递自定义类型数据给服务器，多用于post请求发送非表单数据，比如用传递Json格式数据，它可以注解很多东西，比如HashMap、实体类等
    @FormUrlEncoded
    @POST("user/emails")
    Call<ResponseBody> getPsotDataBody(@Body RequestBody body);

    //3.http
    //@HTTP注解是替换@GET、@POST、@PUT、@DELETE、@HEAD以及更多拓展功能
    //method:表示请求的方法，区分大小写，这里的值retrofit不会再做任何处理，必须要保证正确
    //path:网络请求地址路径
    //hasBody:是否有请求体
    @HTTP(method = "GET", path = "user/keys", hasBody = false)
    Call<ResponseBody> getHttpData();

    //4.path
    //@Path注解用于Url中的占位符{}，所有在网址中的参数，如下面的id，通过{}占位符来标记id，通过使用@Path注解传入id的值
    //注意有的Url既有占位符又有"?"后面的键值对，其实@Query和@Path两者是可以共用的
    @GET("orgs/{id}")
    Call<ResponseBody> getPathData(@Query("name") String nameStr, @Path("id") long idLon);

    //5.URl
    //@URL注解表示指定请求路径，如果需要重新地址接口地址，可以使用@URL，将地址以参数的形式传入即可。
    //如果有@Url注解时，GET传入的Url可以省略
    @GET
    Call<ResponseBody> getUrlData(@Url String nameStr, @Query("id") long idLon);

    //6.@Header和@Headers

    //@Header添加固定的请求头
    //作为方法的参数传入，用于添加不固定的header，该注解会更新已有的请求头
    @GET("user/emails")
    Call<ResponseBody> getHeaderData(@Header("token") String token);

    //@Headers添加多个请求头
    //用于添加固定请求头，可以同时添加多个，通过该注解添加的请求头不会相互覆盖，而是共同存在
    @Headers({"phone-type:android", "version:1.1.1"})
    @GET("user/emails")
    Call<ResponseBody> getHeadersData(@Header("token") String token);

    //7.@Streaming
    //表示响应体的数据用流的方式返回，使用于返回数据比较大，该注解在下载大文件时特别有用
    @Streaming
    @POST("gists/public")
    Call<ResponseBody> getStreamingBig();


    //8.@Multipart、@part、@PartMap
    //@Multipart表示请求实体是一个支持文件上传的表单，需要配合@Part和@PartMap使用，适用于文件上传
    //@Part用于表单字段，适用于文件上传的情况,@PartMap用于表单字段，可用于多文件上传的情况
    //@Part支持三种类型：RequestBody、MultipartBody.Part、任意类型
    @Multipart
    @POST("user/followers")
    Call<ResponseBody> getPartData(@Part("name") RequestBody name, @Part MultipartBody.Part file);

    //多文件上传
    @Multipart
    @POST("user/followers")
    Call<ResponseBody> getPartMapData(@PartMap Map<String, MultipartBody.Part> map);

    //9.实例
    //get请求
    @GET("FollowExcellence/Rxjava_Retrofit/master/data1.js")
    Call<Data<Info>> getJsonData(@Query("id") String id);

    //post请求
    @FormUrlEncoded
    @POST("translate?doctype=json&jsonversion=&type=&keyfrom=&model=&mid=&imei=&vendor=&screen=&ssid=&network=&abtest=")
    Call<Translation> postDataCall(@Field("i") String targetStr);

}
