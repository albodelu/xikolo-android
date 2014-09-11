package de.xikolo.controller.course.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.xikolo.R;
import de.xikolo.entities.Course;
import de.xikolo.entities.Item;
import de.xikolo.entities.Module;
import de.xikolo.util.ItemTitle;

public class ItemListAdapter extends BaseAdapter {

    public static final String TAG = ItemListAdapter.class.getSimpleName();

    private List<Item> mItems;

    private Activity mContext;
    private Course mCourse;
    private Module mModule;

    private OnItemButtonClickListener mCallback;

    public ItemListAdapter(Activity context, Course course, Module module, OnItemButtonClickListener callback) {
        this.mContext = context;
        this.mItems = new ArrayList<Item>();
        this.mCourse = course;
        this.mModule = module;
        this.mCallback = callback;
    }

    public void updateItems(List<Item> items) {
        this.mItems = items;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_module_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.textTitle);
            viewHolder.icon = (TextView) rowView.findViewById(R.id.textIcon);
            viewHolder.container = (ViewGroup) rowView.findViewById(R.id.container);
            viewHolder.unseenIndicator = rowView.findViewById(R.id.unseenIndicator);
            rowView.setTag(viewHolder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        final Item item = (Item) getItem(i);

        holder.title.setText(ItemTitle.format(mModule.name, item.title));

        if (item.type.equals(Item.TYPE_TEXT)) {
            holder.icon.setText(mContext.getString(R.string.icon_text));
        } else if (item.type.equals(Item.TYPE_VIDEO)) {
            holder.icon.setText(mContext.getString(R.string.icon_video));
        } else if (item.type.equals(Item.TYPE_SELFTEST)) {
            holder.icon.setText(mContext.getString(R.string.icon_selftest));
        } else if (item.type.equals(Item.TYPE_ASSIGNMENT) || item.type.equals(Item.TYPE_EXAM)) {
            holder.icon.setText(mContext.getString(R.string.icon_assignment));
        }

        // TODO set progression when unseen
        if (i % 2 == 0) {
//            holder.unseenIndicator.setVisibility(View.VISIBLE);
            holder.unseenIndicator.setVisibility(View.GONE);
        } else {
            holder.unseenIndicator.setVisibility(View.GONE);
        }

//        holder.icon.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        ViewGroup.LayoutParams params = holder.unseenIndicator.getLayoutParams();
//        Log.d(TAG, "height: " + holder.icon.getMeasuredHeight());
//        params.height = holder.icon.getMeasuredHeight();
//        holder.unseenIndicator.setLayoutParams(params);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemButtonClicked(mCourse, mModule, item);
            }
        });

        return rowView;
    }

    public interface OnItemButtonClickListener {

        public void onItemButtonClicked(Course course, Module module, Item item);

    }

    static class ViewHolder {
        TextView title;
        TextView icon;

        View unseenIndicator;

        ViewGroup container;
    }

}
