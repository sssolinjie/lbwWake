#import "LbwWakePlugin.h"

@implementation LbwWakePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"lbwWake"
            binaryMessenger:[registrar messenger]];
  LbwWakePlugin* instance = [[LbwWakePlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"initsdk" isEqualToString:call.method]) {
      NSLog(@"TTS该插件不支持ios");
  }else {
    result(FlutterMethodNotImplemented);
  }
}

@end
