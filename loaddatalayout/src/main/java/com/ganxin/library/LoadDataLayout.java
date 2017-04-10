package com.ganxin.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Description : 公共组件：加载数据Layout  <br/>
 * author : WangGanxin <br/>
 * date : 2017/3/31 <br/>
 * email : mail@wangganxin.me <br/>
 */
public class LoadDataLayout extends FrameLayout implements View.OnClickListener {

    public final static int LOADING = 10;
    public final static int SUCCESS = 11;
    public final static int EMPTY = 12;
    public final static int ERROR = 13;
    public final static int NO_NETWORK = 14;

    public final static int FULL = 20;
    public final static int BUTTON = 21;

    private Context mContext;
    private int currentState;

    private View contentView;
    private View loadingView;
    private View emptyView;
    private View errorView;
    private View noNetworkView;

    private LinearLayout emptyLayout, errorLayout, noNetWorkLayout;
    private ImageView emptyImg, errorImg, noNetworkImg;
    private TextView loadingTv, emptyTv, errorTv, noNetworkTv;
    private TextView emptyReloadBtn, errorReloadBtn, noNetWorkReloadBtn;

    private OnReloadListener onReloadListener;
    private static Builder builder = new Builder();

    public LoadDataLayout(Context context) {
        this(context, null);
    }

    public LoadDataLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadDataLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadDataLayout);

        builder.setLoadingText(TextUtils.isEmpty(typedArray.getString(R.styleable.LoadDataLayout_loadingText)) ? builder.loadingText : typedArray.getString(R.styleable.LoadDataLayout_loadingText));
        builder.setLoadingTextColor(typedArray.getResourceId(R.styleable.LoadDataLayout_loadingTextColor, builder.loadingTextColor));
        builder.setLoadingTextSize(typedArray.getInteger(R.styleable.LoadDataLayout_loadingTextSize, builder.loadingTextSize));
        builder.setLoadingViewLayoutId(typedArray.getResourceId(R.styleable.LoadDataLayout_loadingViewLayoutId, builder.loadingViewLayoutId));

        builder.setEmptyText(TextUtils.isEmpty(typedArray.getString(R.styleable.LoadDataLayout_emptyText)) ? builder.emptyText : typedArray.getString(R.styleable.LoadDataLayout_emptyText));
        builder.setEmptyImgId(typedArray.getResourceId(R.styleable.LoadDataLayout_emptyImgId, builder.emptyImgId));
        builder.setEmptyImageVisible(typedArray.getBoolean(R.styleable.LoadDataLayout_emptyImageVisible, builder.emptyImageVisible));

        builder.setErrorText(TextUtils.isEmpty(typedArray.getString(R.styleable.LoadDataLayout_errorText)) ? builder.errorText : typedArray.getString(R.styleable.LoadDataLayout_errorText));
        builder.setErrorImgId(typedArray.getResourceId(R.styleable.LoadDataLayout_errorImgId, builder.errorImgId));
        builder.setErrorImageVisible(typedArray.getBoolean(R.styleable.LoadDataLayout_errorImageVisible, builder.errorImageVisible));

        builder.setNoNetWorkText(TextUtils.isEmpty(typedArray.getString(R.styleable.LoadDataLayout_noNetWorkText)) ? builder.noNetWorkText : typedArray.getString(R.styleable.LoadDataLayout_noNetWorkText));
        builder.setNoNetWorkImgId(typedArray.getResourceId(R.styleable.LoadDataLayout_noNetWorkImgId, builder.noNetWorkImgId));
        builder.setNoNetWorkImageVisible(typedArray.getBoolean(R.styleable.LoadDataLayout_noNetWorkImageVisible, builder.noNetWorkImageVisible));

        builder.setAllPageBackgroundColor(typedArray.getResourceId(R.styleable.LoadDataLayout_allPageBackgroundColor, builder.allPageBackgroundColor));
        builder.setAllTipTextSize(typedArray.getInteger(R.styleable.LoadDataLayout_allTipTextSize, builder.allTipTextSize));
        builder.setAllTipTextColor(typedArray.getResourceId(R.styleable.LoadDataLayout_allTipTextColor, builder.allTipTextColor));

        builder.setReloadBtnText(TextUtils.isEmpty(typedArray.getString(R.styleable.LoadDataLayout_reloadBtnText)) ? builder.reloadBtnText : typedArray.getString(R.styleable.LoadDataLayout_reloadBtnText));
        builder.setReloadBtnTextSize(typedArray.getInteger(R.styleable.LoadDataLayout_reloadBtnTextSize, builder.reloadBtnTextSize));
        builder.setReloadBtnTextColor(typedArray.getResourceId(R.styleable.LoadDataLayout_reloadBtnTextColor, builder.reloadBtnTextColor));
        builder.setReloadBtnBackgroundResource(typedArray.getResourceId(R.styleable.LoadDataLayout_reloadBtnBackgroundResource, builder.reloadBtnBackgroundResource));
        builder.setReloadBtnVisible(typedArray.getBoolean(R.styleable.LoadDataLayout_reloadBtnVisible, builder.reloadBtnVisible));
        builder.setReloadClickAreaType(typedArray.getInt(R.styleable.LoadDataLayout_reloadClickArea, builder.reloadClickArea));

        typedArray.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new IllegalStateException(getClass().getSimpleName() + " can host only one direct child");
        }
        build();
    }

    private void build() {

        //inflate View
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
            contentView.setVisibility(GONE);
        }
        if (builder.loadingViewLayoutId != NO_ID) {
            loadingView = LayoutInflater.from(mContext).inflate(builder.loadingViewLayoutId, null);
        } else {
            loadingView = LayoutInflater.from(mContext).inflate(R.layout.widget_loading_view, null);
        }
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.widget_empty_view, null);
        errorView = LayoutInflater.from(mContext).inflate(R.layout.widget_error_view, null);
        noNetworkView = LayoutInflater.from(mContext).inflate(R.layout.widget_no_network_view, null);

        //findView
        emptyLayout = (LinearLayout) emptyView.findViewById(R.id.empty_layout);
        errorLayout = (LinearLayout) errorView.findViewById(R.id.error_layout);
        noNetWorkLayout = (LinearLayout) noNetworkView.findViewById(R.id.no_network_layout);

        emptyImg = (ImageView) emptyView.findViewById(R.id.empty_img);
        errorImg = (ImageView) errorView.findViewById(R.id.error_img);
        noNetworkImg = (ImageView) noNetworkView.findViewById(R.id.no_network_img);

        loadingTv = (TextView) loadingView.findViewById(R.id.loading_text);
        emptyTv = (TextView) emptyView.findViewById(R.id.empty_text);
        errorTv = (TextView) errorView.findViewById(R.id.error_text);
        noNetworkTv = (TextView) noNetworkView.findViewById(R.id.no_network_text);

        emptyReloadBtn = (TextView) emptyView.findViewById(R.id.empty_reload_btn);
        errorReloadBtn = (TextView) errorView.findViewById(R.id.error_reload_btn);
        noNetWorkReloadBtn = (TextView) noNetworkView.findViewById(R.id.no_network_reload_btn);

        //setUpView
        setAllPageBackgroundColor(builder.allPageBackgroundColor);

        setEmptyImage(builder.emptyImgId);
        setErrorImage(builder.errorImgId);
        setNoNetWorkImage(builder.noNetWorkImgId);

        setEmptyImageVisible(builder.emptyImageVisible);
        setErrorImageVisible(builder.errorImageVisible);
        setNoNetWorkImageVisible(builder.noNetWorkImageVisible);

        setLoadingText(builder.loadingText);
        setEmptyText(builder.emptyText);
        setErrorText(builder.errorText);
        setNoNetWorkText(builder.noNetWorkText);

        setLoadingTextSize(builder.loadingTextSize);
        setAllTipTextSize(builder.allTipTextSize);

        setLoadingTextColor(builder.loadingTextColor);
        setAllTipTextColor(builder.allTipTextColor);

        setReloadBtnText(builder.reloadBtnText);
        setReloadBtnTextColor(builder.reloadBtnTextColor);
        setReloadBtnTextSize(builder.reloadBtnTextSize);
        setReloadBtnBackgroundResource(builder.reloadBtnBackgroundResource);
        setReloadBtnVisible(builder.reloadBtnVisible);
        setReloadClickArea(builder.reloadClickArea);

        addView(loadingView);
        addView(emptyView);
        addView(errorView);
        addView(noNetworkView);
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.empty_layout
                || i == R.id.empty_reload_btn
                || i == R.id.error_layout
                || i == R.id.error_reload_btn
                || i == R.id.no_network_layout
                || i == R.id.no_network_reload_btn) {

            if (onReloadListener != null) {
                onReloadListener.onReload(view, getStatus());
            }
        }
    }

    private int getColor(@ColorRes int id) {
        return ContextCompat.getColor(mContext, id);
    }

    public void setStatus(@Flavour int state) {

        this.currentState = state;
        switch (state) {
            case SUCCESS:
                if (contentView != null) {
                    contentView.setVisibility(VISIBLE);
                }
                loadingView.setVisibility(GONE);
                emptyView.setVisibility(GONE);
                errorView.setVisibility(GONE);
                noNetworkView.setVisibility(GONE);
                break;
            case LOADING:
                if (contentView != null) {
                    contentView.setVisibility(GONE);
                }
                loadingView.setVisibility(VISIBLE);
                emptyView.setVisibility(GONE);
                errorView.setVisibility(GONE);
                noNetworkView.setVisibility(GONE);
                break;
            case EMPTY:
                if (contentView != null) {
                    contentView.setVisibility(GONE);
                }
                loadingView.setVisibility(GONE);
                emptyView.setVisibility(VISIBLE);
                errorView.setVisibility(GONE);
                noNetworkView.setVisibility(GONE);
                break;
            case ERROR:
                if (contentView != null) {
                    contentView.setVisibility(GONE);
                }
                loadingView.setVisibility(GONE);
                emptyView.setVisibility(GONE);
                errorView.setVisibility(VISIBLE);
                noNetworkView.setVisibility(GONE);
                break;
            case NO_NETWORK:
                if (contentView != null) {
                    contentView.setVisibility(GONE);
                }
                loadingView.setVisibility(GONE);
                emptyView.setVisibility(GONE);
                errorView.setVisibility(GONE);
                noNetworkView.setVisibility(VISIBLE);
                break;
        }
    }

    public LoadDataLayout setOnReloadListener(OnReloadListener onReloadListener) {
        this.onReloadListener = onReloadListener;
        return this;
    }

    public LoadDataLayout setEmptyText(@NonNull String text) {
        emptyTv.setText(text);
        return this;
    }

    public LoadDataLayout setErrorText(@NonNull String text) {
        errorTv.setText(text);
        return this;
    }

    public LoadDataLayout setNoNetWorkText(@NonNull String text) {
        noNetworkTv.setText(text);
        return this;
    }

    public LoadDataLayout setLoadingText(@NonNull String text) {
        if (loadingTv != null) {
            loadingTv.setText(text);
        }
        return this;
    }


    public LoadDataLayout setLoadingTextSize(int sp) {
        if (loadingTv != null) {
            loadingTv.setTextSize(sp);
        }
        return this;
    }

    public LoadDataLayout setLoadingTextColor(@ColorRes int colorId) {
        if (loadingTv != null) {
            loadingTv.setTextColor(getColor(colorId));
        }
        return this;
    }

    public LoadDataLayout setEmptyImage(@DrawableRes int resId) {
        emptyImg.setImageResource(resId);
        return this;
    }

    public LoadDataLayout setErrorImage(@DrawableRes int resId) {
        errorImg.setImageResource(resId);
        return this;
    }

    public LoadDataLayout setNoNetWorkImage(@DrawableRes int resId) {
        noNetworkImg.setImageResource(resId);
        return this;
    }

    public LoadDataLayout setEmptyImageVisible(boolean visible) {
        if (visible) {
            emptyImg.setVisibility(VISIBLE);
        } else {
            emptyImg.setVisibility(GONE);
        }
        return this;
    }

    public LoadDataLayout setErrorImageVisible(boolean visible) {
        if (visible) {
            errorImg.setVisibility(VISIBLE);
        } else {
            errorImg.setVisibility(GONE);
        }
        return this;
    }

    public LoadDataLayout setNoNetWorkImageVisible(boolean visible) {
        if (visible) {
            noNetworkImg.setVisibility(VISIBLE);
        } else {
            noNetworkImg.setVisibility(GONE);
        }
        return this;
    }

    public LoadDataLayout setReloadBtnText(@NonNull String text) {
        emptyReloadBtn.setText(text);
        errorReloadBtn.setText(text);
        noNetWorkReloadBtn.setText(text);
        return this;
    }

    public LoadDataLayout setReloadBtnTextSize(int sp) {
        emptyReloadBtn.setTextSize(sp);
        errorReloadBtn.setTextSize(sp);
        noNetWorkReloadBtn.setTextSize(sp);
        return this;
    }

    public LoadDataLayout setReloadBtnTextColor(@ColorRes int colorId) {
        emptyReloadBtn.setTextColor(getColor(colorId));
        errorReloadBtn.setTextColor(getColor(colorId));
        noNetWorkReloadBtn.setTextColor(getColor(colorId));
        return this;
    }

    public LoadDataLayout setReloadBtnBackgroundResource(@DrawableRes int drawableId) {
        if (drawableId != NO_ID) {
            emptyReloadBtn.setBackgroundResource(drawableId);
            errorReloadBtn.setBackgroundResource(drawableId);
            noNetWorkReloadBtn.setBackgroundResource(drawableId);
        }
        return this;
    }

    public LoadDataLayout setReloadBtnVisible(boolean visible) {
        if (visible) {
            emptyReloadBtn.setVisibility(VISIBLE);
            errorReloadBtn.setVisibility(VISIBLE);
            noNetWorkReloadBtn.setVisibility(VISIBLE);
        } else {
            emptyReloadBtn.setVisibility(GONE);
            errorReloadBtn.setVisibility(GONE);
            noNetWorkReloadBtn.setVisibility(GONE);
        }
        return this;
    }

    public LoadDataLayout setReloadClickArea(@Area int area) {
        switch (area) {
            case FULL:
                emptyLayout.setOnClickListener(this);
                errorLayout.setOnClickListener(this);
                noNetWorkLayout.setOnClickListener(this);
                break;
            case BUTTON:
                emptyReloadBtn.setOnClickListener(this);
                errorReloadBtn.setOnClickListener(this);
                noNetWorkReloadBtn.setOnClickListener(this);
                break;
        }
        return this;
    }

    public LoadDataLayout setAllPageBackgroundColor(@ColorRes int colorId) {
        loadingView.setBackgroundColor(getColor(colorId));
        emptyView.setBackgroundColor(getColor(colorId));
        errorView.setBackgroundColor(getColor(colorId));
        noNetworkView.setBackgroundColor(getColor(colorId));
        return this;
    }

    public LoadDataLayout setAllTipTextColor(@ColorRes int colorId) {
        emptyTv.setTextColor(getColor(colorId));
        errorTv.setTextColor(getColor(colorId));
        noNetworkTv.setTextColor(getColor(colorId));
        return this;
    }

    public LoadDataLayout setAllTipTextSize(int sp) {
        emptyTv.setTextSize(sp);
        errorTv.setTextSize(sp);
        noNetworkTv.setTextSize(sp);
        return this;
    }

    public LoadDataLayout setLoadingViewLayoutId(@LayoutRes int layoutId) {

        this.removeView(loadingView);
        View view = LayoutInflater.from(mContext).inflate(layoutId, null);
        loadingView = view;
        loadingView.setVisibility(View.GONE);
        this.addView(loadingView);
        return this;
    }

    public static Builder getBuilder() {
        return builder;
    }

    public int getStatus() {
        return currentState;
    }


    public View getLoadingView() {
        return loadingView;
    }

    public View getEmptyView() {
        return emptyView;
    }

    public View getErrorView() {
        return errorView;
    }

    public View getNoNetworkView() {
        return noNetworkView;
    }

    @IntDef({SUCCESS, EMPTY, ERROR, NO_NETWORK, LOADING})
    public @interface Flavour {

    }

    @IntDef({FULL, BUTTON})
    public @interface Area {

    }

    public interface OnReloadListener {
        void onReload(View v, int status);
    }

    public static class Builder {

        int loadingViewLayoutId = NO_ID;

        String loadingText = "加载中";
        String emptyText = "暂无数据";
        String errorText = "加载失败，请稍后重试";
        String noNetWorkText = "网络出问题了";
        String reloadBtnText = "点击重试";

        int emptyImgId = R.drawable.ic_empty;
        int errorImgId = R.drawable.ic_error;
        int noNetWorkImgId = R.drawable.ic_no_network;

        boolean emptyImageVisible = true;
        boolean errorImageVisible = true;
        boolean noNetWorkImageVisible = true;
        boolean reloadBtnVisible = true;

        int allPageBackgroundColor = R.color.pageBackground;
        int allTipTextColor = R.color.text_color_child;
        int allTipTextSize = 16;
        int loadingTextSize = 16;
        int loadingTextColor = R.color.text_color_theme;
        int reloadBtnTextSize = 16;
        int reloadBtnTextColor = R.color.colorPrimary;
        int reloadBtnBackgroundResource = NO_ID;
        int reloadClickArea = BUTTON;

        public Builder setLoadingViewLayoutId(@LayoutRes int loadingViewLayoutId) {
            this.loadingViewLayoutId = loadingViewLayoutId;
            return builder;
        }

        public Builder setLoadingText(@NonNull String loadingText) {
            this.loadingText = loadingText;
            return builder;
        }

        public Builder setEmptyText(@NonNull String emptyText) {
            this.emptyText = emptyText;
            return builder;
        }

        public Builder setErrorText(@NonNull String errorText) {
            this.errorText = errorText;
            return builder;
        }

        public Builder setNoNetWorkText(@NonNull String noNetWorkText) {
            this.noNetWorkText = noNetWorkText;
            return builder;
        }

        public Builder setReloadBtnText(@NonNull String reloadBtnText) {
            this.reloadBtnText = reloadBtnText;
            return builder;
        }

        public Builder setEmptyImgId(@DrawableRes int emptyImgId) {
            this.emptyImgId = emptyImgId;
            return builder;
        }

        public Builder setErrorImgId(@DrawableRes int errorImgId) {
            this.errorImgId = errorImgId;
            return builder;
        }

        public Builder setNoNetWorkImgId(@DrawableRes int noNetWorkImgId) {
            this.noNetWorkImgId = noNetWorkImgId;
            return builder;
        }

        public Builder setEmptyImageVisible(boolean emptyImageVisible) {
            this.emptyImageVisible = emptyImageVisible;
            return builder;
        }

        public Builder setErrorImageVisible(boolean errorImageVisible) {
            this.errorImageVisible = errorImageVisible;
            return builder;
        }

        public Builder setNoNetWorkImageVisible(boolean noNetWorkImageVisible) {
            this.noNetWorkImageVisible = noNetWorkImageVisible;
            return builder;
        }

        public Builder setAllPageBackgroundColor(@ColorRes int allPageBackgroundColor) {
            this.allPageBackgroundColor = allPageBackgroundColor;
            return builder;
        }

        public Builder setAllTipTextColor(@ColorRes int tipTextColor) {
            this.allTipTextColor = tipTextColor;
            return builder;
        }

        public Builder setAllTipTextSize(int tipTextSize) {
            this.allTipTextSize = tipTextSize;
            return builder;
        }

        public Builder setLoadingTextSize(int loadingTextSize) {
            this.loadingTextSize = loadingTextSize;
            return builder;

        }

        public Builder setLoadingTextColor(@ColorRes int loadingTextColor) {
            this.loadingTextColor = loadingTextColor;
            return builder;
        }

        public Builder setReloadBtnTextSize(int reloadBtnTextSize) {
            this.reloadBtnTextSize = reloadBtnTextSize;
            return builder;
        }

        public Builder setReloadBtnTextColor(@ColorRes int reloadBtnTextColor) {
            this.reloadBtnTextColor = reloadBtnTextColor;
            return builder;
        }

        public Builder setReloadBtnBackgroundResource(@DrawableRes int reloadBtnBackgroundResource) {
            this.reloadBtnBackgroundResource = reloadBtnBackgroundResource;
            return builder;
        }

        public Builder setReloadBtnVisible(boolean reloadBtnVisible) {
            this.reloadBtnVisible = reloadBtnVisible;
            return builder;
        }

        public Builder setReloadClickArea(@Area int reloadClickArea) {
            setReloadClickAreaType(reloadClickArea);
            return builder;
        }

        private void setReloadClickAreaType(int reloadClickArea) {
            this.reloadClickArea = reloadClickArea;
        }
    }

}
