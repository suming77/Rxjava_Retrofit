package com.example.okhttp3demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Request是OKHttp的访问请求，Builder是访问辅助类，Response是OKHttp的请求响应
 * Request(请求)
 * 每一个HTTP请求中，都应该包含一个URL,一个GET或POST方法，以及Header和其他参数，还可以包含特定内容类型的数据流。
 * Responses(响应)
 * 响应则包含一个回复代码(200代表成功，404代表未找到)，Header和定制可选的body。
 * OKHttp
 * 简单来说，通过OkHttpClient可以发送一个http请求，并且可以读取该请求的响应，它是一个生产Call的工厂。
 * 收益于一个共享的响应缓存/线程池/复用的链接等因素，绝大多数应用使用一个OKHttpClient实例，便可满足整个应用的Http请求
 * <p>
 * 我的博客：https://blog.csdn.net/m0_37796683
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_get_sync).setOnClickListener(this);
        findViewById(R.id.btn_get_async).setOnClickListener(this);
        findViewById(R.id.btn_post_sync_str).setOnClickListener(this);
        findViewById(R.id.btn_post_async_str).setOnClickListener(this);
        findViewById(R.id.btn_post_key_value).setOnClickListener(this);
        findViewById(R.id.btn_post_key_value_more).setOnClickListener(this);
        findViewById(R.id.btn_post_file).setOnClickListener(this);
        findViewById(R.id.btn_post_form).setOnClickListener(this);
        findViewById(R.id.btn_post_streaming).setOnClickListener(this);
        findViewById(R.id.btn_post_multipart).setOnClickListener(this);
        findViewById(R.id.btn_header_set_read).setOnClickListener(this);
        findViewById(R.id.btn_timeout).setOnClickListener(this);

        //方式一：创建OkHttpClient实例,使用默认构造函数，创建默认配置OkHttpClient(官方建议全局只有一个实例)
        mOkHttpClient = new OkHttpClient();

        //方式二：通过new OkHttpClient.Builder() 一步步配置一个OkHttpClient实例
//        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(13, TimeUnit.SECONDS).build();

        //方式三：如果要求使用现有的实例，可以通过newBuilder().build()方法进行构造
//        OkHttpClient client = mOkHttpClient.newBuilder().build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_sync://get同步
                setGetSync();
                break;
            case R.id.btn_get_async://get异步
                setGetAsync();
                break;
            case R.id.btn_post_sync_str://post同步提交String
                setPostSnyc();
                break;
            case R.id.btn_post_async_str://post异步提交String
                setPostAsnyc();
                break;
            case R.id.btn_post_key_value://post单个键值对
                setPostKeyValueOne();
                break;
            case R.id.btn_post_key_value_more://post多个键值对
                setPostKeyValueMore();
                break;
            case R.id.btn_post_file://post提交文件
                setPostFile();
                break;
            case R.id.btn_post_form://post提交表单
                setPostForm();
                break;
            case R.id.btn_post_streaming://post提交流
                setPostStreaming();
                break;
            case R.id.btn_post_multipart://post提交分块请求
                setPostMultiPart();
                break;
            case R.id.btn_header_set_read://head设置和读取
                setAndReadHead();
                break;
            case R.id.btn_timeout://超时设置
                setTimeOuts();
                break;
            default:
                break;
        }
    }

    /**
     * get同步请求
     */
    private void setGetSync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //通过Builder辅助类构建请求对象
                Request request = new Request.Builder()
                        .get()//get请求
                        .url("https://www.baidu.com")//请求地址
                        .build();//构建

                try {
                    //通过mOkHttpClient调用请求得到Call
                    final Call call = mOkHttpClient.newCall(request);
                    //执行同步请求，获取Response对象
                    Response response = call.execute();

                    if (response.isSuccessful()) {//如果请求成功
                        String string = response.body().string();
                        Log.e(TAG, "get同步请求success==" + string);
                        //响应体的string()对于小文档来说十分方便高效，但是如果响应体太大(超过1M)，应避免使用string()方法，
                        //因为它会把整个文档加载到内存中，对用超多1M的响应body，应该使用流的方式来处理。

                        //response.body().bytes();//字节数组类型
                        //response.body().byteStream();//字节流类型
                        //response.body().charStream();//字符流类型

                        printHeads(response.headers());
                    } else {
                        Log.e(TAG, "get同步请求failure==");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * get异步请求
     */
    private void setGetAsync() {
        //通过Builder辅助类构建Request对象,链式编程
        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .get()
                .build();

        //异步  onFailure()和onResponse()是在异步线程里执行的，所以如果你在Android把更新UI的操作写在这两个方法里面是会报错的
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Log.e(TAG, "get异步响应失败==" + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //主线程中更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 在主线程中更新UI的操作
                    }
                });

                Log.e(TAG, "get异步当前线程，线程id==" + Thread.currentThread().getId());
                String result = response.body().string();
                Log.e(TAG, "get异步响应成功==" + result);
                printHeads(response.headers());
            }
        });

        Log.e(TAG, "主线程，线程id==" + Thread.currentThread().getId());
    }


    /**
     * 打印请求头信息
     *
     * @param headers 请求头集合
     */
    private void printHeads(Headers headers) {
        if (headers == null) return;
        for (int i = 0; i < headers.size(); i++) {
            Log.e(TAG, "请求头==" + headers.name(i) + ":" + headers.value(i));
        }
    }

    /**
     * Post同步请求(string),同步的请求实际应用中很少用到，这里就举一个例子
     * 提交字符串
     */
    private void setPostSnyc() {
        //构建RequestBody对象，post提交的过程需要将提交的内容封装到一个RequestBody中
        //MediaType用于描述Http请求和响应体的内容类型，也就是Content-Type
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody requestBody = RequestBody.create("提交的内容", mediaType);
        final Request request = new Request.Builder()
                .post(requestBody)
                .url("https://api.github.com/markdown/raw")
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Post请求String同步响应success==" + response.body().string());
                    } else {
                        Log.e(TAG, "Post请求String同步响应failure==" + response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Post请求String同步响应failure==" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Post异步请求(string)
     */
    private void setPostAsnyc() {
        RequestBody requestBody = RequestBody.create("提交内容", MediaType.parse("text/plain; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求String异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                Log.e(TAG, "Post请求String异步响应success==" + string);
            }
        });
    }

    /**
     * Post异步(单个键值对)
     */
    private void setPostKeyValueOne() {
        //提交键值对需要用到FormBody,FormBody继承自RequestBody
        FormBody formBody = new FormBody.Builder()
                //添加键值对(通多Key-value的形式添加键值对参数)
                .add("key", "value")
                .build();
        final Request request = new Request.Builder()
                .post(formBody)
                .url("url")
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求(键值对)异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, "Post请求(键值对)异步响应Success==" + result);
            }
        });
    }


    /**
     * Post异步(多个键值对)
     */
    private void setPostKeyValueMore() {
        Map<String, String> map = new HashMap<>();
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url("url")
                .post(formBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求(多个键值对)异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, "Post请求(多个键值对)异步响应Success==" + result);
            }
        });
    }

    /**
     * post异步提交文件
     */
    private void setPostFile() {
        File file = getFile("User", "okhttp3.txt", "okhttp提交文件");
        if (file == null) {
            Toast.makeText(this, "没有此文件", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), file);
        Request request = new Request.Builder()
                .post(requestBody)
                .url("https://api.github.com/markdown/raw")
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求(文件)异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, "Post请求(文件)异步响应Success==" + result);
            }
        });
    }

    /**
     * post提交表单
     */
    private void setPostForm() {
        //使用FormEncodingBuilder来构建和HTML标签相同效果的请求体，键值对将使用一种HTML兼容形式的URL编码来进行编码
        FormBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(formBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求(表单)异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, "Post请求(表单)异步响应Success==" + result);
            }
        });
    }

    /**
     * post方式提交流
     */
    private void setPostStreaming() {
        //以流的方式Post提交请求体，请求体的内容由流写入产生，这里是流直接写入OKIO的BufferedSink。
        // 你的程序可能会使用OutputStream, 你可以使用BufferedSink.outputStream()来获取
        final MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        //重写RequestBody中的几个方法，将本地数据放入到Http协议的请求体中，然后发送到服务器
        final RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                //返回内容类型
                return mediaType;
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                //输入数据头
                bufferedSink.writeUtf8("Numbers\\n");
                bufferedSink.writeUtf8("-------\\n");

                //构造数据
                for (int i = 2; i < 997; i++) {
                    bufferedSink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }
        };

        final Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String result = response.body().toString();
                        Log.e(TAG, "Post请求(流)异步响应Success==" + result);
                    } else {
                        Log.e(TAG, "Post请求(流)异步响应failure==" + response);
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String factor(int n) {
        for (int i = 2; i < n; i++) {
            int x = n / i;
            if (x * i == n) {
                return factor(x) + "x" + i;
            }
        }
        return Integer.toString(n);
    }

    /**
     * post异步提交分块请求
     */
    private void setPostMultiPart() {
        //MultipartBuilder可以构建复杂的请求体，与HTML文件上传形式兼容，多块请求头中的每个请求体都是一个亲求体，
        // 可以定义自己的请求体，这些请求体可以用来描述这块请求，例如他的Content-Disposition，
        // 如果Content-Length和Content-Type可用的话，他们会被自动添加到请求头中
        MediaType mediaType = MediaType.parse("image/png");
        String IMGUR_CLIENT_ID = "...";
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("image", "logo-square.png", RequestBody.create(mediaType,
                        new File("website/static/logo-square.png")))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url("https://api.imgur.com/3/image")
                .post(multipartBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求(分块)异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.e(TAG, "Post请求(分块)异步响应Success==" + result);
            }
        });
    }

    /**
     * HTTP的头部的设置和读取
     */
    private void setAndReadHead() {
        //HTTP头部的数据结构是Map<String,List<String>>类型，也就是说，对于每个HTTP的头，可能有多个值，
        // 但是大部分的HTTP头只有一个值，只有少部分HTTP头允许多个值，至于name的取值说明请参考：http://tools.jb51.net/table/http_header
        //OKHTTP的处理方式是：
        //使用header(name, value)来设置HTTP头的唯一值, 如果请求中已经存在响应的信息那么直接替换掉
        //使用addHeader(name, value)来补充新值，如果请求头中已经存在name的name-value, 那么还会继续添加，请求头中便会存在多个name相同value不同的“键值对”
        //使用header(name, value)读取唯一值或多个值的最后一个值
        //使用headers(name)获取所有值

        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java")//设置唯一值
                .addHeader("Server", "application/json; q=0.5")//设置新值
                .addHeader("Server", "application/vnd.github.v3+json")//设置新值
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Post请求(HTTP头)异步响应failure==" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e(TAG, "header：Date==" + response.header("Date"));
                Log.e(TAG, "header：User-Agent==" + response.header("User-Agent"));
                Log.e(TAG, "headers：Server==" + response.headers("Server"));
                Log.e(TAG, "headers：Vary==" + response.headers("Vary"));

                Log.e(TAG, "Post请求(HTTP头)异步响应Success==" + response.body().string());
            }
        });
    }


    /**
     * 超时测试
     *
     * @throws IOException
     */
    private void setTimeOuts() {
        //1.构建OkHttpClient实例
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)//链接超时为2秒，单位为秒
                .writeTimeout(2, TimeUnit.SECONDS)//写入超时
                .readTimeout(2, TimeUnit.SECONDS)//读取超时
                .build();

        //2.通过Builder辅助类构建请求对象
        final Request request = new Request.Builder()
                .url("http://httpbin.org/delay/10")//URL地址
                .build();//构建

        //创建线程，在子线程中运行
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //3.通过mOkHttpClient调用请求得到Call
                    final Call call = okHttpClient.newCall(request);
                    //4.执行同步请求，获取响应体Response对象
                    Response response = call.execute();
                    Log.e(TAG, "请求(超时)==" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "请求(超时)==" + e.toString());
                }
            }
        }).start();
    }

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param content  文件内容
     * @return 文件
     */
    private File getFile(String filePath, String fileName, String content) {
        if (filePath == null || filePath.length() == 0 || fileName == null || fileName.length() == 0) {
            return null;
        }

        //判断是否有SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请插入SD卡", Toast.LENGTH_SHORT).show();
            return null;
        }

        //创建文件夹
        File sdCardDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filePath);
        if (!sdCardDir.exists()) {
            if (!sdCardDir.mkdirs()) {
                try {
                    sdCardDir.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //创建.txt文件
        File saveFile = new File(sdCardDir, fileName);
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //文件写入数据
        if (!TextUtils.isEmpty(content)) {
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(saveFile);
                outStream.write(content.getBytes());
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return saveFile;
    }
}