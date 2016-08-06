package com.example.abhishek.indoorlocalization;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class LocationFinder extends ActionBarActivity {

    ListView mList;
    String[] mBuildings;
    String[] mAutoCompleteBuilding;
    private AutoCompleteTextView mAuto;
    private Button mFind;
    Bitmap bm = null;
    RoundImage roundedImage;
    int[] image = {R.drawable.image_wells, R.drawable.image_kelley, R.drawable.image_informatics, R.drawable.image_jordan,
                    R.drawable.image_law, R.drawable.image_music, R.drawable.image_woodburn, R.drawable.image_ballantine};
    DatabaseHelperAdapter mDatabaseHelperAdapter;
    EnterSignsAndLocation mEnterData = null;
    private String mDebug = LocationFinder.class.getName();
    private int mNumberOfFloors = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mAuto = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        mAuto.setImeActionLabel("GO",EditorInfo.IME_ACTION_GO);
        mList = (ListView) findViewById(R.id.listView);
        mFind = (Button)findViewById(R.id.button_find_location);

        init();
        softKeyboardFunction(mAuto,false);

        mFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuto.getText().toString().equals("")){
                    Toast.makeText(LocationFinder.this, "Please enter a building name", Toast.LENGTH_LONG).show();
                }
                else
                    startLocatorDetailsActivity(mAuto.getText().toString(),mDatabaseHelperAdapter.getFloorCount(mAuto.getText().toString()));
            }
        });
        mAuto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                softKeyboardFunction(mAuto, true);
                return false;
            }
        });
        mAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    check_Text_entered(v);
                }
                return false;
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = (TextView) view.findViewById(R.id.list_building_name);
                String mBuildingName = text.getText().toString();
                /* Get the number of floors on the basis of building clicked */
                Log.d(mDebug, "Number of floors in "+mBuildingName+" are: " + mDatabaseHelperAdapter.getFloorCount(mBuildingName));
                startLocatorDetailsActivity(mBuildingName,mDatabaseHelperAdapter.getFloorCount(mBuildingName));
            }
        });
    }

    private void check_Text_entered(TextView v){
        String mBuildingName = v.getText().toString();
        if(!mBuildingName.equals("")) {
            Log.d(mDebug, "Name of the building is: " + mBuildingName);
            startLocatorDetailsActivity(mBuildingName, mDatabaseHelperAdapter.getFloorCount(mBuildingName));
        }
        else
            Toast.makeText(LocationFinder.this, "Please enter a building name", Toast.LENGTH_LONG).show();
    }

    private void startLocatorDetailsActivity(String str, int no_of_floors){
        if(str.equalsIgnoreCase("jordan hall")) {
            Intent intent = new Intent(LocationFinder.this, LocatorDetailsActivity.class);
            intent.putExtra("number_of_floors", String.valueOf(no_of_floors));
            startActivity(intent);
        }
        else{
            Toast.makeText(LocationFinder.this, "Coming Soon", Toast.LENGTH_LONG).show();
        }
    }
    private void init(){
        mEnterData = new EnterSignsAndLocation();
        mEnterData.fill_data();
        mDatabaseHelperAdapter = DatabaseHelperAdapter.getInstance(this);
        //dataHelper= new DatabaseHelperAdapter(this);
        mAutoCompleteBuilding = getResources().getStringArray(R.array.all_buildings);
        mBuildings = getResources().getStringArray(R.array.popular_buildings_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LocationFinder.this,android.R.layout.simple_list_item_1,mAutoCompleteBuilding);
        mAuto.setAdapter(adapter);
        PopularBuildingAdapter mPopularAdapter = new PopularBuildingAdapter(LocationFinder.this,image,mBuildings);
        mList.setAdapter(mPopularAdapter);
    }

    private void softKeyboardFunction(AutoCompleteTextView auto, boolean isFocused){
        auto.setFocusable(isFocused);
        auto.setFocusableInTouchMode(isFocused);
        if(isFocused){
            auto.requestFocus();
        }
        else{
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(auto.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public class PopularBuildingAdapter extends ArrayAdapter<String>{

        Context context;
        String[] mBuildings;
        int[] image;
        public PopularBuildingAdapter(Context context, int[] image, String[] mBuildings) {
            super(context, R.layout.single_row,R.id.list_building_name,mBuildings);
            this.context = context;
            this.mBuildings = mBuildings;
            this.image = image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            if(row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.single_row, parent, false);
            }
            TextView mText = (TextView) row.findViewById(R.id.list_building_name);
            mText.setText(mBuildings[position]);
            ImageView images = (ImageView) row.findViewById(R.id.images);
            bm = BitmapFactory.decodeResource(getResources(),image[position]);
            roundedImage = new RoundImage(bm, getResources().getColor(R.color.custom_blue_shade_1));
            images.setImageDrawable(roundedImage);

            return row;
        }
    }

    private void cleanup(){
        if(bm!=null)
            bm.recycle();
    }

}
