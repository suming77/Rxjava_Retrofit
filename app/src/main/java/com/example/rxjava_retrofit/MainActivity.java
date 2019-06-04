package com.example.rxjava_retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Map<String, Object> map = new HashMap<>();
        map.put("id", 10006);
        map.put("name", "刘亦菲");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ResponseBody> call = retrofit.create(Api.class).getData3(map);

        Map<String, Object> fieldMap = new HashMap<>();
        map.put("name", "刘亦菲");
        map.put("sex", "女");

        Call<ResponseBody> psotData3 = retrofit.create(Api.class).getPostData3(map);


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

        Map<String,MultipartBody.Part> mapPart = new HashMap<>();
        mapPart.put("file1",filePart1);
        mapPart.put("file2",filePart2);

        Call<ResponseBody> partMapDataCall = retrofit.create(Api.class).getPartMapData(mapPart);

    }
}
