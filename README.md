# IntervalSeekBar
Android SeekBar with Interval and interval indicators.

<img src="http://imgur.com/b3hevWR.png" width="540" height="960"/>

Its a Simple View group wrapped over SeekBar. The indicator views are placed below SeekBar at equal distance. 

The indicator view is mostly customizable without touching the view internals.

<b><I>NOTE :</B>  There is a one major issue with this. IntervalSeekBar needs to be wrapped in a container before it can be placed in any general ViewGroup. 
 e.g.
 ```java
      ...
      ...
     <!--Place this code everywhere-->
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="10dp"
            android:layout_marginLeft="5dp"
            android:layout_marginRight="5dp"
            android:layout_marginTop="10dp">

            <com.ankitgusai.IntervalSeekbar.view.IntervalSeekBar
                android:id="@+id/seekBar1"
                android:layout_width="match_parent"
                android:layout_height="wrap_content" />
        </RelativeLayout>

      .....
      ....

 ```


If you put this IntervalSeekBar directly under a ViewGroup(e.g. RelativeLayout) with multiple child then it will not behave properly. i still have to figure out how to fix that.</I> 



How to use it
-------------------

To set intervals, create ArrayList of IntervalSeekBar.Item 
```java
 private ArrayList<IntervalSeekBar.Item> getItems() {
        ArrayList<IntervalSeekBar.Item> items = new ArrayList<>();
        items.add(new IntervalSeekBar.Item("much happy", R.drawable.dummy_thumb, 1));
        items.add(new IntervalSeekBar.Item("wow", 0, 1));
        items.add(new IntervalSeekBar.Item("", R.drawable.dummy_thumb, 1));
        items.add(new IntervalSeekBar.Item("", 0, 1));
        items.add(new IntervalSeekBar.Item("such doge", 0, 0));
        return items;
    }
```
and pass it over to IntervalSeekBar instance via setter method.
```java
 public void setItems(ArrayList<IntervalSeekBar.Item> items)
```


Getter/Setter for current position of SeekBar
  ```java  
public int getInterval() 
public void setInterval(interval) 
```


There is also a `IntervalChangeListener` implemented which will be triggered when interval changes 
  ```java  
  mBinding.seekBar1.setIntervalChangeListener(new IntervalSeekBar.OnIntervalChangeListener() {
            @Override
            public void onIntervalChanged(int pos) {
                Toast.makeText(MainActivity.this, "interval -> " + pos, Toast.LENGTH_SHORT).show();
            }
        });
  ```
  
The SeekBar is not directly exposed and is private element of the this Custom View Group but simple getter/setter can solve that. apart from that there are couple of method which you can use to customize the SeekBar.   

SeekBar line color can be changed with 
```java
mBinding.seekBar1.setSeekBarLineColor(ContextCompat.getColor(this, R.color.colorAccent));
````
and SeekBar thumb property can be set using 
```java
mBinding.seekBar1.setSeekBarThumb(mThumbDrawable);
```

<I>This is no way near a proper custom view, there are many flaws that you may encounter. i have plan to improve it.<I>  
