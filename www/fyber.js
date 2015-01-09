function FyberPlugin() {}

FyberPlugin.prototype.initialize = function (appKey, userId, securityToken, success, error) {
	cordova.exec(success, error, 'Fyber', 'initialize', [appKey, userId, securityToken]);
};

FyberPlugin.prototype.showOfferwall = function (success, error) {
	cordova.exec(success, error, 'Fyber', 'showOfferwall', []);
};

FyberPlugin.prototype.showRewardedVideo = function (success, error) {
	cordova.exec(success, error, 'Fyber', 'showRewardedVideo', []);
};

module.exports = new FyberPlugin();