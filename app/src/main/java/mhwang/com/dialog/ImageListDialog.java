package mhwang.com.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import mhwang.com.takecareofmoney.R;

/**
 * 项目名称：
 * 类描述：
 * 作者：王明海
 * 创建时间：2016/5/26
 */
public class ImageListDialog extends Activity {
    private GridView gv_photos;
    public static final String KEY_IMAGE_INDEX = "imageindex";
    private int[] images = {R.drawable.t1,R.drawable.t2,R.drawable.t3,
            R.drawable.t4,R.drawable.t5,R.drawable.t6,
            R.drawable.t7,R.drawable.t8,R.drawable.t9};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image);
        initComponent();
        initEvent();
    }

    private void initComponent(){
        gv_photos = (GridView) findViewById(R.id.gv_image_list);
        ImageAdapter adapter = new ImageAdapter(this);
        gv_photos.setAdapter(adapter);
    }

    private void initEvent(){
        gv_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra(KEY_IMAGE_INDEX,position);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

    private class ImageAdapter extends BaseAdapter{
        Context context;
        LayoutInflater inflater;
        public ImageAdapter(Context context){
            this.context = context;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return images[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null){
//                convertView = inflater.inflate(R.layout.item_image,null);
                imageView = new ImageView(context);
                GridView.LayoutParams params = new GridView.LayoutParams(130,130);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }else{
                imageView = (ImageView) convertView;
            }
//            imageView = (ImageView) convertView.findViewById(R.id.image_iv_photo);
            imageView.setImageResource(images[position]);
            return imageView;
        }
    }

}
