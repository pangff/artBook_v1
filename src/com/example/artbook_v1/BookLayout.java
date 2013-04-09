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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

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

		webView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				longTouch = true;
				return true;
			}
		});

		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {

				return false;
			}
		});

		this.addView(webView);

		curlView = new CustomCurlView(getContext());
//		curlView.setLayoutParams(new ViewGroup.LayoutParams(
//				ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.MATCH_PARENT));
		curlView.setPageProvider(this);
		curlView.setViewMode(CurlView.SHOW_ONE_PAGE);
		curlView.setCurrentIndex(0);
		curlView.setBackgroundColor(0xFF202830);
		curlView.setVisibility(View.INVISIBLE);

		topView = new TopView(getContext());
		topView.setVisibility(View.INVISIBLE);
		
		
		this.addView(curlView,new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		this.addView(topView);
	}

	boolean longTouch;

	public void setCurlViewVisible(final boolean visible) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				curlView.setVisibility(visible ? VISIBLE : INVISIBLE);
			}
		});

	}

	private Handler handler = new Handler();

	private int pageCount = 1;
	private int currentPage = 1;
	long time = 0;

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

	public void onScrollFinish() {

		handler.post(new Runnable() {
			@Override
			public void run() {
				// loadDialog.dismiss();
				Log.e("加载页数:", "loading" + currentPage);
				webView.postInvalidate();
				ArtBookUtils.saveBitmap(ArtBookUtils
						.loadBitmapFromViewByCapture(getContext(), webView),
						"loading" + currentPage + ".png");

				currentPage++;
				if (currentPage <= pageCount) {
					webView.loadUrl("javascript:pageScroll(" + currentPage
							+ ")");
				} else {
					loadDialog.dismiss();
					 setCurlViewVisible(true);
					Log.e("全部时间:", "" + (System.currentTimeMillis() - time));
				}
			}
		});
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
							+ "        }");
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

	int pageIndex = 0;

	public void nextPage() {
		pageIndex++;
		webView.scrollTo((int) ((dpWidth + 10) * scale) * pageIndex, 0);
	}

	@Override
	public int getPageCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public void updatePage(CurlPage page, int width, int height, int index) {
		Bitmap bitmap = ArtBookUtils.loadBitmap(index+1);

		page.setTexture(bitmap, CurlPage.SIDE_FRONT);
		page.setTexture(flip(new BitmapDrawable(bitmap)).getBitmap(),
				CurlPage.SIDE_BACK);
		page.setColor(Color.argb(50, 255, 255, 255), CurlPage.SIDE_BACK);
	}
	

	public BitmapDrawable flip(BitmapDrawable d) {
		Matrix m = new Matrix();
		m.preScale(-1, 1);
		Bitmap src = d.getBitmap();
		Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
				src.getHeight(), m, false);
		dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
		return new BitmapDrawable(dst);
	}
}
