package com.cvbmgh.growthseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * author : lhl
 * date : 2020/4/24/024
 * desc : 自定义成长值的seekBar
 */
public class GrowthSeekBar extends View {
    private static final float THIRD = 0.333333f;
    private static final float FOURTH = 0.25f;

    private Paint mPaint;
    private int textSize; //字体大小 (默认)
    private int textColor; //字体颜色 (默认)
    private String currentValue; //当前的成长值

    //进度指示框
    private boolean isShowIndicator;  //是否显示指示框
    private boolean isShowIndicatorStroke; //是否显示指示框的边线
    private RectF indicatorRectContent = new RectF();  //指示牌里内容矩形(不包含边线和箭头)
    private RectF indicatorRectAround = new RectF();  //指示牌的矩形(包含边线和箭头)
    private int indicatorContentHeight;  //指示框的高度(不包括箭头的高度)
    private int indicatorContentWidth; //宽度
    private int indicatorArrowWight; //箭头宽度
    private int indicatorArrowHeight; //箭头高度
    private int indicatorTextSize; //字体大小
    private int indicatorTextColor; //字体颜色
    private int indicatorBackgroundColor; //指示框的背景颜色
    private int indicatorStrokeColor; //边框线的颜色
    private int indicatorStrokeWidth; //边框线的宽度
    private int indicatorRadius;  //圆角大小
    private int indicatorMarginBottom; //指示框与下方控件的距离
    private Paint indicatorPaint; //指示牌背景的画笔
    private Paint indicatorStrokePaint;  //指示牌边线和箭头边线的画笔
    private TextPaint indicatorTextPaint; //指示牌的文字的画笔
    private Path trianglePath; //三角形的路径
    private Path triangleboderPath; //三角形的边线路径
    private Point point1;
    private Point point2;
    private Point point3;
    private StaticLayout valueTextLayout;


    //进度条
    private int progressLeft, progressTop, progressRight, progressBottom;
    private float curProgressX; //当前进度的x轴坐标，也是滑块中心点的X轴
    private float progressRadius;  //进度条圆角
    //private float progress;  //进度条已完成进度值
    private int progressBackgroundColor; //进度条的背景颜色
    private int progressColor;  //进度条已完成进度的颜色
    private int progressHeight; //进度条的高度
    private RectF progressBackgroundRect = new RectF(); //进度条的背景
    private RectF progressRect = new RectF();  //进度条的进度
    private int thumbId;  //滑块
    private Bitmap thumbBitmap;

    //进度标记
    private int interval;   // 进度条分几等份，默认3等份
    private int textMarginGrowthIcon; //文字距离等级图标的距离
    private int growthImgSize; //等级图片的宽高
    private int growthImgPlaceHolderId;  //等级图片的占位图
    private Bitmap descentBmp;  //底一级的等级图片
    private String descentName; //底一级的等级名称
    private String descentValue; //底一级的等级成长值;
    private Bitmap ascentBmp;   //高一级的等级图片
    private String ascentName;  //高一级的等级名称
    private String asscentValue; //高一级的等级成长值;

    private int valueStrColor; //成长值的字体颜色
    private Paint growthNamePaint;  //等级名称的画笔;
    private Paint textPaint;  //标记的文字的画笔
    private float descentX;   //低一级的x坐标
    private float ascentX;   //高一级的x坐标

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setDescentBmp(Bitmap descentBmp) {
        if (descentBmp == null) {
            return;
        }
        this.descentBmp = descentBmp;
        Bitmap resizeBmp = resizeBitmap(descentBmp, growthImgSize);
        if (resizeBmp != null) {
            this.descentBmp = resizeBmp;
        }
    }

    public void setDescentName(String descentName) {
        this.descentName = descentName;
    }

    public void setDescentValue(String descentValue) {
        this.descentValue = descentValue;
    }

    public void setAscentBmp(Bitmap ascentBmp) {
        if (ascentBmp == null) {
            return;
        }
        this.ascentBmp = ascentBmp;
        Bitmap resizeBmp = resizeBitmap(ascentBmp, growthImgSize);
        if (resizeBmp != null) {
            this.ascentBmp = resizeBmp;
        }
    }

    public void setAscentName(String ascentName) {
        this.ascentName = ascentName;
    }

    public void setAsscentValue(String asscentValue) {
        this.asscentValue = asscentValue;
    }

    //不能动态创建,只能在xml布局中使用
    private GrowthSeekBar(Context context) {
        this(context, null);
    }

    public GrowthSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrowthSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.GrowthSeekBar);
        textSize = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_text_size, sp2px(12));
        textColor = ta.getColor(R.styleable.GrowthSeekBar_gsb_text_color, ContextCompat.getColor(getContext(), R.color.color_333333));

        //指示牌
        isShowIndicator = ta.getBoolean(R.styleable.GrowthSeekBar_gsb_is_show_indicator, true);
        isShowIndicatorStroke = ta.getBoolean(R.styleable.GrowthSeekBar_gsb_is_show_indicator_stroke, true);
        indicatorContentHeight = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_indicator_height, dp2px(22));
        indicatorArrowWight = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_indicator_arrow_width, dp2px(5f));
        indicatorArrowHeight = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_indicator_arrow_height, dp2px(2));
        indicatorTextColor = ta.getColor(R.styleable.GrowthSeekBar_gsb_indicator_text_color, ContextCompat.getColor(getContext(), R.color.color_4caa9a));
        indicatorStrokeColor = ta.getColor(R.styleable.GrowthSeekBar_gsb_indicator_stroke_color, ContextCompat.getColor(getContext(), R.color.color_4caa9a));
        indicatorStrokeWidth = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_indicator_stroke_width, dp2px(1));
        indicatorMarginBottom = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_indicator_margin_bottom, dp2px(2f));
        indicatorBackgroundColor = ta.getColor(R.styleable.GrowthSeekBar_gsb_indicator_background_color, Color.WHITE);
        indicatorRadius = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_indicator_radius, 0);

        //进度条
        progressHeight = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_progress_height, sp2px(3));
        progressBackgroundColor = ta.getColor(R.styleable.GrowthSeekBar_gsb_progress_background_color, ContextCompat.getColor(getContext(), R.color.color_b3b3b3));
        progressColor = ta.getColor(R.styleable.GrowthSeekBar_gsb_progress_color, ContextCompat.getColor(getContext(), R.color.color_4caa9a));
        progressRadius = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_progress_radius, 0);
        thumbId = ta.getResourceId(R.styleable.GrowthSeekBar_gsb_thumb_id, R.drawable.ic_member_center_seekbar_thumb);
        growthImgSize = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_growth_img_size, dp2px(24));
        growthImgPlaceHolderId = ta.getResourceId(R.styleable.GrowthSeekBar_gsb_growth_img_place_holder_id, R.drawable.ic_rank_baomi);
        valueStrColor = ta.getDimensionPixelSize(R.styleable.GrowthSeekBar_gsb_value_str_color, ContextCompat.getColor(getContext(), R.color.color_999));

        //等级
        textMarginGrowthIcon = (int) ta.getDimension(R.styleable.GrowthSeekBar_gsb_text_margin_growth_icon, dp2px(4));
        interval = ta.getInt(R.styleable.GrowthSeekBar_gsb_interval, 3);

        ta.recycle();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint.setStyle(Paint.Style.FILL);
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setColor(indicatorBackgroundColor);

        indicatorStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorStrokePaint.setStyle(Paint.Style.STROKE);
        indicatorStrokePaint.setStrokeWidth(indicatorStrokeWidth);
        indicatorStrokePaint.setColor(indicatorStrokeColor);
        indicatorStrokePaint.setAntiAlias(true);
        indicatorStrokePaint.setPathEffect(new CornerPathEffect(4));

        indicatorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        indicatorTextPaint.setStyle(Paint.Style.FILL);
        indicatorTextPaint.setTextSize(textSize);
        indicatorTextPaint.setColor(indicatorTextColor);

        point1 = new Point();
        point2 = new Point();
        point3 = new Point();

        trianglePath = new Path();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);
        triangleboderPath = new Path();

        growthNamePaint = new Paint();
        growthNamePaint.setColor(textColor);
        growthNamePaint.setTextSize(textSize);
        growthNamePaint.setAntiAlias(true);
        //descentPaint.setStrokeCap(Paint.Cap.ROUND);
        growthNamePaint.setTextAlign(Paint.Align.CENTER);
        growthNamePaint.setFakeBoldText(true);

        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(valueStrColor);
        textPaint.setAntiAlias(true);
        //textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextAlign(Paint.Align.CENTER);

        if (progressRadius <= 0 || progressRadius > progressHeight / 2f) {
            progressRadius = progressHeight / 2f;
        }

        if (indicatorRadius <= 0 || indicatorRadius > indicatorContentHeight / 2) {
            indicatorRadius = indicatorContentHeight / 2;
        }

        thumbBitmap = BitmapFactory.decodeResource(getResources(), thumbId);

        descentBmp = BitmapFactory.decodeResource(getResources(), growthImgPlaceHolderId);
        ascentBmp = BitmapFactory.decodeResource(getResources(), growthImgPlaceHolderId);

        currentValue = "0";

        float scaleWidth = progressHeight * 2f / thumbBitmap.getWidth();
        float scaleHeight = progressHeight * 2f / thumbBitmap.getHeight();
        if (scaleWidth > 1 && scaleHeight > 1) { //只放大,不缩小
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap scaleBmp = Bitmap.createBitmap(thumbBitmap, 0, 0, thumbBitmap.getWidth(), thumbBitmap.getHeight(), matrix, true);
            if (scaleBmp != null) {
                thumbBitmap = scaleBmp;
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算控件的高度
        int height = sp2px(65); //默认高度65
        if (isShowIndicator) {
            if (isShowIndicatorStroke) {
                height += indicatorStrokeWidth * 2;
            }
            height += indicatorContentHeight;
            height += indicatorArrowHeight;
            height += indicatorMarginBottom;
        }
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);

        //进度条的left,top,right,bottom的值
        if (isShowIndicator) {
            progressLeft = getPaddingLeft() + indicatorStrokeWidth + indicatorRadius;
            progressTop = getPaddingTop() + (growthImgSize / 2 - progressHeight / 2) + indicatorStrokeWidth * 2 + indicatorContentHeight + indicatorArrowHeight + indicatorMarginBottom;
            progressRight = getMeasuredWidth() - getPaddingRight() - indicatorStrokeWidth - indicatorRadius;
        } else {
            progressLeft = getPaddingLeft();
            progressTop = getPaddingTop() + (growthImgSize / 2 - progressHeight / 2);
            progressRight = getMeasuredWidth() - getPaddingRight();
        }
        progressBottom = progressTop + progressHeight;


        //高低级的x坐标
        if (interval == 4) {
            descentX = progressLeft + (progressRight - progressLeft) * FOURTH;
            ascentX = progressLeft + (progressRight - progressLeft) * FOURTH * 3;
        } else {
            descentX = progressLeft + (progressRight - progressLeft) * THIRD;
            ascentX = progressLeft + (progressRight - progressLeft) * THIRD * 2;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            onDrawProgressBar(canvas, mPaint);
            onDrawIndicator(canvas);
            onDrawSteps(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getCurProgressX() {
        int progressBgGrange = progressRight - progressLeft;
        float distanceX = 0;
        int currentValueInt = 0;
        int descentValueInt = 0;
        int ascentValueInt = 0;
        try {
            currentValueInt = Integer.parseInt(currentValue);
            descentValueInt = Integer.parseInt(descentValue);
            ascentValueInt = Integer.parseInt(asscentValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (currentValueInt > ascentValueInt) {
            if (interval == 4) {
                distanceX = progressBgGrange * FOURTH * 3.5f;
            } else {
                distanceX = progressBgGrange * THIRD * 2.5f;
            }
        } else if (currentValueInt == ascentValueInt) {
            if (interval == 4) {
                distanceX = progressBgGrange * FOURTH * 3;
            } else {
                distanceX = progressBgGrange * THIRD * 2;
            }
        } else if (currentValueInt > descentValueInt) {

            try {
                if (interval == 4) {
                    float baseDist = progressBgGrange * FOURTH + growthImgSize / 2f + thumbBitmap.getWidth() / 2f;
                    float middleDist = progressBgGrange * FOURTH * 2 - growthImgSize - thumbBitmap.getWidth();
                    float percent = (currentValueInt - descentValueInt) * 100f / (ascentValueInt - descentValueInt) / 100f;
                    distanceX = baseDist + middleDist * percent;
                } else {
                    float baseDist = progressBgGrange * THIRD + growthImgSize / 2f + thumbBitmap.getWidth() / 2f;
                    float middleDist = progressBgGrange * THIRD - growthImgSize - thumbBitmap.getWidth();
                    float percent = (currentValueInt - descentValueInt) * 100f / (ascentValueInt - descentValueInt) / 100f;
                    distanceX = baseDist + middleDist * percent;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (currentValueInt == descentValueInt) {
            if (interval == 4) {
                distanceX = progressBgGrange * FOURTH;
            } else {
                distanceX = progressBgGrange * THIRD;
            }
        } else {
            try {
                float dist;
                if (interval == 4) {
                    dist = progressBgGrange * FOURTH - growthImgSize / 2f - thumbBitmap.getWidth() / 2f;
                } else {
                    dist = progressBgGrange * THIRD - growthImgSize / 2f - thumbBitmap.getWidth() / 2f;
                }
                distanceX = dist * currentValueInt * 100f / descentValueInt / 100f;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        float progressX = progressLeft + distanceX;
        if (progressX < progressLeft) {
            progressX = progressLeft;
        }
        return progressX;
    }

    /**
     * 进度条
     *
     * @param canvas
     * @param paint
     */
    private void onDrawProgressBar(Canvas canvas, Paint paint) {
        curProgressX = getCurProgressX();

        progressBackgroundRect.set(progressLeft, progressTop, progressRight, progressBottom);
        progressRect.set(progressLeft, progressTop, curProgressX, progressBottom);

        paint.setColor(progressBackgroundColor);
        canvas.drawRoundRect(progressBackgroundRect, progressRadius, progressRadius, paint);

        paint.setColor(progressColor);
        canvas.drawRoundRect(progressRect, progressRadius, progressRadius, paint);

        canvas.drawBitmap(thumbBitmap, progressRect.right - thumbBitmap.getWidth() / 2f, progressTop - (thumbBitmap.getHeight() / 2f - progressHeight / 2f), paint);
    }

    private void onDrawIndicator(Canvas canvas) {
        if (isShowIndicator) {
            if (TextUtils.isEmpty(currentValue)) {
                currentValue = "0";
            }
            String indicatorText = "成长值 " + currentValue;
            indicatorContentWidth = (int) (indicatorTextPaint.measureText(indicatorText) + dp2px(8) * 2);

            //指示牌
            indicatorRectContent.set(curProgressX - indicatorContentWidth / 2f - indicatorStrokeWidth,
                    getPaddingTop() + indicatorStrokeWidth,
                    curProgressX + indicatorContentWidth / 2f + indicatorStrokeWidth,
                    getPaddingTop() + indicatorContentHeight - indicatorArrowHeight);

            // Move if not fit horizontal
            if (indicatorRectContent.left < getPaddingLeft()) {
                float difference = -indicatorRectContent.left + getPaddingLeft() + indicatorStrokeWidth;
                indicatorRectAround.set(indicatorRectContent.left + difference, indicatorRectContent.top, indicatorRectContent.right +
                        difference, indicatorRectContent.bottom);
            } else if (indicatorRectContent.right > getMeasuredWidth() - getPaddingRight()) {
                float difference = indicatorRectContent.right - getMeasuredWidth() + getPaddingRight() + indicatorStrokeWidth;
                indicatorRectAround.set(indicatorRectContent.left - difference, indicatorRectContent.top, indicatorRectContent.right -
                        difference, indicatorRectContent.bottom);
            } else {
                indicatorRectAround.set(indicatorRectContent.left, indicatorRectContent.top, indicatorRectContent.right,
                        indicatorRectContent.bottom);
            }


            canvas.drawRoundRect(indicatorRectAround, indicatorRadius, indicatorRadius, indicatorPaint);
            if (isShowIndicatorStroke) {
                canvas.drawRoundRect(indicatorRectAround, indicatorRadius, indicatorRadius, indicatorStrokePaint);
            }

            float difference = 0;
            if (curProgressX - indicatorArrowWight / 2f < getPaddingLeft() + indicatorStrokeWidth + indicatorRadius) {
                difference = indicatorRadius - curProgressX + getPaddingLeft() + indicatorStrokeWidth;
            } else if (curProgressX + indicatorArrowWight / 2 > getMeasuredWidth() - getPaddingRight() - indicatorStrokeWidth - indicatorRadius) {
                difference = (getMeasuredWidth() - indicatorRadius) - curProgressX - getPaddingRight() - indicatorStrokeWidth;
            }
            point1.set((int) (curProgressX - indicatorArrowWight / 2 + difference), getPaddingTop() + indicatorContentHeight - indicatorArrowHeight);
            point2.set((int) (curProgressX + indicatorArrowWight / 2 + difference), getPaddingTop() + indicatorContentHeight - indicatorArrowHeight);
            point3.set((int) (curProgressX + difference), getPaddingTop() + indicatorContentHeight + indicatorArrowHeight);

            drawTriangle(canvas, point1, point2, point3, indicatorPaint);
            if (isShowIndicatorStroke) {
                drawTriangleStroke(canvas, point1, point2, point3, indicatorStrokePaint);
            }

            // Draw value text
            canvas.save();
            valueTextLayout = new StaticLayout(indicatorText, indicatorTextPaint, indicatorContentWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
            if (valueTextLayout != null) {
                canvas.translate(indicatorRectAround.left, indicatorRectAround.top + indicatorRectAround.height() / 2 - valueTextLayout.getHeight() / 2);
                valueTextLayout.draw(canvas);
            }
            canvas.restore();
        }

    }

    /**
     * 画三角形
     *
     * @param canvas
     * @param point1
     * @param point2
     * @param point3
     * @param paint
     */
    private void drawTriangle(Canvas canvas, Point point1, Point point2, Point point3, Paint paint) {
        trianglePath.reset();
        trianglePath.moveTo(point1.x, point1.y);
        trianglePath.lineTo(point2.x, point2.y);
        trianglePath.lineTo(point3.x, point3.y);
        trianglePath.lineTo(point1.x, point1.y);
        trianglePath.close();

        canvas.drawPath(trianglePath, paint);
    }

    /**
     * 将三角形的一条顶边用颜色给覆盖掉
     */
    private void drawTriangleStroke(Canvas canvas, Point point1, Point point2, Point point3, Paint paint) {
        triangleboderPath.reset();
        triangleboderPath.moveTo(point1.x, point1.y);
        triangleboderPath.lineTo(point2.x, point2.y);
        paint.setColor(indicatorBackgroundColor);
        float value = indicatorStrokeWidth / 6;
        paint.setStrokeWidth(indicatorStrokeWidth + 1f);
        canvas.drawPath(triangleboderPath, paint);
        triangleboderPath.reset();
        paint.setStrokeWidth(indicatorStrokeWidth);
        triangleboderPath.moveTo(point1.x - value, point1.y - value);
        triangleboderPath.lineTo(point3.x, point3.y);
        triangleboderPath.lineTo(point2.x + value, point2.y - value);
        paint.setColor(indicatorStrokeColor);
        canvas.drawPath(triangleboderPath, paint);
    }


    /**
     * 画高低级的图片、名称、成长值
     *
     * @param canvas
     */
    private void onDrawSteps(Canvas canvas) {
        //低一级的等级图片,名称,成长值数据
        if (descentBmp == null) {
            descentBmp = BitmapFactory.decodeResource(getResources(), growthImgPlaceHolderId);
        }
        if (descentName == null) {
            descentName = "";
        }
        if (descentValue == null) {
            descentValue = "";
        }

        //高一级的等级图片,名称,成长值数据
        if (ascentBmp == null) {
            ascentBmp = BitmapFactory.decodeResource(getResources(), growthImgPlaceHolderId);
        }
        if (ascentName == null) {
            ascentName = "";
        }
        if (asscentValue == null) {
            asscentValue = "";
        }

        Paint.FontMetrics fm = textPaint.getFontMetrics();

        //底一级的图片和名称
        canvas.drawBitmap(descentBmp, descentX - descentBmp.getWidth() / 2f, progressBackgroundRect.top - (descentBmp.getHeight() / 2f - progressHeight / 2f), mPaint);
        canvas.drawText(descentName, descentX, progressBackgroundRect.bottom + (descentBmp.getHeight() / 2f - progressHeight / 2f) + textMarginGrowthIcon - fm.top, growthNamePaint);

        //高一级的图片和名称
        canvas.drawBitmap(ascentBmp, ascentX - ascentBmp.getWidth() / 2f, progressBackgroundRect.top - (ascentBmp.getHeight() / 2f - progressHeight / 2f), mPaint);
        canvas.drawText(ascentName, ascentX, progressBackgroundRect.bottom + (ascentBmp.getHeight() / 2f - progressHeight / 2f) + textMarginGrowthIcon - fm.top, growthNamePaint);

        //高低级的成长值
        textPaint.setColor(valueStrColor);
        canvas.drawText(descentValue, descentX, progressBackgroundRect.bottom + (descentBmp.getHeight() / 2f - progressHeight / 2f) + textMarginGrowthIcon - fm.top + fm.bottom - fm.top, textPaint);
        canvas.drawText(asscentValue, ascentX, progressBackgroundRect.bottom + (ascentBmp.getHeight() / 2f - progressHeight / 2f) + textMarginGrowthIcon - fm.top + fm.bottom - fm.top, textPaint);

    }

    /**
     * 调整图片的大小
     *
     * @param bmp
     * @param targetSize
     * @return
     */
    private Bitmap resizeBitmap(Bitmap bmp, int targetSize) {
        try {
            Matrix matrix = new Matrix();
            float scaleX = targetSize * 1f / bmp.getWidth();
            float scaleY = targetSize * 1f / bmp.getHeight();
            matrix.postScale(scaleX, scaleY);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * dp转px
     */
    public int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        if (scale <= 0) {
            return (int) dpValue;
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
