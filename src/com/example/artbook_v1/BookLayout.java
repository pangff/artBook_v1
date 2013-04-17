package com.example.artbook_v1;

import java.io.ByteArrayInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.example.artbook_v1.CurlView.OnPageAnimationDoneListener;

/**
 * Created with IntelliJ IDEA. User: marshal Date: 13-4-3 Time: 下午3:59 To change
 * this template use File | Settings | File Templates.
 */
public class BookLayout extends FrameLayout implements CurlView.PageProvider {
	private WebView webView;
	private int dpWidth, dpHeight;
	private float scale;
	private ProgressDialog loadDialog;
	private CustomCurlView curlView;
	private TopView topView;
	private float mScrollDiffY = 0;
	private float mLastTouchY = 0;
	private float mScrollDiffX = 0;
	private float mLastTouchX = 0;
	private boolean isInSelection;
	protected Context ctx;

	public BookLayout(Context context) {
		super(context);
		this.init();
	}

	public BookLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public BookLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() {
		this.ctx = getContext();
		this.scale = getContext().getResources().getDisplayMetrics().density;

		webView = new WebView(getContext());

		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

		webView.addJavascriptInterface(this, "Android");

		webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage message) {
				Log.d("artbook",
						message.message() + " -- From line "
								+ message.lineNumber() + " of "
								+ message.sourceId());
				return true;
			}
		});


		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					setCurlViewVisible(true);
					hasDispatch = false;
				}

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if(!isInSelection && !hasDispatch){
						Log.e("#######################",":");
						event.setAction(MotionEvent.ACTION_DOWN);
						dispatchTouchEvents(event);
						//webView.setVisibility(View.INVISIBLE);
						return true;
					} 
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					isInSelection  = false;
				}
				return false;
			}
		});
		
		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Log.e("!!!!!!!!!!!!!", "long touch");
				setWebViewVisible(true);
				setCurlViewVisible(false);
				webView.loadUrl("javascript:android.selection.longTouch();");
				isInSelection = true;
				return true;
			}
		});
		
		

	
		curlView = new CustomCurlView(getContext());
		curlView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		curlView.setPageProvider(this);
		curlView.setViewMode(CurlView.SHOW_ONE_PAGE);
		curlView.setCurrentIndex(0);
		curlView.setBackgroundColor(0xFF202830);
		//curlView.setVisibility(View.INVISIBLE);
		
		//curlView.setWebView(webView);

		topView = new TopView(getContext());
		topView.setBackgroundColor(Color.BLUE);
		//topView.setVisibility(View.INVISIBLE);
//		topView.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				
//				return true;
//			}
//		});
		
		
		
		
		
		this.addView(topView);
		this.addView(webView);
		this.addView(curlView);
		//curlView.setAllowLastPageCurl(false);
		
		
		
		/**
		 * curlview动画结束回调
		 */
		curlView.setOnPageAnimationDoneListener(new OnPageAnimationDoneListener() {
			@Override
			public void onPageAnimationDone(final int index) {
				handler.post(new Runnable() {
					@Override
					public void run() {
//						Log.e("%%%%%%%%%%%%%%%","翻页到第"+index+"页");
						
						setCurlViewVisible(false);
						setWebViewVisible(true);
					}
				});
			}

			@Override
			public void onBeforeAnimationDone(int index) {
				webView.loadUrl("javascript:justPageScroll("+(index)+")");
			}

			@Override
			public void onPageNoAnimation(int index) {
				setCurlViewVisible(false);
				setWebViewVisible(true);
			}
		});
		
	}
	
	/**
	 * 动画结束后js同步滚动完成，隐藏curlview
	 */
	public void onJustScrollFinish() {
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				setCurlViewVisible(false);
//				setWebViewVisible(true);
//			}
//		}, 100);
//		setCurlViewVisible(false);
//		setWebViewVisible(true);
		Log.e("%%%%%%%%%%%%%%%","翻页完毕加载webview");
	}
	
	/**
	 * 事件传递给curlview
	 * @param event
	 */
	public void dispatchTouchEvents(final MotionEvent event){
		handler.post(new Runnable() {
			@Override
			public void run() {
				
				if(event!=null && curlView.getVisibility() == View.VISIBLE ){
					event.setAction(MotionEvent.ACTION_DOWN);
					event.setSource(curlView.getId());
					BookLayout.this.dispatchTouchEvent(event);
					hasDispatch = true;
				}
			}
		});
	}
	
	public void setWebViewVisible(boolean flag){
		
		if(flag){
			//webView.setVisibility(View.VISIBLE);
			webView.setAlpha(1);
			webView.getParent().bringChildToFront(webView);
		}else{
			webView.setAlpha(0);
			//webView.setVisibility(flag?View.VISIBLE:View.INVISIBLE);
		}
	}

	boolean hasDispatch = false;
	public void setCurlViewVisible(final boolean visible) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(!visible){
					curlView.setAlpha(0);
				}else{
					curlView.setAlpha(1);
					curlView.getParent().bringChildToFront(curlView);
				}
				
			}
		});
	}

	private Handler handler = new Handler();

	private int pageCount = 1;
	private int currentPage = 0;
	private int currentIndex = 0;
	private int globalIndex = -1;
	long time = 0;

	
	/**
	 * 页面加载完成
	 * @param totalWidth
	 * @param pageWidth
	 */
	public void onPageLoaded(final float totalWidth, final float pageWidth) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (totalWidth % pageWidth == 0) {
					pageCount = (int) (totalWidth / pageWidth);
				} else {
					pageCount = (int) (totalWidth / pageWidth) + 1;
				}
				time = System.currentTimeMillis();
				webView.loadUrl("javascript:pageScroll(" + currentPage + ")");
			}
		});
	}
	

	/**
	 * 初始化webview截屏
	 */
	public void onScrollFinish() {

		handler.post(new Runnable() {
			@Override
			public void run() {
				// loadDialog.dismiss();
				Log.e("加载页数:", "loadings" + currentPage);
				
				Bitmap bitmap = ArtBookUtils.loadBitmapFromView(webView, webView.getWidth(),
						webView.getHeight());

				ArtBookUtils.saveBitmap(bitmap,
						"loadings_" + currentPage + ".png");

				currentPage++;
				if (currentPage <= pageCount) {
					webView.loadUrl("javascript:pageScroll(" + currentPage
							+ ")");
				} else {
					webView.loadUrl("javascript:resetScroll()");
					Log.e("全部时间:", "" + (System.currentTimeMillis() - time));
				}
			}
		});
	}
	
	/**
	 * 恢复第1页
	 */
	public void onResetScrollFinish(){
		setCurlViewVisible(false);
		setWebViewVisible(true);
		loadDialog.dismiss();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		dpWidth = (int) (w / scale);
		dpHeight = (int) (h / scale);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view,
					String url) {
				if (url.equals("file:///android_asset/book.css")) {
					StringBuilder builder = new StringBuilder();
					builder.append("" + "        body{"
							+ "           margin:0px;" + "        }"
							+ "        #content{" + "           margin:0px;"
							+ "            width: "
							+ (dpWidth)
							+ "px;"
							+ "            height:"
							+ (dpHeight - 20)
							+ "px;"
							+ "            overflow: hidden;"
							+ "        }"
							+ "        header{\n"
							+ "           font-size: 40px;\n"
						
							+ "        }"
							+ "        img{\n"
							+ "            max-width: "
							+ (dpWidth - 20)
							+ "px;\n"
							+ "        }"
							+ "        article {"
							+ "            "
							+ // margin: 4px 5px 4px 5px;
							"        }"
							+ "        article{"
							+ "            -webkit-column-width:"
							+ dpWidth
							+ "px;"
		
							+ "            -webkit-column-gap: 10px;"
							+ "            height:"
							+ (dpHeight - 20 - 40)
							+ "px;"
							+ "            padding: 0px;"
							+ "            margin:0px;"
							+ "            margin-top:40px"
							+ "        }"
							+ "        article *{"
							+ "            padding:5px;"
							+ "        }"
							
							);
					
					
					ByteArrayInputStream inputStream = new ByteArrayInputStream(
							builder.toString().getBytes());
					return new WebResourceResponse("text/css", "UTF-8",
							inputStream);
				}

				return null;
			}
		});

		webView.loadUrl("file:///android_asset/book.html");

		loadDialog = new ProgressDialog(getContext());
		loadDialog.setMessage("加载网页..");
		loadDialog.setCancelable(false);
		loadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadDialog.show();
	}

	@Override
	public int getPageCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public synchronized void updatePage(CurlPage page, int width, int height, final int index) {
//		Log.e("*****************************", "index:"+index+"currentIndex:"+currentIndex);
//		if((currentIndex>index) && currentIndex>0 && globalIndex!=index){
//			Log.e("*****************************", "index:"+index+"currentPage:"+currentIndex);
//			currentIndex--;
//			webView.loadUrl("javascript:justPageScroll("+(currentIndex)+")");
//		}else if(globalIndex!=index){
//			webView.loadUrl("javascript:justPageScroll("+(index)+")");
//			currentIndex = index;
//		}
//		webView.loadUrl("javascript:justPageScroll("+(index)+")");
//		webView.loadUrl("javascript:justPageScroll("+(index)+")");
//		handler.post(new Runnable() {
//			@Override
//			public void run() {
//				webView.loadUrl("javascript:justPageScroll("+(index)+")");
//			}
//		});
	
		
		
		
//		webView.scrollTo((getWidth()) * index, 0);
//        Bitmap bitmap = ArtBookUtils.loadBitmapFromView(webView, webView.getWidth(),
//        		webView.getHeight());
        
		Bitmap bitmap = ArtBookUtils.loadBitmap(index);
		if(bitmap!=null){
			page.setTexture(bitmap, CurlPage.SIDE_FRONT);
			page.setTexture(flip(new BitmapDrawable(bitmap)).getBitmap(),CurlPage.SIDE_BACK);
			page.setColor(Color.argb(50, 255, 255, 255), CurlPage.SIDE_BACK);
		}
		
		//setCurlViewVisible(false,null);
	}
	

	public BitmapDrawable flip(BitmapDrawable d) {
		Matrix m = new Matrix();
		m.preScale(-1, 1);
		Bitmap src = d.getBitmap();
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(),src.getHeight(), m, false);
		
		dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		return new BitmapDrawable(dst);
	}
	
	/**
	 * Returns the density independent value of the given float
	 * 
	 * @param val
	 * @param ctx
	 * @return
	 */
	public float getDensityIndependentValue(float val, Context ctx) {

		Display display = ((WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return val / (metrics.densityDpi / 160f);

	}

	@Override
	public void laseUpdatePage() {
		//webView.loadUrl("javascript:justPageScroll("+(0)+")");
	}

}
