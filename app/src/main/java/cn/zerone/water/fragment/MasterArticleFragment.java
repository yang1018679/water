package cn.zerone.water.fragment;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.reflect.*;

import cn.zerone.water.activity.NewsWebActivity;
import cn.zerone.water.activity.NoticeActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.map.PoiSearchActivity;
import cn.zerone.water.model.HeaderAdapter;
import cn.zerone.water.model.ItemArticle;
import cn.zerone.water.R;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class MasterArticleFragment extends Fragment {
    private static final String ARTICLE_LATEST_PARAM = "param";

    private static final int UPTATE_VIEWPAGER = 0;
    public static final String Image = "http://47.105.187.185:8011";
    //轮播的最热新闻图片
    @InjectView(R.id.vp_hottest)
    ViewPager vpHottest;
    //轮播图片下面的小圆点
    @InjectView(R.id.ll_hottest_indicator)
    LinearLayout llHottestIndicator;
    //存储的参数
    private String mParam;
    //获取 fragment 依赖的 Activity，方便使用 Context
    private Activity mAct;
    //private int[] ids = new int[]{R.id.bn1,R.id.bn2,R.id.bn3,R.id.bn4};

    //设置当前 第几个图片 被选中
    private int autoCurrIndex = 0;

    private ImageView[] mBottomImages;//底部只是当前页面的小圆点

    private Timer timer = new Timer(); //为了方便取消定时轮播，将 Timer 设为全局

    private ImageButton mButNavi = null;

    //定时轮播图片，需要在主线程里面修改 UI
    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    if (msg.arg1 != 0) {
                        vpHottest.setCurrentItem(msg.arg1);
                    } else {
                        //false 当从末页调到首页是，不显示翻页动画效果，
                        vpHottest.setCurrentItem(msg.arg1, false);
                    }
                    break;
            }
        }
    };

    public static MasterArticleFragment newInstance(String param) {
        MasterArticleFragment fragment = new MasterArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARTICLE_LATEST_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mParam = savedInstanceState.getString(ARTICLE_LATEST_PARAM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_web, container, false);
        mAct = getActivity();
        ButterKnife.inject(this, view);
        TextView title ;
        title = view.findViewById(R.id.title);
        title.setGravity(Gravity.CENTER);
        title.setText(title());

        // 添加导航按钮
        mButNavi = view.findViewById(R.id.imageButton10);
        mButNavi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PoiSearchActivity.class);
                getContext().startActivity(intent);
            }

        });

        return view;
    }

    public String title(){
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ImageTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void setUpViewPager(final List<ItemArticle> headerArticles) {
        final HeaderAdapter imageAdapter = new HeaderAdapter(mAct, headerArticles);

        vpHottest.setAdapter(imageAdapter);

        //创建底部指示位置的导航栏
        mBottomImages = new ImageView[headerArticles.size()];
        for (int i = 0; i < mBottomImages.length; i++) {
            final ImageView imageView = new ImageView(mAct);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.mipmap.ellipse_1);
            } else {
                imageView.setBackgroundResource(R.mipmap.ellipse);
            }
            mBottomImages[i] = imageView;
            //把指示作用的原点图片加入底部的视图中
            llHottestIndicator.addView(mBottomImages[i]);

        }


        vpHottest.setOnTouchListener(new View.OnTouchListener() {
            float mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            int touchFlag = 0;
            float x = 0, y = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touchFlag = 0;
                        x = event.getX();
                        y = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float xDiff = Math.abs(event.getX() - x);
                        float yDiff = Math.abs(event.getY() - y);
                        if (xDiff < mTouchSlop && xDiff >= yDiff)
                            touchFlag = 0;
                        else
                            touchFlag = -1;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (touchFlag == 0) {
                            Intent intent = new Intent(mAct,NewsWebActivity.class);
                            Bundle bundle = new Bundle();
                            //bundle.putString("url", "http://www.chinanews.com/tp/hd2011/2019/05-18/883050.shtml");
                            int currentItem = vpHottest.getCurrentItem();
                            //intent.putExtra("story_id", headerArticles.get(currentItem).getNewsId());
                            intent.putExtra("imageUrl",headerArticles.get(currentItem).getImageHref());
                            startActivity(intent);

                        }
                        break;

                }

                return false;
            }
        });

        vpHottest.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //图片左右滑动时候，将当前页的圆点图片设为选中状态
            @Override
            public void onPageSelected(int position) {
                // 一定几个图片，几个圆点，但注意是从0开始的
                int total = mBottomImages.length;
                for (int j = 0; j < total; ++j) {
                    if (j == position) {
                        mBottomImages[j].setBackgroundResource(R.mipmap.ellipse_1);
                    } else {
                        mBottomImages[j].setBackgroundResource(R.mipmap.ellipse);
                    }
                }

                //设置全局变量，currentIndex为选中图标的 index
                autoCurrIndex = position;
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // 设置自动轮播图片，5s后执行，周期是5s
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                if (autoCurrIndex == headerArticles.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        }, 5000, 5000);
    }

    class ImageTask extends AsyncTask<String, Void, List<ItemArticle>> {
        public List<ItemArticle> articles = new ArrayList<ItemArticle>();
        @Override
        protected List<ItemArticle> doInBackground(String... params) {


            Requests.AdInfo_GetList(new Observer<JSONArray>() {
                 @Override
                 public void onSubscribe(Disposable d) {
                     }

                  @Override
                  public void onNext(JSONArray objects) {
                      for(int i = 0; i<objects.size();i++){
                          JSONObject json1 = new JSONObject();
                          JSONObject  jsonObject  =  objects.getJSONObject(i) ;
                          System.out.println();
                          int ID = jsonObject.getInteger("ID");
                          String imagepath = jsonObject.getString("ImgPath");
                          String imageUrl = Image + imagepath;
                          System.out.println("pppppppppppppppp"+imageUrl);
                          String href = jsonObject.getString("Href");
                          System.out.println("llllllllllllllllllll"+href);
                          ItemArticle temp = new ItemArticle(ID, imageUrl, href);
                          System.out.println("gggggggggggggggggg" + temp);
                          articles.add(temp);
                          System.out.println("ssssssssssss" + articles.size());
                                  //new ItemArticle(ID,"imageUrl","href"));
                     }
                 }
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                }
                });

            Class clazz = this.getClass();
            Object obj = null;
            try {
                obj = clazz.getDeclaredField("articles");
                System.out.println("jjjjjjjjjjjjjjj"+obj);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            List<ItemArticle> lll = obj;
            //System.out.println("oooooooooooooooooo"+this.articles.size());
            return obj;
        }

        @Override
        protected void onPostExecute(List<ItemArticle> articles) {
            //这儿的 是 url 的集合
            super.onPostExecute(articles);
            setUpViewPager(articles);

        }
    }
}

