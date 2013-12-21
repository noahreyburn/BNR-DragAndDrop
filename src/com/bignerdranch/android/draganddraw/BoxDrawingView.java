package com.bignerdranch.android.draganddraw;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BoxDrawingView extends View {
	public static final String EXTRA_BOX_DRAWING_ID = "com.bignerdranch.android.draganddrop_boxdrawingview_id";
	
	public static final String TAG = "BoxDrawingView";
	
	private Box mCurrentBox;
	private ArrayList<Box> mBoxes = new ArrayList<Box>();
	private Paint mBoxPaint;
	private Paint mBackgroundPaint;
	private float rotate;

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		bundle.putSerializable(EXTRA_BOX_DRAWING_ID, mBoxes);
		return bundle;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if( state instanceof Bundle ){
			Bundle bundle = (Bundle) state;
			mBoxes = (ArrayList<Box>)bundle.getSerializable( EXTRA_BOX_DRAWING_ID );
			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}
	
	// Used when creating the view
	public BoxDrawingView(Context context) {
		this( context, null );
	}
	
	// Used when inflating the view from XML
	// AttributeSet contains XML attributes
	public BoxDrawingView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		
		// Paint the boxes a nice semi transparent red ( ARGB )
		mBoxPaint = new Paint();
		mBoxPaint.setColor( 0x22ff0000 );
		
		// Paint the back ground off-white
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor( 0xfff8efe0 );
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public boolean onTouchEvent( MotionEvent event ) {
		
		PointF curr = new PointF( event.getX(), event.getY() );
		Log.i( TAG, "Recived event at x=" + curr.x + " y=" + curr.y + ":" );
		
		switch ( event.getActionMasked() ) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "  ACTION_DOWN" );
			// Reset drawing state
			mCurrentBox = new Box(curr);
			mBoxes.add(mCurrentBox);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "  ACTION_MOVE" );
			if ( mCurrentBox != null ) {
				mCurrentBox.setCurrent(curr);
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "  ACTION_UP" );
			mCurrentBox = null;
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.i(TAG, "  ACTION_CANCEL" );
			mCurrentBox = null;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			int index = event.getActionIndex();
			rotate = event.getTouchMajor( index );
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.i(TAG, "  ACTION_POINTER_UP" );
			rotate = 0;
			break;
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Fill the back ground
		canvas.drawPaint( mBackgroundPaint );
		
		for ( Box box : mBoxes ) {
			float left = Math.min( box.getOrigin().x,  box.getCurrent().x );
			float right = Math.max( box.getOrigin().x,  box.getCurrent().x );
			float top = Math.min( box.getOrigin().y,  box.getCurrent().y );
			float bottom = Math.max( box.getOrigin().y,  box.getCurrent().y );
		
			canvas.drawRect( left, top, right, bottom, mBoxPaint );
			
			if ( 0 != rotate ) {
				canvas.rotate(rotate, Math.abs(left - right), Math.abs(top - bottom) );

			}
		}
	}
}
