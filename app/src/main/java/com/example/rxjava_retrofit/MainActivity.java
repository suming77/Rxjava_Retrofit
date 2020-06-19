package com.example.rxjava_retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit的简单使用
 * 我的博客：https://blog.csdn.net/m0_37796683/article/details/90702095
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextView;
    private Retrofit mRetrofit;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv);

        ////步骤4:构建Retrofit实例
        mRetrofit = new Retrofit.Builder()
                //设置网络请求BaseUrl地址
                .baseUrl("https://api.uomg.com/")
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                .build();

//        getData3Call(retrofit);

//        partDataCall(retrofit);

//        partMapDataCall(retrofit);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJsonData();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postJsonData();
            }
        });
    }

    /**
     * 示例，get加载Json数据
     */
    private void getJsonData() {
        // 步骤5:创建网络请求接口对象实例
        Api api = mRetrofit.create(Api.class);
        //步骤6：对发送请求进行封装，传入接口参数
        Call<Data<Info>> jsonDataCall = api.getJsonData("新歌榜", "json");

        //同步执行
//         Response<Data<Info>> execute = jsonDataCall.execute();

        //步骤7:发送网络请求(异步)
        Log.e(TAG, "get == url：" + jsonDataCall.request().url());
        jsonDataCall.enqueue(new Callback<Data<Info>>() {
            @Override
            public void onResponse(Call<Data<Info>> call, Response<Data<Info>> response) {
                //步骤8：请求处理,输出结果
                Toast.makeText(MainActivity.this, "get回调成功:异步执行", Toast.LENGTH_SHORT).show();
                Data<Info> body = response.body();
                if (body == null) return;
                Info info = body.getData();
                if (info == null) return;
                mTextView.setText("返回的数据：" + "\n\n" + info.getName() + "\n" + info.getPicurl());
            }

            @Override
            public void onFailure(Call<Data<Info>> call, Throwable t) {
                Log.e(TAG, "get回调失败：" + t.getMessage() + "," + t.toString());
                Toast.makeText(MainActivity.this, "get回调失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 示例，Post加载Json数据
     */
    private void postJsonData() {
        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.uomg.com/") // 设置网络请求baseUrl
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .build();

        // 步骤5:创建网络请求接口的实例
        Api request = retrofit.create(Api.class);
        //步骤6：对发送请求进行封装:传入参数
        Call<Object> call = request.postDataCall("JSON");

        //步骤7:发送网络请求(异步)

        //请求地址
        Log.e(TAG, "post == url：" + call.request().url());

        //请求参数
        StringBuilder sb = new StringBuilder();
        if (call.request().body() instanceof FormBody) {
            FormBody body = (FormBody) call.request().body();
            for (int i = 0; i < body.size(); i++) {
                sb.append(body.encodedName(i))
                        .append(" = ")
                        .append(body.encodedValue(i))
                        .append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            Log.e(TAG, "| RequestParams:{" + sb.toString() + "}");
        }

        call.enqueue(new Callback<Object>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                //步骤8：请求处理,输出结果
                Object body = response.body();
                if (body == null) return;
                mTextView.setText("返回的数据：" + "\n\n" + response.body().toString());
                Toast.makeText(MainActivity.this, "post回调成功:异步执行", Toast.LENGTH_SHORT).show();
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<Object> call, Throwable throwable) {
                Log.e(TAG, "post回调失败：" + throwable.getMessage() + "," + throwable.toString());
                Toast.makeText(MainActivity.this, "post回调失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 多个文件上传
     *
     * @param retrofit
     */
    private void partMapDataCall(Retrofit retrofit) {
        File file1 = new File("文件路径");
        File file2 = new File("文件路径");
        if (!file1.exists()) {
            file1.mkdir();
        }
        if (!file2.exists()) {
            file2.mkdir();
        }

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("image/png"), file1);
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("image/png"), file2);
        MultipartBody.Part filePart1 = MultipartBody.Part.createFormData("file1", file1.getName(), requestBody1);
        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("file2", file2.getName(), requestBody2);

        Map<String, MultipartBody.Part> mapPart = new HashMap<>();
        mapPart.put("file1", filePart1);
        mapPart.put("file2", filePart2);

        Call<ResponseBody> partMapDataCall = retrofit.create(Api.class).getPartMapData(mapPart);
    }

    /**
     * 图文上传
     *
     * @param retrofit
     */
    private void partDataCall(Retrofit retrofit) {
        //声明类型,这里是文字类型
        MediaType textType = MediaType.parse("text/plain");
        //根据声明的类型创建RequestBody,就是转化为RequestBody对象
        RequestBody name = RequestBody.create(textType, "这里是你需要写入的文本：刘亦菲");

        //创建文件，这里演示图片上传
        File file = new File("文件路径");
        if (!file.exists()) {
            file.mkdir();
        }

        //将文件转化为RequestBody对象
        //需要在表单中进行文件上传时，就需要使用该格式：multipart/form-data
        RequestBody imgBody = RequestBody.create(MediaType.parse("image/png"), file);
        //将文件转化为MultipartBody.Part
        //第一个参数：上传文件的key；第二个参数：文件名；第三个参数：RequestBody对象
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), imgBody);

        Call<ResponseBody> partDataCall = retrofit.create(Api.class).getPartData(name, filePart);
    }

    /**
     * get请求
     *
     * @param retrofit
     */
    private void getData3Call(Retrofit retrofit) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 10006);
        map.put("name", "刘亦菲");
        Call<ResponseBody> getData3call = retrofit.create(Api.class).getData3(map);

        map.put("name", "刘亦菲");
        map.put("sex", "女");
        Call<ResponseBody> postData3 = retrofit.create(Api.class).getPostData3(map);
    }

    /**
     * post请求
     *
     * @param retrofit
     */
    private void getPostData3(Retrofit retrofit) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", "刘亦菲");
        map.put("sex", "女");
        Call<ResponseBody> postData3 = retrofit.create(Api.class).getPostData3(map);
    }
}
