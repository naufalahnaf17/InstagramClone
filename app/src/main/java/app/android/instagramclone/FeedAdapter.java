package app.android.instagramclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter {

    private Context ctx;
    private List<Upload>mUploads;
    public TextView txtCaption;
    public ImageView imgFeed;

    public FeedAdapter(Context context , List<Upload> uploads ){
        ctx = context;
        mUploads = uploads;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.list_layout , parent , false);
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        txtCaption.setText(uploadCurrent.getCaptionPost());
        Glide.with(ctx).load(uploadCurrent.getUrlPost())
                .centerCrop()
                .into(imgFeed);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder{

        public FeedViewHolder(View itemView) {
            super(itemView);
            txtCaption = itemView.findViewById(R.id.txt_caption);
            imgFeed = itemView.findViewById(R.id.gambar);
        }
    }

}
