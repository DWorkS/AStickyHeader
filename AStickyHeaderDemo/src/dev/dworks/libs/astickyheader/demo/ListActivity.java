package dev.dworks.libs.astickyheader.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import dev.dworks.libs.astickyheader.R;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class ListActivity extends Activity {
	private ListView list;
	private ImageAdapter mAdapter;
	private ArrayList<Section> sections = new ArrayList<Section>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initControls();
	}

	private void initControls() {
		list = (ListView)findViewById(R.id.list);
		mAdapter = new ImageAdapter(this);
		for (int i = 0; i < mHeaderPositions.length; i++) {
			sections.add(new Section(mHeaderPositions[i], mHeaderNames[i]));
		}
		SimpleSectionedListAdapter simpleSectionedGridAdapter = new SimpleSectionedListAdapter(this, R.layout.list_item_header, mAdapter);
		simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
		list.setAdapter(simpleSectionedGridAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.grid, menu);
		return true;
	}
	
	private class ImageAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;

		public ImageAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mImageIds.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, parent, false);
			}

			image = ViewHolder.get(convertView, R.id.image);
			image.setImageResource(mImageIds[position]);
			return convertView;
		}

	}
	
    private Integer[] mImageIds = { R.drawable.empty_photo,R.drawable.empty_photo,
    		R.drawable.empty_photo,R.drawable.empty_photo,
    		R.drawable.empty_photo,R.drawable.empty_photo,
    		R.drawable.empty_photo,R.drawable.empty_photo,
    		R.drawable.empty_photo,R.drawable.empty_photo,
    		R.drawable.empty_photo,R.drawable.empty_photo,
    		R.drawable.empty_photo,R.drawable.empty_photo,
    };
    
    private String[] mHeaderNames = { "A Header", "One Header", "Some Header" };
    private Integer[] mHeaderPositions = { 0, 6, 11 };


	public static class ViewHolder {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}
}
