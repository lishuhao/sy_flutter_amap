#import "SyFlutterAmapPlugin.h"
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <AMapLocationKit/AMapLocationKit.h>
#import <CoreLocation/CoreLocation.h>


@interface SyFlutterAmapPlugin()

@property AMapLocationManager* locationManager;
@property (strong, nonatomic) CLLocationManager *mannger;

@end

@implementation SyFlutterAmapPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"cn.isanye.sy_flutter_amap"
            binaryMessenger:[registrar messenger]];
  SyFlutterAmapPlugin* instance = [[SyFlutterAmapPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
    
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getLastKnownLocation" isEqualToString:call.method]) {
      [self getLastKnownLocation:call result:result];
  }else if ([@"hasPermission" isEqualToString:call.method]) {
      [self hasPermission:call result:result];
  }else if ([@"requestPermission" isEqualToString:call.method]) {
      [self requestPermission:call result:result];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

//检查是否授予定位权限
- (void)hasPermission:(FlutterMethodCall*)call result:(FlutterResult)result{
    CLAuthorizationStatus locationStatus =  [CLLocationManager authorizationStatus];
    result(@((bool)(locationStatus == kCLAuthorizationStatusAuthorizedWhenInUse || locationStatus == kCLAuthorizationStatusAuthorizedAlways)));
}

//请求定位权限
- (void)requestPermission:(FlutterMethodCall*)call result:(FlutterResult)result{
    self.mannger =  [[CLLocationManager alloc] init];
    [self.mannger requestWhenInUseAuthorization];
    NSLog(@"re");
    result(@((bool)FALSE));//TODO 目前请求权限不会弹出选项，需要用户手动设置
}

//获取最近一次定位
- (void) getLastKnownLocation:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSString *apiKey = call.arguments[@"iosApiKey"];
    [AMapServices sharedServices].apiKey = apiKey;
    self.locationManager = [[AMapLocationManager alloc] init];
    
    [self.locationManager setDesiredAccuracy:kCLLocationAccuracyHundredMeters];
    [self.locationManager requestLocationWithReGeocode:TRUE completionBlock:^(CLLocation *location, AMapLocationReGeocode *regeocode, NSError *error) {
        if(error){
            NSLog(@"locError:{%ld - %@};", (long)error.code, error.localizedDescription);
            result([FlutterError errorWithCode:@"GET_LOCATION_FAILED" message:error.localizedDescription details:error.localizedDescription]);
            return;
        }
        
        NSLog(@"location:%@", location);
        if (regeocode){
            NSLog(@"reGeocode:%@", regeocode);
        }
        
        NSDictionary *dict = @{
                               @"lon":@(location.coordinate.longitude),
                               @"lat":@(location.coordinate.latitude),
                               @"country":regeocode.country,
                               @"province":regeocode.province,
                               @"city":regeocode.city,
                               @"district":regeocode.district,
                               @"road":@"",
                               @"street":regeocode.street,
                               @"number":regeocode.number,
                               @"address":regeocode.formattedAddress
                               };
        NSData *data = [NSJSONSerialization dataWithJSONObject:dict options:kNilOptions error:nil];
        NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        result(str);
    }];
}

@end
