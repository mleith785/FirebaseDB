package mleith785.cs499.firebasedb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*
 * Stolen from
 * https://www.youtube.com/watch?v=gGFvbvkZiMs&list=PLk7v1Z2rk4hjHrGKo9GqOtLs1e2bglHHA&index=4
 * https://www.youtube.com/watch?v=gGFvbvkZiMs&list=PLk7v1Z2rk4hjHrGKo9GqOtLs1e2bglHHA&index=4
 *
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder>
{

    private List<FavoriteListItem> ListItems;
    private Context context;
    private ItemClickListener mClickListener;

    public FavoriteAdapter(List<FavoriteListItem> listItems, Context context)
    {
        ListItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_list,parent,false);
        return new ViewHolder(v);
    }

    /**
     * <h1> onBindViewHolder</h1>
     * This paints the row for the recyclerview
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final FavoriteListItem list_item = ListItems.get(position);
        holder.textViewHead.setText(list_item.getCampsiteName());
        holder.textViewDesc.setText("City: "+list_item.getCampsiteCity());

    }


    @Override
    public int getItemCount()
    {
        int size=0;
        //Protect the list if empty, saw this happen once
        if(ListItems !=null)
        {
            size = ListItems.size();
        }

        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView textViewHead;
        public TextView textViewDesc;
        public LinearLayout FavLinLayoutGui;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.FavoriteHeadingGui);
            textViewDesc = (TextView) itemView.findViewById(R.id.FavoriteDescGui);
            FavLinLayoutGui = (LinearLayout) itemView.findViewById(R.id.FavLinLayoutGui);
            itemView.setOnClickListener(this);
        }

        /**
         * <h1> onClick</h1>
         * This is the callback that is called when someone clicks on the recyclerview/a favorite
         * campsite.  What this will do is callback to the main activity/FavoritesActivity so
         * that it can launch a details activity based on the selection.
         * @param view
         */
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
            {
                int position = getAdapterPosition();
                String campsite_key = ListItems.get(position).getCampsiteKey();
                mClickListener.onItemClick(view, getAdapterPosition(), campsite_key);
            }
        }
    }


    /**
     * <h1>Set a listener from the adapter creator</h1>
     * This is used to set a listener when they choose a site from the list
     * In this case it is coming from the FavoritesActivity that uses the recycler view
     * @param itemClickListener
     */
    //The crazy custom onclick handling was taken from here
    //https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    /**
     * <h1>Interface for setting a listenter</h1>
     * This is used by the favorites activity to implement this specific listener when an
     * item in the recyclerView is clicked
     */
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, String campsite_key);
    }
}
