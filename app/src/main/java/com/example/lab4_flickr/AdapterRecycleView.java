package com.example.lab4_flickr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4_flickr.model.Photo;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AdapterRecycleView extends RecyclerView.Adapter<AdapterRecycleView.ItemHolder> {

    private Context context;
    private List<Photo> photoList;

    public AdapterRecycleView(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item2, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {

        Photo photo = photoList.get(i);
   //     itemHolder.tvItemView.setText(photo.getViews());
        final Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadImageFromNet(photo.getUrlZ());
                itemHolder.imgItemImage.post(new Runnable() {
                    @Override
                    public void run() {
                        itemHolder.imgItemImage.setImageBitmap(bitmap);
                    }
                });
            }
        });
        myThread.start();

    }

    @Override
    public int getItemCount() {
        if (photoList == null) return 0;
        return photoList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private ImageView imgItemImage;
        private TextView tvItemView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            imgItemImage = itemView.findViewById(R.id.imgItemImage);
            tvItemView = itemView.findViewById(R.id.tvItemView);


        }

    }

    private Bitmap loadImageFromNet(String link) {

        URL url;
        Bitmap bitmap = null;
        try {
            url = new URL(link);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (IOException e) {
            Log.e("LoadImage", e + "");
        }

        return bitmap;

    }


    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
                return false;
            }
        };
        private RecyclerView.OnChildAttachStateChangeListener mAttachListener
                = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
            }
        };

        private ItemClickSupport(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            mRecyclerView.setTag(R.id.item_click_support, this);
            mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
        }

        public static ItemClickSupport addTo(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new ItemClickSupport(view);
            }
            return support;
        }

        public static ItemClickSupport removeFrom(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
            return this;
        }

        private void detach(RecyclerView view) {
            view.removeOnChildAttachStateChangeListener(mAttachListener);
            view.setTag(R.id.item_click_support, null);
        }

        public interface OnItemClickListener {
            void onItemClicked(RecyclerView recyclerView, int position, View v);
        }

        public interface OnItemLongClickListener {
            boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
        }
    }
}
