package uk.mondosports.plugins.fyber;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.util.Log;

import com.fyber.Fyber;
import com.fyber.utils.FyberLogger;
import com.fyber.requesters.OfferWallRequester;

import com.fyber.ads.videos.RewardedVideoActivity;
import com.fyber.currency.VirtualCurrencyErrorResponse;
import com.fyber.currency.VirtualCurrencyResponse;
import com.fyber.requesters.RewardedVideoRequester;
import com.fyber.requesters.VirtualCurrencyCallback;
import com.fyber.requesters.VirtualCurrencyRequester;

import com.fyber.requesters.InterstitialRequester;

import com.fyber.ads.AdFormat;
import com.fyber.requesters.RequestCallback;
import com.fyber.requesters.RequestError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FyberPlugin extends CordovaPlugin implements RequestCallback, VirtualCurrencyCallback{

    private static final String LOGTAG = "FyberPlugin";
    private static final String DEFAULT_APP_KEY = "27434";

    protected static final int INTERSTITIAL_REQUEST_CODE = 8792;
    protected static final int OFFERWALL_REQUEST_CODE = 8795;
    protected static final int REWARDED_VIDEO_REQUEST_CODE = 8796;

    private static final String ACTION_INITIALIZE = "initialize";
    private static final String ACTION_SHOW_OFFERWALL = "showOfferwall";
    private static final String ACTION_SHOW_REWARDEDVIDEO = "showRewardedVideo";
    private static final String ACTION_SHOW_INTERSTITIAL = "showInterstitial";
    private static final String OPT_APPLICATION_KEY = "appKey";
    private static final String OPT_USER_ID = "userId";
    private static final String OPT_SECURITY_TOKEN = "securityToken";

    private String appKey = DEFAULT_APP_KEY;
    private String userId = "5043b715c3bd823b760000ff";
    private String securityToken = "";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        PluginResult result = null;
        
        if (ACTION_INITIALIZE.equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = executeInitialize(options, callbackContext);
        } else if (ACTION_SHOW_OFFERWALL.equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = executeShowOfferwall(options, callbackContext);
        } else if (ACTION_SHOW_REWARDEDVIDEO.equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = executeShowRewardedVideo(options, callbackContext);
        } else if (ACTION_SHOW_INTERSTITIAL.equals(action)) {
            JSONObject options = args.optJSONObject(0);
            result = executeShowInterstitial(options, callbackContext);
        }

        if (result != null) callbackContext.sendPluginResult( result );

        return true;
    }

    private PluginResult executeInitialize(JSONObject options, CallbackContext callbackContext) {
        Log.w(LOGTAG, "executeInitialize");
        
        this.initialize( options );
        
        callbackContext.success();

        return null;
    }

    private void initialize( JSONObject options ) {
        if(options.has(OPT_APPLICATION_KEY)) this.appKey = options.optString( OPT_APPLICATION_KEY );
        if(options.has(OPT_USER_ID)) this.userId = options.optString( OPT_USER_ID );
        if(options.has(OPT_SECURITY_TOKEN)) this.securityToken = options.optString( OPT_SECURITY_TOKEN );

        try {

            // ** SDK INITIALIZATION **
            //when you start Fyber SDK you get a Settings object that you can use to customise the SDK behaviour.
            //Have a look at the method 'customiseFyberSettings' to learn more about possible customisation.
            Fyber.Settings fyberSettings = Fyber
                    .with(this.appKey, cordova.getActivity())
                    .withSecurityToken(this.securityToken)
// by default Fyber SDK will start precaching. If you wish to only start precaching at a later time you can uncomment this line and use 'CacheManager' to start, pause or resume on demand.
//                  .withManualPrecaching()
// if you do not provide an user id Fyber SDK will generate one for you
                    .withUserId(this.userId)
                    .start();
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    private PluginResult executeShowOfferwall(JSONObject options, CallbackContext callbackContext) {
        Log.w(LOGTAG, "executeShowOfferwall");
        
        this.showOfferWall( options );
        
        callbackContext.success();

        return null;
    }

    private void showOfferWall(JSONObject options) {
        //Intent offerWallIntent = SponsorPayPublisher.getIntentForOfferWallActivity(cordova.getActivity().getApplicationContext(), true);
        //cordova.getActivity().startActivityForResult(offerWallIntent, OFFERWALL_REQUEST_CODE);
        OfferWallRequester.create(this).request(cordova.getActivity());
    }
    
    private PluginResult executeShowRewardedVideo(JSONObject options, CallbackContext callbackContext) {
        Log.w(LOGTAG, "executeShowRewardedVideo");
        
        this.showRewardedVideo( );
        
        callbackContext.success();

        return null;
    }

    private void showRewardedVideo() {
        RewardedVideoRequester.create(this)
                .withVirtualCurrencyRequester(getVirtualCurrencyRequester())
                .request(cordova.getActivity());
    }

    private PluginResult executeShowInterstitial(JSONObject options, CallbackContext callbackContext) {
        Log.w(LOGTAG, "executeShowInterstitial");
        
        this.showInterstitial( );
        
        callbackContext.success();

        return null;
    }

    private void showInterstitial() {
        InterstitialRequester.create(this)
                .request(cordova.getActivity());
    }

    @Override
    public void onAdAvailable(Intent intent) {
        resetRequestingState();
        
        AdFormat adFormat = AdFormat.fromIntent(intent);
        switch (adFormat) {
            case OFFER_WALL:
                //in our sample app, we want to show the offer wall in a single step.
                cordova.getActivity().startActivityForResult(intent, OFFERWALL_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onAdNotAvailable(AdFormat adFormat) {
        FyberLogger.d(getLogTag(), "No ad available");
    }

    @Override
    public void onRequestError(RequestError requestError) {
        FyberLogger.d(getLogTag(), "Semething went wrong with the request: " + requestError.getDescription());
    }

    private VirtualCurrencyRequester getVirtualCurrencyRequester() {
        return VirtualCurrencyRequester.create(this)
                // user will get a toast notification upon reward
                .notifyUserOnReward(true)
//              this is the currency id for RV ad format
//               you can refer to this -- http://developer.fyber.com/content/android/basics/rewarding-the-user/vcs/
                .forCurrencyId("mondos")
                ;
    }

    @Override
    public void onError(VirtualCurrencyErrorResponse virtualCurrencyErrorResponse) {
        FyberLogger.d(TAG, "VCS error received - " + virtualCurrencyErrorResponse.getErrorMessage());
    }

    @Override
    public void onSuccess(VirtualCurrencyResponse virtualCurrencyResponse) {
        FyberLogger.d(TAG, "VCS coins received - " + virtualCurrencyResponse.getDeltaOfCoins());
    }

    @Override
    public void onRequestError(RequestError requestError) {
        FyberLogger.d(TAG, "error requesting vcs: " + requestError.getDescription());
    }
}