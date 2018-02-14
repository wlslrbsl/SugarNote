package com.isens.sugarnote;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by BSPL on 2018-02-05.
 */

public interface FragmentInterActionListener {
    void setFrag(String state);
    void connectAPIClient();
    GoogleApiClient getAPIClient();
    boolean getIsAPIConnected();
}
