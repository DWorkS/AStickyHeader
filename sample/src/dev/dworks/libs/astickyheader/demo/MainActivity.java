package dev.dworks.libs.astickyheader.demo;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(getSampleAdapter());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ActivityInfo info = (ActivityInfo) l.getItemAtPosition(position);
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(this, info.name));
		startActivity(intent);
	}

	private ListAdapter getSampleAdapter() {
		ArrayList<ActivityInfo> items = new ArrayList<ActivityInfo>();
		final String thisClazzName = getClass().getName();

		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_ACTIVITIES);
			ActivityInfo[] aInfos = pInfo.activities;

			for (ActivityInfo aInfo : aInfos) {
				if (!thisClazzName.equals(aInfo.name)) {
					items.add(aInfo);
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return new SampleAdapter(this, items);
	}

	private static class SampleAdapter extends BaseAdapter {

		private final ArrayList<ActivityInfo> mItems;

		private final LayoutInflater mInflater;

		public SampleAdapter(Context context, ArrayList<ActivityInfo> activities) {
			mItems = activities;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public ActivityInfo getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = (TextView) convertView;
			if (tv == null) {
				tv = (TextView) mInflater.inflate(
						android.R.layout.simple_list_item_1, parent, false);
			}
			ActivityInfo item = getItem(position);
			if (!TextUtils.isEmpty(item.nonLocalizedLabel)) {
				tv.setText(item.nonLocalizedLabel);
			} else {
				tv.setText(item.labelRes);
			}
			return tv;
		}
	}
}