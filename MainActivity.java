package create.persion.com.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import create.persion.com.testapp.view.CircleView;

public class MainActivity extends AppCompatActivity {

    private ArcLimtView myCircleMenuLayout;


    private String[] mItemTexts = new String[]{"安全中心 ", "特色服务", "投资理财",
            "转账汇款", "我的账户", "安全中心", "特色服务", "投资理财", "转账汇款", "我的账户"};
    private int[] mItemImgs = new int[]{R.drawable.home_mbank_1_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
            R.drawable.home_mbank_1_normal, R.drawable.home_mbank_2_normal,
            R.drawable.home_mbank_3_normal, R.drawable.home_mbank_4_normal,
            R.drawable.home_mbank_5_normal,
    };
    private CircleView circleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.initScreen(this);
        setContentView(R.layout.activity_main);
        circleView = ((CircleView) findViewById(R.id.circleView));
        circleView.setProgressItem(2);
        myCircleMenuLayout = (ArcLimtView) findViewById(R.id.id_mymenulayout);
        myCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs);
        myCircleMenuLayout.setSwipListener(new ArcLimtView.SwipProgressListener() {
            @Override
            public void progress(double swipProgress) {
                circleView.setSwipProgress(swipProgress);

            }
        });

    }
}
