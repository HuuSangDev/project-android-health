package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_selfcare.R;
import com.example.app_selfcare.Adapter.IngredientsAdapter;
import com.example.app_selfcare.Data.Model.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Tomato", "Củ cà chua", "500g"));
        ingredients.add(new Ingredient("Cabbage", "Bắp cải", "300g"));
        ingredients.add(new Ingredient("Taco", "Bánh taco", "300g"));
        ingredients.add(new Ingredient("Bread", "Bánh mì", "300g"));

        IngredientsAdapter adapter = new IngredientsAdapter(ingredients);
        recyclerView.setAdapter(adapter);

        return view;
    }
}