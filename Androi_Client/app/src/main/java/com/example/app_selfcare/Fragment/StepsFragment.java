// File: app/src/main/java/com/example/app_selfcare/fragment/StepsFragment.java
package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.R;
import com.example.app_selfcare.Data.Model.Step;
import com.example.app_selfcare.Adapter.StepsAdapter;

import java.util.ArrayList;
import java.util.List;

public class StepsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_steps);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu các bước nấu ăn
        List<Step> steps = new ArrayList<>();
        steps.add(new Step("1", "Chuẩn bị gà: Rửa sạch với nước, cắt dọc theo chiều dọc, ướp với muối, tiêu, bột ngọt trong 10 phút."));
        steps.add(new Step("2", "Chiên gà: Cho dầu vào chảo đun nóng 4-5 phút, cho gà vào chiên mỗi mặt khoảng 5-7 phút đến khi vàng giòn."));
        steps.add(new Step("3", "Làm nước mắm: Pha 3 muỗng nước mắm + 2 muỗng đường + 1 muỗng nước cốt chanh + tỏi ớt băm."));
        steps.add(new Step("4", "Trình bày: Cho gà ra đĩa, rưới nước mắm lên trên, ăn kèm rau sống và cơm nóng."));

        StepsAdapter adapter = new StepsAdapter(steps);
        recyclerView.setAdapter(adapter);

        return view;
    }
}