package com.byteflow.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.byteflow.app.egl.EGLActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static com.byteflow.app.MyGLSurfaceView.IMAGE_FORMAT_NV21;
import static com.byteflow.app.MyGLSurfaceView.IMAGE_FORMAT_RGBA;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_BASIC_LIGHTING;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_COORD_SYSTEM;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_EGL;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_FBO;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_FBO_LEG;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_MULTI_LIGHTS;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_TEXTURE_MAP;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_TRANS_FEEDBACK;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_TRIANGLE;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_VAO;
import static com.byteflow.app.MyNativeRender.SAMPLE_TYPE_YUV_TEXTURE_MAP;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String[] SAMPLE_TITLES = {
            "DrawTriangle",
            "TextureMap",
            "YUV Rendering",
            "VAO&VBO",
            "FBO Offscreen Rendering",
            "EGL Background Rendering",
            "FBO Stretching",
            "Coordinate System",
            "Basic Lighting",
            "Transform Feedback",
            "Complex Lighting",
    };

    private MyGLSurfaceView mGLSurfaceView;
    private int mSampleSelectedIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGLSurfaceView = findViewById(R.id.my_gl_surface_view);
        mGLSurfaceView.getNativeRender().native_OnInit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLSurfaceView.getNativeRender().native_OnUnInit();

        /*
        * Once the EGL context gets destroyed all the GL buffers etc will get destroyed with it,
        * so this is unnecessary.
        * */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_sample) {
            showGLSampleDialog();
        }
        return true;
    }

    private void LoadRGBAImage(int resId) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                mGLSurfaceView.getNativeRender().native_SetImageData(IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
            }
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void LoadNV21Image() {
        InputStream is = null;
        try {
            is = getAssets().open("YUV_Image_840x1074.NV21");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int lenght = 0;
        try {
            lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            mGLSurfaceView.getNativeRender().native_SetImageData(IMAGE_FORMAT_NV21, 840, 1074, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void showGLSampleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View rootView = inflater.inflate(R.layout.sample_selected_layout, null);

        final AlertDialog dialog = builder.create();

        Button confirmBtn = rootView.findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final RecyclerView resolutionsListView = rootView.findViewById(R.id.resolution_list_view);

        final MyRecyclerViewAdapter myPreviewSizeViewAdapter = new MyRecyclerViewAdapter(this, Arrays.asList(SAMPLE_TITLES));
        myPreviewSizeViewAdapter.setSelectIndex(mSampleSelectedIndex);
        myPreviewSizeViewAdapter.addOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int selectIndex = myPreviewSizeViewAdapter.getSelectIndex();
                myPreviewSizeViewAdapter.setSelectIndex(position);
                myPreviewSizeViewAdapter.notifyItemChanged(selectIndex);
                myPreviewSizeViewAdapter.notifyItemChanged(position);
                mSampleSelectedIndex = position;

                mGLSurfaceView.getNativeRender().native_SetParamsInt(SAMPLE_TYPE, position + SAMPLE_TYPE, 0);
                switch (position + SAMPLE_TYPE) {
                    case SAMPLE_TYPE_TRIANGLE:
                        break;
                    case SAMPLE_TYPE_TEXTURE_MAP:
                        LoadRGBAImage(R.drawable.dzzz);
                        break;
                    case SAMPLE_TYPE_YUV_TEXTURE_MAP:
                        LoadNV21Image();
                        break;
                    case SAMPLE_TYPE_VAO:
                        break;
                    case SAMPLE_TYPE_FBO:
                        LoadRGBAImage(R.drawable.java);
                        break;
                    case SAMPLE_TYPE_FBO_LEG:
                        LoadRGBAImage(R.drawable.leg);
                        break;
                    case SAMPLE_TYPE_EGL:
                        startActivity(new Intent(MainActivity.this, EGLActivity.class));
                        break;
                    case SAMPLE_TYPE_COORD_SYSTEM:
                    case SAMPLE_TYPE_BASIC_LIGHTING:
                    case SAMPLE_TYPE_TRANS_FEEDBACK:
                    case SAMPLE_TYPE_MULTI_LIGHTS:
                        LoadRGBAImage(R.drawable.dzzz);
                        break;
                    default:
                        break;
                }

                mGLSurfaceView.requestRender();

                dialog.cancel();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        resolutionsListView.setLayoutManager(manager);

        resolutionsListView.setAdapter(myPreviewSizeViewAdapter);

        dialog.show();
        dialog.getWindow().setContentView(rootView);

    }

    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> implements View.OnClickListener {
        private List<String> mTitles;
        private Context mContext;
        private int mSelectIndex = 0;
        private OnItemClickListener mOnItemClickListener = null;

        public MyRecyclerViewAdapter(Context context, List<String> titles) {
            mContext = context;
            mTitles = titles;
        }

        public void setSelectIndex(int index) {
            mSelectIndex = index;
        }

        public int getSelectIndex() {
            return mSelectIndex;
        }

        public void addOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_item_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            view.setOnClickListener(this);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.mTitle.setText(mTitles.get(position));
            if (position == mSelectIndex) {
                holder.mRadioButton.setChecked(true);
                holder.mTitle.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            } else {
                holder.mRadioButton.setChecked(false);
                holder.mTitle.setText(mTitles.get(position));
                holder.mTitle.setTextColor(Color.GRAY);
            }
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mTitles.size();
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            RadioButton mRadioButton;
            TextView mTitle;

            public MyViewHolder(View itemView) {
                super(itemView);
                mRadioButton = itemView.findViewById(R.id.radio_btn);
                mTitle = itemView.findViewById(R.id.item_title);
            }
        }
    }

}
