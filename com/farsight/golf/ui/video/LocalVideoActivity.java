
package com.farsight.golf.ui.video;

import java.io.File;
import java.util.ArrayList;

import com.farsight.golf.R;
import com.farsight.golf.adapter.GridViewAdapter;
import com.farsight.golf.ui.component.CommonIntentExtra;
import com.farsight.golf.util.MediaChooserConstants;
import com.yixia.camera.model.MediaObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class LocalVideoActivity extends Activity implements OnScrollListener {

	private final static Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	private final static String MEDIA_DATA = MediaStore.Video.Media.DATA;

	private GridViewAdapter mVideoAdapter;
	private GridView mVideoGridView;
	private Cursor mCursor;
	private int mDataColumnIndex;
	private ArrayList<String> mSelectedItems = new ArrayList<String>();
	private String choicedSelectedItems;
	private ArrayList<MediaModel> mGalleryModelList;
	int lastPosition = -1;
	private View mView;
	private OnVideoSelectedListener mCallback;


	// Container Activity must implement this interface
	public interface OnVideoSelectedListener {
		public void onVideoSelected(int count);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_grid_layout_media_chooser);
		RelativeLayout toolbar = (RelativeLayout) findViewById(R.id.toolBarLayout);
		toolbar.setBackgroundColor(Color.parseColor("#5fb336"));
		mVideoGridView = (GridView)findViewById(R.id.gridViewFromMediaChooser);
		initVideos();
		
	}


	public void initVideos() {

		try {
			final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
			//Here we set up a string array of the thumbnail ID column we want to get back

			String [] proj = {MediaStore.Video.Media.DATA,MediaStore.Video.Media._ID};

			mCursor =  this.getContentResolver().query(MEDIA_EXTERNAL_CONTENT_URI, proj, null,null, orderBy + " DESC");
			setAdapter();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setAdapter() {
		int count = mCursor.getCount();

		if(count > 0){
			mDataColumnIndex = mCursor.getColumnIndex(MEDIA_DATA);

			//move position to first element
			mCursor.moveToFirst();

			mGalleryModelList = new ArrayList<MediaModel>();
			for(int i= 0; i < count; i++) {
				mCursor.moveToPosition(i);
				String url = mCursor.getString(mDataColumnIndex);
				mGalleryModelList.add(new MediaModel(url, false));
			}


			mVideoAdapter =  new GridViewAdapter(this, 0, mGalleryModelList, true);
			mVideoAdapter.videoFragment = this;
			mVideoGridView.setAdapter(mVideoAdapter);
			mVideoGridView.setOnScrollListener(this);
		}else{
			Toast.makeText(this,"no file", Toast.LENGTH_SHORT).show();

		}


		mVideoGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);
				Intent intent = new Intent(LocalVideoActivity.this,VideoPlayerActivity.class);
				intent.putExtra("path", galleryModel.url);
				/*File file = new File(galleryModel.url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "video/*");*/
				startActivity(intent);
				return false;
			}
		});

		mVideoGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// update the mStatus of each category in the adapter
				GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
				MediaModel galleryModel = (MediaModel) adapter.getItem(position);
				
				if(lastPosition != -1)
					adapter.getItem(lastPosition).status = ! adapter.getItem(lastPosition).status;
				if(! galleryModel.status){
					long size = MediaChooserConstants.ChekcMediaFileSize(new File(galleryModel.url.toString()), true);
					if(size != 0){
						Toast.makeText(LocalVideoActivity.this, "文件为空  " + MediaChooserConstants.SELECTED_VIDEO_SIZE_IN_MB + " ", Toast.LENGTH_SHORT).show();
						return;
					}

					if((MediaChooserConstants.MAX_MEDIA_LIMIT == MediaChooserConstants.SELECTED_MEDIA_COUNT)){
						if (MediaChooserConstants.SELECTED_MEDIA_COUNT < 2) {
							Toast.makeText(LocalVideoActivity.this, "  文件过大" + MediaChooserConstants.SELECTED_MEDIA_COUNT + " " , Toast.LENGTH_SHORT).show();
							return;
						} else {
							Toast.makeText(LocalVideoActivity.this, " 文件过小 " + MediaChooserConstants.SELECTED_MEDIA_COUNT + " ", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}

				// inverse the status
				galleryModel.status = ! galleryModel.status;
				adapter.notifyDataSetChanged();
				lastPosition = position;
				choicedSelectedItems = galleryModel.url;
				/*if (galleryModel.status) {
					mSelectedItems.add(galleryModel.url.toString());
					MediaChooserConstants.SELECTED_MEDIA_COUNT ++;

				}else{
					mSelectedItems.remove(galleryModel.url.toString().trim());
					MediaChooserConstants.SELECTED_MEDIA_COUNT --;
				}

				if (mCallback != null) {
					mCallback.onVideoSelected(mSelectedItems.size());
					Intent intent = new Intent();
					intent.putStringArrayListExtra("list", mSelectedItems);
					setResult(Activity.RESULT_OK, intent);
				}*/

			}
		});

	}

	public GridViewAdapter getAdapter() {
		if (mVideoAdapter != null) {
			return mVideoAdapter;
		}
		return null;
	}

	public ArrayList<String> getSelectedVideoList() {
		return mSelectedItems;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//		if (view.getId() == android.R.id.list) {
		if (view == mVideoGridView) {
			// Set scrolling to true only if the user has flinged the
			// ListView away, hence we skip downloading a series
			// of unnecessary bitmaps that the user probably
			// just want to skip anyways. If we scroll slowly it
			// will still download bitmaps - that means
			// that the application won't wait for the user
			// to lift its finger off the screen in order to
			// download.
			if (scrollState == SCROLL_STATE_FLING) {
				//chk
			} else {
				mVideoAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	public void nextStep(View view) {
		if(!TextUtils.isEmpty(choicedSelectedItems)) {
			
			String key = choicedSelectedItems.substring(choicedSelectedItems.lastIndexOf("/")+1, choicedSelectedItems.lastIndexOf("."));
			String path = choicedSelectedItems.substring(0, choicedSelectedItems.lastIndexOf("/"));
			MediaObject mMediaObject =  new MediaObject(key,path);
			Intent intent = new Intent(this, MediaPreviewActivity.class);
			Bundle bundle = getIntent().getExtras();
			if (bundle == null)
				bundle = new Bundle();
			bundle.putSerializable(CommonIntentExtra.EXTRA_MEDIA_OBJECT, mMediaObject);
			bundle.putString("output", mMediaObject.getOutputTempVideoPath());
			bundle.putBoolean("Rebuild", false);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}
	}

}

