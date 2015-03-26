package com.skooterapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by aayushranaut on 3/24/15.
 * com.skooterapp
 */
public class FeedImageView extends ImageView {

    public interface ResponseObserver {
        public void onError();

        public void onSuccess();
    }
    private ResponseObserver mObserver;

    public void setResponseObserver(ResponseObserver observer) {
        mObserver = observer;
    }

    /**
     * The url of the image to load from the web
     */
    private  String mUrl;

    /**
     * Resource ID of the image to be used as placeholder until the
     * network image is loaded
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails
     */
    private int mErrorImageId;

    /**
     *  Width of the image to be downloaded
     */
    private int mWidth;

    /**
     * Height of the image to be downloaded
     */
    private int mHeight;

    /**
     * Local copy of ImageLoader
     */
    private ImageLoader mImageLoader;

    /**
     * Current ImageContainer
     */
    private ImageLoader.ImageContainer mImageContainer;

    public FeedImageView(Context context) {
        super(context);
    }

    public FeedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sest URL of the image that should be loaded into the view. Note that
     * calling this will immediately either set the cached image (if available)
     * or the default image specified by
     * {@link com.android.volley.toolbox.NetworkImageView#setDefaultImageResId(int)}
     *
     * NOTE: If applicable, {@link com.android.volley.toolbox.NetworkImageView#setDefaultImageResId(int)}
     * and {@link com.android.volley.toolbox.NetworkImageView#setErrorImageResId(int)} should be called
     * prior to calling this function.
     *
     * @param url The url that should be loaded into this ImageView
     * @param imageLoader ImageLoader that will be used to make the request
     */
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mUrl = url;
        mImageLoader = imageLoader;

        //The URL has potentially changed. See if it necessary to load the image
        loadImageIfNecessary(false);
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * Sets the default image resource id to be used for this view until the
     * attempt to load the image request is finished
     *
     * @param defaultImageId
     */
    public void setDefaultImageId(int defaultImageId) {
        mDefaultImageId = defaultImageId;
    }

    /**
     * Sets the error image resource id to be used for this view in the event
     * that the image requested fails to load
     *
     * @param errorImageId
     */
    public void setErrorImageId(int errorImageId) {
        mErrorImageId = errorImageId;
    }

    private void loadImageIfNecessary(final boolean isInLayoutPass) {
        final int width = getWidth();
        int height = getHeight();

        boolean isFullyWrapContent = getLayoutParams() != null
                && getLayoutParams().height == LinearLayout.LayoutParams.WRAP_CONTENT
                && getLayoutParams().width == LinearLayout.LayoutParams.WRAP_CONTENT;

        // if the view's bound aren't known yet, and this is not a
        // wrap-conten/wrap-content
        // view, hold off on loading the image

        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        //If the url to be laoded in this view is empty, cancel any
        // old requests and clear the currently loaded image

        if(TextUtils.isEmpty(mUrl)) {
            if(mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        //The pre-existing content of this view didn't match the current URL
        // Load the new image from the network
        adjustImageAspect(mWidth, mHeight);
        ImageLoader.ImageContainer newContainer = mImageLoader.get(mUrl,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                        // If this was an immediate response that was delivered
                        // inside of a layout
                        // pass do not set the image immediately as it will
                        // trigger a requestLayout inside of a layout.
                        // Instead, defer settings the image by posting back to the main thread.
                        if(isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }

                        int bWidth = 0, bHeight = 0;
                        if (response.getBitmap() != null) {
                            setImageBitmap(response.getBitmap());
                            bWidth = response.getBitmap().getWidth();
                            bHeight = response.getBitmap().getHeight();
//                            adjustImageAspect(bWidth, bHeight);
                        } else if (mDefaultImageId != 0) {
                            setImageResource(mDefaultImageId);
                        }

                        if(mObserver != null) {
                            mObserver.onSuccess();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        }

                        if(mObserver != null) {
                            mObserver.onError();
                        }
                    }
                }
        );
        // Update the ImageContainer to be the new bitmap container
        mImageContainer = newContainer;
    }

    private void setDefaultImageOrNull() {
        if(mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        } else {
            setImageBitmap(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view
            mImageContainer.cancelRequest();
            setImageBitmap(null);

            //Also clear out the container so we can reload the image if necessary
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private void adjustImageAspect(int bWidth, int bHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();

        if (bWidth == 0 || bHeight == 0) {
            return;
        }

        int sWidth = getWidth();
        int new_height = 0;
        new_height = sWidth * bHeight / bWidth;

        params.width = sWidth;
        params.height = new_height;
        setLayoutParams(params);
    }
}
