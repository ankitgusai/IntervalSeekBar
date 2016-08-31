package com.ankitgusai.IntervalSeekbar;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ankitgusai.IntervalSeekbar.databinding.ActivityMainBinding;
import com.ankitgusai.IntervalSeekbar.view.IntervalSeekBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mBinding;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.seekBar1.setItems(getItems());

        mBinding.seekBar1.setSeekBarLineColor(ContextCompat.getColor(this, R.color.colorSeekBarTestColor));
        mBinding.seekBar1.setIntervalChangeListener(new IntervalSeekBar.OnIntervalChangeListener() {
            @Override
            public void onIntervalChanged(int pos) {
                Toast.makeText(MainActivity.this, "interval -> " + pos, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<IntervalSeekBar.Item> getItems() {
        ArrayList<IntervalSeekBar.Item> items = new ArrayList<>();
        items.add(new IntervalSeekBar.Item("much happy", R.drawable.dummy_thumb, 1));
        items.add(new IntervalSeekBar.Item("wow", 0, 1));
        items.add(new IntervalSeekBar.Item("", R.drawable.dummy_thumb, 1));
        items.add(new IntervalSeekBar.Item("", 0, 1));
        items.add(new IntervalSeekBar.Item("such doge", 0, 0));
        return items;
    }
}


