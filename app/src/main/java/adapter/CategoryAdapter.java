package adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readwritedemo.R;

import java.util.ArrayList;

import interfaces.OnClickListener;
import model.CategoryList;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public int mSelectedItem = -1;
    ArrayList<CategoryList> categoryLists;
    Activity activity;
    private Context mContext;
    OnClickListener onClickListener;

    public CategoryAdapter(Activity activity, Context mContext, ArrayList<CategoryList> categoryLists,
                           OnClickListener onClickListener) {
        this.activity = activity;
        this.mContext = mContext;
        this.categoryLists = categoryLists;
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.raw_categorylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CategoryList categoryListItem = categoryLists.get(position);

        holder.tvCategoryName.setText(categoryListItem.getCategoryName());

        holder.tvCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategoryName;

        public ViewHolder(final View inflate) {
            super(inflate);
            //mText = (TextView) inflate.findViewById(R.id.text);
            tvCategoryName = (TextView) inflate.findViewById(R.id.tvCategoryName);
        }
    }
}