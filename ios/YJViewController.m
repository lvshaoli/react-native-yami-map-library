//
//  YJViewController.m
//  RNYamiMapLibrary
//
//  Created by lvshaoli on 2019/8/22.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "YJViewController.h"

@interface YJViewController ()

@end
#import "YJViewController.h"
#import <CoreLocation/CoreLocation.h>
#import <CoreBluetooth/CoreBluetooth.h>

@interface YJViewController ()<WKNavigationDelegate,WKUIDelegate, WKScriptMessageHandler, WKNavigationDelegate,CLLocationManagerDelegate,CBPeripheralManagerDelegate>
@property(nonatomic, strong)WKWebView* mainWeb;
@property(nonatomic, strong)CBPeripheralManager* blueToothManager;
@property(nonatomic, strong)CLLocationManager* locationManager;
@end

@implementation YJViewController

BOOL callResultBack;
- (void)viewDidLoad {
    [super viewDidLoad];
    self.blueToothManager = [[CBPeripheralManager alloc] init];
    self.locationManager = [[CLLocationManager alloc] init];
    callResultBack = NO;
    
    // Do any additional setup after loading the view.
    
    //! 为userContentController添加ScriptMessageHandler，并指明name
    WKUserContentController *userContentController = [[WKUserContentController alloc] init];
    
    NSArray* functionNames = [[NSArray alloc] initWithObjects:@"startBeacon", @"searchBeacon", @"stopBeacon", nil];
    
    for (NSString* functionName in functionNames) {
        [userContentController addScriptMessageHandler:self name:functionName];
    }
    
    //! 使用添加了ScriptMessageHandler的userContentController配置configuration
    WKWebViewConfiguration *configuration = [[WKWebViewConfiguration alloc] init];
    configuration.userContentController = userContentController;
    
    
    
    
    self.mainWeb = [[WKWebView alloc] initWithFrame:self.view.frame configuration: configuration];
    //    mainWeb.delegate = self
    [self.mainWeb loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"https://tx.yunjinginc.com/wechatexp/beaconDemo/index.html"]]];
    self.mainWeb.navigationDelegate = self;
    self.mainWeb.UIDelegate = self;
    //开了支持滑动返回
    self.mainWeb.allowsBackForwardNavigationGestures = YES;
    [self.view addSubview:self.mainWeb];
    
    self.blueToothManager.delegate = self;
    self.locationManager.delegate = self;
    self.locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    
    NSLog(@"添加完成");
}

- (void)peripheralManagerDidUpdateState:(CBPeripheralManager *)peripheral{
    if(peripheral.state == CBManagerStatePoweredOn) {
        NSLog(@"蓝牙已经打开");
    } else {
        NSLog(@"蓝牙没有打开");
    }
}

// 启动iBean扫描服务
-(void)startBeaconLocationService {
    [self.locationManager requestWhenInUseAuthorization];
    NSString *wechatUUID = @"FDA50693-A4E2-4FB1-AFCF-C6EB07647825";
    NSUUID *uuId = [[NSUUID alloc] initWithUUIDString:wechatUUID];
    CLBeaconRegion* beaconIdentifier = [[CLBeaconRegion alloc] initWithProximityUUID:uuId identifier:@"wechatUUID"];
    [self.locationManager startRangingBeaconsInRegion:beaconIdentifier];
    [self startWebJSFunction:@"backSDK.startBeacon.complete({'errMsg' : 'startSearchBeacons:ok'})"];
}

///// 停止iBeacon扫描服务
//func stopBeaconLocationService() -> Void {
//    let beaconIdentifier = CLBeaconRegion.init(proximityUUID: NSUUID.init(uuidString: wechatUUID)! as UUID, identifier: "wechatUUID")
//    self.locationManager.stopRangingBeacons(in: beaconIdentifier)
//    callResultBack = false
//    startWebJSFunction(with: "backSDK.stopBeacon.complete({'errMsg' : 'stopSearchBeacons:ok'})")
//}

-(void)stopBeaconLocationService {
    NSString *wechatUUID = @"FDA50693-A4E2-4FB1-AFCF-C6EB07647825";
    NSUUID *uuId = [[NSUUID alloc] initWithUUIDString:wechatUUID];
    CLBeaconRegion* beaconIdentifier = [[CLBeaconRegion alloc] initWithProximityUUID:uuId identifier:@"wechatUUID"];
    [self.locationManager stopRangingBeaconsInRegion:beaconIdentifier];
    callResultBack = NO;
    [self startWebJSFunction:@"backSDK.stopBeacon.complete({'errMsg' : 'stopSearchBeacons:ok'})"];
}

//func startBeacon() {
//    if !CLLocationManager.locationServicesEnabled() && CLLocationManager.authorizationStatus() != .restricted && CLLocationManager.authorizationStatus() != .denied {
//        startWebJSFunction(with: "backSDK.startBeacon.complete({'errMsg' : 'startSearchBeacons:location service disable'})")
//    }else if self.blueToothManager.state != .poweredOn {
//        startWebJSFunction(with: "backSDK.startBeacon.complete({'errMsg' : 'startSearchBeacons:bluetooth power off'})")
//    }else {
//        startBeaconLocationService()
//    }
//}

-(void)startBeacon{
    if(!CLLocationManager.locationServicesEnabled && CLLocationManager.authorizationStatus != kCLAuthorizationStatusRestricted && CLLocationManager.authorizationStatus != kCLAuthorizationStatusDenied) {
        
        [self startWebJSFunction:@"backSDK.startBeacon.complete({'errMsg' : 'startSearchBeacons:location service disable'})"];
    } else if(self.blueToothManager.state != CBManagerStatePoweredOn) {
        [self startWebJSFunction:@"backSDK.startBeacon.complete({'errMsg' : 'startSearchBeacons:bluetooth power off'})"];
    } else {
        [self startBeaconLocationService];
    }
}


-(void)startWebJSFunction: (NSString *)jsString {
    [self.mainWeb evaluateJavaScript:jsString completionHandler:^(id _Nullable succ, NSError * _Nullable error) {
        NSLog(@"!!!!!!!startWebJSFunction!!!!!!%@", error);
    }];
}


//#---CLLocationManagerDelegate----
- (void)locationManager:(CLLocationManager *)manager
        didRangeBeacons:(NSArray<CLBeacon *> *)beacons inRegion:(CLBeaconRegion *)region {
    if (!callResultBack) return;
    //    if beacons.count > 0 {
    //        var reachableBeacons = Array<[String : Any]>.init()
    //        for beacon: CLBeacon in beacons {
    //            if beacon.proximity != CLProximity.unknown {
    //                let beaconInfo : [String : Any] = [
    //                                                   "uuid" : beacon.proximityUUID.uuidString,
    //                                                   "major" : beacon.major,
    //                                                   "minor" : beacon.minor,
    //                                                   "rssi" : "\(beacon.rssi)",
    //                                                   "proximity" : "0",
    //                                                   "accuracy" : "\(beacon.accuracy)"
    //                                                   ]
    //                reachableBeacons.append(beaconInfo)
    //            }
    //        }
    //        let result = ["beacons" : reachableBeacons]
    //        let argv = try! result.json()
    //        startWebJSFunction(with: "backSDK.searchBeacon.complete(\(argv))")
    //    }
    
    if (beacons.count > 0) {
        NSArray* reachableBeacons = [[NSArray alloc] init];
        for (CLBeacon* beacon in beacons) {
            NSLog(@"====================!!!!%@", beacon);
            if(beacon.proximity != CLProximityUnknown) {
                //                NSArray* beaconInfo = [[NSArray alloc] initWithObjects:@{}, nil]
                NSLog(@"====================!!!!%@", beacon);
            }
        }
    }
    
}

- (void)yjTest {
    
    NSLog(@"#######云景test");
}

#pragma mark - WKScriptMessageHandler

//if message.name == "startBeacon" {
//    startBeacon()
//}
//if message.name == "searchBeacon" {
//    callResultBack = true
//}
//if message.name == "stopBeacon" {
//    stopBeaconLocationService()
//}

//! WKWebView收到ScriptMessage时回调此方法
- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if([message.name caseInsensitiveCompare:@"startBeacon"] == NSOrderedSame) {
        NSLog(@"-------------------%@", message.name);
        [self startBeacon];
        
    } else if([message.name caseInsensitiveCompare:@"searchBeacon"] == NSOrderedSame) {
        callResultBack = YES;
    }else if([message.name caseInsensitiveCompare:@"stopBeacon"] == NSOrderedSame) {
        [self stopBeaconLocationService];
    }
    
    //    if ([message.name caseInsensitiveCompare:@"jsToOc"] == NSOrderedSame) {
    //
    //    }
}

/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

@end
