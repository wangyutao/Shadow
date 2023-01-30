package test.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.jpyy001.tools.core.runtime.ShadowActivity;

public class UseGetActivityFragment {

    ShadowActivity test(TestFragment fragment) {
        fragment.startActivity(new Intent());
        fragment.startActivity(new Intent(), new Bundle());
        return fragment.getActivity();
    }
}
