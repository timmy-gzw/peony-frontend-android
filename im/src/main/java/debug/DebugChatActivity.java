package debug;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tftechsz.im.R;
import com.tftechsz.im.mvp.ui.fragment.ChatFragment;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;


public class DebugChatActivity extends BaseMvpActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        ChatFragment fragment = new ChatFragment();
        FragmentManager m = getSupportFragmentManager();
        FragmentTransaction t = m.beginTransaction();
        t.replace(R.id.chat,fragment);
        t.commit();
    }

    @Override
    protected int getLayout() {
        return R.layout.chat_activity_debug_chat;
    }

    @Override
    public BasePresenter initPresenter() {
        return null;
    }
}
