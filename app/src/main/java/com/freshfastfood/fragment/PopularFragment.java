package com.freshfastfood.fragment;

import static com.freshfastfood.utils.SessionManager.iscart;
import static com.freshfastfood.utils.Utiles.productItems;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freshfastfood.R;
import com.freshfastfood.activity.HomeActivity;
import com.freshfastfood.activity.ItemDetailsActivity;
import com.freshfastfood.adepter.ReletedItemAllAdp;
import com.freshfastfood.model.ProductItem;
import com.freshfastfood.model.User;
import com.freshfastfood.utils.SessionManager;

public class PopularFragment extends Fragment implements ReletedItemAllAdp.ItemClickListener {
    RecyclerView reyCategory;

    SessionManager sessionManager;
    User userData;
    ReletedItemAllAdp itemAdp;
    public PopularFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reyCategory = view.findViewById(R.id.recycler_view);
        sessionManager = new SessionManager(getActivity());
        userData = sessionManager.getUserDetails();
        HomeActivity.getInstance().setFrameMargin(0);
        reyCategory.setHasFixedSize(true);
        reyCategory.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        itemAdp = new ReletedItemAllAdp(getActivity(), productItems, this);
        reyCategory.setAdapter(itemAdp);
    }

    @Override
    public void onItemClick(ProductItem productItem, int position) {
        getActivity().startActivity(new Intent(getActivity(), ItemDetailsActivity.class).putExtra("MyClass", productItem).putParcelableArrayListExtra("MyList", productItem.getPrice()));
    }
    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().titleChange();
        HomeActivity.getInstance().setFrameMargin(60);
        if(itemAdp!=null){
            itemAdp.notifyDataSetChanged();
        }
        if (iscart) {
            iscart = false;
            CardFragment fragment = new CardFragment();
            HomeActivity.getInstance().callFragment(fragment);
        }
    }
}
