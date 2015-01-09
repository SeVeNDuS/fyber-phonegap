package uk.mondosports.plugins.fyber;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.sponsorpay.publisher.SponsorPayPublisher;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FyberPlugin extends CordovaPlugin {

    private static final String LOGTAG = "FyberPlugin";
    private static final String DEFAULT_APP_KEY = "27434";

    private static final int OFFERWALL_REQUEST_CODE = 12102013;

    private static final String ACTION_INITIALIZE = "initialize";
    private static final String ACTION_SHOW_OFFERWALL = "showOfferwall";
    private static final String ACTION_SHOW_REWARDEDVIDEO = "showRewardedVideo";
    private static final String OPT_APPLICATION_KEY = "appKey";
    private static final String OPT_USER_ID = "userId";
    private static final String OPT_SECURITY_TOKEN = "securityToken";

    private String appKey = DEFAULT_APP_KEY;
    private String userId = "5043b715c3bd823b760000ff";
    private String securityToken = "";
    private SSAPublisher ssaPub; 

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
            SponsorPay.start(this.appKey, this.userId, this.securityToken, cordova.getActivity());
        } catch (RuntimeException e){
            Log.d(LOGTAG, e.getLocalizedMessage());
        }
    }

    private PluginResult executeShowOfferwall(JSONObject options, CallbackContext callbackContext) {
        Log.w(LOGTAG, "executeShowOfferwall");
        
        this.showOfferWall( options );
        
        callbackContext.success();

        return null;
    }

    private void showOfferWall(JSONObject options) {
        Intent offerWallIntent = SponsorPayPublisher.getIntentForOfferWallActivity(cordova.getContext(), true);
        startActivityForResult(offerWallIntent, OFFERWALL_REQUEST_CODE);
    }
    
    private PluginResult executeShowRewardedVideo(JSONObject options, CallbackContext callbackContext) {
        Log.w(LOGTAG, "executeShowRewardedVideo");
        
        this.showRewardedVideo( );
        
        callbackContext.success();

        return null;
    }

    private void showRewardedVideo() {
        //ssaPub.showRewardedVideo();
    }
}