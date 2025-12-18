package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.SearchAdapter;
import com.example.app_selfcare.Data.Model.SearchItem;
import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageView btnBack;
    private RecyclerView rvSuggestions;

    private SearchAdapter adapter;
    private final List<SearchItem> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.etSearchQuery);
        btnBack = view.findViewById(R.id.btnBack);
        rvSuggestions = view.findViewById(R.id.rvSuggestions);

        setupRecyclerView();
        setupEvent();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new SearchAdapter(list, item -> {
            // TODO: xử lý click item (đóng fragment, mở detail)
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSuggestions.setAdapter(adapter);

        // Fake data test UI
        list.add(new SearchItem(1, "Salad trứng luộc", SearchItem.TYPE_FOOD));
        list.add(new SearchItem(2, "Tập thân trên", SearchItem.TYPE_WORKOUT));
        list.add(new SearchItem(3, "Ức gà áp chảo", SearchItem.TYPE_FOOD));
        adapter.notifyDataSetChanged();
    }

    private void setupEvent() {
        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
    }
}
